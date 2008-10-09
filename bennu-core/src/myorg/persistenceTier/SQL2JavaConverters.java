package myorg.persistenceTier;

import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

public class SQL2JavaConverters {
    public static FieldConversion LocalDate2SqlFieldConverter = new LocalDate2SqlFieldConverter();
    public static FieldConversion MultiLanguageString2SqlMultiLanguageStringConversion = new MultiLanguageString2SqlMultiLanguageStringConversion();
    public static FieldConversion TimeStamp2DateTimeFieldConversion = new TimeStamp2DateTimeFieldConversion();
}
