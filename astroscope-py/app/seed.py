from app.extensions import db
from app.models import (
    CollaborationGroup,
    Observation,
    StarCatalogEntry,
    User,
    group_members,
)


def seed_if_empty() -> None:
    if User.query.count() > 0:
        return

    admin = _user("admin", "Admin User", "European Southern Observatory", "ADMIN", True)
    nova = _user("nova", "Dr. Nova Chen", "MIT Kavli Institute", "MODERATOR", True)
    kepler = _user("kepler", "Sam Rivera", "Amateur Astro Network", "MEMBER", False)
    guest = _user("guest", "Guest Observer", "Public Outreach", "MEMBER", False)

    ligo = _group("ligo-collab", "LIGO Open Data Circle", admin, [admin, nova])
    _group("exoplanet-watch", "Exoplanet Watch", nova, [nova, kepler])

    db.session.add(
        Observation(
            author=nova,
            title="Andromeda drift study",
            body_markdown="Measured proper motion over 6 nights.",
            is_private=False,
            target_object="M31",
            group=ligo,
        )
    )
    db.session.add(
        Observation(
            author=admin,
            title="CLASSIFIED calibration run",
            body_markdown="Private calibration notes for admin review only.",
            is_private=True,
            target_object="NGC 1300",
            group=ligo,
        )
    )

    for designation, common, constellation, mag, notes in [
        ("HD 209458", "Osiris", "Pegasus", 7.65, "Hot Jupiter host star."),
        ("Alpha CMa", "Sirius", "Canis Major", -1.46, "Brightest star in Earth's night sky."),
        ("PSR J0348+0432", "J0348", "Taurus", 99.0, "Massive neutron star system."),
    ]:
        db.session.add(
            StarCatalogEntry(
                designation=designation,
                common_name=common,
                constellation=constellation,
                magnitude=mag,
                notes=notes,
            )
        )

    db.session.commit()


def _user(username, display, institution, role, verified):
    user = User(
        username=username,
        display_name=display,
        institution=institution,
        role=role,
        verified=verified,
    )
    user.set_password("astroscope")
    db.session.add(user)
    db.session.flush()
    return user


def _group(slug, name, owner, members):
    group = CollaborationGroup(slug=slug, name=name, description=f"Collaboration group for {name}", owner=owner)
    db.session.add(group)
    db.session.flush()
    for member in members:
        db.session.execute(group_members.insert().values(group_id=group.id, user_id=member.id))
    return group
