# SAML client

This module adds SAML 2.0 Service Provider support to Bennu applications, using Spring Security
(the 5.8.x line, the last release supporting the javax.servlet stack) purely as a SAML protocol
engine. Bennu remains the security authority: sessions, CSRF, user provisioning and login
management are still handled by Bennu.

## Endpoints

| Endpoint | Method | Purpose |
| --- | --- | --- |
| `/api/saml-client/returnFromSAML` | POST | Assertion Consumer Service (receives the IdP SAML response) |
| `/api/saml-client/metadata` | GET | Generated SP metadata (served to the IdP) |
| `/api/saml-client/logout` | POST | SP-initiated Single Logout (when `saml.slo.enabled = true`) |
| `/saml2/authenticate/bennu` | GET | Authentication request: redirects the user to the IdP |

## Setup

1. Configure the properties below in the application's `configuration.properties` file (a full
   annotated sample is available at `bennu-saml-client/src/main/resources/configuration.properties.sample`).

   Minimal required set:

   ```properties
   saml.enabled = true
   saml.username.attribute = eduPersonPrincipalName
   saml.sp.certificate = MIIC...
   saml.sp.private-key = MIIE...
   saml.idp.metadata = https://idp.example.edu/metadata
   ```

2. Generate an SP key pair if you don't have one:

   ```sh
   openssl req -x509 -newkey rsa:2048 -keyout sp-key.pem -out sp-cert.pem -days 3650 -nodes
   openssl pkcs8 -topk8 -nocrypt -in sp-key.pem -out sp-key.pkcs8.pem
   ```

   Both values are inlined in the configuration (`saml.sp.certificate` / `saml.sp.private-key`,
   base64 or PEM). **Restrict file permissions of any file containing `saml.sp.private-key`**, as it
   is a secret. Encrypted PKCS#8 keys are supported through `saml.sp.private-key.password`.

3. Point the IdP at the generated metadata: `https://<your-app>/api/saml-client/metadata`. The
   metadata is generated from the configuration; the optional `saml.organization.*`,
   `saml.contact.*` and `saml.service.name` keys enrich it with the corresponding SAML metadata
   elements.

4. If your application is not deployed at the root context, or your registered entity ID differs
   from the application URL, set `saml.sp.entityId` accordingly.

## Logout

- **`/logout`** (portal) is the universal logout entrypoint: it always terminates the local Bennu
  session. Registered login providers are given a best-effort chance to terminate their own
  sessions first — with SAML SLO enabled and a SAML session, `/logout` redirects the user to the
  IdP's Single Logout endpoint and lands on `saml.logoutSuccessUrl` when the IdP confirms.
- **`POST /api/saml-client/logout`** is the direct SAML SLO endpoint (requires
  `saml.slo.enabled = true`); it performs the same local logout plus the SLO round trip.

With SAML, plain `/logout` (or a CAS-style deployment without SLO) leaves the IdP session alive:
the next `/login` will silently SSO the user back in.

**Signed SLO messages.** Spring Security requires every SLO message to be signed (an XML
signature or a signed redirect query), and this module enforces that by default
(`saml.slo.requireSignedMessages = true`). SimpleSAMLphp does not sign logout messages by
default, so SimpleSAMLphp-based environments need one of:

- set `saml.slo.requireSignedMessages = false` — signature errors on SLO messages are ignored;
  every other validation (issuer, destination, `InResponseTo`, …) still applies. Recommended
  only for test environments.
- enable [`'redirect.sign' => true`](https://simplesamlphp.org/docs/stable/simplesamlphp-reference-idp-hosted)
  in your SimpleSAMLphp `saml20-idp-hosted.php` and keep the default — recommended in production.

## Migrating from a previous release (OneLogin java-saml based)

- The `saml.auth.properties` file is gone. All configuration now lives in the standard
  `configuration.properties` under the `saml.*` keys. Relevant mappings:

  | Old key (`saml.auth.properties`) | New key |
  | --- | --- |
  | `onelogin.saml2.sp.entityid` | `saml.sp.entityId` |
  | `onelogin.saml2.sp.assertion_consumer_service.url` | Fixed: `APP_URL + /api/saml-client/returnFromSAML` |
  | `onelogin.saml2.sp.x509cert` | `saml.sp.certificate` |
  | `onelogin.saml2.sp.privatekey` | `saml.sp.private-key` |
  | `onelogin.saml2.idp.entityid`, `.single_sign_on_service.url`, `.x509cert`, ... | `saml.idp.metadata` (all IdP data now comes from the IdP metadata) |
  | `onelogin.saml2.organization.*` | `saml.organization.*` |
  | `onelogin.saml2.contacts.*` | `saml.contact.*` |

- The `saml.serviceProviderFinalMetadataLocation` property is gone: metadata is always generated
  from the configuration. The generated metadata declares a **single** Assertion Consumer Service
  (`APP_URL + /api/saml-client/returnFromSAML`). Deployments that relied on hand-crafted metadata
  with multiple ACS URLs must re-register the single ACS at their IdP, or stay on the previous
  release.

- The callback URL is unchanged, so no IdP re-registration is needed as long as the entity ID is
  preserved (set `saml.sp.entityId` to the old `onelogin.saml2.sp.entityid` value).

- The username attribute is now configurable (`saml.username.attribute`), defaulting to
  `eduPersonPrincipalName` with domain stripping (previous behavior), controlled via
  `saml.username.stripDomain`.

- Silent user provisioning is now explicit: `saml.autoCreateUsers`.

- `SAMLClientSDK` has been removed, along with the OneLogin toolkit dependency.

## Implementation notes

- Spring Security is used only as the SAML protocol engine: the `springSecurityFilterChain` is
  mapped solely to the SAML endpoints, permits all requests, disables CSRF (Bennu's CSRF remains
  the authority) and never writes a Spring `SecurityContext` into the session.
- The engine lives in a module-owned Spring context (published under
  `org.fenixedu.bennu.saml.client.WEB_APPLICATION_CONTEXT_ATTRIBUTE`), created by
  `SAMLClientInitializer`; it does not interfere with any application-provided Spring context.
- When `saml.enabled = false`, the module registers only a pass-through filter and the SAML
  endpoints respond with 404, as in previous releases.
- Spring Security 5.8.x is the last release for the javax.servlet stack and is OSS-EOL; this is an
  accepted trade-off until Bennu migrates to jakarta, at which point this module should move to a
  current Spring Security release.
