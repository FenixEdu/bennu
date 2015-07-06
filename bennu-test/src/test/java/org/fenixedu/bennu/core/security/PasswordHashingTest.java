package org.fenixedu.bennu.core.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.UserProfile;
import org.fenixedu.bennu.core.domain.User_Base;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import pt.ist.fenixframework.test.core.FenixFrameworkRunner;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;

@RunWith(FenixFrameworkRunner.class)
public class PasswordHashingTest {
    private static final String TEST_PASSWORD = "mypasswordisstrong";

    @Test
    public void testWrongPassword() {
        User user1 = createTestUser();
        user1.generatePassword();
        assertFalse(user1.matchesPassword(TEST_PASSWORD));
    }

    @Test
    public void testPasswordHashAndValidation() {
        User user1 = createTestUser();
        user1.changePassword(TEST_PASSWORD);
        assertTrue(user1.matchesPassword(TEST_PASSWORD));
    }

    @Test
    public void testGeneratedPasswordHashAndValidation() {
        User user1 = createTestUser();
        assertTrue(user1.matchesPassword(user1.generatePassword()));
    }

    @Test
    public void testOldPasswordMechanism() {
        try {
            User user1 = createTestUser();
            SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
            byte salt[] = new byte[64];
            prng.nextBytes(salt);
            String encodedSalt = BaseEncoding.base64().encode(salt);

            Method saltSetter = User_Base.class.getDeclaredMethod("setSalt", String.class);
            saltSetter.setAccessible(true);
            saltSetter.invoke(user1, encodedSalt);

            String hash = Hashing.sha512().hashString(encodedSalt + TEST_PASSWORD, Charsets.UTF_8).toString();

            Method passwordSetter = User_Base.class.getDeclaredMethod("setPassword", String.class);
            passwordSetter.setAccessible(true);
            passwordSetter.invoke(user1, hash);

            assertTrue(user1.matchesPassword(TEST_PASSWORD));
        } catch (NoSuchAlgorithmException | NoSuchMethodException | SecurityException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            Assert.fail(e.getMessage());
        }
    }

    private User createTestUser() {
        return new User(new UserProfile("John", "Doe", null, "johndoe@gmail.com", null));
    }
}
