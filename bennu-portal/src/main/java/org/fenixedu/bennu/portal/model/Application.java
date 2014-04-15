package org.fenixedu.bennu.portal.model;

import java.util.Set;
import java.util.TreeSet;

import org.fenixedu.commons.i18n.LocalizedString;

/**
 * An {@link Application} represents an aggregation of {@link Functionality}s. This class acts as a model descriptor, which is
 * used to create {@link MenuContainers}.
 * 
 * <p>
 * Note that the key of an {@link Application} MUST be unique across your whole application.
 * </p>
 * 
 * @see Functionality
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 * 
 */
public final class Application {

    private final Set<Functionality> functionalities = new TreeSet<Functionality>();

    private final String key;

    private final String path;

    private final String accessGroup;

    private final LocalizedString title;

    private final LocalizedString description;

    private final String group;

    public Application(String key, String path, String accessGroup, LocalizedString title, LocalizedString description,
            String group) {
        this.key = key;
        this.path = path;
        this.accessGroup = accessGroup;
        this.title = title;
        this.description = description;
        this.group = group;
    }

    /**
     * An application's key is a logical descriptor, used to uniquely identify an application externally.
     * As such, it must be unique across multiple applications.
     * 
     * @return This application's key
     */
    public String getKey() {
        return key;
    }

    public String getPath() {
        return path;
    }

    public String getAccessGroup() {
        return accessGroup;
    }

    public LocalizedString getTitle() {
        return title;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public Set<Functionality> getFunctionalities() {
        return functionalities;
    }

    public String getGroup() {
        return group;
    }

    public void addFunctionality(Functionality functionality) {
        this.functionalities.add(functionality);
    }

}
