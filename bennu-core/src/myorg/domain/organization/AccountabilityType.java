package myorg.domain.organization;

import java.io.Serializable;

import myorg.domain.MyOrg;
import myorg.domain.exceptions.DomainException;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixWebFramework.util.DomainReference;
import pt.ist.fenixframework.pstm.Transaction;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class AccountabilityType extends AccountabilityType_Base implements Comparable<AccountabilityType> {

    static public class AccountabilityTypeBean implements Serializable {

	private static final long serialVersionUID = -1189746935274309327L;
	private String type;
	private MultiLanguageString name;
	private DomainReference<AccountabilityType> accountabilityType;

	public AccountabilityTypeBean() {
	}

	public AccountabilityTypeBean(final AccountabilityType accountabilityType) {
	    setType(accountabilityType.getType());
	    setName(accountabilityType.getName());
	    setAccountabilityType(accountabilityType);
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

	public AccountabilityType getAccountabilityType() {
	    return accountabilityType != null ? accountabilityType.getObject() : null;
	}

	public void setAccountabilityType(final AccountabilityType accountabilityType) {
	    this.accountabilityType = (accountabilityType != null ? new DomainReference<AccountabilityType>(accountabilityType)
		    : null);
	}

	public AccountabilityType create() {
	    return AccountabilityType.create(this);
	}

	public void edit() {
	    getAccountabilityType().edit(getType(), getName());
	}
    }

    protected AccountabilityType() {
	super();
	setMyOrg(MyOrg.getInstance());
    }

    protected AccountabilityType(final String type) {
	this(type, null);
    }

    protected AccountabilityType(final String type, final MultiLanguageString name) {
	this();
	check(type);
	setType(type);
	setName(name);
    }

    protected void check(final String type) {
	if (type == null || type.isEmpty()) {
	    throw new DomainException("error.AccountabilityType.invalid.type");
	}
	final AccountabilityType accountabilityType = readBy(type);
	if (accountabilityType != null && accountabilityType != this) {
	    throw new DomainException("error.AccountabilityType.duplicated.type", type);
	}
    }

    public boolean hasType(final String type) {
	return getType() != null && getType().equalsIgnoreCase(type);
    }

    @Service
    public void edit(final String type, final MultiLanguageString name) {
	check(type);
	setType(type);
	setName(name);
    }

    @Service
    public void delete() {
	canDelete();
	removeMyOrg();
	Transaction.deleteObject(this);
    }

    protected void canDelete() {
	if (hasAnyAccountabilities()) {
	    throw new DomainException("error.AccountabilityType.has.accountabilities.cannot.delete");
	}
    }

    @Override
    public int compareTo(AccountabilityType other) {
	return getType().compareTo(other.getType());
    }

    public boolean canCreateAccountability(final Party parent, final Party child) {
	return areValidPartyTypes(parent, child);
    }

    protected boolean areValidPartyTypes(final Party parent, final Party child) {
	return true;
    }

    public AccountabilityTypeBean buildBean() {
	return new AccountabilityTypeBean(this);
    }
    
    public boolean isWithRules() {
	return false;
    }
    
    @Service
    static public AccountabilityType create(final AccountabilityTypeBean bean) {
	return new AccountabilityType(bean.getType(), bean.getName());
    }

    static public AccountabilityType readBy(final String type) {
	if (type == null || type.isEmpty()) {
	    return null;
	}
	for (final AccountabilityType element : MyOrg.getInstance().getAccountabilityTypes()) {
	    if (element.hasType(type)) {
		return element;
	    }
	}
	return null;
    }

}
