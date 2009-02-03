package myorg.persistenceTier;

import java.util.ArrayList;
import java.util.List;

import org.apache.ojb.broker.accesslayer.conversions.ConversionException;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

import pt.utl.ist.fenix.tools.util.Strings;

public class StringsConverter implements FieldConversion {

    public Object javaToSql(Object arg0) throws ConversionException {
	if (arg0 == null) {
	    return null;
	}
	Strings strings = (Strings) arg0;
	StringBuilder buffer = new StringBuilder("");
	for (String string : strings) {
	    buffer.append(string.length());
	    buffer.append(":");
	    buffer.append(string);
	}
	return buffer.toString();
    }

    public Object sqlToJava(Object arg0) throws ConversionException {
	if (arg0 == null) {
	    return null;
	}
	List<String> strings = new ArrayList<String>();
	String sqlString = (String) arg0;

	int beginIndex = 0;
	int endIndex = find(beginIndex, ':', sqlString);

	while (beginIndex >= 0 && endIndex > beginIndex) {
	    int size = Integer.valueOf(sqlString.substring(beginIndex, endIndex++));
	    strings.add(sqlString.substring(endIndex, endIndex + size));
	    beginIndex = endIndex + size;
	    endIndex = beginIndex + find(beginIndex, ':', sqlString);
	}
	return new Strings(strings);
    }

    private int find(int index, char c, String string) {
	return string.substring(index).indexOf(c);
    }

}
