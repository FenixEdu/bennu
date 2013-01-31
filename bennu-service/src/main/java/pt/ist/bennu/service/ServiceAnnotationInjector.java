package pt.ist.bennu.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceAnnotationInjector {

	private static Logger LOG = LoggerFactory.getLogger(ServiceAnnotationInjector.class);

	private static final String SERVICE_MANAGER_PACKAGE = ServiceManager.class.getPackage().getName();

	public static void inject(File outputFolder, ClassLoader loader) {
		final ClassPool classPool = ClassPool.getDefault();
		classPool.appendClassPath(new LoaderClassPath(loader));

		classPool.importPackage(SERVICE_MANAGER_PACKAGE);
		classPool.importPackage("pt.ist.fenixframework.pstm");

		File annotationLogFile = null;

		try {
			URL url = loader.getResource(ServiceAnnotationProcessor.LOG_FILENAME);
			if (url != null) {
				annotationLogFile = new File(url.toURI());
				if (annotationLogFile.exists()) {
					String fileContents = FileUtils.readFileToString(annotationLogFile);
					final String[] lines = fileContents.split(ServiceAnnotationProcessor.ENTRY_SEPARATOR);
					for (final String line : lines) {
						final String[] tokens = line.split(ServiceAnnotationProcessor.FIELD_SEPARATOR);
						String className = tokens[0];
						String methodName = tokens[1];
						LOG.debug("Injecting service code on method \"" + methodName + "\" on class\"" + className + "\"");
						process(outputFolder.getAbsolutePath(), classPool, className, methodName);
					}
				} else {
					LOG.info("No Service annotation log file was found. Skipping injection...");
				}
			} else {
				LOG.info("No Service annotation log file was found. Skipping injection...");
			}
		} catch (FileNotFoundException e) {
			throw new Error(e);
		} catch (IOException e) {
			throw new Error(e);
		} catch (URISyntaxException e) {
			throw new Error(e);
		} finally {
			if (annotationLogFile != null && annotationLogFile.exists()) {
				annotationLogFile.delete();
			}
		}
	}

	public static void main(String[] args) {
		final ClassPool classPool = ClassPool.getDefault();
		classPool.appendSystemPath();
		try {
			for (final String arg : args) {
				if (arg.indexOf('*') >= 0) {
					final int wildCardPos = arg.indexOf('*');
					final int lastDirPos = arg.lastIndexOf(File.separatorChar);
					final String dirName = arg.substring(0, lastDirPos);
					final File dir = new File(dirName);

					final String p1 = arg.substring(lastDirPos + 1, wildCardPos);
					final String p2 = arg.substring(wildCardPos + 1);

					for (final File file : dir.listFiles()) {
						final String filename = file.getName();
						if (filename.startsWith(p1) && filename.endsWith(p2)) {
							classPool.appendClassPath(file.getAbsolutePath());
						}
					}
				} else {
					classPool.appendClassPath(arg);
				}
			}
		} catch (NotFoundException e) {
			throw new Error(e);
		}

		inject(new File(args[0]), classPool.getClassLoader());
	}

	private static void process(final String outputFolder, final ClassPool classPool, final String className,
			final String methodName) {
		try {
			CtClass classToInject = classPool.get(className);
			if (!hasInjectedMethod(classToInject, methodName)) {

				for (final CtMethod ctMethod : classToInject.getDeclaredMethods()) {
					if (ctMethod.getName().equals(methodName)) {
						ctMethod.setName("_" + methodName + "_");

						final CtMethod newCtMethod = new CtMethod(ctMethod.getReturnType(), methodName,
								ctMethod.getParameterTypes(), ctMethod.getDeclaringClass());
						newCtMethod.setModifiers(ctMethod.getModifiers());
						final String body = getWrapperMethod(ctMethod, className, methodName);
						newCtMethod.setBody(body);
						newCtMethod.setExceptionTypes(ctMethod.getExceptionTypes());
						classToInject.addMethod(newCtMethod);
					}
				}
				classToInject.writeFile(outputFolder);
				classToInject.detach();
			}
		} catch (NotFoundException e) {
			throw new Error(e);
		} catch (CannotCompileException e) {
			throw new Error(e);
		} catch (IOException e) {
			throw new Error(e);
		}
	}

	private static boolean hasInjectedMethod(final CtClass ctClass, final String methodName) {
		return hasMethod(ctClass, "_" + methodName + "_");
	}

	private static boolean hasMethod(final CtClass ctClass, final String methodName) {
		for (final CtMethod ctMethod : ctClass.getDeclaredMethods()) {
			if (ctMethod.getName().equals(methodName)) {
				return true;
			}
		}
		return false;
	}

	private static String getWrapperMethod(final CtMethod ctMethod, final String className, final String methodName)
			throws NotFoundException {
		final CtClass returnType = ctMethod.getReturnType();
		final boolean isPrimitive = returnType.isPrimitive();
		final boolean isVoid = isPrimitive && returnType.getSimpleName().equals("void");
		final String serviceName = className + "." + methodName;
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{\n");

		if (!isVoid) {
			stringBuilder.append("\t");
			stringBuilder.append(returnType.getName());
			stringBuilder.append(" _result_ = null;\n");
		}

		stringBuilder.append("if (ServiceManager.isInsideService()) {");
		appendSeriveInvocation(stringBuilder, isVoid, returnType, ctMethod);
		stringBuilder.append("} else {");
		stringBuilder.append("    ServiceManager.enterService();");
		stringBuilder.append("    try {");
		stringBuilder.append("        ServiceManager.initServiceInvocation(\"");
		stringBuilder.append(serviceName);
		stringBuilder.append("\");");

		stringBuilder.append("boolean keepGoing = true;");
		stringBuilder.append("int tries = 0;");
		stringBuilder.append("try {");
		stringBuilder.append("    while (keepGoing) {");
		stringBuilder.append("	tries++;");
		stringBuilder.append("	try {");
		stringBuilder.append("	    try {");
		stringBuilder.append("          ServiceManager.beginTransaction();");
		appendSeriveInvocation(stringBuilder, isVoid, returnType, ctMethod);
		stringBuilder.append("          ServiceManager.commitTransaction();");
		stringBuilder.append("          java.util.List commands = ServiceManager.getAfterCommitCommands();");
		stringBuilder.append("          if (commands != null) {");
		stringBuilder.append("          java.util.Iterator iterator = commands.iterator();");
		stringBuilder.append("          while(iterator.hasNext()) {");
		stringBuilder.append("          Command command = (Command) iterator.next();");
		stringBuilder.append("          command.execute();");
		stringBuilder.append("          }");
		stringBuilder.append("          }");

		stringBuilder.append("	        keepGoing = false;");
		stringBuilder.append("	    } finally {");
		stringBuilder.append("          if (keepGoing) {");
		stringBuilder.append("          ServiceManager.abortTransaction();");
		stringBuilder.append("          }");
		stringBuilder.append("          ServiceManager.resetAfterCommitCommands();");
		stringBuilder.append("	    }");
		stringBuilder.append("	} catch (jvstm.CommitException commitException) {");
		stringBuilder.append("      if (tries > 3) {");
		stringBuilder.append("	        ServiceManager.logTransactionRestart(\"" + serviceName + "\", commitException, tries);");
		stringBuilder.append("      }");
		stringBuilder.append("	} catch (pt.ist.fenixframework.pstm.AbstractDomainObject.UnableToDetermineIdException ex) {");
		stringBuilder.append("      if (tries > 3) {");
		stringBuilder.append("	        ServiceManager.logTransactionRestart(\"" + serviceName + "\", ex, tries);");
		stringBuilder.append("      }");
		stringBuilder.append("	} catch (pt.ist.fenixframework.pstm.IllegalWriteException illegalWriteException) {");
		stringBuilder.append("	    ServiceManager.KNOWN_WRITE_SERVICES.put(\"");
		stringBuilder.append(serviceName);
		stringBuilder.append("\", \"");
		stringBuilder.append(serviceName);
		stringBuilder.append("\");");
		stringBuilder.append("	    Transaction.setDefaultReadOnly(false);");
		stringBuilder.append("      if (tries > 3) {");
		stringBuilder.append("	        ServiceManager.logTransactionRestart(\"" + serviceName
				+ "\", illegalWriteException, tries);");
		stringBuilder.append("      }");
		stringBuilder.append("	}");
		stringBuilder.append("    }");
		stringBuilder.append("} finally {");
		stringBuilder.append("    Transaction.setDefaultReadOnly(false);");
		stringBuilder.append("}");

		stringBuilder.append("    } finally {");
		stringBuilder.append("        ServiceManager.exitService();");
		stringBuilder.append("    }");
		stringBuilder.append("}");

		if (!isVoid) {
			stringBuilder.append("\treturn _result_;\n");
		}
		stringBuilder.append("}");
		return stringBuilder.toString();
	}

	private static void appendSeriveInvocation(final StringBuilder stringBuilder, final boolean isVoid, final CtClass returnType,
			final CtMethod ctMethod) {
		if (!isVoid) {
			stringBuilder.append("_result_ = ");
		}
		stringBuilder.append(ctMethod.getName());
		stringBuilder.append("($$);\n");
	}

}
