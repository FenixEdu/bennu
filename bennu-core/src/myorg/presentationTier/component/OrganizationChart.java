package myorg.presentationTier.component;

import java.util.ArrayList;
import java.util.Collection;

public class OrganizationChart<T> extends ArrayList<OrganizationChartRow<T>> {

    final int elementRowIndex;

    final int unitsPerPart;

    public OrganizationChart(final Collection<T> elements, final int unitsPerPart) {
	this.unitsPerPart = unitsPerPart;
	addAll(elements);
	elementRowIndex = -1;
    }

    public OrganizationChart(final T element, final Collection<T> parents, final Collection<T> children, final int unitsPerPart) {
	this.unitsPerPart = unitsPerPart;
	if (parents != null && !parents.isEmpty()) {
	    addAll(parents);
	}
	elementRowIndex = size();
	add(new OrganizationChartRow<T>(element, unitsPerPart));
	addAll(children);
    }

    public void addAll(final Collection<T> elements) {
	if (elements != null && !elements.isEmpty()) {
	    OrganizationChartRow<T> row = null;
	    for (final T t : elements) {
		if (row == null || row.isFull()) {
		    row = new OrganizationChartRow<T>(t, unitsPerPart);
		    add(row);
		} else {
		    row.add(t);
		}	    
	    }
	}
    }

    public int getElementRowIndex() {
        return elementRowIndex;
    }

    public T getElement() {
        return elementRowIndex == -1 ? null : get(elementRowIndex).iterator().next();
    }

    public int getUnitsPerPart() {
        return unitsPerPart;
    }

}
