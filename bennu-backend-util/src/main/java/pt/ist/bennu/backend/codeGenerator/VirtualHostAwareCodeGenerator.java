package pt.ist.bennu.backend.codeGenerator;

import java.io.PrintWriter;
import java.util.List;

import pt.ist.fenixframework.backend.jvstmojb.codeGenerator.FenixCodeGeneratorOneBoxPerObject;
import pt.ist.fenixframework.dml.CompilerArgs;
import pt.ist.fenixframework.dml.DomainClass;
import pt.ist.fenixframework.dml.DomainEntity;
import pt.ist.fenixframework.dml.DomainModel;
import pt.ist.fenixframework.dml.Role;

public class VirtualHostAwareCodeGenerator extends FenixCodeGeneratorOneBoxPerObject {

    public VirtualHostAwareCodeGenerator(final CompilerArgs arg0, final DomainModel arg1) {
        super(arg0, arg1);
    }

    @Override
    protected void generateRelationGetter(final Role role, final String paramListType, final PrintWriter out) {
        final String slotExpression = wrapSlotExpression(role, getSlotExpression(role.getName()));
        generateRelationGetter("get" + capitalize(role.getName()) + "Set", slotExpression, paramListType, out);
    }

    private String wrapSlotExpression(final Role role, final String slotExpression) {
        final DomainEntity type = role.getType();
        final String typeFullName = getTypeFullName(type);
        if (type instanceof DomainClass) {
            final DomainClass domainClass = (DomainClass) type;
            if (implementsVirtualHost(domainClass)) {
                final DomainEntity thisType = role.getOtherRole().getType();
                final DomainClass thisDomainClass = (DomainClass) thisType;
                if (!isVirtualHostSpecific(thisDomainClass)) {
                    final String wrapperType =
                            makeGenericType("pt.ist.bennu.core.domain.VirtualHostFilterCollectionWrapper", typeFullName);
                    return "new " + wrapperType + "(" + slotExpression + ")";
                }
            }
        }
        return slotExpression;
    }

    private boolean implementsVirtualHost(final DomainClass domainClass) {
        final List<?> interfacesNames = domainClass.getInterfacesNames();
        if (interfacesNames.contains("pt.ist.bennu.core.domain.VirtualHostAware")) {
            return true;
        }
        final DomainEntity superclass = domainClass.getSuperclass();
        return superclass != null && superclass instanceof DomainClass && implementsVirtualHost((DomainClass) superclass);
    }

    private boolean isVirtualHostSpecific(final DomainClass domainClass) {
        if (implementsVirtualHost(domainClass)) {
            return true;
        }
        for (final Role role : domainClass.getRoleSlotsList()) {
            final DomainEntity type = role.getType();
            if (type instanceof DomainClass) {
                final DomainClass dc = (DomainClass) type;
                if (dc.getFullName().equals("pt.ist.bennu.core.domain.VirtualHost")) {
                    return true;
                }
            }
        }
        return false;
    }

}
