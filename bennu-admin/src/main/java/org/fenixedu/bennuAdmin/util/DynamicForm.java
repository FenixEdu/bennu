package org.fenixedu.bennuAdmin.util;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.json.JsonUtils;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.io.domain.GenericFile;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.commons.stream.StreamUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import pt.ist.fenixframework.FenixFramework;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// This code is copied from fenixedu-admissions.
public class DynamicForm {

  private static String indent(final String str) {
    return str.replace("\n", "\n  ");
  }

  // bc LocalizedString won't let us initialize localized strings with empty content <3
  private static final Supplier<LocalizedString.Builder> LS_BUILDER =
      () -> {
        LocalizedString.Builder builder = new LocalizedString.Builder();
        for (final Locale locale : CoreConfiguration.supportedLocales()) {
          builder.with(locale, "\u200B"); // U+200B is a zero-width space
        }
        return builder;
      };

  private static final Supplier<LocalizedString> EMPTY_LS = () -> LS_BUILDER.get().build();

  protected final JsonObject form;
  protected JsonObject data;
  protected final Set<Field> fields = new LinkedHashSet<>();

  private boolean overwriteReadonly;

  protected DynamicForm() {
    this.form = new JsonObject();
  }

  public DynamicForm(final JsonObject form) {
    this.form = form;
    this.overwriteReadonly = false;
    if (form == null || !form.has("pages") || !form.get("pages").isJsonArray()) {
      return;
    }
    final JsonArray pages = form.get("pages").getAsJsonArray();
    for (int p = 0; p < pages.size(); p++) {
      final JsonObject page = pages.get(p).getAsJsonObject();
      if (!page.has("sections") || !page.get("sections").isJsonArray()) {
        continue;
      }
      final JsonArray sections = page.get("sections").getAsJsonArray();
      for (int s = 0; s < sections.size(); s++) {
        final JsonObject section = sections.get(s).getAsJsonObject();
        if (!section.has("properties") || !section.get("properties").isJsonArray()) {
          continue;
        }
        final JsonArray properties = section.get("properties").getAsJsonArray();
        for (final JsonElement field : properties) {
          if (field.isJsonObject()) {
            fields.add(Field.create(p, s, field.getAsJsonObject(), this));
          }
        }
      }
    }
  }

  public JsonObject getForm() {
    return form;
  }

  public DynamicForm withData(final JsonObject data) {
    this.data = data;

    this.fields.forEach(
        field -> {
          final String page = String.valueOf(field.page);
          final String section = String.valueOf(field.section);
          final JsonObject pageData =
              data != null && data.has(page) ? data.getAsJsonObject(page) : null;
          final JsonObject sectionData =
              pageData != null && pageData.has(section) ? pageData.getAsJsonObject(section) : null;
          final JsonElement fieldData =
              sectionData != null && sectionData.has(field.getName())
                  ? sectionData.get(field.getName())
                  : null;
          field.withData(fieldData);
        });
    return this;
  }

  public <T extends Field> Stream<T> filter(
      final Predicate<Field> predicate, final boolean shallow) {
    return shallow ? this.fields.stream().filter(predicate).map(f -> (T) f) : filter(predicate);
  }

  public <T extends Field> Stream<T> filter(final Predicate<Field> predicate) {
    return this.fields.stream().flatMap(f -> f.find(predicate)).map(f -> (T) f);
  }

  public <T extends Field> T get(final String field) {
    return get(field, false);
  }

  public <T extends Field> T get(final String field, final boolean shallow) {
    return get(f -> f.getName().equals(field), shallow);
  }

  public <T extends Field> T get(final Predicate<Field> predicate) {
    return get(predicate, false);
  }

  public <T extends Field> T get(final Predicate<Field> predicate, final boolean shallow) {
    return (T) filter(predicate, shallow).findFirst().orElse(null);
  }

  public Stream<Field> all() {
    return all(false);
  }

  public Stream<Field> all(final boolean shallow) {
    return filter(field -> true, shallow);
  }

  public Stream<Field> page(final int page) {
    return page(page, false);
  }

  public Stream<Field> page(final int page, final boolean shallow) {
    return filter(field -> field.page == page, shallow);
  }

  public Stream<Field> section(final int page, final int section) {
    return section(page, section, false);
  }

  public Stream<Field> section(final int page, final int section, final boolean shallow) {
    return filter(field -> field.page == page && field.section == section, shallow);
  }

  public <T extends Field> Optional<T> getFieldByPath(final String path) {
    final String[] pathParts = path.split("\\.", 4);
    if (pathParts.length < 3) {
      return Optional.empty();
    }
    final int page = Integer.parseInt(pathParts[0]);
    final int section = Integer.parseInt(pathParts[1]);

    return section(page, section, true)
        .filter(field -> field.getName().equals(pathParts[2]))
        .map(field -> field.getFieldByPath(pathParts.length > 3 ? pathParts[3] : null))
        .findFirst()
        .orElseGet(Optional::empty)
        .map(field -> (T) field);
  }

  public int pages() {
    return form == null || !form.has("pages") || !form.get("pages").isJsonArray()
        ? 0
        : form.getAsJsonArray("pages").size();
  }

  public int sections(final int page) {
    if (pages() == 0) {
      return 0;
    }
    final JsonElement p = form.getAsJsonArray("pages").get(page);
    if (p == null
        || !p.isJsonObject()
        || !p.getAsJsonObject().has("sections")
        || !p.getAsJsonObject().get("sections").isJsonArray()) {
      return 0;
    }
    return p.getAsJsonObject().getAsJsonArray("sections").size();
  }

  @Override
  public String toString() {
    return "DynamicForm {"
        + "fields="
        + indent("\n" + fields.stream().map(Field::toString).collect(Collectors.joining("\n")))
        + "\n}";
  }

  public LocalizedString toSummary() {
    final LocalizedString.Builder builder = LS_BUILDER.get();
    for (int p = 0; p < pages(); p++) {
      final JsonObject page = form.getAsJsonArray("pages").get(p).getAsJsonObject();
      builder
          .append(String.valueOf(p + 1))
          .append(". ")
          .append(LocalizedString.fromJson(page.getAsJsonObject("title")))
          .append("\n");
      for (int s = 0; s < sections(p); s++) {
        final JsonObject section = page.getAsJsonArray("sections").get(s).getAsJsonObject();
        builder
            .append(String.valueOf(p + 1))
            .append(".")
            .append(String.valueOf(s + 1))
            .append(". ")
            .append(LocalizedString.fromJson(section.getAsJsonObject("title")))
            .append("\n");
        section(p, s, true)
            .forEach(
                field -> {
                  builder.append(field.toLocalizedString().map(String::trim)).append("\n");
                });
      }
    }

    return builder.build();
  }

  public JsonObject toDataJson() {
    final JsonObject root = new JsonObject();
    int pageCount = pages();
    for (int p = 0; p < pageCount; p++) {
      final JsonObject page = new JsonObject();
      int sectionCount = sections(p);
      for (int s = 0; s < sectionCount; s++) {
        final JsonObject section = new JsonObject();
        section(p, s, true).forEach(field -> section.add(field.getName(), field.getData()));
        page.add(Integer.toString(s), section);
      }
      root.add(Integer.toString(p), page);
    }
    return root;
  }

  /**
   * Controls whether the value of readonly fields can be overwritten by <code>withData</code>
   * methods.
   */
  public DynamicForm overwriteReadonly(final boolean readonly) {
    this.overwriteReadonly = readonly;
    return this;
  }

  public boolean canOverwriteReadonly() {
    return this.overwriteReadonly;
  }

  public abstract static class Field implements LocalizedStringBuilder {
    public final JsonObject config;
    protected JsonElement data;
    public final int page;
    public final int section;
    public final Field parent;
    protected final DynamicForm form;

    Field(
        final int page,
        final int section,
        final JsonObject config,
        Field parent,
        final DynamicForm form) {
      this.page = page;
      this.section = section;
      this.config = config;
      this.parent = parent;
      this.form = form;
    }

    public JsonObject getConfig() {
      return this.config;
    }

    public JsonElement getData() {
      return this.data;
    }

    protected void setData(final JsonElement data) {
      this.data = data;
    }

    public Field withData(final JsonElement data) {
      if (!this.isReadonly() || this.form.canOverwriteReadonly()) {
        this.setData(data);
      }
      return this;
    }

    public Optional<Field> getFieldByPath(String path) {
      return Optional.of(this);
    }

    public String getName() {
      return this.config != null ? this.config.get("field").getAsString() : null;
    }

    public LocalizedString label() {
      return this.config != null ? LocalizedString.fromJson(this.config.get("label")) : null;
    }

    public String getPath() {
      if (this.parent == null) {
        return "" + this.page + "." + this.section + "." + this.getName();
      }
      return this.parent.getPath(this);
    }

    protected String getPath(final Field child) {
      return this.getPath() + "." + child.getName();
    }

    public <T extends Field> Stream<T> find(final Predicate<Field> predicate) {
      return predicate.test(this) ? Stream.of((T) this) : Stream.empty();
    }

    public static Field create(
        final int page, final int section, final JsonObject config, final DynamicForm form) {
      return create(page, section, config, null, form);
    }

    public static Field create(
        final int page,
        final int section,
        final JsonObject config,
        final Field parent,
        final DynamicForm form) {
      final String type = JsonUtils.getString(config, "type");

      switch (type) {
        case "Boolean":
          return new Boolean(page, section, config, parent, form);
        case "Text":
          return new Text(page, section, config, parent, form);
        case "Quantity":
          return new Quantity(page, section, config, parent, form);
        case "Numeric":
          return new Numeric(page, section, config, parent, form);
        case "Select":
          return new Select(page, section, config, parent, form);
        case "AsyncSelect":
          return new AsyncSelect(page, section, config, parent, form);
        case "DateTime":
          return new DateTime(page, section, config, parent, form);
        case "File":
          return new File(page, section, config, parent, form);
        case "Array":
          return new Array(page, section, config, parent, form);
        case "Composite":
          return new Composite(page, section, config, parent, form);
        case "LocalizedText":
          return new LocalizedText(page, section, config, parent, form);
        default:
          {
            throw new RuntimeException("Unsupported Dynamic Form field type: " + type);
          }
      }
    }

    public abstract <V> V value();

    protected LocalizedString empty() {
      return BundleUtil.getLocalizedString(
          "resources.BennuAdminResources", "DynamicForm.empty.value");
    }

    public boolean sensitive() {
      final JsonPrimitive sensitive = JsonUtils.getPrimitive(config, "sensitive");

      return (sensitive != null && sensitive.isBoolean() && sensitive.getAsBoolean())
          || (parent != null && parent.sensitive());
    }

    @Override
    public String toString() {
      return getClass().getSimpleName()
          + " {"
          + "page="
          + page
          + ", section="
          + section
          + ", name="
          + getName()
          + ", data="
          + (this.data != null ? data.toString() : "null")
          + '}';
    }

    abstract LocalizedString toLocalizedString();

    public LocalizedString displayValue() {
      if (value() != null) {
        return ls(value(), value());
      }
      return null;
    }

    public boolean isReadonly() {
      final JsonPrimitive readonly = JsonUtils.getPrimitive(config, "readonly");
      return (readonly != null && readonly.isBoolean() && readonly.getAsBoolean())
          || (parent != null && parent.isReadonly());
    }
  }

  public static class Boolean extends Field {
    Boolean(
        int page,
        int section,
        final JsonObject config,
        final Field parent,
        final DynamicForm form) {
      super(page, section, config, parent, form);
    }

    @Override
    LocalizedString toLocalizedString() {
      return new LocalizedString.Builder()
          .append(label())
          .append(": ")
          .append(
              value() == null
                  ? empty()
                  : value()
                      ? LocalizedString.fromJson(config.get("labelYes"))
                      : LocalizedString.fromJson(config.get("labelNo")))
          .build();
    }

    @Override
    public LocalizedString displayValue() {
      return valueLabel();
    }

    @Override
    public java.lang.Boolean value() {
      return getData() == null || getData().isJsonNull() ? null : getData().getAsBoolean();
    }

    public LocalizedString valueLabel() {
      return value() == null
          ? null
          : value()
              ? LocalizedString.fromJson(config.get("labelYes"))
              : LocalizedString.fromJson(config.get("labelNo"));
    }
  }

  public static class Text extends Field {
    Text(
        int page,
        int section,
        final JsonObject config,
        final Field parent,
        final DynamicForm form) {
      super(page, section, config, parent, form);
    }

    @Override
    LocalizedString toLocalizedString() {
      final LocalizedString.Builder builder =
          new LocalizedString.Builder().append(label()).append(": ");
      if (Strings.isNullOrEmpty(value())) {
        builder.append(empty());
      } else {
        builder.append(value());
      }
      return builder.build();
    }

    @Override
    public String value() {
      return getData() == null
              || getData().isJsonNull()
              || Strings.isNullOrEmpty(getData().getAsString())
          ? null
          : getData().getAsString();
    }
  }

  public static class LocalizedText extends Field {
    LocalizedText(
        int page,
        int section,
        final JsonObject config,
        final Field parent,
        final DynamicForm form) {
      super(page, section, config, parent, form);
    }

    @Override
    public LocalizedString value() {
      return getData() == null || getData().isJsonNull()
          ? null
          : LocalizedString.fromJson(getData());
    }

    @Override
    public LocalizedString displayValue() {
      return value();
    }

    public Set<Locale> locales() {
      return this.config.getAsJsonArray("locales").asList().stream()
          .map(JsonElement::getAsString)
          .map(Locale::forLanguageTag)
          .collect(Collectors.toSet());
    }

    @Override
    LocalizedString toLocalizedString() {
      final LocalizedString.Builder builder =
          new LocalizedString.Builder().append(label()).append(": ").append("\n");

      final LocalizedString.Builder translationsBuilder = LS_BUILDER.get();

      this.locales()
          .forEach(
              locale -> {
                translationsBuilder
                    .append(locale.getDisplayLanguage(I18N.getLocale()))
                    .append(": ")
                    .append(value().getContent(locale));
              });
      builder.append(translationsBuilder.build().map(DynamicForm::indent));
      return builder.build();
    }
  }

  public static class Quantity extends Field {
    Quantity(
        int page,
        int section,
        final JsonObject config,
        final Field parent,
        final DynamicForm form) {
      super(page, section, config, parent, form);
    }

    @Override
    LocalizedString toLocalizedString() {
      final LocalizedString.Builder builder =
          new LocalizedString.Builder().append(label()).append(": ");
      if (value() == null) {
        builder.append(empty());
      } else {
        builder.append(value().toPlainString());
      }
      return builder.build();
    }

    @Override
    public BigDecimal value() {
      return getData() == null || getData().isJsonNull() ? null : getData().getAsBigDecimal();
    }

    @Override
    public LocalizedString displayValue() {
      if (value() != null) {
        final String string = value().toPlainString();
        return ls(string, string);
      }
      return null;
    }
  }

  public static class Numeric extends Field {
    Numeric(
        int page,
        int section,
        final JsonObject config,
        final Field parent,
        final DynamicForm form) {
      super(page, section, config, parent, form);
    }

    @Override
    LocalizedString toLocalizedString() {
      LocalizedString.Builder builder = new LocalizedString.Builder().append(label()).append(": ");
      if (value() == null) {
        builder.append(empty());
      } else {
        builder.append(value());
      }
      return builder.build();
    }

    @Override
    public String value() {
      return getData() == null || getData().isJsonNull() ? null : getData().getAsString();
    }
  }

  public static class Select extends Field {
    Select(
        int page,
        int section,
        final JsonObject config,
        final Field parent,
        final DynamicForm form) {
      super(page, section, config, parent, form);
    }

    public boolean multiple() {
      final JsonPrimitive multiple = JsonUtils.getPrimitive(config, "multiple");
      return multiple != null && multiple.isBoolean() && multiple.getAsBoolean();
    }

    @Override
    LocalizedString toLocalizedString() {
      final LocalizedString.Builder builder =
          new LocalizedString.Builder().append(label()).append(": ");
      final Set<JsonObject> options = valueOptions();
      if (options == null) {
        builder.append(empty());
      } else {
        builder.append(
            options.stream()
                .map(
                    option -> {
                      final JsonElement label = option.get("label");
                      return label == null || label.isJsonNull()
                          ? EMPTY_LS.get().append(option.get("value").getAsString())
                          : LocalizedString.fromJson(label);
                    })
                .reduce(EMPTY_LS.get(), (joined, s) -> joined.append(", ").append(s))
                .map(s -> s.substring(3)) // remove leading zero-width-space+comma+space
            );
      }
      return builder.build();
    }

    @Override
    public LocalizedString displayValue() {
      return valueLabel().stream().reduce(new LocalizedString(), (l1, l2) -> l1.append(l2, ", "));
    }

    private Set<JsonObject> valueOptions() {
      return getData() == null || getData().isJsonNull()
          ? null
          : !multiple()
              ? Collections.singleton(getData().getAsJsonObject())
              : StreamUtils.of(getData().getAsJsonArray())
                  .map(JsonElement::getAsJsonObject)
                  .collect(Collectors.collectingAndThen(Collectors.toSet(), ImmutableSet::copyOf));
    }

    @Override
    public Set<String> value() {
      final Set<JsonObject> options = valueOptions();
      return options == null
          ? null
          : options.stream()
              .map(option -> option.get("value").getAsString())
              .collect(Collectors.collectingAndThen(Collectors.toSet(), ImmutableSet::copyOf));
    }

    public Set<LocalizedString> valueLabel() {
      final Set<JsonObject> options = valueOptions();
      return options == null
          ? null
          : options.stream()
              .map(option -> option.get("label"))
              .map(LocalizedString::fromJson)
              .collect(Collectors.collectingAndThen(Collectors.toSet(), ImmutableSet::copyOf));
    }
  }

  public static class AsyncSelect extends Field {
    AsyncSelect(
        int page,
        int section,
        final JsonObject config,
        final Field parent,
        final DynamicForm form) {
      super(page, section, config, parent, form);
    }

    @Override
    LocalizedString toLocalizedString() {
      final LocalizedString.Builder builder =
          new LocalizedString.Builder().append(label()).append(": ");
      if (value() == null) {
        builder.append(empty());
      } else {
        final JsonElement label = getData().getAsJsonObject().get("label");
        if (label == null || label.isJsonNull()) {
          builder.append(value());
        } else {
          builder.append(LocalizedString.fromJson(label));
        }
      }
      return builder.build();
    }

    @Override
    public LocalizedString displayValue() {
      return valueLabel();
    }

    @Override
    public String value() {
      return getData() == null || getData().isJsonNull()
          ? null
          : getData().getAsJsonObject().get("value").getAsString();
    }

    public LocalizedString valueLabel() {
      return value() == null
          ? null
          : LocalizedString.fromJson(getData().getAsJsonObject().get("label"));
    }
  }

  public static class DateTime extends Field {
    DateTime(
        int page,
        int section,
        final JsonObject config,
        final Field parent,
        final DynamicForm form) {
      super(page, section, config, parent, form);
    }

    public boolean hasTime() {
      final JsonPrimitive time = JsonUtils.getPrimitive(config, "time");
      return time != null && time.getAsBoolean();
    }

    @Override
    LocalizedString toLocalizedString() {
      final LocalizedString.Builder builder =
          new LocalizedString.Builder().append(label()).append(": ");
      if (value() == null) {
        builder.append(empty());
      } else if (hasTime()) {
        builder.append(format(ISODateTimeFormat.dateTimeNoMillis()));
      } else {
        builder.append(format(ISODateTimeFormat.date()));
      }
      return builder.build();
    }

    public String format(DateTimeFormatter formatter) {
      if (value() != null) {
        return value().toString(formatter);
      }
      return null;
    }

    @Override
    public LocalizedString displayValue() {
      return getData() == null || getData().isJsonNull()
          ? null
          : ls(
              locale ->
                  format(
                      hasTime()
                          ? ISODateTimeFormat.dateTimeNoMillis().withLocale(locale)
                          : ISODateTimeFormat.date().withLocale(locale)));
    }

    @Override
    public org.joda.time.DateTime value() {
      return getData() == null || getData().isJsonNull()
          ? null
          : hasTime()
              ? ISODateTimeFormat.dateTime().parseDateTime(getData().getAsString())
              : ISODateTimeFormat.date().parseDateTime(getData().getAsString());
    }
  }

  public static class Array extends Field {
    protected final List<Set<Field>> items = new ArrayList<>();

    Array(
        int page,
        int section,
        final JsonObject config,
        final Field parent,
        final DynamicForm form) {
      super(page, section, config, parent, form);
    }

    @Override
    protected void setData(final JsonElement data) {
      super.setData(data != null && data.isJsonArray() ? data : null);
      this.items.clear();
      if (data != null && data.isJsonArray()) {
        JsonArray items = data.getAsJsonArray();
        for (final JsonElement itemEl : items) {
          final JsonObject item = itemEl.getAsJsonObject();
          final Set<Field> itemFields =
              StreamUtils.of(config.getAsJsonArray("properties"))
                  .filter(field -> field instanceof JsonObject)
                  .map(JsonElement::getAsJsonObject)
                  .map(
                      field ->
                          Field.create(this.page, this.section, field, this, this.form)
                              .withData(item.get(JsonUtils.get(field, "field"))))
                  .collect(Collectors.toCollection(LinkedHashSet::new));
          this.items.add(itemFields);
        }
      }
    }

    @Override
    public JsonElement getData() {
      // Get data from items' fields to avoid stale data
      return this.items.stream()
          .map(
              item ->
                  JsonUtils.toJson(
                      itemData ->
                          item.forEach(
                              itemField -> itemData.add(itemField.getName(), itemField.getData()))))
          .collect(StreamUtils.toJsonArray());
    }

    @Override
    public String value() {
      throw new UnsupportedOperationException();
    }

    @Override
    public LocalizedString displayValue() {
      throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Field> Stream<T> find(Predicate<Field> predicate) {
      final Stream<Field> sub =
          this.items.stream()
              .flatMap(item -> item.stream().flatMap(field -> field.find(predicate)));
      return Stream.concat(super.find(predicate), sub).map(f -> (T) f);
    }

    public Stream<Set<Field>> findItem(Predicate<Set<Field>> predicate) {
      return this.items.stream().filter(predicate);
    }

    @Override
    protected String getPath(final Field child) {
      return super.getPath()
          + "."
          + IntStream.range(0, this.items.size())
              .filter(i -> this.items.get(i).contains(child))
              .findFirst()
              .getAsInt()
          + "."
          + child.getName();
    }

    @Override
    public Optional<Field> getFieldByPath(final String path) {
      if (path == null) {
        return Optional.of(this);
      }
      final String[] pathParts = path.split("\\.", 3);
      final int index = Integer.parseInt(pathParts[0]);
      if (index >= this.items.size()) {
        return Optional.empty();
      }
      return this.items.get(index).stream()
          .filter(field -> field.getName().equals(pathParts[1]))
          .map(field -> field.getFieldByPath(pathParts.length > 2 ? pathParts[2] : null))
          .findAny()
          .orElseGet(Optional::empty);
    }

    public List<Set<Field>> getItems() {
      return items;
    }

    @Override
    public String toString() {
      return "Array {"
          + "page="
          + page
          + ", section="
          + section
          + ", name="
          + getName()
          + ", items="
          + "\n"
          + items.stream()
              .map(
                  item ->
                      "- "
                          + indent(
                              item.stream().map(Field::toString).collect(Collectors.joining("\n")))
                          + "\n")
              .collect(Collectors.joining())
          + "}";
    }

    @Override
    LocalizedString toLocalizedString() {
      final LocalizedString.Builder builder =
          new LocalizedString.Builder().append(label()).append(": ");
      if (items.size() == 0) {
        return builder.append(empty()).build();
      }
      for (int i = 0; i < items.size(); i++) {
        builder.append("\n");
        builder
            .append(
                BundleUtil.getLocalizedString(
                    "resources.AdmissionsResources",
                    "DynamicForm.array.item.n",
                    String.valueOf(i + 1)))
            .append(":");
        final LocalizedString.Builder itemBuilder = LS_BUILDER.get();
        items.get(i).forEach(field -> itemBuilder.append("\n").append(field.toLocalizedString()));
        builder.append(itemBuilder.build().map(DynamicForm::indent));
      }
      return builder.build();
    }
  }

  public static class Composite extends Field {
    protected final Set<Field> fields = new LinkedHashSet<>();

    Composite(
        int page,
        int section,
        final JsonObject config,
        final Field parent,
        final DynamicForm form) {
      super(page, section, config, parent, form);
      if (config != null && config.has("properties") && config.get("properties").isJsonArray()) {
        final JsonArray properties = config.getAsJsonArray("properties");
        for (final JsonElement p : properties) {
          final JsonObject property = p.isJsonObject() ? p.getAsJsonObject() : null;
          if (property != null) {
            fields.add(Field.create(this.page, this.section, property, this, form));
          }
        }
      }
    }

    @Override
    protected void setData(JsonElement data) {
      super.setData(data != null && data.isJsonObject() ? data : null);
      if (data != null && data.isJsonObject()) {
        fields.forEach(
            field -> {
              field.withData(data.getAsJsonObject().get(field.config.get("field").getAsString()));
            });
      }
    }

    @Override
    public JsonElement getData() {
      // Get data from fields to avoid stale data
      return JsonUtils.toJson(
          itemData ->
              this.fields.forEach(
                  itemField -> itemData.add(itemField.getName(), itemField.getData())));
    }

    @Override
    public <T extends Field> Stream<T> find(Predicate<Field> predicate) {
      final Stream<Field> sub = this.fields.stream().flatMap(field -> field.find(predicate));
      return Stream.concat(super.find(predicate), sub).map(f -> (T) f);
    }

    @Override
    protected String getPath(final Field child) {
      return super.getPath() + "." + child.getName();
    }

    @Override
    public Optional<Field> getFieldByPath(String path) {
      if (path == null) {
        return Optional.of(this);
      }
      final String[] pathParts = path.split("\\.", 2);
      return this.fields.stream()
          .filter(field -> field.getName().equals(pathParts[0]))
          .map(field -> field.getFieldByPath(pathParts.length > 1 ? pathParts[1] : null))
          .findAny()
          .orElseGet(Optional::empty);
    }

    @Override
    public String value() {
      throw new UnsupportedOperationException();
    }

    @Override
    public LocalizedString displayValue() {
      throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
      return "Composite {"
          + "page="
          + page
          + ", section="
          + section
          + ", name="
          + getName()
          + ", properties="
          + indent("\n" + fields.stream().map(Field::toString).collect(Collectors.joining("\n")))
          + "\n}";
    }

    @Override
    LocalizedString toLocalizedString() {
      final LocalizedString.Builder builder = new LocalizedString.Builder();
      fields.forEach(field -> builder.append(field.toLocalizedString().append("\n")));
      return builder.build().map(String::trim);
    }
  }

  public static class File extends Field {
    File(
        int page,
        int section,
        final JsonObject config,
        final Field parent,
        final DynamicForm form) {
      super(page, section, config, parent, form);
    }

    public String getFileId() {
      return getData() == null || getData().isJsonNull()
          ? null
          : JsonUtils.get(getData().getAsJsonObject(), "id");
    }

    private GenericFile genericFile() {
      final String id = getFileId();
      return id == null ? null : FenixFramework.getDomainObject(id);
    }

    @Override
    public byte[] value() {
      final GenericFile genericFile = genericFile();
      return genericFile == null ? null : genericFile.getContent();
    }

    @Override
    public LocalizedString displayValue() {
      final String fileDetails = fileDetails();
      return fileDetails != null ? ls(fileDetails, fileDetails) : null;
    }

    @Override
    public String getName() {
      return this.data != null && this.data.isJsonObject() && data.getAsJsonObject().has("field")
          ? this.data.getAsJsonObject().get("field").getAsString()
          : super.getName();
    }

    private String fileDetails() {
      if (value() == null) {
        return null;
      } else {
        final GenericFile file = genericFile();
        file.ensureChecksum();
        return file.getFilename()
            + " ("
            + file.getSize().toString()
            + " bytes, checksum: "
            + file.getChecksum()
            + ")";
      }
    }

    @Override
    LocalizedString toLocalizedString() {
      final LocalizedString.Builder builder = new LocalizedString.Builder();
      builder.append(label()).append(": ");
      if (value() == null) {
        builder.append(empty());
      } else {
        builder.append(fileDetails());
      }
      return builder.build();
    }
  }
}
