package pt.ist.bennu.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import pt.ist.fenixframework.pstm.AbstractDomainObject;
import pt.ist.fenixframework.pstm.IllegalWriteException;
import pt.ist.fenixframework.pstm.Transaction;

public class ServiceManager {

    public static final Map<String, String> KNOWN_WRITE_SERVICES = new ConcurrentHashMap<>();

    private static ThreadLocal<Boolean> isInServiceVar = new ThreadLocal<>();

    private static ThreadLocal<List<Command>> afterCommitCommands = new ThreadLocal<>();

    /**
     * Invoker for services implemented in static methods.
     * 
     * @param classname
     *            The full name of the class where the method is.
     * @param methodname
     *            The method name. A static method with this named must exist in
     *            the specified class.
     * @param arguments
     *            an array to be passed onto the service.
     * @return returns whatever the service invocation returns.
     * @throws ServiceManagerException
     *             Thrown if the reflection mechanism fails to find and invoke
     *             the desired service.
     */
    public static Object invokeServiceByName(String classname, String methodname, Object[] arguments)
	    throws ServiceManagerException {
	try {
	    Class<?> clazz = Class.forName(classname);
	    Method[] methods = clazz.getDeclaredMethods();
	    List<Method> matchingMethods = new LinkedList<>();
	    for (Method method : methods) {
		Class<?>[] types = method.getParameterTypes();
		if (method.getName().equals(methodname) && types.length == arguments.length
			&& Modifier.isStatic(method.getModifiers())) {
		    boolean isGood = true;
		    for (int i = 0; i < types.length; i++) {
			if (arguments[i] != null && !types[i].getClass().isAssignableFrom(arguments[i].getClass())) {
			    isGood = false;
			}
		    }
		    if (isGood) {
			matchingMethods.add(method);
		    }
		}
	    }
	    if (matchingMethods.size() == 1) {
		Method method = matchingMethods.get(0);
		return method.invoke(null, arguments);
	    } else if (matchingMethods.isEmpty()) {
		throw new ServiceManagerException("Could not find a method compatible with: "
			+ constructMethodPrint(classname, methodname, arguments));
	    } else {
		throw new ServiceManagerException("Found more than one matching method for: "
			+ constructMethodPrint(classname, methodname, arguments));
	    }
	} catch (ClassNotFoundException e) {
	    throw new ServiceManagerException("Inexisting Service Class on this call: "
		    + constructMethodPrint(classname, methodname, arguments), e);
	} catch (IllegalArgumentException e) {
	    throw new ServiceManagerException("Unable to invoke method for this call: "
		    + constructMethodPrint(classname, methodname, arguments), e);
	} catch (IllegalAccessException e) {
	    throw new ServiceManagerException("Unable to invoke method for this call:"
		    + constructMethodPrint(classname, methodname, arguments), e);
	} catch (InvocationTargetException e) {
	    throw new ServiceManagerException("Unable to invoke method for this call:"
		    + constructMethodPrint(classname, methodname, arguments), e);
	}
    }

    private static String constructMethodPrint(String classname, String methodname, Object[] arguments) {
	StringBuilder print = new StringBuilder();
	print.append(classname);
	print.append(methodname);
	print.append("(");
	for (Object argument : arguments) {
	    print.append(argument != null ? argument.getClass() : "<NULL>");
	    print.append(", ");
	}
	if (print.toString().endsWith(", ")) {
	    print.delete(print.length() - 2, print.length());
	}
	print.append(")");
	return print.toString();
    }

    public static void resetAfterCommitCommands() {
	afterCommitCommands.set(null);
    }

    public static List<Command> getAfterCommitCommands() {
	return afterCommitCommands.get();
    }

    public static void registerAfterCommitCommand(Command command) {
	List<Command> commands = getAfterCommitCommands();
	if (commands == null) {
	    commands = new ArrayList<>();
	    afterCommitCommands.set(commands);
	}
	commands.add(command);
    }

    public static boolean isInsideService() {
	return isInServiceVar.get() != null;
    }

    public static void enterService() {
	Boolean inside = isInServiceVar.get();
	if (inside == null) {
	    isInServiceVar.set(Boolean.TRUE);
	}
    }

    public static void exitService() {
	Boolean inside = isInServiceVar.get();
	if (inside == null) {
	    isInServiceVar.remove();
	}
    }

    public static void initServiceInvocation(final String serviceName) {
	Transaction.setDefaultReadOnly(!ServiceManager.KNOWN_WRITE_SERVICES.containsKey(serviceName));
    }

    public static void beginTransaction() {
	if (jvstm.Transaction.current() != null) {
	    jvstm.Transaction.commit();
	}
	Transaction.begin();
    }

    public static void commitTransaction() {
	jvstm.Transaction.checkpoint();
	Transaction.currentFenixTransaction().setReadOnly();
    }

    public static void abortTransaction() {
	Transaction.abort();
	Transaction.begin();
	Transaction.currentFenixTransaction().setReadOnly();
    }

    public static void logTransactionRestart(String service, Throwable cause, int tries) {
	System.out.println("Service " + service + " has been restarted " + tries + " times because of "
		+ cause.getClass().getSimpleName());
    }

    public static <T extends Object> T execute(final Callable<T> callable) throws Exception {
	if (isInsideService()) {
	    return callable.call();
	}
	final String serviceName = callable.getClass().getName();
	enterService();
	try {
	    ServiceManager.initServiceInvocation(serviceName);

	    boolean keepGoing = true;
	    int tries = 0;
	    try {
		while (keepGoing) {
		    tries++;
		    try {
			try {
			    ServiceManager.beginTransaction();
			    T result = callable.call();
			    ServiceManager.commitTransaction();
			    List<Command> commands = getAfterCommitCommands();
			    if (commands != null) {
				for (Command command : commands) {
				    command.execute();
				}
			    }
			    keepGoing = false;
			    return result;
			} finally {
			    if (keepGoing) {
				ServiceManager.abortTransaction();
			    }
			    resetAfterCommitCommands();
			}
		    } catch (jvstm.CommitException commitException) {
			if (tries > 3) {
			    logTransactionRestart(serviceName, commitException, tries);
			}
		    } catch (AbstractDomainObject.UnableToDetermineIdException unableToDetermineIdException) {
			if (tries > 3) {
			    logTransactionRestart(serviceName, unableToDetermineIdException, tries);
			}
		    } catch (IllegalWriteException illegalWriteException) {
			ServiceManager.KNOWN_WRITE_SERVICES.put(callable.getClass().getName(), callable.getClass().getName());
			Transaction.setDefaultReadOnly(false);
			if (tries > 3) {
			    logTransactionRestart(serviceName, illegalWriteException, tries);
			}
		    }
		}
		// never reached
		return null;
	    } finally {
		Transaction.setDefaultReadOnly(false);
	    }
	} finally {
	    ServiceManager.exitService();
	}
    }

}
