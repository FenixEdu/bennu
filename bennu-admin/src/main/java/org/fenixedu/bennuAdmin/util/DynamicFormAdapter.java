package org.fenixedu.bennuAdmin.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.bennu.BennuAdminConfiguration;
import org.fenixedu.bennu.core.json.ImmutableJsonElement;
import org.fenixedu.bennu.core.json.JsonUtils;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.commons.stream.StreamUtils;
import org.joda.time.DateTime;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.dml.DomainClass;
import pt.ist.fenixframework.dml.Modifier;
import pt.ist.fenixframework.dml.Role;
import pt.ist.fenixframework.dml.Slot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;

public class DynamicFormAdapter {
  private static final Supplier<LocalizedString.Builder> LS_BUILDER =
      () -> {
        LocalizedString.Builder builder = new LocalizedString.Builder();
        for (final Locale locale : CoreConfiguration.supportedLocales()) {
          builder.with(locale, "\u200B"); // U+200B is a zero-width space
        }
        return builder;
      };

  private final DomainObject domainObject;

  public DynamicFormAdapter(DomainObject domainObject) {
    this.domainObject = domainObject;
  }

  private LocalizedString ls(final String pt, final String en) {
    return new LocalizedString(Locale.forLanguageTag("pt-PT"), pt)
        .with(Locale.forLanguageTag("en-GB"), en);
  }

  private LocalizedString ls(final String str) {
    return ls(str, str);
  }

  private String slotFieldType(Slot slot) {
    String type = slot.getTypeName().substring(slot.getTypeName().lastIndexOf(".") + 1);

    try {
      if (Class.forName(slot.getTypeName()).isEnum()) {
        return "Select";
      }
    } catch (ClassNotFoundException e) {
      // ignore: not an enum
    }

    return switch (type) {
      case "LocalizedString" -> "LocalizedText";
      case "Boolean", "boolean" -> "Boolean";
      case "DateTime" -> "DateTime";
      case "Integer" -> "Numeric";
      default -> "Text";
    };
  }

  private JsonArray getEnumSlotOptions(Slot slot) {
    return JsonUtils.toJsonArray(
        arr -> {
          try {
            Class<?> enumClass = Class.forName(slot.getTypeName());
            Object[] enumConstants = enumClass.getEnumConstants();
            for (Object enumConstant : enumConstants) {
              arr.add(
                  JsonUtils.toJson(
                      obj -> {
                        obj.add("label", ls(enumConstant.toString()).json());
                        obj.addProperty("value", enumConstant.toString());
                      }));
            }
          } catch (ClassNotFoundException e) {
            // ignore
          }
        });
  }

  private JsonArray getLocalizedTextSlotLocales(Slot slot) {
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

  private JsonObject slotToProperty(Slot slot) {
    return JsonUtils.toJson(
        prop -> {
          prop.addProperty("type", slotFieldType(slot));
          prop.addProperty("readonly", slot.hasModifier(Modifier.PROTECTED));
          prop.addProperty("field", slot.getName());
          prop.addProperty("required", false); // todo: check this
          prop.add("label", ls(slot.getName()).json());
          prop.add("description", ls(slot.getTypeName()).json());

          if (slotFieldType(slot).equals("LocalizedText")) {
            // todo: improve this
            prop.add("locales", getLocalizedTextSlotLocales(slot));
          }

          if (slotFieldType(slot).equals("Select")) {
            prop.add("options", getEnumSlotOptions(slot));
          }

          // todo: how should I handle this?
          if (slotFieldType(slot).equals("DateTime")) {
            prop.addProperty("date", true);
            prop.addProperty("time", true);
          }

          if (slotFieldType(slot).equals("Boolean")) {
            prop.add("labelYes", ls("Verdadeiro", "true").json());
            prop.add("labelNo", ls("Falso", "false").json());
          }
        });
  }

  private String domainProviderUrl(Role role) {
    return CoreConfiguration.getConfiguration().applicationUrl()
        + "/bennu-admin/domain-objects/"
        + domainObject.getExternalId()
        + "/roles/"
        + role.getName();
  }

  private JsonObject roleToProperty(Role role) {
    return JsonUtils.toJson(
        prop -> {
          prop.addProperty("type", "AsyncSelect");
          prop.addProperty("readonly", role.hasModifier(Modifier.PROTECTED));
          prop.addProperty("optionsProvider", domainProviderUrl(role));
          prop.addProperty("field", role.getName());
          prop.addProperty("required", false); // todo: check this
          prop.add("label", ls(role.getName()).json());
          prop.add("description", ls(role.getType().getName()).json());
        });
  }

  public DynamicForm toDynamicForm() {
    JsonObject slotsSection =
        JsonUtils.toJson(
            sec -> {
              sec.add("title", ls("Slots").json());
              sec.add(
                  "properties",
                  JsonUtils.toJsonArray(
                      propsArr -> {
                        DomainObjectUtils.getDomainObjectSlots(domainObject).stream()
                            .sorted(Comparator.comparing(Slot::getName))
                            .forEach(slot -> propsArr.add(slotToProperty(slot)));
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
                        DomainClass domainClass =
                            FenixFramework.getDomainModel()
                                .findClass(domainObject.getClass().getName());
                        DomainObjectUtils.getRoles(domainClass, true).stream()
                            .sorted(Comparator.comparing(Role::getName))
                            .forEach(role -> propsArr.add(roleToProperty(role)));
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

    return new DynamicForm(
        JsonUtils.toJson(
            form -> {
              form.add(
                  "pages",
                  JsonUtils.toJsonArray(
                      pagesArr -> {
                        pagesArr.add(p);
                      }));
            }));
  }

  private void getSlotData(JsonObject data) {
    DomainObjectUtils.getDomainObjectSlots(domainObject)
        .forEach(
            slot -> {
              String slotFieldType = slotFieldType(slot);
              String slotValueString = DomainObjectUtils.getSlotValueString(domainObject, slot);

              switch (slotFieldType) {
                case "LocalizedText", "Boolean" -> {
                  data.add(slot.getName(), JsonUtils.parseJsonElement(slotValueString));
                }
                case "Select" -> {
                  data.add(
                      slot.getName(),
                      JsonUtils.toJson(
                          j -> {
                            j.add("label", ls(slotValueString).json());
                            j.addProperty("value", slotValueString);
                          }));
                }
                default -> data.addProperty(slot.getName(), slotValueString);
              }
            });
  }

  private void getRolesData(JsonObject data) {
    DomainClass domainClass =
        FenixFramework.getDomainModel().findClass(domainObject.getClass().getName());

    DomainObjectUtils.getRoles(domainClass, true)
        .forEach(
            role -> {
              String roleValueString = DomainObjectUtils.getRelationSlot(domainObject, role);

              data.add(
                  role.getName(),
                  JsonUtils.toJson(
                      j -> {
                        j.add("label", ls(roleValueString).json());
                        j.addProperty("value", roleValueString);
                      }));
            });
  }

  public JsonObject getData() {
    JsonObject jsonData =
        JsonUtils.toJson(
            j -> {
              getSlotData(j);
              getRolesData(j);
            });

    return JsonUtils.toJson(p -> p.add("0", JsonUtils.toJson(s -> s.add("0", jsonData))));
  }

  @Atomic(mode = Atomic.TxMode.WRITE) // We must pass the mode to avoid transaction error
  public void setData(JsonObject data) throws RuntimeException {
    JsonObject slotData = data.getAsJsonObject("0").getAsJsonObject("0");
    Set<Slot> slots = DomainObjectUtils.getDomainObjectSlots(domainObject);

    slotData
        .asMap()
        .forEach(
            (slotName, slotValue) -> {
              Slot slot =
                  slots.stream().filter(s -> s.getName().equals(slotName)).findFirst().orElse(null);

              if (slot == null) {
                throw new RuntimeException("Slot " + slotName + " not found");
              }

              String slotFieldType = slotFieldType(slot);

              try {
                switch (slotFieldType) {
                  case "LocalizedText" -> {
                    setLocalizedTextSlot(slot, slotValue);
                  }
                  case "Select" -> {
                    setSelectSlot(slot, slotValue);
                  }
                  case "Boolean" -> {
                    setBooleanSlot(slot, slotValue);
                  }
                  case "DateTime" -> {
                    setDateTimeSlot(slot, slotValue);
                  }
                  default -> {
                    setTextSlot(slot, slotValue);
                  }
                }
              } catch (IllegalAccessException
                  | ClassNotFoundException
                  | InvocationTargetException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
              }
            });
  }

  private void setLocalizedTextSlot(Slot slot, JsonElement data)
      throws IllegalAccessException, InvocationTargetException {
    Method setMethod =
        DomainObjectUtils.getMethod("set", domainObject, slot.getName(), LocalizedString.class);

    if (setMethod == null) {
      throw new RuntimeException("Slot " + slot.getName() + " has no setter");
    }

    setMethod.setAccessible(true);

    setMethod.invoke(domainObject, LocalizedString.fromJson(data));
  }

  private void setSelectSlot(Slot slot, JsonElement data)
      throws IllegalAccessException, InvocationTargetException, ClassNotFoundException {

    Class<?> enumClass = Class.forName(slot.getTypeName());
    if (!enumClass.isEnum()) {
      throw new RuntimeException(
          "Slot " + slot.getName() + " is not an enum, but it's being treated as one");
    }

    Method setMethod = DomainObjectUtils.getMethod("set", domainObject, slot.getName(), enumClass);

    if (setMethod == null) {
      throw new RuntimeException("Slot " + slot.getName() + " has no setter");
    }

    setMethod.setAccessible(true);

    Enum<?> enumValue =
        Enum.valueOf((Class<Enum>) enumClass, data.getAsJsonObject().get("value").getAsString());

    setMethod.invoke(domainObject, enumValue);
  }

  private void setBooleanSlot(Slot slot, JsonElement data)
      throws IllegalAccessException, InvocationTargetException {
    Method setMethod =
        DomainObjectUtils.getMethod("set", domainObject, slot.getName(), boolean.class);

    if (setMethod == null) {
      throw new RuntimeException("Slot " + slot.getName() + " has no setter");
    }

    setMethod.setAccessible(true);

    setMethod.invoke(domainObject, data.getAsBoolean());
  }

  private void setDateTimeSlot(Slot slot, JsonElement data)
      throws IllegalAccessException, InvocationTargetException {
    Method setMethod =
        DomainObjectUtils.getMethod("set", domainObject, slot.getName(), DateTime.class);

    if (setMethod == null) {
      throw new RuntimeException("Slot " + slot.getName() + " has no setter");
    }

    setMethod.setAccessible(true);

    setMethod.invoke(domainObject, DateTime.parse(data.getAsString()));
  }

  private void setTextSlot(Slot slot, JsonElement data)
      throws IllegalAccessException, InvocationTargetException, ClassNotFoundException {
    String slotType = slot.getTypeName();

    // ImmutableJsonElement<com.google.gson.JsonObject> causes ClassNotFoundException
    if (slotType.contains("<")) {
      // removes type annotation
      slotType = slotType.split("<")[0];
    }

    Method method =
        DomainObjectUtils.getMethod("set", domainObject, slot.getName(), Class.forName(slotType));

    if (method == null) {
      throw new RuntimeException("Slot " + slot.getName() + " has no setter");
    }

    method.setAccessible(true);

    switch (slotType) {
      case "org.fenixedu.bennu.core.json.ImmutableJsonElement" -> {
        method.invoke(domainObject, ImmutableJsonElement.of(data));
      }
      case "com.google.gson.JsonElement" -> {
        method.invoke(domainObject, data);
      }
      default -> {
        method.invoke(domainObject, data.getAsString());
      }
    }
  }
}
