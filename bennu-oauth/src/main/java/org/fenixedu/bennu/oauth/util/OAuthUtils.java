/**
 * Copyright © 2015 Instituto Superior Técnico
 *
 * This file is part of Bennu OAuth.
 *
 * Bennu OAuth is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bennu OAuth is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Bennu OAuth.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.oauth.util;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.fenixedu.bennu.oauth.OAuthProperties;
import org.fenixedu.bennu.oauth.domain.ApplicationUserSession;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class OAuthUtils {

    public final static String ACCESS_TOKEN = "access_token";
    public final static String STANDARD_ACCESS_TOKEN = "token";
    public final static String REFRESH_TOKEN = "refresh_token";
    public final static String EXPIRES_IN = "expires_in";
    public final static String TOKEN_TYPE = "token_type";
    public final static String TOKEN_TYPE_HEADER_ACCESS_TOKEN = "Bearer";
    public static final String USER_DIALOG = "userdialog";
    public static final String STANDARD_USER_DIALOG = "authorize";
    public static final String USER_CONFIRMATION = "userconfirmation";
    private static Cipher cipher;
    private static IvParameterSpec ivspec;

    static {
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // nosemgrep
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }; // nosemgrep
            ivspec = new IvParameterSpec(iv); // nosemgrep
        } catch (Exception e) {
            throw new RuntimeException("Not possible to get instance of AES/CBC/PKCS5Padding");
        }
    }

    public static class Code {
        private String random;
        private String userId;

        public Code(String random, String userId) {
            this.random = random;
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }
    }

    private static String generateRandom() {
        return Hashing.sha512().hashString(UUID.randomUUID().toString(), StandardCharsets.UTF_8).toString();
    }

    public static String generateCodeKey() {
        KeyGenerator generator;
        try {
            generator = KeyGenerator.getInstance("AES");
        } catch (Exception exception) {
            throw new RuntimeException("Error generating code key");
        }
        generator.init(128);
        SecretKey key = generator.generateKey();
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    private static SecretKey getKeyFromString(String keyString) {
        byte[] keyBytes = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(keyBytes, "AES");
    }

    private static String encrypt(String value, String keyString) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        SecretKey key = getKeyFromString(keyString);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivspec);
        byte[] valueCiphered = cipher.doFinal(value.getBytes());
        return Base64.getEncoder().encodeToString(valueCiphered);
    }

    private static String decrypt(String encryptedValue, String keyString)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, InvalidAlgorithmParameterException {
        SecretKey key = getKeyFromString(keyString);
        cipher.init(Cipher.DECRYPT_MODE, key, ivspec);
        byte[] value = cipher.doFinal(Base64.getDecoder().decode(encryptedValue));
        return new String(value);
    }

    public static String generateCode(String userId, String key) {
        Code codeObject = new Code(generateRandom(), userId);
        Gson gson = new Gson();
        String json = gson.toJson(codeObject);
        String code = null;
        try {
            code = Base64.getUrlEncoder().withoutPadding().encodeToString(encrypt(json, key).getBytes());
        } catch (Exception err) {
            throw new RuntimeException("Error generating code");
        }
        return code;
    }

    public static String getUserIdFromCode(String codeCiphered, String key) {
        Gson gson = new Gson();
        String code = null;

        try {
            String json = new String(Base64.getUrlDecoder().decode(codeCiphered));
            code = decrypt(json, key);
        } catch (Exception err) {
            throw new RuntimeException("Error extracting user id");
        }
        Code codeObject = gson.fromJson(code, Code.class);
        return codeObject.getUserId();
    }

    public static String generateToken(DomainObject domainObject) {
        return generateToken(domainObject.getExternalId(), generateRandom());
    }

    public static String generateToken(String id, String random) {
        return Base64.getEncoder().encodeToString(Joiner.on(":").join(id, random).getBytes(StandardCharsets.UTF_8))
                .replace("=", "").replace("+", "-").replace("/", "-");
    }

    public static JsonObject getJsonTokens(ApplicationUserSession appUserSession) {
        return getJsonTokens(appUserSession.getAccessToken(), appUserSession.getRefreshToken());
    }

    public static JsonObject getJsonTokens(String accessToken) {
        return getJsonTokens(accessToken, null, null, null);
    }

    public static JsonObject getJsonTokens(String accessToken, String refreshToken) {
        return getJsonTokens(accessToken, refreshToken, OAuthProperties.getConfiguration().getAccessTokenExpirationSeconds(),
                TOKEN_TYPE_HEADER_ACCESS_TOKEN);
    }

    public static JsonObject getJsonTokens(String accessToken, String refreshToken, Integer accessTokenExpirationSeconds,
            String tokenType) {
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty(ACCESS_TOKEN, accessToken);
        if (!Strings.isNullOrEmpty(refreshToken)) {
            jsonResponse.addProperty(REFRESH_TOKEN, refreshToken);
        }
        if (accessTokenExpirationSeconds != null) {
            jsonResponse.addProperty(EXPIRES_IN, accessTokenExpirationSeconds);
        }
        if (!Strings.isNullOrEmpty(tokenType)) {
            jsonResponse.addProperty(TOKEN_TYPE, tokenType);
        }
        return jsonResponse;
    }

    public static final <T extends DomainObject> Optional<T> getDomainObject(final String externalId) {
        return getDomainObject(externalId, null);
    }

    public static final <T extends DomainObject> Optional<T> getDomainObject(final String externalId, final Class<T> clazz) {
        try {
            T domainObject = FenixFramework.getDomainObject(externalId);
            if (!FenixFramework.isDomainObjectValid(domainObject)
                    || (clazz != null && !clazz.isAssignableFrom(domainObject.getClass()))) {
                return Optional.empty();
            }
            return Optional.of(domainObject);
        } catch (Exception nfe) {
            return Optional.empty();
        }
    }
}