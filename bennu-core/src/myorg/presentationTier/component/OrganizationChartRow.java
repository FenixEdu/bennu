package myorg.presentationTier.component;

import java.util.ArrayList;

public class OrganizationChartRow<T> extends ArrayList<T> {

    public static final int PARTS = 2;

    private final int unitsPerPart;

    public OrganizationChartRow(final T t, final int unitsPerPart) {
	this.unitsPerPart = unitsPerPart;
	add(t);
    }

    private int capacity() {
	return PARTS * unitsPerPart;
    }

    public boolean isFull() {
	return size() == capacity();
    }

    @Override
    public boolean add(final T t) {
	if (isFull()) {
	    throw new Error("Row is full!");
	}
	return super.add(t);
    }

    public int getUnitsPerPart() {
        return unitsPerPart;
    }

}
