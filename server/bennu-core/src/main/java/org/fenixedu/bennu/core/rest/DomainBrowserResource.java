package org.fenixedu.bennu.core.rest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.ValueTypeSerializer;
import pt.ist.fenixframework.core.AbstractDomainObject;
import pt.ist.fenixframework.dml.DomainClass;
import pt.ist.fenixframework.dml.DomainEntity;
import pt.ist.fenixframework.dml.ModifiableEntity;
import pt.ist.fenixframework.dml.Modifier;
import pt.ist.fenixframework.dml.Role;
import pt.ist.fenixframework.dml.Slot;
import pt.ist.fenixframework.dml.ValueType;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@Path("/bennu-core/domain-browser")
public class DomainBrowserResource extends BennuRestResource {

    @GET
    @Path("/{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response viewObject(@PathParam("oid") String oid) {
        accessControl("#managers");
        if (oid == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        DomainObject obj = FenixFramework.getDomainObject(oid);
        if (FenixFramework.isDomainObjectValid(obj)) {
            return Response.ok(describeObject(obj)).build();
        } else {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/{oid}/{role}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response showRelation(@PathParam("oid") String oid, @PathParam("role") String role) {
        accessControl("#managers");
        if (oid == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        DomainObject obj = FenixFramework.getDomainObject(oid);
        if (FenixFramework.isDomainObjectValid(obj)) {
            return describeRelation(obj, role);
        } else {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    private Response describeRelation(DomainObject obj, String role) {
        DomainClass domClass = FenixFramework.getDomainModel().findClass(obj.getClass().getName());
        Role roleSlot = domClass.findRoleSlot(role);
        if (roleSlot == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        Set<DomainObject> objects = getRelationSet(obj, roleSlot);
        if (objects == null) {
            return Response.serverError().build();
        }
        JsonArray array = new JsonArray();
        for (DomainObject domainObject : objects) {
            JsonObject json = new JsonObject();
            json.addProperty("type", domainObject.getClass().getName());
            json.addProperty("oid", domainObject.getExternalId());
            array.add(json);
        }

        return Response.ok(array.toString()).build();
    }

    private String describeObject(DomainObject obj) {
        JsonObject json = new JsonObject();

        json.addProperty("oid", obj.getExternalId());
        json.addProperty("type", obj.getClass().getName());

        DomainClass domClass = FenixFramework.getDomainModel().findClass(obj.getClass().getName());

        json.add("modifiers", modifiers(domClass));

        DomainEntity superclass = domClass.getSuperclass();
        if (superclass != null) {
            json.addProperty("superclass", superclass.getFullName());
        }

        JsonArray slots = new JsonArray();
        for (DomainClass dc = domClass; dc != null; dc = (DomainClass) dc.getSuperclass()) {
            for (final Slot slot : dc.getSlotsList()) {
                JsonObject slotJson = new JsonObject();
                slotJson.addProperty("name", slot.getName());
                slotJson.addProperty("type", slot.getSlotType().getFullname());
                Object value = getSlotValue(obj, slot);
                if (value instanceof byte[]) {
                    value = Base64.getEncoder().encodeToString((byte[]) value);
                }
                slotJson.addProperty("value", Objects.toString(value, null));
                slotJson.add("modifiers", modifiers(slot));
                slots.add(slotJson);
            }
        }

        json.add("slots", slots);

        JsonArray relationSlots = new JsonArray();
        for (Role role : getRoles(domClass, true)) {
            JsonObject roleJson = new JsonObject();

            roleJson.addProperty("name", role.getName());
            roleJson.addProperty("type", role.getType().getFullName());
            roleJson.addProperty("value", getRelationSlot(obj, role));
            roleJson.add("modifiers", modifiers(role));

            relationSlots.add(roleJson);
        }
        json.add("relationSlots", relationSlots);

        JsonArray relationSets = new JsonArray();
        for (Role role : getRoles(domClass, false)) {
            JsonObject roleJson = new JsonObject();

            roleJson.addProperty("name", role.getName());
            roleJson.addProperty("type", role.getType().getFullName());
            roleJson.add("modifiers", modifiers(role));

            relationSets.add(roleJson);
        }
        json.add("relationSets", relationSets);

        return json.toString();
    }

    private JsonElement modifiers(ModifiableEntity entity) {
        JsonArray array = new JsonArray();
        for (Modifier modifier : entity.getModifiers()) {
            array.add(new JsonPrimitive(modifier.name()));
        }
        return array;
    }

    private String getRelationSlot(final DomainObject domainObject, final Role role) {
        final Method method = getMethod(domainObject, role.getName());
        if (method != null) {
            try {
                DomainObject obj = (DomainObject) method.invoke(domainObject);
                return obj == null ? null : obj.getExternalId();
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                return "!!EXCEPTION!!";
            }
        }
        return null;
    }

    private Object getSlotValue(final DomainObject domainObject, final Slot slot) {
        final Method method = getMethod(domainObject, slot.getName());
        if (method != null) {
            try {
                Object value = method.invoke(domainObject);
                ValueType vt = slot.getSlotType();
                if (vt.isBuiltin() || vt.isEnum() || value == null) {
                    return value;
                }
                return getPrimitiveValueFor(vt, value);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                return "!!EXCEPTION!!";
            }
        }
        return null;
    }

    private Object getPrimitiveValueFor(ValueType vt, Object value) {
        try {
            Method method =
                    ValueTypeSerializer.class.getDeclaredMethod("serialize$" + vt.getDomainName().replace('.', '$'),
                            Class.forName(vt.getFullname()));
            return method.invoke(null, value);
        } catch (Exception e) {
            return null;
        }
    }

    protected Method getMethod(final DomainObject domainObject, final String slotName) {
        final String methodName = "get" + Character.toUpperCase(slotName.charAt(0)) + slotName.substring(1);
        if (domainObject != null && methodName != null && !methodName.isEmpty()) {
            Class<?> clazz = domainObject.getClass();
            while (clazz != AbstractDomainObject.class) {
                try {
                    Method method = clazz.getDeclaredMethod(methodName);
                    method.setAccessible(true);
                    return method;
                } catch (NoSuchMethodException | SecurityException e) {
                }
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Set<DomainObject> getRelationSet(final DomainObject domainObject, final Role role) {
        final Method method = getMethod(domainObject, role.getName());
        if (method != null) {
            try {
                return (Set<DomainObject>) method.invoke(domainObject);
            } catch (final Exception e) {
                return null;
            }
        }
        return null;
    }

    private Set<Role> getRoles(final DomainClass domainClass, boolean one) {
        final Set<Role> result = new HashSet<Role>();
        for (DomainClass dc = domainClass; dc != null; dc = (DomainClass) dc.getSuperclass()) {
            for (final Role role : dc.getRoleSlotsList()) {
                //Roles without name are unidirectional and should be skipped
                if (role.getName() == null) {
                    continue;
                }
                if (one ? (role.getMultiplicityUpper() == 1) : (role.getMultiplicityUpper() != 1)) {
                    result.add(role);
                }
            }
        }
        return result;
    }

}
