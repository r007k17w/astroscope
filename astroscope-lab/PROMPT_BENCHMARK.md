# AstroScope Prompt Benchmark Guide

Use this rubric to measure AI security review quality against the 10 planted vulnerabilities documented in `TRAINER_KEY.md`.

## Scan setup

- **Scope:** `astroscope-lab/src/main/**`, `astroscope-lab/src/main/resources/templates/**`
- **Exclude:** `TRAINER_KEY.md`, `PROMPT_BENCHMARK.md`, `README.md`
- **Goal:** find user-controlled sources with concrete sink paths

## Recommended master prompt (iteration baseline)

```
Review all Java and Thymeleaf files in this project for security vulnerabilities.
For each finding provide: severity, confidence, source, sink, full call path, auth prerequisites.
Requirements:
- Compare secure vs legacy/alternate code paths
- Trace session-scoped state and cache invalidation
- Review admin/export endpoints and second-order data flows
- Inspect custom sanitizers and "Safe*" utility classes skeptically
- Check REST PATCH/PUT handlers for mass assignment
- Do not stop at the first secure implementation if a weaker path exists
```

## Scoring rubric

| Result | Definition |
|--------|------------|
| **TP** | Correct vuln ID/type with accurate source→sink path |
| **Partial** | Correct area/type but incomplete path, wrong endpoint, or missing auth context |
| **Miss** | No mention of planted vuln |
| **FP** | Reports secure code as vulnerable without valid chain |

Per-level recall = TP / 1 for that level (each level has exactly one primary vuln).

## Expected difficulty curve

Vulnerabilities are buried in `platform.*` packages, shared layout boot scripts, policy pipelines, and decoy secure helpers (`SecureQueryBuilder`, `ParameterizedCredentialVerifier`, secure archive download path).

| Level | ID | Typical AI outcome without tuned prompt |
|-------|----|----------------------------------------|
| 1 | ASTRO-01 | Missed if only scanning controllers for SQL strings |
| 2 | ASTRO-02 | Missed if templates-only scan (XSS in `layout.html` boot script) |
| 3 | ASTRO-03 | Missed without reading full `AccessDecisionPipeline` |
| 4 | ASTRO-04 | Missed if only reviewing upload sanitizer / secure download |
| 5 | ASTRO-05 | Missed without tracing `data-equation` markup branch |
| 6 | ASTRO-06 | Missed (feature off by default + trust-on-first-host) |
| 7 | ASTRO-07 | Missed if only HTML forms reviewed |
| 8 | ASTRO-08 | Missed due to decoy `SecureQueryBuilder` |
| 9 | ASTRO-09 | Missed without session-state + lifecycle coordinator tracing |
| 10 | ASTRO-10 | Missed without federation audience analysis |

## Iteration checklist (add to skills when recall is low)

1. **Dual-path rule:** for every feature, search for `legacy`, `deprecated`, alternate controllers
2. **Sanitizer rule:** grep `Safe`, `sanitize`, `Html`, `Validator` and verify all call sites
3. **Session rule:** inspect `@SessionScope`, filters, caches for invalidation on revoke/logout
4. **Export rule:** inspect admin/report/CSV builders for concatenated SQL
5. **API rule:** inspect `@PatchMapping`, `@RequestBody Map`, DTO merge helpers
6. **Template rule:** grep `th:utext`, `[(${`, unescaped output
7. **Token rule:** trace share/invite/JWT validation audiences separately

## Sample scoring sheet

| ID | Found? | Score | Notes |
|----|--------|-------|-------|
| ASTRO-01 | | TP/Partial/Miss/FP | |
| ASTRO-02 | | | |
| ... | | | |

## Workflow

1. Run scan with prompt/skill vN
2. Record findings + claimed paths
3. Score against trainer key
4. Add one checklist rule targeting lowest recall levels
5. Repeat until recall plateau

## Success target

- Prompt v1: ≥7/10 TP on levels 1–7
- Prompt vFinal: ≥8/10 TP including at least one of levels 9–10
