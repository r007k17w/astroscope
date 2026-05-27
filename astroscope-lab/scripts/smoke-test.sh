#!/usr/bin/env bash
# Smoke-test helper for AstroScope lab (requires running app + curl)
set -euo pipefail
BASE="${BASE_URL:-http://127.0.0.1:8080}"

echo "ASTRO-02 reflected XSS probe (should reflect unescaped sort in HTML)"
curl -s "$BASE/catalog?sort=%3Cscript%3Ealert(1)%3C/script%3E" | grep -q '<script>alert(1)</script>' && echo OK || echo FAIL

echo "ASTRO-01 legacy login SQLi probe (manual: POST /legacy/login with username ' OR '1'='1' --)"
echo "ASTRO-03 IDOR probe (manual: GET /observations/2?groupSlug=ligo-collab as guest)"
echo "ASTRO-07 mass assignment (manual: PATCH /api/profile with role ADMIN)"
echo "See TRAINER_KEY.md for full PoC list."
