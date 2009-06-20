package myorg.presentationTier.component;

import java.util.ArrayList;

public class OrganizationChartRow<T> extends ArrayList<T> {

    public static final int UNITS_PER_PART = 3;

    public static final int PARTS = 2;

    private static final int CAPACITY = UNITS_PER_PART * PARTS;

    public OrganizationChartRow(final T t) {
	add(t);
    }

    public boolean isFull() {
	return size() == CAPACITY;
    }

    @Override
    public boolean add(final T t) {
	if (isFull()) {
	    throw new Error("Row is full!");
	}
	return super.add(t);
    }

}
