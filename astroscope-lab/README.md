# AstroScope Lab

**DO NOT DEPLOY PUBLICLY.** This is an intentionally vulnerable Spring Boot application for local security prompt benchmarking only.

AstroScope is a fictional astrophysics community social network used to evaluate how thoroughly AI security review prompts/skills detect planted vulnerabilities.

## Run locally

Requirements: Java 17+, Maven 3.9+

If Java is not installed (macOS):

```bash
brew install openjdk@17 maven
export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"
export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
```

```bash
cd astroscope-lab
mvn spring-boot:run
```

Open: http://127.0.0.1:8080

Default binds to `127.0.0.1` only.

## Demo accounts

| Username | Password   | Role      |
|----------|------------|-----------|
| admin    | astroscope | ADMIN     |
| nova     | astroscope | MODERATOR |
| kepler   | astroscope | MEMBER    |
| guest    | astroscope | MEMBER    |

## Benchmarking AI scans

1. Run your Cursor skill/prompt against `src/main/**` and templates.
2. **Exclude** [`TRAINER_KEY.md`](TRAINER_KEY.md) from the scan context to avoid leaking answers.
3. Score results using [`PROMPT_BENCHMARK.md`](PROMPT_BENCHMARK.md).

## Features

- Observation feed and private observations
- Star catalog search (safe + legacy sort modes)
- Collaboration groups with delegated moderation
- Telescope image archive
- External metadata import (SSRF lab sink; network disabled by default)
- Admin CSV export
- Paper share links and collaboration invite tokens

## Safety

- SSRF real network fetch is disabled unless `lab.ssrf.enabled=true` in `application.yml`.
- H2 database file stored under `./data/`.
- Contains exactly **10** documented vulnerabilities in `TRAINER_KEY.md`.
