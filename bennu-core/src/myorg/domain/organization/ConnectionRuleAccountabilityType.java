package myorg.domain.organization;

import myorg.domain.exceptions.DomainException;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.pstm.Transaction;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class ConnectionRuleAccountabilityType extends ConnectionRuleAccountabilityType_Base {

    static public class ConnectionRuleAccountabilityTypeBean extends AccountabilityTypeBean {

	private static final long serialVersionUID = -704400799482432047L;

	public ConnectionRuleAccountabilityTypeBean() {
	    super();
	}

	public ConnectionRuleAccountabilityTypeBean(final ConnectionRuleAccountabilityType accountabilityType) {
	    setType(accountabilityType.getType());
	    setName(accountabilityType.getName());
	    setAccountabilityType(accountabilityType);
	}

	@Override
	public ConnectionRuleAccountabilityType getAccountabilityType() {
	    return (ConnectionRuleAccountabilityType) super.getAccountabilityType();
	}

	@Override
	public AccountabilityType create() {
	    return ConnectionRuleAccountabilityType.create(this);
	}
    }

    private ConnectionRuleAccountabilityType() {
	super();
    }

    protected ConnectionRuleAccountabilityType(final String type) {
	this(type, null);
    }

    protected ConnectionRuleAccountabilityType(final String type, final MultiLanguageString name) {
	this();
	check(type);
	setType(type);
	setName(name);
    }

    @Service
    @Override
    public void delete() {
	canDelete();
	removeConnectionRules();
	removeMyOrg();
	Transaction.deleteObject(this);
    }

    private void removeConnectionRules() {
	while (hasAnyConnectionRules()) {
	    getConnectionRules().get(0).delete();
	}
    }

    @Service
    public void addConnectionRule(final PartyType parent, final PartyType child) {
	checkCreateConnectionRule(parent, child);
	addConnectionRules(new ConnectionRule(parent, child));
    }

    private void checkCreateConnectionRule(final PartyType parent, final PartyType child) {
	for (final ConnectionRule rule : getConnectionRulesSet()) {
	    if (rule.has(parent, child)) {
		throw new DomainException("error.AccountabilityType.already.has.connection.rule", parent.getType(), child
			.getType());
	    }
	}
    }

    @Override
    protected boolean areValidPartyTypes(final Party parent, final Party child) {
	for (final ConnectionRule rule : getConnectionRulesSet()) {
	    if (rule.isValid(parent, child)) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public ConnectionRuleAccountabilityTypeBean buildBean() {
	return new ConnectionRuleAccountabilityTypeBean(this);
    }

    @Service
    public void deleteConnectionRule(final ConnectionRule connectionRule) {
	connectionRule.delete();
    }

    @Override
    public boolean isWithRules() {
	return true;
    }

    @Service
    static public ConnectionRuleAccountabilityType create(final ConnectionRuleAccountabilityTypeBean bean) {
	return new ConnectionRuleAccountabilityType(bean.getType(), bean.getName());
    }
}
