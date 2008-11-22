package myorg.domain.organization;

import java.io.Serializable;

import pt.ist.fenixWebFramework.util.DomainReference;
import pt.ist.fenixframework.pstm.Transaction;
import myorg.domain.MyOrg;
import myorg.domain.exceptions.DomainException;

public class ConnectionRule extends ConnectionRule_Base {

    static public class ConnectionRuleBean implements Serializable {

	private static final long serialVersionUID = -5549142750086201478L;

	private DomainReference<ConnectionRuleAccountabilityType> accountabilityType;
	private DomainReference<PartyType> parent;
	private DomainReference<PartyType> child;
	private DomainReference<ConnectionRule> connectionRule;

	public ConnectionRuleBean(final ConnectionRuleAccountabilityType accountabilityType) {
	    setAccountabilityType(accountabilityType);
	}

	public ConnectionRuleBean(final ConnectionRule connectionRule) {
	    setAccountabilityType(connectionRule.getAccountabilityType());
	    setParent(connectionRule.getAllowedParent());
	    setChild(connectionRule.getAllowedChild());
	    setConnectionRule(connectionRule);
	}

	public ConnectionRuleAccountabilityType getAccountabilityType() {
	    return accountabilityType != null ? accountabilityType.getObject() : null;
	}

	public void setAccountabilityType(final ConnectionRuleAccountabilityType accountabilityType) {
	    this.accountabilityType = (accountabilityType != null ? new DomainReference<ConnectionRuleAccountabilityType>(
		    accountabilityType) : null);
	}

	public PartyType getParent() {
	    return parent != null ? parent.getObject() : null;
	}

	public void setParent(final PartyType parent) {
	    this.parent = (parent != null ? new DomainReference<PartyType>(parent) : null);
	}

	public PartyType getChild() {
	    return child != null ? child.getObject() : null;
	}

	public void setChild(final PartyType child) {
	    this.child = (child != null ? new DomainReference<PartyType>(child) : null);
	}

	public ConnectionRule getConnectionRule() {
	    return connectionRule != null ? connectionRule.getObject() : null;
	}

	public void setConnectionRule(final ConnectionRule connectionRule) {
	    this.connectionRule = (connectionRule != null ? new DomainReference<ConnectionRule>(connectionRule) : null);
	}

	public void create() {
	    getAccountabilityType().addConnectionRule(getParent(), getChild());
	}
    }

    private ConnectionRule() {
	super();
	setMyOrg(MyOrg.getInstance());
    }

    ConnectionRule(final PartyType allowedParent, final PartyType allowedChild) {
	this();
	check(allowedParent, "error.ConnectionRule.invalid.parent.type");
	check(allowedChild, "error.ConnectionRule.invalid.child.type");
	setAllowedParent(allowedParent);
	setAllowedChild(allowedChild);
    }

    private void check(final Object obj, final String message) {
	if (obj == null) {
	    throw new DomainException(message);
	}
    }

    void delete() {
	removeAllowedParent();
	removeAllowedChild();
	removeAccountabilityType();
	removeMyOrg();
	Transaction.deleteObject(this);
    }

    boolean has(final PartyType parent, final PartyType child) {
	return hasAllowedParent(parent) && hasAllowedChild(child);
    }

    boolean hasAllowedParent(final PartyType parent) {
	return getAllowedParent().equals(parent);
    }

    boolean hasAllowedChild(final PartyType child) {
	return getAllowedChild().equals(child);
    }

    public boolean isValid(final Party parent, final Party child) {
	return has(parent.getPartyType(), child.getPartyType());
    }

}
