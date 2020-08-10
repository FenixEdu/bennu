package org.fenixedu.bennu.saml.client;

import org.pac4j.saml.client.SAML2Client;

import java.io.File;
import java.io.IOException;

public class ManualMetadataGenerator {
    public static void main(String[] args) throws IOException {
        // Make sure you have the configurations.properties files with the correct variables
        SAML2Client client = SAMLClientSDK.getClient();
        client.init();
        String destinationFilePath =
                SAMLClientConfiguration.getConfiguration().serviceProviderMetadataGenerationDestinationPath();
        File file = new File(destinationFilePath);
        if (file.exists()) {
            System.out.println("SAML metadata written to: " + destinationFilePath
                    + " If you need more than one saml callback location you need add mode AssertionConsumerService");
        } else {
            System.out.println("Failed to write to SAML metadata file: " + destinationFilePath);
        }
    }
}
