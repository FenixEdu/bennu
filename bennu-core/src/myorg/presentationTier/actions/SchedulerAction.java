package myorg.presentationTier.actions;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.domain.scheduler.Task;
import myorg.presentationTier.Context;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import dml.DomainClass;

@Mapping( path="/scheduler" )
public class SchedulerAction extends ContextBaseAction {

    public ActionForward viewScheduler(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
	    final HttpServletResponse response) throws Exception {
	final Context context = getContext(request);

	final Set<DomainClass> taskDomainClasses = Task.getAllTaskDomainClasses();
	request.setAttribute("taskDomainClasses", taskDomainClasses);

	return context.forward("/myorg/scheduler.jsp");
    }

}
