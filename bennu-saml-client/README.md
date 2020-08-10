# SAML client

## Setup
1. Define properties: define the properties required on SAMLClientConfiguration in the `configuration.properties` file.
 Sample available at `bennu-saml-client/src/main/resources/configuration.properties.sample`.
 You need to define these properties in the webapp and on
  `bennu-saml-client/src/main/resources/configuration.properties`) if you want to generate the metadata
 automatically. 
 
2. Generate Metadata file: run the ManualMetadataGenerator

3. Add More callbacks: if you need the IDP to callback to more than one location after authentication you
need to change the file generated on the previous step. Search for `AssertionConsumerService` and add similar
entries but with the other urls.

4. Copy the metadata file with all the `AssertionConsumerService` to the location you specified in the variable
 `saml.serviceProviderFinalMetadataLocation`, this file will be made available at your webapp endpoint 
 `/api/saml-client/metadata`
 
5. Configure IDP to fetch metadata from the path `/api/saml-client/metadata`

IMPORTANT: at this moment filters on fenix are causing problems on the requests, and when they arrive at the saml client
they are broken. To solve this you should add the filter `FixRequestArgumentsForSAMLFilter` to the callback urls