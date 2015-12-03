package org.fenixedu.bennu.alerts;

/**
 * AlertTypes classify {@link Alert} messages.
 * These types loosely map to colors in Bootstrap ( <a href="http://getbootstrap.com">http://getbootstrap.com</a>).
 *
 * @author Artur Ventura (artur.ventura@tecnico.pt)
 * @since 3.5.0
 */
enum AlertType {
    /**
     * A success message
     */
    SUCCESS("success"),

    /**
     * A information message
     */
    INFO("info"),

    /**
     * A warning message
     */
    WARNING("warning"),

    /**
     * A danger message
     */
    DANGER("danger");

    private String tag;

    AlertType(String tag) {
        this.tag = tag;
    }

    /**
     * Retrives the tag
     *
     * @return the tag1
     */
    public String getTag() {
        return tag;
    }
}
