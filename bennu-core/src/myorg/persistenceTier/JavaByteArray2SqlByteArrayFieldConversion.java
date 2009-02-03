package myorg.persistenceTier;

import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

import myorg.domain.util.ByteArray;

public class JavaByteArray2SqlByteArrayFieldConversion implements FieldConversion {

    public Object javaToSql(Object source) {
        if (source instanceof ByteArray) {
            final ByteArray byteArray = (ByteArray) source;
            return byteArray.getBytes();
        }
        return source;
    }

    public Object sqlToJava(Object source) {
        if (source instanceof byte[]) {            
            return new ByteArray((byte[]) source);
        }
        return source;
    }

}
