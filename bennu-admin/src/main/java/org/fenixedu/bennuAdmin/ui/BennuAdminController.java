package org.fenixedu.bennuAdmin.ui;

import com.google.gson.JsonObject;
import org.apache.http.protocol.HTTP;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.json.ImmutableJsonElement;
import org.fenixedu.bennu.core.json.JsonUtils;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.security.SkipCSRF;
import org.fenixedu.bennuAdmin.util.*;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.connect.ui.AccountController;
import org.fenixedu.connect.ui.BaseController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.dml.DomainClass;
import pt.ist.fenixframework.dml.Role;
import pt.ist.fenixframework.dml.Slot;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.fenixedu.bennu.spring.BaseController.respond;
import static org.fenixedu.bennu.spring.BaseController.respondPagination;

@RestController
@RequestMapping("/bennu-admin")
public class BennuAdminController {
  private static final String ACCESS_TOKEN_COOKIE = AccountController.ACCESS_TOKEN_COOKIE;

  @ExceptionHandler({BennuAdminError.class})
  public ResponseEntity<?> handleConnectError(final BennuAdminError error) {
    return ResponseEntity.status(error.status).body(error.getMessage());
  }

  public static <T> ResponseEntity<?> ok(final BiConsumer<JsonObject, T> consumer, final T object) {
    return BaseController.ok(data -> consumer.accept(data, object));
  }

  public static <T> ResponseEntity<?> ok(
      final BiConsumer<JsonObject, T> consumer, final Stream<T> stream) {
    return ok(consumer, stream.collect(Collectors.toSet()));
  }

  public static <T> ResponseEntity<?> ok(
      final BiConsumer<JsonObject, T> consumer, final Set<T> set) {
    return BaseController.ok(Schema.toJson(consumer, set));
  }

  private void requireGroup(Group group) {
    if (!group.isMember(Authenticate.getUser())) {
      throw new BennuAdminError(HttpStatus.NOT_FOUND, "error.notFound");
    }
  }

  @RequestMapping(value = "/domain-objects/classes", method = RequestMethod.GET)
  public ResponseEntity<?> listDomainClasses(
      final @RequestParam(required = false) String query,
      final @RequestParam(required = false) Long skip,
      final @RequestParam(required = false) Long limit) {
    requireGroup(Group.managers());
    Stream<DomainClass> classes = FenixFramework.getDomainModel().getDomainClasses().stream();
    return search(
        query,
        skip,
        limit,
        classes,
        (domainClass) -> getLocalizedString(domainClass.getFullName()),
        Comparator.comparing(DomainClass::getFullName),
        Schema.DOMAIN_CLASS);
  }

  @RequestMapping(value = "/domain-objects/{objectId}", method = RequestMethod.GET)
  public ResponseEntity<?> getDomainObject(final @PathVariable String objectId) {
    requireGroup(Group.managers());
    DomainObject domainObject = FenixFramework.getDomainObject(objectId);
    if (!FenixFramework.isDomainObjectValid(domainObject)) {
      throw new BennuAdminError(HttpStatus.NOT_FOUND, "error.notFound");
    }
    return ok(Schema.DOMAIN_OBJECT, domainObject);
  }

  @RequestMapping(value = "/domain-objects/{objectId}/meta", method = RequestMethod.GET)
  public ResponseEntity<?> getDomainObjectMeta(final @PathVariable String objectId) {
    requireGroup(Group.managers());
    DomainObject domainObject = FenixFramework.getDomainObject(objectId);
    if (!FenixFramework.isDomainObjectValid(domainObject)) {
      throw new BennuAdminError(HttpStatus.NOT_FOUND, "error.notFound");
    }
    return ok(Schema.DOMAIN_OBJECT_META, domainObject);
  }

  @SkipCSRF
  @RequestMapping(value = "/domain-objects/{objectId}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteDomainObject(final @PathVariable String objectId) {
    requireGroup(Group.managers());

    DomainObject domainObject = FenixFramework.getDomainObject(objectId);
    if (!FenixFramework.isDomainObjectValid(domainObject)) {
      throw new BennuAdminError(HttpStatus.NOT_FOUND, "error.notFound");
    }

    try {
      DomainObjectUtils.deleteObject(domainObject);
      return ResponseEntity.status(HttpStatus.OK).build();
    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException error) {
      // When error is IllegalAccessException, the real error is in the target property, not in the
      // error itself
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  @RequestMapping(value = "/domain-objects/{objectId}/form", method = RequestMethod.GET)
  public ResponseEntity<?> domainObjectForm(final @PathVariable String objectId) {
    requireGroup(Group.managers());

    DomainObject domainObject = FenixFramework.getDomainObject(objectId);
    if (!FenixFramework.isDomainObjectValid(domainObject)) {
      throw new BennuAdminError(HttpStatus.NOT_FOUND, "error.notFound");
    }

    return ok(Schema.DOMAIN_OBJECT_FORM, new DomainObjectForm(domainObject));
  }

  @SkipCSRF
  @RequestMapping(value = "/domain-objects/{objectId}/edit", method = RequestMethod.POST)
  public ResponseEntity<?> editDomainObject(
      final @PathVariable String objectId, final @RequestBody String data) {
    requireGroup(Group.managers());

    DomainObject domainObject = FenixFramework.getDomainObject(objectId);
    if (!FenixFramework.isDomainObjectValid(domainObject)) {
      throw new BennuAdminError(HttpStatus.NOT_FOUND, "error.notFound");
    }

    DomainObjectForm dynamicForm =
        new DomainObjectForm(domainObject).withData(JsonUtils.parse(data));

    dynamicForm.save();

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @RequestMapping(value = "/domain-objects/{objectId}/slots", method = RequestMethod.GET)
  public ResponseEntity<?> domainObjectSlots(
      final @PathVariable String objectId,
      final @RequestParam(required = false) String query,
      final @RequestParam(required = false) Long skip,
      final @RequestParam(required = false) Long limit) {
    requireGroup(Group.managers());

    DomainObject domainObject = FenixFramework.getDomainObject(objectId);
    if (!FenixFramework.isDomainObjectValid(domainObject)) {
      throw new BennuAdminError(HttpStatus.NOT_FOUND, "error.notFound");
    }

    ImmutableJsonElement<com.google.gson.JsonObject> testObject =
        new ImmutableJsonElement<>(JsonUtils.parse("{ \"hello\": \"world\" }"));

    Set<Slot> slots = DomainObjectUtils.getDomainObjectSlots(domainObject);

    return search(
        query,
        skip,
        limit,
        slots.stream(),
        (slot) -> getLocalizedString(slotQueryString(slot, domainObject)),
        Comparator.comparing(Slot::getName),
        Schema.DOMAIN_OBJECT_SLOT(domainObject));
  }

  @RequestMapping(value = "/domain-objects/{objectId}/roles", method = RequestMethod.GET)
  public ResponseEntity<?> domainObjectRoles(
      final @PathVariable String objectId,
      final @RequestParam(required = false) String query,
      final @RequestParam(required = false) Long skip,
      final @RequestParam(required = false) Long limit) {
    requireGroup(Group.managers());

    DomainObject domainObject = FenixFramework.getDomainObject(objectId);
    if (!FenixFramework.isDomainObjectValid(domainObject)) {
      throw new BennuAdminError(HttpStatus.NOT_FOUND, "error.notFound");
    }

    DomainClass domClass =
        FenixFramework.getDomainModel().findClass(domainObject.getClass().getName());

    Set<Role> roles = DomainObjectUtils.getRoles(domClass, DomainObjectUtils.Multiplicity.ONE);

    return search(
        query,
        skip,
        limit,
        roles.stream(),
        (role) -> getLocalizedString(roleQueryString(role, domainObject)),
        Comparator.comparing(Role::getName),
        Schema.DOMAIN_OBJECT_ROLE(domainObject));
  }

  @RequestMapping(value = "/domain-objects/{objectId}/role-sets", method = RequestMethod.GET)
  public ResponseEntity<?> domainObjectRoleSets(
      final @PathVariable String objectId,
      final @RequestParam(required = false) String query,
      final @RequestParam(required = false) Long skip,
      final @RequestParam(required = false) Long limit) {
    requireGroup(Group.managers());

    DomainObject domainObject = FenixFramework.getDomainObject(objectId);
    if (!FenixFramework.isDomainObjectValid(domainObject)) {
      throw new BennuAdminError(HttpStatus.NOT_FOUND, "error.notFound");
    }

    DomainClass domClass =
        FenixFramework.getDomainModel().findClass(domainObject.getClass().getName());

    Set<Role> roles = DomainObjectUtils.getRoles(domClass, DomainObjectUtils.Multiplicity.MANY);

    return search(
        query,
        skip,
        limit,
        roles.stream(),
        (role) -> getLocalizedString(roleSetQueryString(role)),
        Comparator.comparing(Role::getName),
        Schema.DOMAIN_OBJECT_ROLE_SET);
  }

  @RequestMapping(
      value = "/domain-objects/{objectId}/role-sets/{roleName}/count",
      method = RequestMethod.GET)
  public ResponseEntity<?> domainObjectRoleSetCount(
      final @PathVariable String objectId, final @PathVariable String roleName) {
    requireGroup(Group.managers());

    DomainObject domainObject = FenixFramework.getDomainObject(objectId);
    if (!FenixFramework.isDomainObjectValid(domainObject)) {
      throw new BennuAdminError(HttpStatus.NOT_FOUND, "error.not-found");
    }

    DomainClass domClass =
        FenixFramework.getDomainModel().findClass(domainObject.getClass().getName());

    Role roleSlot = domClass.findRoleSlot(roleName);
    Set<DomainObject> objects = DomainObjectUtils.getRelationSet(domainObject, roleSlot);

    return ok(Schema.DOMAIN_OBJECT_ROLE_SET_COUNT, objects);
  }

  @RequestMapping(
      value = "/domain-objects/{objectId}/role-sets/{roleName}",
      method = RequestMethod.GET)
  public ResponseEntity<?> domainObjectRoleSet(
      final @PathVariable String objectId,
      final @PathVariable String roleName,
      final @RequestParam(required = false) String query,
      final @RequestParam(required = false) Long skip,
      final @RequestParam(required = false) Long limit) {
    requireGroup(Group.managers());

    DomainObject domainObject = FenixFramework.getDomainObject(objectId);
    if (!FenixFramework.isDomainObjectValid(domainObject)) {
      throw new BennuAdminError(HttpStatus.NOT_FOUND, "error.not-found");
    }

    DomainClass domClass =
        FenixFramework.getDomainModel().findClass(domainObject.getClass().getName());
    Role roleSlot = domClass.findRoleSlot(roleName);
    Set<DomainObject> objects = DomainObjectUtils.getRelationSet(domainObject, roleSlot);

    return search(
        query,
        skip,
        limit,
        objects != null ? objects.stream() : Stream.<DomainObject>builder().build(),
        (object) -> getLocalizedString(objectQueryString(object)),
        Comparator.comparing(DomainObject::getExternalId),
        Schema.DOMAIN_OBJECT);
  }

  // Todo: see if this endpoint will be used
  @RequestMapping("/domain-objects/{objectId}/roles/{roleName}")
  public ResponseEntity<?> domainObjectProvider(
      final @PathVariable String objectId,
      final @PathVariable String roleName,
      final @RequestParam(required = false) String query) {
    requireGroup(Group.managers());

    DomainObject domainObject = FenixFramework.getDomainObject(objectId);
    if (!FenixFramework.isDomainObjectValid(domainObject)) {
      throw new BennuAdminError(HttpStatus.NOT_FOUND, "error.not-found");
    }

    DomainClass domClass =
        FenixFramework.getDomainModel().findClass(domainObject.getClass().getName());

    Role role = domClass.findRoleSlot(roleName);

    if (role == null) {
      throw new BennuAdminError(HttpStatus.NOT_FOUND, "error.not-found");
    }

    if (role.getMultiplicityUpper() != 1) {
      throw new BennuAdminError(HttpStatus.BAD_REQUEST, "error.invalid-role");
    }

    // todo: find all objects of type role.getType()
    Set<DomainObject> domainObjects = Collections.EMPTY_SET;

    return search(
        query,
        null,
        null,
        domainObjects.stream(),
        (object) -> getLocalizedString(object.getExternalId()),
        Comparator.comparing(DomainObject::getExternalId),
        Schema.DOMAIN_OBJECT);
  }

  private String objectQueryString(final DomainObject domainObject) {
    // Todo: get some information about the object (Include slot values?)
    return String.format("%s %s", domainObject.getExternalId(), domainObject.getClass().getName());
  }

  private String slotQueryString(final Slot slot, final DomainObject domainObject) {
    return String.format(
        "%s %s %s",
        slot.getName(),
        slot.getSlotType().getFullname(),
        DomainObjectUtils.getSlotValueString(domainObject, slot));
  }

  private String roleQueryString(final Role role, final DomainObject domainObject) {
    return String.format(
        "%s %s %s",
        role.getName(),
        role.getType().getFullName(),
        DomainObjectUtils.getRelationSlot(domainObject, role));
  }

  private String roleSetQueryString(final Role role) {
    return String.format("%s %s", role.getName(), role.getType().getFullName());
  }

  private <T> boolean match(
      final Function<T, LocalizedString> toString, final T t, final String[] tokens) {
    return tokens == null || tokens.length == 0 || match(toString.apply(t), tokens);
  }

  private String normalize(final String string) {
    return string == null ? null : StringNormalizer.normalizeAndRemoveAccents(string).toLowerCase();
  }

  private boolean match(final LocalizedString localizedString, final String[] tokens) {
    final String string = localizedString == null ? null : normalize(localizedString.getContent());
    return string != null && Arrays.stream(tokens).allMatch(string::contains);
  }

  public <T> ResponseEntity<?> paginate(
      final Long skip,
      final Long limit,
      final Stream<T> stream,
      final Comparator<T> comparator,
      final BiConsumer<JsonObject, T> consumer) {
    final List<T> list = stream.sorted(comparator).collect(Collectors.toList());
    return skip == null || limit == null
        ? respond(list.stream().map(t -> Schema.toJson(consumer, t)))
        : respondPagination(skip, limit, list, t -> Schema.toJson(consumer, t));
  }

  public <T> ResponseEntity<?> search(
      final String query,
      final Long skip,
      final Long limit,
      final Stream<T> stream,
      final Function<T, LocalizedString> toString,
      final Comparator<T> comparator,
      final BiConsumer<JsonObject, T> consumer) {
    final String normalizedQuery = normalize(query);
    final String[] tokens = normalizedQuery == null ? null : normalizedQuery.split(" ");
    final List<T> list =
        stream
            .filter(t -> match(toString, t, tokens))
            .sorted(comparator)
            .collect(Collectors.toList());
    return skip == null || limit == null
        ? respond(list.stream().map(t -> Schema.toJson(consumer, t)))
        : respondPagination(skip, limit, list, t -> Schema.toJson(consumer, t));
  }

  private LocalizedString getLocalizedString(final String content) {
    return new LocalizedString(Locale.forLanguageTag("pt-PT"), content)
        .with(Locale.forLanguageTag("en-US"), content);
  }
}
