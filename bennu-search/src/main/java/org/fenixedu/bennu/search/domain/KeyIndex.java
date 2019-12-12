package org.fenixedu.bennu.search.domain;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KeyIndex extends KeyIndex_Base {

    private Map<String, KeyIndex> keyIndexMap = new ConcurrentHashMap<>();

    Map<String, KeyIndex> getKeyIndexMap() {
        if (keyIndexMap == null) {
            synchronized (DomainIndexSystem.getInstance()) {
                if (keyIndexMap == null) {
                    keyIndexMap = new ConcurrentHashMap<>();
                }
            }
        }
        return keyIndexMap;
    }

    private static final int KEY_LENGTH = 2;
    private static final int HASH_PARTS = 40 / KEY_LENGTH;

    static String[] hash(final String key) {
        final String hash = DigestUtils.shaHex(key);
        final String[] result = new String[HASH_PARTS];
        for (int i = 0; i < result.length; i++) {
            final int offset = i * KEY_LENGTH;
            result[i] = hash.substring(offset, offset + KEY_LENGTH);
        }
        return result;
    }

    public KeyIndex(final KeyIndex keyIndex, final String key) {
        super();
        if (keyIndex == null) {
            setIndexSystem(DomainIndexSystem.getInstance());
        } else {
            setParentIndex(keyIndex);
        }
        setKeyPart(key);
    }
    
}
