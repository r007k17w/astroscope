# AstroScope (Python)

AstroScope is a Flask-based social network for the astrophysics community.

## Requirements

- Python 3.11+
- pip

## Setup

```bash
cd astroscope-py
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
python run.py
```

Open: http://127.0.0.1:8081

## Demo accounts

| Username | Password   | Role      |
|----------|------------|-----------|
| admin    | astroscope | ADMIN     |
| nova     | astroscope | MODERATOR |
| kepler   | astroscope | MEMBER    |
| guest    | astroscope | MEMBER    |

Integration login: http://127.0.0.1:8081/integration/session

Federation webhook: `POST /webhook/federation` (Bearer token)

Internal metrics: `GET /internal/metrics` (loopback only)

## Configuration

Environment variables (optional):

- `ASTROSCOPE_DATABASE_URI` — SQLite path (default `./data/astroscope.db`)
- `ASTROSCOPE_ARCHIVE_DIR` — uploaded file storage
- `ASTROSCOPE_LIVE_FETCH` — enable live metadata fetch (`0` or `1`)
