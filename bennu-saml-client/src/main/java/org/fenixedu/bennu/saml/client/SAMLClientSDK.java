package org.fenixedu.bennu.saml.client;


import com.onelogin.saml2.Auth;
import com.onelogin.saml2.exception.Error;
import com.onelogin.saml2.exception.SettingsException;
import com.onelogin.saml2.settings.Saml2Settings;
import com.onelogin.saml2.settings.SettingsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

public class SAMLClientSDK {

    private static Auth auth = null;

    private static KeyStore keyStore = null;
    private static Key serviceProviderPrivateKey = null;
    private static Certificate serviceProviderCert = null;
    private static Certificate identityProviderCert = null;

    private static Object lock = new Object();

    private static String SPKeyAlias = "fenix.tecnico.ulisboa.pt-auth"; // TODO ADD THIS TO CONFIG
    private static String idpCertAlias = "id.tecnico.ulisboa.pt"; // TODO ADD THIS TO CONFIG

    private static KeyStore getKeyStore() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        synchronized (lock) {
            if (keyStore == null) {
                String keyStoreFile = SAMLClientConfiguration.getConfiguration().keystorePath();
                String storePass = SAMLClientConfiguration.getConfiguration().keystorePassword();
                keyStore = KeyStore.getInstance("PKCS12");
                keyStore.load(new FileInputStream(keyStoreFile), storePass.toCharArray());
            }
        }
        return keyStore;
    }

    private static Key getServiceProviderPrivateKey() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {
        synchronized (lock) {
            if (serviceProviderPrivateKey == null) {
                String keyPassword = SAMLClientConfiguration.getConfiguration().privateKeyPassword();
                serviceProviderPrivateKey = getKeyStore().getKey(SPKeyAlias, keyPassword.toCharArray());
            }
        }
        return serviceProviderPrivateKey;
    }

    private static Certificate getServiceProviderCert() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        synchronized (lock) {
            if (serviceProviderCert == null) {
                serviceProviderCert = getKeyStore().getCertificate(SPKeyAlias);
            }
        }
        return serviceProviderCert;
    }

    private static Certificate getIdentityProviderCert() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        synchronized (lock) {
            if (identityProviderCert == null) {
                identityProviderCert = getKeyStore().getCertificate(idpCertAlias);
            }
        }
        return identityProviderCert;
    }

    public static Auth getAuth(HttpServletRequest request, HttpServletResponse response) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, SettingsException, Error, UnrecoverableKeyException {
        synchronized (lock) {
            if(auth == null) {
                Map<String, Object> samlData = new HashMap<>();
                samlData.put("onelogin.saml2.sp.x509cert", getServiceProviderCert());
                samlData.put("onelogin.saml2.sp.privatekey", getServiceProviderPrivateKey());
                samlData.put("onelogin.saml2.idp.x509cert", getIdentityProviderCert());
                SettingsBuilder builder = new SettingsBuilder();
                Saml2Settings settings = builder.fromFile("onelogin.saml.properties").fromValues(samlData).build();
                auth = new Auth(settings, request, response);
            }
        }
        return auth;
    }

    /*
    <samlp:Extensions>
    <RequestedAttributes xmlns="http://autenticacao.cartaodecidadao.pt/atributos">
      <RequestedAttribute Name="http://interop.gov.pt/MDC/Cidadao/NIC" NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:uri" isRequired="true"/>
      <RequestedAttribute Name="http://interop.gov.pt/MDC/Cidadao/NIF" NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:uri" isRequired="false"/>
      <RequestedAttribute Name="http://interop.gov.pt/MDC/Cidadao/NomeCompleto" NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:uri" isRequired="true"/>
      <RequestedAttribute Name="http://interop.gov.pt/MDC/Cidadao/NomeProprio" NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:uri" isRequired="true"/>
    </RequestedAttributes>
    <FAAALevel xmlns="http://autenticacao.cartaodecidadao.pt/atributos">3</FAAALevel>
    </samlp:Extensions>
    */

}
