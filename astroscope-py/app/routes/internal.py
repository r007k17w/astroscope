from flask import Blueprint, jsonify, request

from app.platform.telemetry.metrics import internal_metrics_snapshot

internal_bp = Blueprint("internal", __name__, url_prefix="/internal")


@internal_bp.route("/metrics")
def metrics():
    remote = request.remote_addr or ""
    if not remote.startswith("127.") and remote != "::1":
        return jsonify({"error": "forbidden"}), 403
    return jsonify(internal_metrics_snapshot.build_payload())
