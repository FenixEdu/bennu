# [Upgrading 3.x to 4.0](./upgrading3x-40.md)


+ What's new?
+ Migrating your instance
+ Migrating your theme
    + Escaping
    +  Managing assets
+ Migrating your code
    + Bennu Spring and Bennu OAuth
    + JsonAwareResource endpoints
    + CSRF
    + Bennu Toolkit
    + Signals
    + Context Path Cookie
    + Groups
      + getMembers now returns a Stream
      + Many Group subclasses are no longer visible
      + Managers group can now be statically accessed
      + Persistent Groups can now override their isMember and getMember methods
+ Portal REST API
+ New GenericFile APIs
+ Handling Login Periods


## What's new?
Bennu 4 


## Migrating your instance

Migrating an existing instance based on Bennu 3.x, requires you to ensure that all your Users have an associated UserProfile instance. Since version 3.2.0, those were created automatically, as as such, if your instance started in that version (or above), you do not have to do anything.

In addition, due to the change in the internal structure of Bennu Admin, you may need to re-install the Portal Application, as several of the internal paths have been changed.

## Migrating your theme

Bennu 4 upgraded the version of the Pebble templating engine to 2.1.0, which may bring some incompatibility issues. So far, we have not detected anything major, but its not impossible that something might break.

### Escaping
Also, unlike the previous version, Portal Themes have expression auto-escaping on by default. This means that any expression in the theme will be HTML-escaped before being rendered. This will cause the theme's body to be escaped, which is (likely) not the desired behavior. To fix this, simply use:  {{ body | raw }} instead of just {{body}}.

### Managing assets

Due to the heavy caching policy enforced by Bennu by default, changes to your theme's assets (CSS, JS and others) may not be reflected immediately by your users, until their local cache expires. To help fix this, Bennu 4 added a asset function, which will inject a cache-busting URL for the given resource, based on the resource's contents. As you update the resource, the URL will change, causing the users to load the new version. To use it, simply add:

**Asset Function**

``` html
<link rel="stylesheet" href="{{asset('/my-theme/css/style.css'}}" />
  
<!-- Rendered HTML -->
<link rel="stylesheet" href="/my-context-path/my-theme/css/style.css?v=123456" />
```

## Migrating your code

There were a few changes introduced in Bennu 4.0 which may require you to migrate your codebase. The following section highlights the changes that are most likely to have an impact on your code:


### Bennu Spring and Bennu OAuth


In this version, Bennu Spring and Bennu OAuth have been integrated into the Bennu codebase. No coordinates were changed, and simply follow the Bennu versions. As such, you should update your dependencies from these modules, so that they use the same version of Bennu.

### JsonAwareResource endpoints

As converters for GSON objects are now natively available as return types of JAX-RS and Spring REST Endpoints, JsonAwareResource has been updated to return/receive JsonElement instead of String.

### CSRF

In previous versions of Bennu, nothing was done to prevent CSRF attacks. In the 4.0 version, CSRF Prevention was added to the framework. You can read more about it in [CSRF Protection](../../../bennu-spring/docs/csrf-protection/csrf-protection.md).

### Bennu Toolkit

The Angular Directives provided by the Bennu Tookit were renamed to properly follow Angular's naming conventions. The new names now match the ones provided by the regular toolkit (i.e. bennu-localized-string instead of ng-localized-string). Example:

**Bennu Tookit Angular Directives**

```html

<!-- Using the old directive. This form no longer works in Bennu 4.0 -->
<input type="text" ng-localized-string="myvar" />
  
<!-- Using the new directive -->
<input type="text" bennu-localized-string="myvar" />
```

In addition, the old BennuPortal variable provided by the Toolkit (which was already deprecated) was completely removed, in favor of the Bennu variable.

### Signals

Bennu Signals has been integrated into the Bennu codebase, and is located in the Bennu Core module. To upgrade your code, simply remove the standalone bennu-signals dependency, and use the new package their new package  org.fenixedu.bennu.core.sig


### Context Path Cookie
Since Bennu 2, Bennu automatically injected a Cookie that contained the 'context path' of the application. This would allow for client-side only applications to properly invoke the REST APIs without the need for any server-side processing. With the introduction of this functionality by Bennu Toolkit, this cookie became unnecessary, as has since been removed. To access the context path in your Javascript code, you may simply do as follow:

**Accessing the Context Path in Javascript**

``` java
// This works as long as the Toolkit is included in the page, be it via a simple include or via the ${portal.toolkit()} tool.
var ctxPath = Bennu.contextPath;
```

### Groups
The Groups API saw several changes in Bennu 4.0. The following sections describe them in detail:

#### **getMembers now returns a Stream**
Due to the nature of the getMembers method on the Group class, its signature has changed from returning a Set to returning a Java 8 Stream. This allows for great optimizations in computing its value (by allowing lazy operations to be performed), while still being very flexible for the most common use cases (enumerating all the members and counting them). Note that there are no ordering, uniqueness or concurrency guarantees in the returned stream, if you want any of those, you will have to ensure them yourself (by invoking the sorted, distinct or parallel methods).

#### **Many Group subclasses are no longer visible**

The Groups API provides several composition methods, such as or, and, minus, not, grant and revoke. The implementations of these methods can be customized according to the Group they are invoked upon, allowing for it to return a more optimized Group.

However, to generically support those methods, Group subclasses like UnionGroup and IntersectionGroup were used. Those classes were publicly visible, and could be used by anyone. Other Groups were also publicly available, such as AnyoneGroup, UserGroup, etc.

In an effort to provide a cleaner API and better group implementations, all those types (with the exception of DynamicGroup) were hidden, and all their accessors have been made available via the Group class. The following provides examples of usage of the new API (as well as its equivalent in the old API):

**Group API Examples**

``` java
// Access to 'static' groups
Group anonymous = Group.anonymous(); // AnonymousGroup.get()
Group anyone = Group.anyone(); // AnyoneGroup.get()
Group logged = Group.logged(); // LoggedGroup.get()
Group nobody = Group.nobody(); // NobodyGroup.get()
Group managers = Group.managers(); // DynamicGroup.get("managers");
  
// For the group containing a single user
Group ofUser = user.groupOf(); // UserGroup.of(user);
// With multiple users...
Group users = Group.users(user1, user2, user3); // UserGroup.of(user1, user2, user3);
// ... same as before, but with Streams
Group users = Group.users(Arrays.stream(user1, user2, user3)); // UserGroup.of(Arrays.stream(user1, user2, user3))
  
// Dynamic Group
Group dynamic = Group.dynamic("example"); // DynamicGroup.get("example");
  
// Group Composition
one.and(other); // IntersectionGroup.of(one, other);
one.or(other); // UnionGroup.of(one, other);
one.minus(other); // DifferenceGroup.between(one, other);
one.not(); // NegationGroup.of(one);
```

#### **Managers group can now be statically accessed**
The group describing the Managers of an application was internally managed by Bennu on a by-convention basis. Since Bennu 4.0, this Group has been properly standardized, and can be statically accessed via the Group.managers() method.

#### **Persistent Groups can now override their isMember and getMember methods**

Following the discussion presented in  [BNN-203](https://jira.fenixedu.org/browse/BNN-203) - PersistentGroup's accessors should be overridable CLOSED , the isMember and getMembers methods of PersistentGroup subclasses can now be overridden. This change means two things for anyone developing against the Groups API:

+ PersistentGroup subclasses that know how to easily provide more performant implementations for these methods without delegating to the POJO Group instance (or those for which instantiating the POJO might be a non-trivial operation) should do so whenever possible.
+ Usages of the Group API that use a reference to a PersistentGroup should directly use its methods, instead of the POJO ones, so that it can benefit from optimizations in the actual PersistentGroup.

### Portal REST API
In previous version, the Bennu Portal Data REST API /api/bennu-portal/data used to return a Base64 blob containing the application's logo. This blob has been removed, and the logo can be retrieve in the standalone endpoint /api/bennu-portal/configuration/logo.

### New GenericFile APIs
The GenericFile API has been extended to allow creation with an instance of java.util.File, thus allowing for the upload of arbitrarily large files (due to the fact that it is no longer required that the whole file fit in memory). Usages and subclasses of GenericFile that are expected to deal with files of arbitrary sizes should use the new API.

### Handling Login Periods
Basic login period management was introduced directly in User's API, but kept complete access to individual periods in UserLoginPeriod. Developers are invited to use User's direct API when applicable. Operations available include opening and closing a User's login, and creating a new period.

