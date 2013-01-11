package pt.ist.bennu.core.domain.groups.annotation;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import pt.ist.bennu.core.domain.groups.CustomGroup;

@HandlesTypes({ CustomGroupOperator.class })
public class CustomGroupAnnotationInitializer implements ServletContainerInitializer {
	@Override
	@SuppressWarnings("unchecked")
	public void onStartup(Set<Class<?>> classes, ServletContext ctx) throws ServletException {
		if (classes != null) {
			for (Class<?> type : classes) {
				CustomGroup.registerOperator((Class<? extends CustomGroup>) type);
			}
		}
	}
}
