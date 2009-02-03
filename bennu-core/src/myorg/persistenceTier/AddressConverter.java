package myorg.persistenceTier;

import org.apache.ojb.broker.accesslayer.conversions.ConversionException;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

import myorg.domain.util.Address;

public class AddressConverter implements FieldConversion {

    final String CHARACTER = ":";
    final String SEPARATOR = CHARACTER + CHARACTER;

    public Object javaToSql(Object source) throws ConversionException {
	if (source instanceof Address) {
	    final Address address = (Address) source;
	    final StringBuilder builder = new StringBuilder();
	    builder.append(escapeFieldSeparator(address.getLine1()));
	    builder.append(SEPARATOR);
	    builder.append(escapeFieldSeparator(address.getLine2()));
	    builder.append(SEPARATOR);
	    builder.append(escapeFieldSeparator(address.getPostalCode()));
	    builder.append(SEPARATOR);
	    builder.append(escapeFieldSeparator(address.getLocation()));
	    builder.append(SEPARATOR);
	    builder.append(escapeFieldSeparator(address.getCountry()));
	    return builder.toString();
	}

	return source;
    }

    private String escapeFieldSeparator(String field) {
	return field != null ? field.replaceAll(CHARACTER, "\\\\" + CHARACTER + "\\\\") : "";
    }

    public Object sqlToJava(Object source) throws ConversionException {
	if (source instanceof String) {
	    final String stringAddress = (String) source;
	    String[] split = stringAddress.split(SEPARATOR);
	    return new Address(removeEscapeFieldSeparator(split[0]), removeEscapeFieldSeparator(split[1]),
		    removeEscapeFieldSeparator(split[2]), removeEscapeFieldSeparator(split[3]),
		    removeEscapeFieldSeparator(split[4]));
	}
	return source;
    }

    private String removeEscapeFieldSeparator(String field) {
	return field != null ? field.replaceAll("\\\\" + CHARACTER + "\\\\", CHARACTER) : null;
    }
}
