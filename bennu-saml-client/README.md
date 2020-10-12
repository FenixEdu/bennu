# SAML client

## Setup
1. Create public and private key

2. SAML Service provider(SP) metadata XML file

3. Define properties: define the properties required on SAMLClientConfiguration in the `configuration.properties` file of your
 webapp, sample available at `bennu-saml-client/src/main/resources/configuration.properties.sample`.
 You also need to add the file `saml.auth.properties` to you webapp, for that you can use the
  `bennu-saml-client/src/main/resources/saml.auth.properties.sample` as base, you need to change the callback
  address and add your service provider(SP) private key and certificate.

4. Make sure the callback address is one of the assertion consuming services urls in the SP metadata

5. Copy the metadata file to the location you specified in the variable
 `saml.serviceProviderFinalMetadataLocation`, this file will be made available at your webapp endpoint 
 `/api/saml-client/metadata`
 
6. Configure IDP to fetch metadata from the path `/api/saml-client/metadata`

IMPORTANT: at this moment filters on fenix are causing problems on the requests, and when they arrive at the saml client
they are broken. To solve this you should add the filter `FixRequestArgumentsForSAMLFilter` to the callback urls