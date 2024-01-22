package org.fenixedu.bennuAdmin.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.bennu.core.json.JsonUtils;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.commons.i18n.LocalizedString;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.dml.Modifier;
import pt.ist.fenixframework.dml.Slot;

import java.util.Comparator;
import java.util.Locale;
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
      // ignore
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

  public DynamicForm toDynamicForm() {
    JsonObject p =
        JsonUtils.toJson(
            page -> {
              page.add("title", ls("Cabeçalho", "Header").json());
              page.add("description", ls("Descrição", "Description").json());
              page.add(
                  "sections",
                  JsonUtils.toJsonArray(
                      sectionsArr -> {
                        JsonObject slotsSection =
                            JsonUtils.toJson(
                                sec -> {
                                  sec.add("title", ls("Slots", "Slots").json());
                                  sec.add("description", ls("Slots", "Slots").json());
                                  sec.add(
                                      "properties",
                                      JsonUtils.toJsonArray(
                                          propsArr -> {
                                            DomainObjectUtils.getDomainObjectSlots(domainObject)
                                                .stream()
                                                .sorted(Comparator.comparing(Slot::getName))
                                                .forEach(
                                                    slot -> {
                                                      propsArr.add(
                                                          JsonUtils.toJson(
                                                              prop -> {
                                                                prop.addProperty(
                                                                    "type", slotFieldType(slot));
                                                                prop.addProperty(
                                                                    "readonly",
                                                                    slot.hasModifier(
                                                                        Modifier.PROTECTED));
                                                                prop.addProperty(
                                                                    "field", slot.getName());
                                                                prop.addProperty(
                                                                    "required",
                                                                    false); // todo: check this
                                                                prop.add(
                                                                    "label",
                                                                    ls(slot.getName()).json());
                                                                prop.add(
                                                                    "description",
                                                                    ls(slot.getTypeName()).json());
                                                                if (slotFieldType(slot)
                                                                    .equals("LocalizedText")) {
                                                                  // todo: improve this
                                                                  prop.add(
                                                                      "locales",
                                                                      JsonUtils.parseJsonArray(
                                                                          "[\"pt-PT\",\"en-GB\"]"));
                                                                }

                                                                if (slotFieldType(slot)
                                                                    .equals("Select")) {
                                                                  prop.add(
                                                                      "options",
                                                                      getEnumSlotOptions(slot));
                                                                }

                                                                // todo: how should I handle this?
                                                                if (slotFieldType(slot)
                                                                    .equals("DateTime")) {
                                                                  prop.addProperty("date", true);
                                                                  prop.addProperty("time", true);
                                                                }

                                                                if (slotFieldType(slot)
                                                                    .equals("Boolean")) {
                                                                  prop.add(
                                                                      "labelYes",
                                                                      ls("Verdadeiro", "true")
                                                                          .json());
                                                                  prop.add(
                                                                      "labelNo",
                                                                      ls("Falso", "false").json());
                                                                }
                                                              }));
                                                    });
                                          }));
                                });

                        sectionsArr.add(slotsSection);
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

  public JsonObject getData() {
    JsonObject jsonData =
        JsonUtils.toJson(
            data -> {
              DomainObjectUtils.getDomainObjectSlots(domainObject)
                  .forEach(
                      slot -> {
                        String slotFieldType = slotFieldType(slot);
                        String slotValueString =
                            DomainObjectUtils.getSlotValueString(domainObject, slot);

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
            });

    return JsonUtils.toJson(p -> p.add("0", JsonUtils.toJson(s -> s.add("0", jsonData))));
  }
}
