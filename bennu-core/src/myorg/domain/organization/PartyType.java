package myorg.domain.organization;

import java.io.Serializable;

import myorg.domain.MyOrg;
import myorg.domain.exceptions.DomainException;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixWebFramework.util.DomainReference;
import pt.ist.fenixframework.pstm.Transaction;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class PartyType extends PartyType_Base implements Comparable<PartyType> {

    static public class PartyTypeBean implements Serializable {

	private static final long serialVersionUID = -3867902288197067597L;
	private String type;
	private MultiLanguageString name;
	private DomainReference<PartyType> partyType;

	public PartyTypeBean() {
	}

	public PartyTypeBean(final PartyType partyType) {
	    setType(partyType.getType());
	    setName(partyType.getName());
	    setPartyType(partyType);
	}

	public String getType() {
	    return type;
	}

	public void setType(String type) {
	    this.type = type;
	}

	public MultiLanguageString getName() {
	    return name;
	}

	public void setName(MultiLanguageString name) {
	    this.name = name;
	}

	public PartyType getPartyType() {
	    return partyType != null ? partyType.getObject() : null;
	}

	public void setPartyType(final PartyType partyType) {
	    this.partyType = (partyType != null ? new DomainReference<PartyType>(partyType) : null);
	}

	public void edit() {
	    getPartyType().edit(this);
	}
    }

    private PartyType() {
	super();
	setMyOrg(MyOrg.getInstance());
    }

    public PartyType(final String type) {
	this(type, null);
    }

    public PartyType(final String type, final MultiLanguageString name) {
	this();
	check(type);
	setType(type);
	setName(name);
    }

    private void check(final String type) {
	if (type == null || type.isEmpty()) {
	    throw new DomainException("error.PartyType.invalid.type");
	}
	final PartyType partyType = readBy(type);
	if (partyType != null && partyType != this) {
	    throw new DomainException("error.PartyType.duplicated.type", type);
	}
    }

    public boolean hasType(final String type) {
	return getType() != null && getType().equalsIgnoreCase(type);
    }

    @Service
    public void edit(final PartyTypeBean bean) {
	check(bean.getType());
	setType(bean.getType());
	setName(bean.getName());
    }

    @Service
    public void delete() {
	canDelete();
	removeMyOrg();
	Transaction.deleteObject(this);
    }

    private void canDelete() {
	if (hasAnyParties()) {
	    throw new DomainException("error.PartyType.has.parties.cannot.delete");
	}
	if (hasAnyParentConnectionRules()) {
	    throw new DomainException("error.PartyType.has.parent.connection.rules.cannot.delete");
	}
	if (hasAnyChildConnectionRules()) {
	    throw new DomainException("error.PartyType.has.child.connection.rules.cannot.delete");
	}
    }

    @Override
    public int compareTo(PartyType other) {
	return getType().compareTo(other.getType());
    }

    @Service
    static public PartyType create(final PartyTypeBean bean) {
	return new PartyType(bean.getType(), bean.getName());
    }

    static public PartyType readBy(final String type) {
	if (type == null || type.isEmpty()) {
	    return null;
	}
	for (final PartyType element : MyOrg.getInstance().getPartyTypesSet()) {
	    if (element.hasType(type)) {
		return element;
	    }
	}
	return null;
    }

}
