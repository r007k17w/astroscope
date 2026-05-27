# AstroScope

AstroScope is a Spring Boot social network for the astrophysics community: observation feeds, collaboration groups, star catalog search, telescope archives, and paper sharing.

## Run locally

Requirements: Java 17+, Maven 3.9+

```bash
cd astroscope-lab
mvn spring-boot:run
```

Open: http://127.0.0.1:8080

The app binds to `127.0.0.1` by default.

## Demo accounts

| Username | Password   | Role      |
|----------|------------|-----------|
| admin    | astroscope | ADMIN     |
| nova     | astroscope | MODERATOR |
| kepler   | astroscope | MEMBER    |
| guest    | astroscope | MEMBER    |

Integration login (observatory clients): http://127.0.0.1:8080/legacy/login

## Features

- Observation feed and private observations
- Star catalog search
- Collaboration groups with delegated moderation
- Telescope image archive
- External metadata import
- Admin CSV export
- Paper share links and collaboration invite tokens

## Configuration

- H2 database file: `./data/`
- Archive storage: `lab.archive-dir` in `application.yml`
- Live metadata fetch: `lab.metadata.live-fetch` (disabled by default)
