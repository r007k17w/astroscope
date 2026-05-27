from flask import Blueprint, jsonify, request

from app.platform.webhook.jwt_verifier import federation_jwt_verifier
from app.services.federation_service import federation_ingress_service

webhook_bp = Blueprint("webhook", __name__, url_prefix="/webhook")


@webhook_bp.route("/federation", methods=["POST"])
def federation():
    auth_header = request.headers.get("Authorization", "")
    token = auth_header[7:] if auth_header.startswith("Bearer ") else request.form.get("token", "")
    payload = federation_jwt_verifier.verify_bearer(token)
    if not payload:
        return jsonify({"status": "rejected"}), 401
    federation_ingress_service.apply_snapshot(payload)
    return jsonify({"status": "accepted"})
