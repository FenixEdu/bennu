package org.fenixedu.bennu.core.groups;

public abstract class CustomGroup extends Group {
    private static final long serialVersionUID = -5827476354896297604L;

    @Override
    public String getExpression() {
        return CustomGroupRegistry.getExpression(this);
    }
}
