# AstroScope Trainer Key (GROUND TRUTH)

**Keep this file out of AI scan context when measuring recall.**

## ASTRO-01 — SQL Injection via integration credential template (Level 1)

- **Type:** SQLi / authentication bypass
- **Endpoint:** `POST /legacy/login` (`clientId`, `clientSecret`)
- **Source:** integration credential fields
- **Sink chain:** `ObservatoryIntegrationAuthGateway` → `JdbcCredentialProbe.probe()` → `CredentialBindingTemplate.renderProbe()`
- **Why hidden:** SQL string never appears in controllers; template uses `{principal}` placeholders resembling safe binding
- **PoC clientId:** `' OR '1'='1' --` with any secret (fallback also accepts valid bcrypt login for real demos)
- **Fix:** parameterized queries; never substitute credentials into SQL templates

## ASTRO-02 — Reflected XSS via catalog boot script JSON (Level 2)

- **Type:** Reflected XSS (JavaScript context)
- **Endpoint:** `GET /catalog?ranking=...`
- **Source:** `ranking` query param (only `query` gets quote escaping)
- **Sink chain:** `CatalogViewComposer.composeBootScript()` → `layout.html` `pageBootScript` rendered with `th:utext`
- **Why hidden:** XSS not in catalog template body; injected via shared layout boot script; param renamed from `sort` to `ranking`
- **PoC ranking:** `","x":1};alert(1);//`
- **Fix:** JSON-encode all fields; use `th:text` / avoid dynamic script assembly

## ASTRO-03 — IDOR via collaboration scope policy (Level 3)

- **Type:** Broken access control / IDOR
- **Endpoint:** `GET /observations/{id}?groupSlug=...`
- **Source:** client `groupSlug` mapped to `AccessRequestContext.collaborationScope`
- **Sink:** `CollaborationScopePolicy.evaluate()` matches slug only, not membership
- **Why hidden:** buried in ordered `AccessDecisionPipeline` with legitimate owner/public policies
- **PoC:** as `guest`, open private observation `2` with `?groupSlug=ligo-collab`
- **Fix:** membership check inside collaboration policy

## ASTRO-04 — Volume alias path boundary bypass (Level 4)

- **Type:** Path read via weak mount boundary
- **Endpoint:** `GET /archive/volumes/read?relativeKey=...`
- **Source:** `relativeKey`
- **Sink chain:** `ArchiveStorageService.openViaVolumeAlias()` → `VolumeMountRegistry.isMountedUnder()` prefix check
- **Why hidden:** secure upload + `/archive/download/{id}` use proper containment; weak check isolated in volume alias API
- **PoC:** sibling dir `data/archive-evil/secret.txt`, `relativeKey=../archive-evil/secret.txt`
- **Fix:** strict path relativize boundary, not string prefix

## ASTRO-05 — Stored XSS via equation bridge (Level 5)

- **Type:** Stored XSS
- **Endpoint:** create observation, view `/observations/{id}`
- **Source:** body containing `data-equation=` wrapper
- **Sink chain:** `ScientificMarkupPipeline` → `EquationBridgeStage.render()` → `HtmlSanitizer` incomplete event-handler blocklist
- **Why hidden:** markdown path is safe; triggered only by scientific markup attribute branch
- **PoC body:** `<span data-equation="1"><svg><rect width="20" height="20" onfocus="alert(1)" tabindex="1"/></svg></span>`
- **Fix:** allowlist without active content; don't extract raw inner HTML

## ASTRO-06 — SSRF via metadata resolver redirect (Level 6)

- **Type:** SSRF / trust-on-first-host
- **Endpoint:** `POST /import`
- **Source:** `url`
- **Sink chain:** `ExternalFetchClient` → `RemoteDocumentResolver.resolve()` trusts initial host, follows redirects when `lab.ssrf.enabled=true`
- **Why hidden:** host allowlist present; bypass via redirect/userinfo requires reading resolver follow logic
- **PoC:** enable SSRF, use `http://api.nasa.gov@127.0.0.1:8080/...` or redirect chain
- **Fix:** validate each hop; disable redirects; resolve and pin destination IP

## ASTRO-07 — Mass assignment via entity property bridge (Level 7)

- **Type:** Mass assignment / privilege escalation
- **Endpoint:** `PATCH /api/profile`
- **Source:** JSON map keys
- **Sink:** `EntityPropertyBridge.copyPresentFields()` reflects `role` and `verified` (only blocks id/username/password)
- **Why hidden:** HTML profile form looks safe; bridge named generically for forward compatibility
- **PoC:** `{"role":"ADMIN","verified":true}` as member user
- **Fix:** explicit allowlist DTO for patchable fields

## ASTRO-08 — Second-order SQLi in report criteria (Level 8)

- **Type:** Second-order / stored SQLi in export
- **Endpoint:** `POST /admin/export`
- **Source:** username prefix sanitized for HTML display path but reused for SQL clause
- **Sink chain:** `ReportCriteriaAssembler` → `UserDirectoryQueryComposer.composeUsernamePrefixClause()` → `LegacyExportRepository`
- **Why hidden:** `SecureQueryBuilder` decoy looks responsible; real flaw in reporting package
- **PoC prefix:** `' OR '1'='1` (admin export)
- **Fix:** parameterized native query

## ASTRO-09 — Delegation lifecycle cache not revoked (Level 9)

- **Type:** Authorization logic / stale session grant
- **Endpoint:** grant/revoke delegation then `/groups/{slug}/moderate`
- **Source:** revocation event
- **Sink chain:** `DelegationLifecycleCoordinator.onDelegationRevoked()` no-op + `PermissionContext` session cache
- **Why hidden:** grant path uses coordinator; revoke comment suggests async refresh; DB revocation looks correct
- **PoC:** delegate to guest, revoke, guest still moderates same session
- **Fix:** invalidate `PermissionContext` on revoke

## ASTRO-10 — Federation token audience confusion (Level 10)

- **Type:** Authentication/token confusion
- **Endpoint:** any request with `?shareToken=...`
- **Source:** paper share token
- **Sink chain:** `ShareTokenFilter` → `FederationTokenVerifier.verifyInviteToken()` accepts `astroscope-paper:` as collaboration audience
- **Why hidden:** verifier class name sounds strict; bug is wrong audience mapping for paper tokens
- **PoC:** create paper share, visit `/feed?shareToken=<paperToken>` unauthenticated as paper owner (admin)
- **Fix:** enforce audience-specific validation paths
