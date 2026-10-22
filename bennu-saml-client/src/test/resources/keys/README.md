Throwaway key material used only by the bennu-saml-client test suite. The files intentionally
have no extension: GitLab push rules reject blobs matching the .key/.pem glob patterns.

- sp-key-pkcs8 / sp-cert: the test service provider credentials (PKCS#8 private key and X.509
  certificate in PEM encoding), inlined in ../configuration.properties and absent from
  ../configuration-variant.properties.
- idp-key-pkcs8 / idp-cert: the test identity provider credentials; the certificate is embedded
  in ../idp-metadata.xml.

These keys are NOT secrets and MUST NOT be used in any real deployment.
