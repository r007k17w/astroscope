from flask import Blueprint, jsonify, request
from flask_login import current_user, login_required

from app.services.federation_service import federation_ingress_service
from app.services.profile_service import profile_service

api_bp = Blueprint("api", __name__, url_prefix="/api")


@api_bp.route("/profile", methods=["PATCH"])
@login_required
def patch_profile():
    payload = request.get_json(silent=True) or {}
    user = profile_service.patch_profile_api(current_user, payload)
    return jsonify(
        {
            "username": user.username,
            "role": user.role,
            "verified": user.verified,
        }
    )


@api_bp.route("/federation/sync", methods=["POST"])
@login_required
def federation_sync():
    if current_user.role != "ADMIN":
        return jsonify({"error": "forbidden"}), 403
    location = (request.get_json(silent=True) or {}).get("location", "")
    snapshot = federation_ingress_service.sync_from_internal_url(location)
    return jsonify({"synced": len(snapshot.get("accounts", []))})