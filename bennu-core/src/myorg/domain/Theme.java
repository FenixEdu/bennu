package myorg.domain;

import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.pstm.Transaction;

public class Theme extends Theme_Base {

    public static enum ThemeType {
	TOP, SIDE;
    }

    public Theme(String name, ThemeType type) {
	super();
	setMyOrg(MyOrg.getInstance());
	setName(name);
	setType(type);
    }

    public Theme(String name, String description, ThemeType type) {
	this(name, type);
	setDescription(description);
    }

    public void delete() {
	removeMyOrg();
	for (; !getVirtualHosts().isEmpty(); getVirtualHosts().get(0).removeTheme())
	    ;
	Transaction.deleteObject(this);
    }

    public static Theme getThemeByName(String name) {
	for (Theme theme : MyOrg.getInstance().getThemes()) {
	    if (theme.getName().equals(name)) {
		return theme;
	    }
	}
	return null;
    }

    public static boolean isThemeAvailable(String name) {
	return getThemeByName(name) != null;
    }

    @Service
    public static Theme createTheme(String themeName, ThemeType type) {
	return new Theme(themeName, type);
    }

    @Service
    public static void deleteTheme(Theme theme) {
	theme.delete();
    }
}
