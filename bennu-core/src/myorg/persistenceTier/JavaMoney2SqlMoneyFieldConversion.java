package myorg.persistenceTier;

import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

import myorg.domain.util.Money;

public class JavaMoney2SqlMoneyFieldConversion implements FieldConversion {

    public Object javaToSql(Object source) {
	if (source instanceof Money) {
	    final Money money = (Money) source;
	    return money.serialize();
	}
	return source;
    }

    public Object sqlToJava(Object source) {
	if (source instanceof String) {
	    final String string = (String) source;
	    return Money.deserialize(string);
	}
	return source;
    }

}
