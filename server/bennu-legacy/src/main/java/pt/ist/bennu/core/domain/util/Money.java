/* 
* @(#)Money.java 
* 
* Copyright 2009 Instituto Superior Tecnico 
* Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes 
*  
*      https://fenix-ashes.ist.utl.pt/ 
*  
*   This file is part of the Bennu Web Application Infrastructure. 
* 
*   The Bennu Web Application Infrastructure is free software: you can 
*   redistribute it and/or modify it under the terms of the GNU Lesser General 
*   Public License as published by the Free Software Foundation, either version  
*   3 of the License, or (at your option) any later version. 
* 
*   Bennu is distributed in the hope that it will be useful, 
*   but WITHOUT ANY WARRANTY; without even the implied warranty of 
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
*   GNU Lesser General Public License for more details. 
* 
*   You should have received a copy of the GNU Lesser General Public License 
*   along with Bennu. If not, see <http://www.gnu.org/licenses/>. 
*  
*/
package pt.ist.bennu.core.domain.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;

import pt.utl.ist.fenix.tools.util.i18n.Language;

/**
 * 
 * @author João Neves
 * @author Luis Cruz
 * 
 */
public class Money implements Serializable, Comparable<Money> {

    private static final String SEPERATOR = ":";
    private static final Currency defaultCurrency = Language.getDefaultLocale() != null ? Currency.getInstance(Language
            .getDefaultLocale()) : Currency.getInstance("EUR");
    public static final Money ZERO = new Money("0");

    private final Currency currency;
    private final BigDecimal value;

    protected Money(final Currency currency, final BigDecimal value) {
        if (currency == null || value == null) {
            throw new RuntimeException("error.wrong.init.money.args");
        }
        this.currency = currency;
        this.value = value;
    }

    public Money(final BigDecimal value) {
        this(defaultCurrency, value);
    }

    public Money(final String value) {
        this(defaultCurrency, new BigDecimal(value));
    }

    private Money newMoney(BigDecimal value) {
        return new Money(getCurrency(), value);
    }

    public String serialize() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getCurrency().getCurrencyCode());
        stringBuilder.append(SEPERATOR);
        stringBuilder.append(getValue().toString());
        return stringBuilder.toString();
    }

    public Money percentage(final BigDecimal percentage) {
        return newMoney(valuePercentage(percentage));
    }

    private BigDecimal valuePercentage(final BigDecimal percentage) {
        return getValue().multiply(percentage.divide(new BigDecimal(100)));
    }

    public Money addPercentage(final BigDecimal percentage) {
        return newMoney(getValue().add(valuePercentage(percentage)));
    }

    public Money subtractPercentage(final BigDecimal percentage) {
        return newMoney(getValue().subtract(valuePercentage(percentage)));
    }

    public Money addAndRound(final Money money) {
        checkCurreny(money);
        return newMoney(getValue().add(money.getRoundedValue()));
    }

    public Money add(final Money money) {
        checkCurreny(money);
        return newMoney(getValue().add(money.getValue()));
    }

    public Money subtract(final Money money) {
        checkCurreny(money);
        return newMoney(getValue().subtract(money.getValue()));
    }

    public Money multiply(final BigDecimal mult) {
        return newMoney(getValue().multiply(mult));
    }

    public Money multiplyAndRound(final BigDecimal mult) {
        return newMoney(getValue().multiply(mult).setScale(getScale(), RoundingMode.HALF_EVEN));
    }

    public Money multiply(final long mult) {
        return multiply(BigDecimal.valueOf(mult));
    }

    public Money divideAndRound(final BigDecimal divisor) {
        return newMoney(getValue().divide(divisor, getScale(), RoundingMode.HALF_EVEN));
    }

    public boolean isPositive() {
        return getValue().signum() == 1;
    }

    public boolean isZero() {
        return getValue().signum() == 0;
    }

    public boolean isNegative() {
        return getValue().signum() == -1;
    }

    public boolean isLessThan(final Money money) {
        return this.compareTo(money) < 0;
    }

    public boolean isGreaterThan(final Money money) {
        return this.compareTo(money) > 0;
    }

    public boolean isLessThanOrEqual(final Money money) {
        return this.compareTo(money) <= 0;
    }

    public boolean isGreaterThanOrEqual(final Money money) {
        return this.compareTo(money) >= 0;
    }

    protected void checkCurreny(Money money) {
        if (!this.getCurrency().equals(money.getCurrency())) {
            throw new RuntimeException("error.diferent.currencies");
        }
    }

    private int getScale() {
        return getCurrency().getDefaultFractionDigits();
    }

    public Money[] allocate(int n) {
        Money lowResult = newMoney(getValue().divide(BigDecimal.valueOf(n), getScale(), RoundingMode.FLOOR));
        BigDecimal remainder = getValue().subtract(lowResult.getValue().multiply(BigDecimal.valueOf(n)));

        BigDecimal addingUnit = BigDecimal.valueOf(1).movePointLeft(getScale());
        Money[] results = new Money[n];
        for (int i = 0; i < n; i++) {
            if (remainder.compareTo(BigDecimal.ZERO) > 0) {
                results[i] = lowResult.add(newMoney(addingUnit));
                remainder = remainder.subtract(addingUnit);
            } else {
                results[i] = lowResult;
            }
        }
        return results;
    }

    public static Money deserialize(final String serializedMoney) {
        final int seperatorIndex = serializedMoney.indexOf(SEPERATOR);
        final String currencyCode = serializedMoney.substring(0, seperatorIndex);
        final String valueString = serializedMoney.substring(seperatorIndex + 1);
        final BigDecimal value = new BigDecimal(valueString);
        return new Money(Currency.getInstance(currencyCode), value);
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public int compareTo(Money money) {
        checkCurreny(money);
        return getValue().compareTo(money.getValue());
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Money) && equals((Money) obj);
    }

    public boolean equals(Money money) {
        return getRoundedValue().compareTo(money.getRoundedValue()) == 0 && getCurrency().equals(money.getCurrency());
    }

    private NumberFormat getCurrencyFormat() {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Language.getLocale());
        numberFormat.setCurrency(getCurrency());
        return numberFormat;
    }

    public BigDecimal getRoundedValue() {
        return getValue().setScale(getCurrencyFormat().getMaximumFractionDigits(), RoundingMode.HALF_EVEN);
    }

    public String toFormatString() {
        return getCurrencyFormat().format(getRoundedValue().doubleValue());
    }

    public String toFormatStringWithoutCurrency() {
        NumberFormat numberInstance = NumberFormat.getNumberInstance(Language.getLocale());
        numberInstance.setMinimumFractionDigits(getCurrencyFormat().getMaximumFractionDigits());

        return numberInstance.format(getRoundedValue().doubleValue());
    }

    public Money round() {
        return new Money(getRoundedValue());
    }

    public String exportAsString() {
        return serialize();
    }

    public static Money importFromString(String string) {
        return deserialize(string);
    }

}
