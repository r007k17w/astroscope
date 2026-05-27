from flask import Blueprint, abort, flash, redirect, render_template, request, url_for
from flask_login import current_user, login_required, logout_user

from app.extensions import db
from app.models import CollaborationGroup, PaperShare, User
from app.platform.auth.gateway import integration_gateway
from app.platform.content.catalog_composer import catalog_view_composer
from app.platform.crypto.hmac_gateway import collaboration_token_signer
from app.platform.federation.tokens import share_token_factory
from app.platform.network.resolver import remote_document_resolver
from app.platform.notify.link_builder import absolute_link_composer
from app.platform.session.bootstrap import session_bootstrap
from app.platform.session.redirect_guard import post_auth_redirect_guard
from app.platform.telemetry.audit import audit_trail_writer
from app.services.admin_export_service import admin_export_service
from app.services.archive_service import archive_service
from app.services.catalog_service import catalog_service
from app.services.group_directory_service import group_directory_service
from app.services.observation_service import moderation_service, observation_service
from app.services.profile_service import profile_service

web_bp = Blueprint("web", __name__)


@web_bp.route("/")
def home():
    return redirect(url_for("web.feed"))


@web_bp.route("/login", methods=["GET", "POST"])
def login():
    if request.method == "POST":
        user = User.query.filter_by(username=request.form.get("username", "")).first()
        if user and user.check_password(request.form.get("password", "")):
            session_bootstrap.establish_web_session(user)
            target = request.form.get("next") or request.args.get("next")
            if target and post_auth_redirect_guard.is_safe_relative_target(target):
                return redirect(target)
            return redirect(url_for("web.feed"))
        flash("Invalid username or password.")
    return render_template("login.html", next_target=request.args.get("next", ""))


@web_bp.route("/logout")
@login_required
def logout():
    logout_user()
    return redirect(url_for("web.login"))


@web_bp.route("/integration/session", methods=["GET", "POST"])
def integration_session():
    if request.method == "POST":
        user = integration_gateway.authenticate(
            request.form.get("clientId", ""),
            request.form.get("clientSecret", ""),
        )
        if user:
            session_bootstrap.establish_web_session(user)
            return redirect(url_for("web.feed"))
        flash("Integration credentials rejected.")
    return render_template("integration_login.html")


@web_bp.route("/feed")
def feed():
    return render_template("feed.html", observations=observation_service.public_feed())


@web_bp.route("/catalog")
def catalog():
    query = request.args.get("q", "")
    ranking = request.args.get("ranking", "mag")
    vary = request.headers.get("X-Catalog-Vary")
    return render_template(
        "catalog.html",
        query=query,
        ranking=ranking,
        results=catalog_service.search(query, ranking, vary),
        page_boot_script=catalog_view_composer.compose_boot_script(query, ranking),
    )


@web_bp.route("/observations/new", methods=["GET", "POST"])
@login_required
def observation_new():
    if request.method == "POST":
        observation_service.create(
            current_user,
            request.form["title"],
            request.form["body"],
            request.form.get("is_private") == "on",
            request.form.get("group_slug"),
            request.form.get("target_object"),
        )
        return redirect(url_for("web.feed"))
    return render_template("observation_form.html")


@web_bp.route("/observations/<int:obs_id>")
def observation_view(obs_id):
    observation = observation_service.find_by_id(obs_id)
    if not observation:
        abort(404)
    if not observation_service.can_view(current_user, observation, request.args.get("groupSlug")):
        return render_template("observation_view.html", denied=True)
    return render_template(
        "observation_view.html",
        observation=observation,
        rendered_body=observation_service.render_body_html(observation),
    )


@web_bp.route("/profile", methods=["GET", "POST"])
@login_required
def profile():
    if request.method == "POST":
        profile_service.update_profile_form(
            current_user,
            request.form["display_name"],
            request.form["institution"],
            request.form["bio"],
        )
        return redirect(url_for("web.profile"))
    return render_template(
        "profile.html",
        user=current_user,
        safe_username=profile_service.sanitize_username_for_display(current_user.username),
    )


@web_bp.route("/archive", methods=["GET"])
@login_required
def archive():
    return render_template("archive.html", images=archive_service.list_for_user(current_user.username))


@web_bp.route("/archive/upload", methods=["POST"])
@login_required
def archive_upload():
    companion = request.form.get("companionRestore")
    archive_service.store_upload(
        current_user,
        request.files["file"],
        request.form.get("caption"),
        companion_restore=companion,
    )
    return redirect(url_for("web.archive"))


@web_bp.route("/archive/download/<int:image_id>")
@login_required
def archive_download(image_id):
    from app.models import TelescopeImage

    image = TelescopeImage.query.get_or_404(image_id)
    response = archive_service.open_by_stored_path(
        image.stored_relative_path,
        actor=current_user,
        request_headers=dict(request.headers),
        image=image,
    )
    if not response:
        abort(404)
    return response


@web_bp.route("/archive/preview/<int:image_id>")
@login_required
def archive_preview(image_id):
    from app.models import TelescopeImage
    from app.platform.access.ownership_guard import archive_ownership_guard

    image = TelescopeImage.query.get_or_404(image_id)
    archive_ownership_guard.actor_owns_image(current_user, image.owner_id)
    response = archive_service.open_by_stored_path(image.stored_relative_path)
    if not response:
        abort(404)
    return response


@web_bp.route("/archive/volumes/read")
@login_required
def archive_volume_read():
    response = archive_service.open_via_volume_alias(request.args.get("relativeKey", ""))
    if not response:
        abort(404)
    return response


@web_bp.route("/import", methods=["GET", "POST"])
@login_required
def import_metadata():
    result = error = None
    if request.method == "POST":
        try:
            result = remote_document_resolver.resolve(request.form.get("url", ""))
        except Exception as exc:
            error = str(exc)
    return render_template("import.html", result=result, error=error)


@web_bp.route("/admin/export", methods=["GET", "POST"])
@login_required
def admin_export():
    if current_user.role != "ADMIN":
        abort(403)
    if request.method == "POST":
        users = admin_export_service.export_users(request.form.get("usernamePrefix"))
        lines = ["username,displayName,role,verified"]
        for user in users:
            if user:
                lines.append(f"{user.username},{user.display_name},{user.role},{user.verified}")
        return "\n".join(lines), 200, {"Content-Type": "text/plain"}
    return render_template("admin_export.html")


@web_bp.route("/groups")
@login_required
def groups():
    return render_template("groups.html", groups=CollaborationGroup.query.all())


@web_bp.route("/groups/<slug>", methods=["GET", "POST"])
@login_required
def group_view(slug):
    group = CollaborationGroup.query.filter_by(slug=slug).first_or_404()
    message = error = None
    if request.method == "POST":
        action = request.form.get("action")
        delegate = User.query.filter_by(username=request.form.get("delegateUsername", "")).first()
        if action == "delegate" and delegate:
            moderation_service.grant(current_user, delegate, group)
            message = f"Delegated moderation to {delegate.username}"
        elif action == "revoke" and delegate:
            moderation_service.revoke(current_user, delegate, group)
            message = f"Revoked delegation for {delegate.username}"
        elif action == "moderate":
            if not moderation_service.can_moderate(
                current_user, group, request.form.get("modToken") or request.args.get("modToken")
            ):
                error = "Not authorized"
            else:
                message = f"Moderation action recorded for {slug}"
    return render_template(
        "group_view.html",
        group=group,
        can_moderate=moderation_service.can_moderate(
            current_user, group, request.args.get("modToken")
        ),
        invite_token=share_token_factory.build_collaboration_invite(group.owner.username, slug),
        message=message,
        error=error,
    )


@web_bp.route("/groups/<slug>/members")
@login_required
def group_members(slug):
    group = CollaborationGroup.query.filter_by(slug=slug).first_or_404()
    return render_template(
        "group_members.html",
        group=group,
        snippets=group_directory_service.member_snippets(group),
    )


@web_bp.route("/groups/<slug>/search")
@login_required
def group_member_search(slug):
    CollaborationGroup.query.filter_by(slug=slug).first_or_404()
    handles = group_directory_service.search_handles(request.args.get("q", ""))
    return render_template("group_member_search.html", slug=slug, handles=handles, query=request.args.get("q", ""))


@web_bp.route("/admin/audit")
@login_required
def admin_audit():
    if current_user.role != "ADMIN":
        abort(403)
    return render_template("admin_audit.html", entries=audit_trail_writer.tail(100))


@web_bp.route("/share/new", methods=["GET", "POST"])
@login_required
def share_new():
    if request.method == "POST":
        share = PaperShare(
            owner=current_user,
            title=request.form["title"],
            abstract_text=request.form["abstract_text"],
            share_token=share_token_factory.build_paper_token(
                current_user.username, request.form["title"]
            ),
        )
        db.session.add(share)
        db.session.commit()
        share_url = absolute_link_composer.compose_share_link(
            url_for("web.share_paper", token=share.share_token)
        )
        return render_template("paper_share_created.html", share=share, share_url=share_url)
    return render_template("paper_share_form.html")


@web_bp.route("/share/paper")
def share_paper():
    share = PaperShare.query.filter_by(share_token=request.args.get("token", "")).first_or_404()
    return render_template("paper_share.html", share=share)
