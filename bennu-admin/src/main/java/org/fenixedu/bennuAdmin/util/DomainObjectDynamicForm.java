package org.fenixedu.bennuAdmin.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.commons.i18n.LocalizedString;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.dml.Slot;

import java.util.Locale;

public class DomainObjectDynamicForm extends DynamicForm {
  private final DomainObject domainObject;

  private static JsonObject buildForm(DomainObject domainObject) {
    // todo: implement this method
    return new JsonObject();
  }

  public DomainObjectDynamicForm(DomainObject domainObject) {
    super(buildForm(domainObject));
    this.domainObject = domainObject;
  }

  public abstract static class Field extends DynamicForm.Field {
    Field(int page, int section, JsonObject config, Field parent, DynamicForm form) {
      super(page, section, config, parent, form);
    }

    public static DynamicForm.Field create(DomainObject domainObject, Slot slot) {
      String type = slot.getTypeName().substring(slot.getTypeName().lastIndexOf(".") + 1);

      try {
        if (Class.forName(slot.getTypeName()).isEnum()) {
          return new Select(domainObject, slot);
        }
      } catch (ClassNotFoundException e) {
        // ignore: not an enum
      }

      switch (type) {
        case "LocalizedString":
          return new LocalizedText(domainObject, slot);
        case "Boolean", "boolean":
          return new Boolean(domainObject, slot);
        case "DateTime":
          return new DateTime(domainObject, slot);
        case "Integer", "BigDecimal":
          return new Numeric(domainObject, slot);
        default:
          return new Text(domainObject, slot);
      }
    }
  }

  public static class Boolean extends DynamicForm.Boolean {
    public Boolean(int page, int section, JsonObject config, Field parent, DynamicForm form) {
      super(page, section, config, parent, form);
    }

    public Boolean(DomainObject domainObject, Slot slot) {
      // todo: implement this constructor
      super(0, 0, new JsonObject(), null, null);
    }

    // Todo: override functions
  }

  public static class Text extends DynamicForm.Text {
    public Text(int page, int section, JsonObject config, Field parent, DynamicForm form) {
      super(page, section, config, parent, form);
    }

    public Text(DomainObject domainObject, Slot slot) {
      // todo: implement this constructor
      super(0, 0, new JsonObject(), null, null);
    }

    // Todo: override functions
  }

  public static class LocalizedText extends DynamicForm.LocalizedText {
    public LocalizedText(int page, int section, JsonObject config, Field parent, DynamicForm form) {
      super(page, section, config, parent, form);
    }

    public LocalizedText(DomainObject domainObject, Slot slot) {
      // todo: implement this constructor
      super(0, 0, new JsonObject(), null, null);
    }

    // Todo: override functions
  }

  public static class Numeric extends DynamicForm.Numeric {
    public Numeric(int page, int section, JsonObject config, Field parent, DynamicForm form) {
      super(page, section, config, parent, form);
    }

    public Numeric(DomainObject domainObject, Slot slot) {
      // todo: implement this constructor
      super(0, 0, new JsonObject(), null, null);
    }

    // Todo: override functions
  }

  public static class Select extends DynamicForm.Numeric {
    public Select(int page, int section, JsonObject config, Field parent, DynamicForm form) {
      super(page, section, config, parent, form);
    }

    public Select(DomainObject domainObject, Slot slot) {
      // todo: implement this constructor
      super(0, 0, new JsonObject(), null, null);
    }

    // Todo: override functions
  }

  public static class DateTime extends DynamicForm.DateTime {
    public DateTime(int page, int section, JsonObject config, Field parent, DynamicForm form) {
      super(page, section, config, parent, form);
    }

    public DateTime(DomainObject domainObject, Slot slot) {
      // todo: implement this constructor
      super(0, 0, new JsonObject(), null, null);
    }

    // Todo: override functions
  }

  private LocalizedString ls(final String pt, final String en) {
    return new LocalizedString(Locale.forLanguageTag("pt-PT"), pt)
        .with(Locale.forLanguageTag("en-GB"), en);
  }

  private LocalizedString ls(final String str) {
    return ls(str, str);
  }
}
