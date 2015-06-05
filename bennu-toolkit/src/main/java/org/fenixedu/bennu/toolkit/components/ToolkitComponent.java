package org.fenixedu.bennu.toolkit.components;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ToolkitComponent {
    String key();

    String name();

    String description();

    String[] editorFiles();

    String[] viewerFiles() default {};
}
