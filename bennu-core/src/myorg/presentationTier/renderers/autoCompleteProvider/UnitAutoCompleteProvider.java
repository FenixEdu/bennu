package myorg.presentationTier.renderers.autoCompleteProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import module.organization.domain.Party;
import module.organization.domain.Unit;
import myorg.domain.MyOrg;
import pt.utl.ist.fenix.tools.util.StringNormalizer;

public class UnitAutoCompleteProvider implements AutoCompleteProvider {

    public Collection getSearchResults(Map<String, String> argsMap, String value, int maxCount) {
	final List<Unit> units = new ArrayList<Unit>();

	final String trimmedValue = value.trim();
	final String[] input = trimmedValue.split(" ");
	StringNormalizer.normalize(input);

	for (final Party party : MyOrg.getInstance().getPartiesSet()) {
	    if (party.isUnit()) {
		final Unit unit = (Unit) party;
		final String unitName = StringNormalizer.normalize(unit.getPartyName().getContent());
		if (hasMatch(input, unitName)) {
		    units.add(unit);
		} else {
		    final String unitAcronym = StringNormalizer.normalize(unit.getAcronym());
		    if (hasMatch(input, unitAcronym)) {
			units.add(unit);
		    }
		}
	    }
	}

	Collections.sort(units, Unit.COMPARATOR_BY_PRESENTATION_NAME);

	return units;
    }

    private boolean hasMatch(final String[] input, final String unitNameParts) {
	for (final String namePart : input) {
	    if (unitNameParts.indexOf(namePart) == -1) {
		return false;
	    }
	}
	return true;
    }

}
