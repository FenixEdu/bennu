package pt.ist.fenixframework.pstm.dml;

import java.io.PrintWriter;
import java.util.List;

import dml.CompilerArgs;
import dml.DomainClass;
import dml.DomainEntity;
import dml.DomainModel;
import dml.Role;

public class VirtualHostAwareCodeGenerator extends FenixCodeGeneratorOneBoxPerObject {

	public VirtualHostAwareCodeGenerator(final CompilerArgs arg0, final DomainModel arg1) {
		super(arg0, arg1);
	}

	@Override
	protected void generateRoleSlotMethodsMultStarCount(final Role role, final PrintWriter out, final String methodModifiers,
			final String capitalizedSlotName, final String slotAccessExpression) {
		final String slotExpression = wrapSlotExpression(role, slotAccessExpression);
		super.generateRoleSlotMethodsMultStarCount(role, out, methodModifiers, capitalizedSlotName, slotExpression);
	}

	@Override
	protected void generateRoleSlotMethodsMultStarHasAnyChild(Role role, PrintWriter out, String methodModifiers,
			String capitalizedSlotName, String slotAccessExpression) {
		final String slotExpression = wrapSlotExpression(role, slotAccessExpression);
		super.generateRoleSlotMethodsMultStarHasAnyChild(role, out, methodModifiers, capitalizedSlotName, slotExpression);
	}

	@Override
	protected void generateRoleSlotMethodsMultStarHasChild(Role role, PrintWriter out, String methodModifiers,
			String capitalizedSlotName, String slotAccessExpression, String typeName, String slotName, boolean isIndexed,
			String indexGetterCall) {
		final String slotExpression = wrapSlotExpression(role, slotAccessExpression);
		super.generateRoleSlotMethodsMultStarHasChild(role, out, methodModifiers, capitalizedSlotName, slotExpression, typeName,
				slotName, isIndexed, indexGetterCall);
	}

	@Override
	protected void generateRoleSlotMethodsMultStarSet(Role role, PrintWriter out, String methodModifiers,
			String capitalizedSlotName, String slotAccessExpression, String slotName, String typeName) {
		final String slotExpression = wrapSlotExpression(role, getSlotExpression(role.getName()));
		super.generateRoleSlotMethodsMultStarSet(role, out, methodModifiers, capitalizedSlotName, slotExpression, slotName,
				typeName);
	}

	@Override
	protected void generateRelationGetter(final Role role, final String paramListType, final PrintWriter out) {
		final String slotExpression = wrapSlotExpression(role, getSlotExpression(role.getName()));
		generateRelationGetter("get" + capitalize(role.getName()), slotExpression, paramListType, out);
	}

	@Override
	protected void generateIteratorMethod(final Role role, final PrintWriter out, final String slotAccessExpression) {
		final String slotExpression = wrapSlotExpression(role, getSlotExpression(role.getName()));
		super.generateIteratorMethod(role, out, slotExpression);
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
		final List interfacesNames = domainClass.getInterfacesNames();
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
