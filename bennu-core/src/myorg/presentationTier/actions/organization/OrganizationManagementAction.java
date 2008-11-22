package myorg.presentationTier.actions.organization;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.domain.exceptions.DomainException;
import myorg.domain.organization.AccountabilityType;
import myorg.domain.organization.ConnectionRule;
import myorg.domain.organization.ConnectionRuleAccountabilityType;
import myorg.domain.organization.PartyType;
import myorg.domain.organization.AccountabilityType.AccountabilityTypeBean;
import myorg.domain.organization.ConnectionRule.ConnectionRuleBean;
import myorg.domain.organization.PartyType.PartyTypeBean;
import myorg.presentationTier.actions.ContextBaseAction;
import myorg.presentationTier.forms.BaseForm;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.struts.annotations.Forward;
import pt.ist.fenixWebFramework.struts.annotations.Forwards;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping(path = "/organization", formBeanClass = OrganizationManagementAction.OrganizationForm.class)
@Forwards( { @Forward(name = "show.options", path = "/organization/showOptions.jsp"),
	@Forward(name = "view.party.types", path = "/organization/viewPartyTypes.jsp"),
	@Forward(name = "create.party.type", path = "/organization/createPartyType.jsp"),
	@Forward(name = "edit.party.type", path = "/organization/editPartyType.jsp"),
	@Forward(name = "view.accountability.types", path = "/organization/viewAccountabilityTypes.jsp"),
	@Forward(name = "select.accountability.type", path = "/organization/selectAccountabilityType.jsp"),
	@Forward(name = "create.accountability.type", path = "/organization/createAccountabilityType.jsp"),
	@Forward(name = "edit.accountability.type", path = "/organization/editAccountabilityType.jsp"),
	@Forward(name = "view.connection.rules", path = "/organization/viewConnectionRules.jsp"),
	@Forward(name = "create.connection.rule", path = "/organization/createConnectionRule.jsp")

})
public class OrganizationManagementAction extends ContextBaseAction {

    static public class OrganizationForm extends BaseForm {
	private static final long serialVersionUID = 4469811183847905665L;

	private String accountabilityTypeClassName;

	public String getAccountabilityTypeClassName() {
	    return accountabilityTypeClassName;
	}

	public void setAccountabilityTypeClassName(String accountabilityTypeClassName) {
	    this.accountabilityTypeClassName = accountabilityTypeClassName;
	}
    }

    public final ActionForward showOptions(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	return mapping.findForward("show.options");
    }

    public final ActionForward viewPartyTypes(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	request.setAttribute("partyTypes", getMyOrg().getPartyTypes());
	return mapping.findForward("view.party.types");
    }

    public final ActionForward prepareCreatePartyType(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {

	request.setAttribute("partyTypeBean", new PartyTypeBean());
	return mapping.findForward("create.party.type");
    }

    public final ActionForward createPartyType(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final PartyTypeBean bean = getRenderedObject("partyTypeBean");
	try {
	    PartyType.create(bean);
	} catch (final DomainException e) {
	    addMessage(request, e.getMessage(), e.getArgs());
	    request.setAttribute("partyTypeBean", bean);
	    return mapping.findForward("create.party.type");
	}
	return viewPartyTypes(mapping, form, request, response);
    }

    public final ActionForward prepareEditPartyType(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	request.setAttribute("partyTypeBean", new PartyTypeBean((PartyType) getDomainObject(request, "partyTypeOid")));
	return mapping.findForward("edit.party.type");
    }

    public final ActionForward editPartyType(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final PartyTypeBean bean = getRenderedObject("partyTypeBean");
	try {
	    bean.edit();
	} catch (final DomainException e) {
	    addMessage(request, e.getMessage(), e.getArgs());
	    request.setAttribute("partyTypeBean", bean);
	    return mapping.findForward("edit.party.type");
	}

	return viewPartyTypes(mapping, form, request, response);
    }

    public final ActionForward deletePartyType(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	((PartyType) getDomainObject(request, "partyTypeOid")).delete();
	return viewPartyTypes(mapping, form, request, response);
    }

    public final ActionForward viewAccountabilityTypes(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	request.setAttribute("accountabilityTypes", getMyOrg().getAccountabilityTypes());
	return mapping.findForward("view.accountability.types");
    }

    public final ActionForward selectAccountabilityType(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	return mapping.findForward("select.accountability.type");
    }

    public final ActionForward prepareCreateAccountabilityType(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final OrganizationForm organizationForm = (OrganizationForm) form;
	final Class<?> clazz = Class.forName(organizationForm.getAccountabilityTypeClassName());
	request.setAttribute("accountabilityTypeBean", clazz.newInstance());
	return mapping.findForward("create.accountability.type");
    }

    public final ActionForward createAccountabilityType(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final AccountabilityTypeBean bean = getRenderedObject("accountabilityTypeBean");
	try {
	    bean.create();
	} catch (final DomainException e) {
	    addMessage(request, e.getMessage(), e.getArgs());
	    request.setAttribute("accountabilityTypeBean", bean);
	    return mapping.findForward("create.accountability.type");
	}
	return viewAccountabilityTypes(mapping, form, request, response);
    }

    public final ActionForward prepareEditAccountabilityType(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final AccountabilityType type = getDomainObject(request, "accountabilityTypeOid");
	request.setAttribute("accountabilityTypeBean", type.buildBean());
	return mapping.findForward("edit.accountability.type");
    }

    public final ActionForward editAccountabilityType(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final AccountabilityTypeBean bean = getRenderedObject("accountabilityTypeBean");
	try {
	    bean.edit();
	} catch (final DomainException e) {
	    addMessage(request, e.getMessage(), e.getArgs());
	    request.setAttribute("accountabilityTypeBean", bean);
	    return mapping.findForward("edit.accountability.type");
	}

	return viewAccountabilityTypes(mapping, form, request, response);
    }

    public final ActionForward deleteAccountabilityType(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	((AccountabilityType) getDomainObject(request, "accountabilityTypeOid")).delete();
	return viewAccountabilityTypes(mapping, form, request, response);
    }

    public final ActionForward viewConnectionRules(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final ConnectionRuleAccountabilityType type = getDomainObject(request, "accountabilityTypeOid");
	request.setAttribute("accountabilityType", type);
	request.setAttribute("connectionRules", type.getConnectionRules());
	return mapping.findForward("view.connection.rules");
    }

    public final ActionForward prepareCreateConnectionRule(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final ConnectionRuleAccountabilityType type = getDomainObject(request, "accountabilityTypeOid");
	request.setAttribute("connectionRuleBean", new ConnectionRuleBean(type));
	return mapping.findForward("create.connection.rule");
    }

    public final ActionForward createConnectionRule(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {

	final ConnectionRuleBean bean = getRenderedObject("connectionRuleBean");
	try {
	    bean.create();
	} catch (final DomainException e) {
	    addMessage(request, e.getMessage(), e.getArgs());
	    request.setAttribute("connectionRuleBean", bean);
	    return mapping.findForward("create.connection.rule");
	}

	request.setAttribute("accountabilityTypeOid", bean.getAccountabilityType().getOID());
	return viewConnectionRules(mapping, form, request, response);
    }

    public final ActionForward deleteConnectionRule(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {

	final ConnectionRule connectionRule = getDomainObject(request, "connectionRuleOid");
	final ConnectionRuleAccountabilityType accountabilityType = connectionRule.getAccountabilityType();
	try {
	    accountabilityType.deleteConnectionRule(connectionRule);
	} catch (final DomainException e) {
	    addMessage(request, e.getMessage(), e.getArgs());
	}

	request.setAttribute("accountabilityTypeOid", accountabilityType.getOID());
	return viewConnectionRules(mapping, form, request, response);
    }

}
