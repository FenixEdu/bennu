package myorg.domain.contents;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.ResourceBundle;

import module.coffee.domain.CoffeeItem;
import module.coffee.domain.ItemBean;
import myorg.domain.MyOrg;
import myorg.domain.groups.PersistentGroup;
import myorg.presentationTier.Context;
import pt.ist.fenixWebFramework.services.Service;
import pt.utl.ist.fenix.tools.util.i18n.Language;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class ActionNode extends ActionNode_Base {
    
    public ActionNode() {
        super();
        init(null, null);
    }

    @Service
    public static ActionNode createActionNode(final String path, final String method, final String bundle, final String key, final PersistentGroup accessibilityGroup) {
	final ActionNode actionNode = new ActionNode();
	actionNode.setPath(path);
	actionNode.setMethod(method);
	actionNode.setLinkBundle(bundle);
	actionNode.setLinkKey(key);
	actionNode.setAccessibilityGroup(accessibilityGroup);

	initializeItems();

	return actionNode;
    }

    private static void initializeItems() {
	if (!MyOrg.getInstance().hasAnyCoffeeItems()) {
	    createItem("Nespresso Variations Crystalized Ginger", "nesclub.nespresso.com/img/pictures/02377.jpg", "0.36", Boolean.TRUE);
	    createItem("Nespresso Variations Caramel", "nesclub.nespresso.com/img/pictures/02372.jpg", "0.36", Boolean.TRUE);
	    createItem("Nespresso Vartiations Mandarine", "nesclub.nespresso.com/img/pictures/02376.jpg", "0.36", Boolean.TRUE);

	    createItem("Ristretto", "nesclub.nespresso.com/img/pictures/00746.gif", "0.31", Boolean.TRUE);
	    createItem("Arpeggio", "nesclub.nespresso.com/img/pictures/00673.gif", "0.31", Boolean.TRUE);
	    createItem("Roma", "nesclub.nespresso.com/img/pictures/00747.gif", "0.31", Boolean.TRUE);
	    createItem("Decaffeinato Intenso", "nesclub.nespresso.com/img/pictures/00707.gif", "0.31", Boolean.TRUE);
	    createItem("Livanto", "nesclub.nespresso.com/img/pictures/00712.gif", "0.31", Boolean.TRUE);
	    createItem("Capriccio", "nesclub.nespresso.com/img/pictures/00674.gif", "0.31", Boolean.TRUE);
	    createItem("Volluto", "nesclub.nespresso.com/img/pictures/00751.gif", "0.31", Boolean.TRUE);
	    createItem("Cosi", "nesclub.nespresso.com/img/pictures/00676.gif", "0.31", Boolean.TRUE);
	    createItem("Decaffeinato", "nesclub.nespresso.com/img/pictures/00706.gif", "0.31", Boolean.TRUE);

	    createItem("Vivalto", "nesclub.nespresso.com/img/pictures/00750.gif", "0.33", Boolean.TRUE);
	    createItem("Decaffeinato Lungo", "nesclub.nespresso.com/img/pictures/00708.gif", "0.33", Boolean.TRUE);
	    createItem("Finezzo", "nesclub.nespresso.com/img/pictures/00709.gif", "0.33", Boolean.TRUE);
	}
    }

    private static void createItem(final String name, final String imageUrl, final String unitValue, final Boolean available) {
	final ItemBean itemBean = new ItemBean();
	itemBean.setName(name);
	itemBean.setUnitValue(new BigDecimal(unitValue));
	itemBean.setImageUrl(imageUrl);
	itemBean.setAvailable(available);
	CoffeeItem.creanteNewItem(itemBean);
    }

    @Override
    public Object getElement() {
	return null;
    }

    @Override
    public MultiLanguageString getLink() {
	final MultiLanguageString multiLanguageString = new MultiLanguageString();
	final String bundle = getLinkBundle();
	final String key = getLinkKey();
	if (!addContent(multiLanguageString, bundle, Language.getLocale(), key)) {
	    if (!addContent(multiLanguageString, bundle, Language.getDefaultLocale(), key)) {
		addContent(multiLanguageString, bundle, null, key);
	    }
	}
	return multiLanguageString;
    }

    private boolean addContent(final MultiLanguageString multiLanguageString, final String bundle, final Locale locale, final String key) {
	final ResourceBundle resourceBundle;
	if (locale == null) {
	    resourceBundle = ResourceBundle.getBundle(getLinkBundle());
	} else {
	    resourceBundle = ResourceBundle.getBundle(getLinkBundle(), Language.getLocale());
	}
	if (resourceBundle != null) {
	    final Language language = Language.valueOf(locale.getLanguage());
	    final String content = resourceBundle.getString(key);
	    multiLanguageString.setContent(language, content);
	    return true;
	}
	return false;
    }

    @Override
    public String getUrl(final Context context) {
	return getPath() + ".do?method=" + getMethod();
    }

}
