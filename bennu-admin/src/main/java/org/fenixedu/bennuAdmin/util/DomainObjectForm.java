package org.fenixedu.bennuAdmin.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.fenixedu.bennu.core.json.JsonUtils;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.commons.stream.StreamUtils;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.dml.DomainClass;
import pt.ist.fenixframework.dml.Role;
import pt.ist.fenixframework.dml.Slot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Set;

public class DomainObjectForm extends DynamicForm {
  private final DomainObject domainObject;

  private JsonObject buildFormData() {
    JsonObject slotsData =
        JsonUtils.toJson(
            data -> {
              this.fields.forEach(
                  f -> {
                    if (f instanceof PersistentField && ((PersistentField) f).isSlotField()) {
                      ((PersistentField) f).addPersistedData(data);
                    }
                  });
            });

    JsonObject rolesData =
        JsonUtils.toJson(
            data -> {
              this.fields.forEach(
                  f -> {
                    if (f instanceof PersistentField && !((PersistentField) f).isSlotField()) {
                      ((PersistentField) f).addPersistedData(data);
                    }
                  });
            });

    return JsonUtils.toJson(
        p ->
            p.add(
                "0",
                JsonUtils.toJson(
                    s -> {
                      s.add("0", slotsData);
                      s.add("1", rolesData);
                    })));
  }

  private JsonArray buildFormPages(DomainObject domainObject) {
    DomainClass domainClass =
        FenixFramework.getDomainModel().findClass(domainObject.getClass().getName());

    Set<Slot> slotsSet = DomainObjectUtils.getDomainObjectSlots(domainObject);
    Set<Role> roleSet = DomainObjectUtils.getRoles(domainClass, DomainObjectUtils.Multiplicity.ONE);

    slotsSet.forEach(slot -> this.fields.add(Field.create(this, slot)));
    roleSet.forEach(role -> this.fields.add(Field.create(this, role)));

    JsonObject slotsSection =
        JsonUtils.toJson(
            sec -> {
              sec.add("title", ls("Slots").json());
              sec.add(
                  "properties",
                  JsonUtils.toJsonArray(
                      propsArr -> {
                        this.fields.forEach(
                            f -> {
                              if (f instanceof PersistentField) {
                                if (((PersistentField) f).isSlotField()) {
                                  propsArr.add(f.getConfig());
                                }
                              }
                            });
                      }));
            });

    JsonObject rolesSection =
        JsonUtils.toJson(
            sec -> {
              sec.add("title", ls("Roles").json());
              sec.add(
                  "properties",
                  JsonUtils.toJsonArray(
                      propsArr -> {
                        this.fields.forEach(
                            f -> {
                              if (f instanceof PersistentField) {
                                if (!((PersistentField) f).isSlotField()) {
                                  propsArr.add(f.getConfig());
                                }
                              }
                            });
                      }));
            });

    JsonObject p =
        JsonUtils.toJson(
            page -> {
              page.add(
                  "sections",
                  JsonUtils.toJsonArray(
                      sectionsArr -> {
                        sectionsArr.add(slotsSection);
                        sectionsArr.add(rolesSection);
                      }));
            });

    return JsonUtils.toJsonArray(pages -> pages.add(p));
  }

  public DomainObjectForm(DomainObject domainObject) {
    this.domainObject = domainObject;
    this.getForm().add("pages", buildFormPages(domainObject));
    this.withData(buildFormData());
  }

  @Atomic(mode = Atomic.TxMode.WRITE)
  public void save() {
    fields.forEach(
        field -> {
          PersistentField persistentField = (PersistentField) field;
          persistentField.save();
        });
  }

  public DomainObjectForm withData(JsonObject data) {
    super.withData(data);
    return this;
  }

  private interface PersistentField {
    void save();

    void addPersistedData(final JsonObject data);

    default boolean isSlotField() {
      return true;
    }

    static JsonObject getDefaultConfig(Slot slot, DomainObject domainObject) {
      return JsonUtils.toJson(
          prop -> {
            prop.addProperty("readonly", !DomainObjectUtils.isEditable(domainObject, slot));
            prop.addProperty("field", slot.getName());
            prop.addProperty("required", false); // todo: check if slot is required
            prop.add("label", ls(slot.getName()).json());
            prop.add("description", ls(slot.getTypeName()).json());
          });
    }
  }

  public abstract static class Field extends DynamicForm.Field {
    Field(int page, int section, JsonObject config, Field parent, DynamicForm form) {
      super(page, section, config, parent, form);
    }

    public static DynamicForm.Field create(DomainObjectForm domainObjectForm, Slot slot) {
      String type = slot.getTypeName().substring(slot.getTypeName().lastIndexOf(".") + 1);

      try {
        if (Class.forName(slot.getTypeName()).isEnum()) {
          return new Select(domainObjectForm, slot);
        }
      } catch (ClassNotFoundException e) {
        // ignore: not an enum
      }

      return switch (type) {
        case "LocalizedString" -> new LocalizedText(domainObjectForm, slot);
        case "Boolean", "boolean" -> new Boolean(domainObjectForm, slot);
        case "DateTime" -> new DateTime(domainObjectForm, slot);
        case "Integer", "BigDecimal" -> new Numeric(domainObjectForm, slot);
        default -> new Text(domainObjectForm, slot);
      };
    }

    public static DynamicForm.Field create(DomainObjectForm domainObjectForm, Role role) {
      // todo: can we make this an AsyncSelect instead?
      return new Text(domainObjectForm, role);
    }
  }

  public static class Boolean extends DynamicForm.Boolean implements PersistentField {
    private Slot slot;

    public Boolean(int page, int section, JsonObject config, Field parent, DynamicForm form) {
      super(page, section, config, parent, form);
    }

    public Boolean(DomainObjectForm domainObjectForm, Slot slot) {
      super(0, 0, getDefaultConfig(slot, domainObjectForm.domainObject), null, domainObjectForm);
      this.slot = slot;
    }

    @Override
    public void save() {
      DomainObjectForm form = (DomainObjectForm) this.form;

      Method setMethod =
          DomainObjectUtils.getMethod(
              "set",
              form.domainObject,
              slot.getName(),
              slot.getTypeName().equals("boolean") ? boolean.class : java.lang.Boolean.class);

      if (setMethod == null) {
        throw new RuntimeException("Slot " + slot.getName() + " has no setter");
      }

      setMethod.setAccessible(true);

      try {
        setMethod.invoke(form.domainObject, data.getAsBoolean());
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new Error(e);
      }
    }

    @Override
    public void addPersistedData(JsonObject data) {
      DomainObjectForm form = (DomainObjectForm) this.form;
      String slotValueString = DomainObjectUtils.getSlotValueString(form.domainObject, slot);
      data.add(this.getName(), JsonUtils.parseJsonElement(slotValueString));
    }

    private static JsonObject getDefaultConfig(Slot slot, DomainObject domainObject) {
      JsonObject config = PersistentField.getDefaultConfig(slot, domainObject);
      config.addProperty("type", "Boolean");
      config.add("labelYes", DomainObjectForm.ls("Verdadeiro", "true").json());
      config.add("labelNo", DomainObjectForm.ls("Falso", "false").json());
      return config;
    }
  }

  public static class Text extends DynamicForm.Text implements PersistentField {
    private Slot slot;
    private Role role;

    public Text(int page, int section, JsonObject config, Field parent, DynamicForm form) {
      super(page, section, config, parent, form);
    }

    public Text(DomainObjectForm domainObjectForm, Slot slot) {
      super(0, 0, getDefaultConfig(slot, domainObjectForm.domainObject), null, domainObjectForm);
      this.slot = slot;
    }

    public Text(DomainObjectForm domainObjectForm, Role role) {
      super(0, 1, getDefaultConfig(role, domainObjectForm.domainObject), null, domainObjectForm);
      this.role = role;
    }

    private void saveRole() {
      DomainObjectForm form = (DomainObjectForm) this.form;

      if (data.getAsString().isEmpty() || data.getAsString().equals("null")) {
        return;
      }

      String type = role.getType().getFullName();

      String currentValue = DomainObjectUtils.getRelationSlot(form.domainObject, role);

      if (currentValue != null && currentValue.equals(data.getAsString())) {
        return;
      }

      Method setMethod = null;
      try {
        setMethod =
            DomainObjectUtils.getMethod(
                "set", form.domainObject, role.getName(), Class.forName(type));
      } catch (ClassNotFoundException e) {
        throw new Error(e);
      }

      if (setMethod == null) {
        throw new RuntimeException("Slot " + role.getName() + " has no setter");
      }

      setMethod.setAccessible(true);

      DomainObject object = FenixFramework.getDomainObject(data.getAsString());
      if (!FenixFramework.isDomainObjectValid(object)) {
        throw new RuntimeException("Invalid domain object");
      }

      try {
        setMethod.invoke(form.domainObject, object);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new Error(e);
      }
    }

    private void saveSlot() {
      DomainObjectForm form = (DomainObjectForm) this.form;

      Method setMethod =
          DomainObjectUtils.getMethod(
              "set", form.domainObject, slot.getName(), LocalizedString.class);

      if (setMethod == null) {
        throw new RuntimeException("Slot " + slot.getName() + " has no setter");
      }

      setMethod.setAccessible(true);

      try {
        setMethod.invoke(form.domainObject, LocalizedString.fromJson(data));
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new Error(e);
      }
    }

    @Override
    public boolean isSlotField() {
      return this.role == null;
    }

    @Override
    public void save() {
      if (this.role != null) {
        saveRole();
      } else {
        saveSlot();
      }
    }

    @Override
    public void addPersistedData(JsonObject data) {
      DomainObjectForm form = (DomainObjectForm) this.form;
      String value;
      if (this.isSlotField()) {
        value = DomainObjectUtils.getSlotValueString(form.domainObject, slot);
      } else {
        value = DomainObjectUtils.getRelationSlot(form.domainObject, role);
      }
      data.addProperty(this.getName(), value);
    }

    private static JsonObject getDefaultConfig(Slot slot, DomainObject domainObject) {
      JsonObject config = PersistentField.getDefaultConfig(slot, domainObject);
      config.addProperty("type", "Text");
      return config;
    }

    private static JsonObject getDefaultConfig(Role role, DomainObject domainObject) {
      return JsonUtils.toJson(
          prop -> {
            prop.addProperty("type", "Text");
            prop.addProperty("min", 0);
            prop.addProperty("readonly", !DomainObjectUtils.isEditable(domainObject, role));
            prop.addProperty("field", role.getName());
            prop.addProperty("required", false); // todo: check this
            prop.add("label", DomainObjectForm.ls(role.getName()).json());
            prop.add("description", DomainObjectForm.ls(role.getType().getName()).json());
          });
    }
  }

  public static class LocalizedText extends DynamicForm.LocalizedText implements PersistentField {
    private Slot slot;

    public LocalizedText(int page, int section, JsonObject config, Field parent, DynamicForm form) {
      super(page, section, config, parent, form);
    }

    public LocalizedText(DomainObjectForm domainObjectForm, Slot slot) {
      super(0, 0, getDefaultConfig(slot, domainObjectForm.domainObject), null, domainObjectForm);
      this.slot = slot;
    }

    @Override
    public void save() {
      DomainObjectForm form = (DomainObjectForm) this.form;

      Method setMethod =
          DomainObjectUtils.getMethod(
              "set", form.domainObject, slot.getName(), LocalizedString.class);

      if (setMethod == null) {
        throw new RuntimeException("Slot " + slot.getName() + " has no setter");
      }

      setMethod.setAccessible(true);

      try {
        setMethod.invoke(form.domainObject, LocalizedString.fromJson(data));
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new Error(e);
      }
    }

    @Override
    public void addPersistedData(JsonObject data) {
      DomainObjectForm form = (DomainObjectForm) this.form;
      String slotValueString = DomainObjectUtils.getSlotValueString(form.domainObject, slot);
      data.add(this.getName(), JsonUtils.parseJsonElement(slotValueString));
    }

    private static JsonArray getLocalizedTextSlotLocales(DomainObject domainObject, Slot slot) {
      String slotValueString = DomainObjectUtils.getSlotValueString(domainObject, slot);

      if (slotValueString == null) {
        return CoreConfiguration.supportedLocales().stream()
            .map(Locale::getLanguage)
            .map(JsonUtils::parseJsonElement)
            .collect(StreamUtils.toJsonArray());
      }

      JsonObject slotValue = JsonUtils.parseJsonElement(slotValueString).getAsJsonObject();

      return slotValue.keySet().stream()
          .map(JsonUtils::parseJsonElement)
          .collect(StreamUtils.toJsonArray());
    }

    private static JsonObject getDefaultConfig(Slot slot, DomainObject domainObject) {
      JsonObject config = PersistentField.getDefaultConfig(slot, domainObject);
      config.addProperty("type", "LocalizedText");
      config.add("locales", getLocalizedTextSlotLocales(domainObject, slot));
      return config;
    }
  }

  public static class Numeric extends DynamicForm.Numeric implements PersistentField {
    private Slot slot;

    public Numeric(int page, int section, JsonObject config, Field parent, DynamicForm form) {
      super(page, section, config, parent, form);
    }

    public Numeric(DomainObjectForm domainObjectForm, Slot slot) {
      super(0, 0, getDefaultConfig(slot, domainObjectForm.domainObject), null, domainObjectForm);
      this.slot = slot;
    }

    @Override
    public void save() {
      DomainObjectForm form = (DomainObjectForm) this.form;

      Method setMethod = null;
      try {
        setMethod =
            DomainObjectUtils.getMethod(
                "set", form.domainObject, slot.getName(), Class.forName(slot.getTypeName()));
      } catch (ClassNotFoundException e) {
        throw new Error(e);
      }

      if (setMethod == null) {
        throw new RuntimeException("Slot " + slot.getName() + " has no setter");
      }

      setMethod.setAccessible(true);

      try {
        setMethod.invoke(form.domainObject, data.getAsInt());
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        throw new Error(e);
      }
    }

    @Override
    public void addPersistedData(JsonObject data) {
      DomainObjectForm form = (DomainObjectForm) this.form;
      String slotValueString = DomainObjectUtils.getSlotValueString(form.domainObject, slot);
      data.add(this.getName(), JsonUtils.parseJsonElement(slotValueString));
    }

    private static JsonObject getDefaultConfig(Slot slot, DomainObject domainObject) {
      JsonObject config = PersistentField.getDefaultConfig(slot, domainObject);
      config.addProperty("type", "Numeric");
      return config;
    }
  }

  public static class Select extends DynamicForm.Numeric implements PersistentField {
    private Slot slot;

    public Select(int page, int section, JsonObject config, Field parent, DynamicForm form) {
      super(page, section, config, parent, form);
    }

    public Select(DomainObjectForm domainObjectForm, Slot slot) {
      super(0, 0, getDefaultConfig(slot, domainObjectForm.domainObject), null, domainObjectForm);
      this.slot = slot;
    }

    @Override
    public void save() {
      DomainObjectForm form = (DomainObjectForm) this.form;

      Class<?> enumClass = null;
      try {
        enumClass = Class.forName(slot.getTypeName());
      } catch (ClassNotFoundException e) {
        throw new Error(e);
      }

      if (!enumClass.isEnum()) {
        throw new RuntimeException(
            "Slot " + slot.getName() + " is not an enum, but it's being treated as one");
      }

      Method setMethod =
          DomainObjectUtils.getMethod("set", form.domainObject, slot.getName(), enumClass);

      if (setMethod == null) {
        throw new RuntimeException("Slot " + slot.getName() + " has no setter");
      }

      setMethod.setAccessible(true);

      Enum<?> enumValue =
          Enum.valueOf((Class<Enum>) enumClass, data.getAsJsonObject().get("value").getAsString());

      try {
        setMethod.invoke(form.domainObject, enumValue);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new Error(e);
      }
    }

    @Override
    public void addPersistedData(JsonObject data) {
      DomainObjectForm form = (DomainObjectForm) this.form;
      String slotValueString = DomainObjectUtils.getSlotValueString(form.domainObject, slot);
      JsonObject slotValue =
          JsonUtils.toJson(
              j -> {
                j.add("label", ls(slotValueString).json());
                j.addProperty("value", slotValueString);
              });

      data.add(this.getName(), slotValue);
    }

    private static JsonArray getSlotOptions(Slot slot) {
      return JsonUtils.toJsonArray(
          arr -> {
            try {
              Class<?> enumClass = Class.forName(slot.getTypeName());
              Object[] enumConstants = enumClass.getEnumConstants();
              for (Object enumConstant : enumConstants) {
                arr.add(
                    JsonUtils.toJson(
                        obj -> {
                          obj.add("label", DomainObjectForm.ls(enumConstant.toString()).json());
                          obj.addProperty("value", enumConstant.toString());
                        }));
              }
            } catch (ClassNotFoundException e) {
              // ignore
            }
          });
    }

    private static JsonObject getDefaultConfig(Slot slot, DomainObject domainObject) {
      JsonObject config = PersistentField.getDefaultConfig(slot, domainObject);
      config.addProperty("type", "Select");
      config.add("options", getSlotOptions(slot));
      return config;
    }
  }

  public static class DateTime extends DynamicForm.DateTime implements PersistentField {
    private Slot slot;

    public DateTime(int page, int section, JsonObject config, Field parent, DynamicForm form) {
      super(page, section, config, parent, form);
    }

    public DateTime(DomainObjectForm domainObjectForm, Slot slot) {
      super(0, 0, getDefaultConfig(slot, domainObjectForm.domainObject), null, domainObjectForm);
      this.slot = slot;
    }

    @Override
    public void save() {
      DomainObjectForm form = (DomainObjectForm) this.form;

      Method setMethod =
          DomainObjectUtils.getMethod(
              "set", form.domainObject, slot.getName(), org.joda.time.DateTime.class);

      if (setMethod == null) {
        throw new RuntimeException("Slot " + slot.getName() + " has no setter");
      }

      setMethod.setAccessible(true);

      try {
        setMethod.invoke(form.domainObject, org.joda.time.DateTime.parse(data.getAsString()));
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new Error(e);
      }
    }

    @Override
    public void addPersistedData(JsonObject data) {
      DomainObjectForm form = (DomainObjectForm) this.form;
      String slotValueString = DomainObjectUtils.getSlotValueString(form.domainObject, slot);
      data.addProperty(this.getName(), slotValueString);
    }

    private static JsonObject getDefaultConfig(Slot slot, DomainObject domainObject) {
      JsonObject config = PersistentField.getDefaultConfig(slot, domainObject);
      config.addProperty("type", "DateTime");
      config.addProperty("date", true);
      config.addProperty("time", true);
      return config;
    }
  }

  private static LocalizedString ls(final String pt, final String en) {
    return new LocalizedString(Locale.forLanguageTag("pt-PT"), pt)
        .with(Locale.forLanguageTag("en-GB"), en);
  }

  private static LocalizedString ls(final String str) {
    return ls(str, str);
  }
}
