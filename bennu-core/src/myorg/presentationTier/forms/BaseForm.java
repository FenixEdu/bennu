package myorg.presentationTier.forms;

import org.apache.struts.action.ActionForm;

public class BaseForm extends ActionForm {

    private static final long serialVersionUID = 6674126534733668126L;

    protected String method;

    public String getMethod() {
	return method;
    }

    public void setMethod(String method) {
	this.method = method;
    }

}
