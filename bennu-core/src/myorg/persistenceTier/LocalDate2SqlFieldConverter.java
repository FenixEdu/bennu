package myorg.persistenceTier;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.accesslayer.conversions.ConversionException;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;
import org.joda.time.LocalDate;

public class LocalDate2SqlFieldConverter implements FieldConversion {

    public Object javaToSql(Object arg0) throws ConversionException {
	LocalDate localDate = (LocalDate) arg0;
	if (localDate != null) {
	    final String dateString = String.format("%d-%02d-%02d", localDate.getYear(), localDate.getMonthOfYear(), localDate
		    .getDayOfMonth());
	    return dateString.length() != 10 ? null : dateString;
	}
	return null;
    }

    public Object sqlToJava(Object arg0) throws ConversionException {
	String value = (String) arg0;
	if (!StringUtils.isEmpty(value)) {
	    int year = Integer.parseInt(value.substring(0, 4));
	    int month = Integer.parseInt(value.substring(5, 7));
	    int day = Integer.parseInt(value.substring(8, 10));
	    return year == 0 || month == 0 || day == 0 ? null : new LocalDate(year, month, day);
	}
	return null;
    }

}
