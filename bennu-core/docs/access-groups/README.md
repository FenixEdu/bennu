## [**Access Groups**](README.md)
Bennu provides a group infrastructure to enable predicate based access control rules, or generally to express a predicate based group of users. They are used by Bennu itself to define who can access each menu node in the portal, to define access to files using the bennu-io infrastructure, and sometimes used in the Rest API for access control. Outside of Bennu they are also commonly used to define recipients for email messages.

Bennu represents groups of users as instances of the [Group](https://github.com/FenixEdu/bennu/blob/master/bennu-core/src/main/java/org/fenixedu/bennu/core/groups/Group.java) class. A group can be mapped to a set of users, translated to a string expression back and forth, or composed with other groups with logic operations.


At the base of the group infrastructure there are logic operations, and dynamic groups. The logic operations can be applied to one or more group operands, the operations supported are composition, intersection, negation, and difference. The dynamic groups can be described as a name tag over a group, that can be re-targeted. Besides that everything is a an implementation of a [CustomGroup](https://github.com/FenixEdu/bennu/blob/master/bennu-core/src/main/java/org/fenixedu/bennu/core/groups/CustomGroup.java). A set of basic implementations is provided, which include the semantics of anyone, nobody, anonymous, and logged, and an explicit fixed set of members.


Group extensions can choose one of tho methods. Either extend directly a [CustomGroup](https://github.com/FenixEdu/bennu/blob/master/bennu-core/src/main/java/org/fenixedu/bennu/core/groups/CustomGroup.java) in case of complex groups with arguments, or extend the [GroupStrategy](https://github.com/FenixEdu/bennu/blob/master/bennu-core/src/main/java/org/fenixedu/bennu/core/groups/GroupStrategy.java) subclass for simple, no argument groups. Both extension mechanisms are described in detail later.

Groups are POJOs, but the infrastructure also requires that any group can be converted in a persistent counterpart. That feature is used when your business domain requires to relate to a group. For example, the Portal's menu items store the persistent counterpart of the groups configured to have access to that menu item. Persistent groups store using domain relations any context required to evaluate the group logic.

### Group Language
Any group can be expressed in a DSL. The language consists of leaf group expression, or composite logic expressions. The supported logic operations are union, intersection, difference and negation, expressed in the language as follows:

```C
<expr> | <expr> | .....
<expr> & <expr> & .....
<expr> - <expr> - .....
!<expr>
```
Parenthesis can be used to control evaluation precedence, for example:
```C
(<expr> & (<expr> | <expr>))
```
Provided by the core language there are dynamic groups, that are expressed using a \verb|#| followed by the name of the dynamic group. For example:
```C
#managers
```
The DSL is extensible, as custom groups contribute with more operators to the language. Each custom group defines a unique operator that starts each custom group expression. If there are no arguments the expression for that group is just the operator:
```C
<operator>
```

If there are arguments, then the expression is the operator followed by comma separated values (with or without the keyword). The exact rules of arguments passing are described later in the Extensibility section. But generally, the expressions for custom groups are as follows:
```C
<operator>(<arg1>, <arg2>, ...)
```
With the arguments being either just a value, or a keyword an \verb|=| sign and a value, or a keyword an \verb|=| sign and an array of values. As follows:
```C
<value>
<argName>=<value>
<argName>=[<value>,<value>,<value>]
```


### Programmatic API
The key class for the entire group API is the [Group](https://github.com/FenixEdu/bennu/blob/master/bennu-core/src/main/java/org/fenixedu/bennu/core/groups/Group.java). Is the super class of every group implementation and the starting point for most operations.

The first part of the core API of a group is composed of methods for querying membership in the group of a given user, and to obtain the complete list of members. Both these methods have a variant with a date parameter, that should evaluate access on the state of the system at that date. Note that the date parameter may not make sense for some group semantics and is not feasible for others, so in some cases the dated variant might return just the same. Also, remember that obtaining all members may be a high cost operation, as some groups can have a complex membership logic.

```C
Stream<User> getMembers();
Stream<User> getMembers(DateTime when);
boolean isMember(User user);
boolean isMember(User user, DateTime when);
```
The second part of the core API is composed of logic operations. Supported operations are composition, intersection, negation, difference, grant and revoke. All operands return a new group representing the result of applying the operand to the target group, without changing the target. Whenever possible the group is compressed to the most canonical form, for example, x.not().not() is x.


```C
Group and(Group group); - Intersect with given group.
Group or(Group group); - Unite with given group.
Group minus(Group group); - Subtract with given group.
Group not(); - Negate the group.
Group grant(User user); - Grants access to the given user.
Group revoke(User user); - Revokes access to the given user.
```
As said before groups can be parsed back and forth to a String expression.

```C
static Group parse(String expression);
String expression();
```

### Base Groups
#### **AnyoneGroup**
[AnyoneGroup](https://github.com/FenixEdu/bennu/blob/master/bennu-core/src/main/java/org/fenixedu/bennu/core/groups/AnyoneGroup.java) instances can be obtained through:
```C
Group.anyone();
```

Anyone group is the always true group. Virtually any user is the system is a member. The following operations have implemented compression mechanisms:

+ and: intersecting another group with anyone will always return the other group;
+ or: conjunctions with another group will always return 'anyone';
+ not: returns nobody.

In the language this group is referred simply as \verb|anyone|

#### **NobodyGroup**
[NobodyGroup](https://github.com/FenixEdu/bennu/blob/master/bennu-core/src/main/java/org/fenixedu/bennu/core/groups/NobodyGroup.java) instances can be obtained through:

```C
Group.nobody();
```


Nobody group is the always false group. The members set is the empty set. The following operations have implemented compression mechanisms:

+ and: intersecting another group with nobody will always return nobody;
+ or: conjunctions with another group will always return the other group;
+ minus: nobody minus any other group will return nobody;
+ not: return anyone.

In the language this group is referred simply as \verb|nobody|

#### **LoggedGroup**
[LoggedGroup](https://github.com/FenixEdu/bennu/blob/master/bennu-core/src/main/java/org/fenixedu/bennu/core/groups/LoggedGroup.java) instances can be obtained through:

```C
Group.logged();
```

Logged group is true for non null users. The members set is hard to define in this case, the current implementation returns all users. The following operations have implemented compression mechanisms:

+ and: intersections with the opposite group anonymous will return nobody, also + if the other group doesn't include null users the result is the other group;
+ or: conjunctions with the opposite group anonymous will return anyone, also if the other group doesn't include null users the result is logged;
+ minus: logged minus anonymous is logged;
+ not: returns anonymous.

In the language this group is referred simply as \verb|logged|

#### **AnonymousGroup** 
[AnonymousGroup](https://github.com/FenixEdu/bennu/blob/master/bennu-core/src/main/java/org/fenixedu/bennu/core/groups/AnonymousGroup.java) instances can be obtained through:

```C
Group.anonymous();
```

Anonymous group is true for null users. The members set is the empty set. The following operations have implemented compression mechanisms:

+ and: intersections with opposite group logged will return nobody;
+ or: conjunctions with the opposite group logged will return anyone;
+ minus: anonymous minus logged returns anonymous;
+ not: returns logged.

In the language this group is referred simply as \verb|anonymous|

#### **UserGroup**

 [UserGroup](https://github.com/FenixEdu/bennu/blob/master/bennu-core/src/main/java/org/fenixedu/bennu/core/groups/UserGroup.java) instances can be obtained through:

 ```C
Group.users(User... members);
Group.users(Stream<User> members);
```
A UserGroup defines the access with an explicit set of members, as opposed to any other group that define access with a predicate. Membership is simply a contains operation over the underlying user list. The following operations have implemented compression mechanisms:

+ and: intersections with other user groups simply intersect both user sets;
+ or: conjunctions with other user groups simply make a union of both user sets;
+ minus: differences with other user groups will return a user group with the difference of the user sets;

The expression of these groups in the group language will be the 'U' symbol with the user's usernames separated by ','. Depending on the format (more specifically on the use of special symbols) usernames can be identifiers, or strings in the language. For example, for alphanumeric usernames:

 ```C
U(ist123, ist456)
```
And for example for usernames with dots:
 ```C
U('john.doe', 'mike.fields')
```

#### **DynamicGroup**
[DynamicGroups](https://github.com/FenixEdu/bennu/blob/master/bennu-core/src/main/java/org/fenixedu/bennu/core/groups/DynamicGroup.java) work as tags, that can be moved from one group to another over time, keeping history when they do. Tags offer semantic over the underlying group, by the name they give to it, for example, you can set a \verb|managers| tag over the group of users John, Mary and Steve. Membership is delegated to the underlying group (the pointed, or tagged group). The membership methods with the date argument consider the history of the dynamic group.

To obtain a dynamic group instance, do this:
 ```C
Group.dynamic(String name);
```
This will always give a DynamicGroup instance, even if no group was ever tagged with that name, in which case the instance is backed by a nobody group. There is an operation to query if a given dynamic group is persisted:

 ```C
boolean isDefined();
```

There is also a way to access the underlying group:
 ```C
Group underlyingGroup();
Group underlyingGroup(DateTime when);
```

Note that the logic operations over this group do not change the dynamic group, as expected. To actually change the group call for a mutator, where the familiar API of logic operations will actually change the targeted group. For example, the following code will change the \verb|qa| group to also contain the current value of \verb|managers|:
 ```C
Group.dynamic("qa").mutator().or(Group.dynamic("managers").underlyingGroup());
```
This other example will change the group entirely, replacing whatever was there with anyone:

 ```C
Group.dynamic("qa").mutator().changeGroup(Group.anyone());
```

### Extensibility
There are two extension options, the choice depends on wether the group has arguments (like a UserGroup) or not (like a NobodyGroup). Either way your group implementation will be annotated with [GroupOperator](https://github.com/FenixEdu/bennu/blob/master/bennu-core/src/main/java/org/fenixedu/bennu/core/annotation/GroupOperator.java), with a value parameter that must be a global unique token for this group. For example the UserGroup is annotated with:

 ```C
@GroupOperator("U")
```

#### **GroupStrategy**
To create a simple, no argument group, just extend GroupStrategy, annotate to specify the operator, and implement the membership logic. The base groups like anyone or nobody are examples of strategy groups. As these groups are simply define by the implementation of the membership logic, the persistent counterpart is always the same, and there is no need to define anything else.

For example consider a group whose membership is defined by: users that are employees.

 ```java
@GroupOperator("employees")
public class EmployeeGroup extends GroupStrategy {
    @Override public String getPresentationName() {
        return "Employess";
    }
 
    @Override public Stream<User> getMembers() {
        return Bennu.getInstance().getEmployeesSet().stream().filter(Employee::isActive)
                .map(Employee::getUser);
    }
 
    @Override public Stream<User> getMembers(DateTime when) {
        return getMembers();
    }
 
    @Override public boolean isMember(User user) {
        return user != null && user.getPerson() != null && user.getPerson().getEmployee() != null
                && user.getPerson().getEmployee().isActive();
    }
 
    @Override public boolean isMember(User user, DateTime when) {
        return isMember(user);
    }
}
```

In the language the group is just the operator \verb|employees|. For example:

 ```java
employees | #managers
```


#### **CustomGroup**
When groups have arguments they need to be persistently linked to the group, in which case both the \verb|Group| and the \verb|PersistentGroup| need to be implemented. First extend CustomGroup, annotate to specify the operator, implement the membership logic, as with the GroupStrategy, but then also include the arguments using the GroupArgument annotation on fields. Arguments are passed by name so each group must have one, by default it's the name of the field, but can be changed using the annotation. For each group one of the arguments can an empty name, compressing the expression. The following are valid argument examples for a group:

 ```java
// argument named: 'minAge' of type int, expression example: 'age(minAge=18)'
@GroupArgument
private int minAge;
  
// argument named: 'a' of type int, expression example: 'age(a=18)'
@GroupArgument("a")
private int minAge;
  
// argument without name of type int, expression example: 'age(18)'
@GroupArgument("")
private int minAge;
```

By default some types are supported as field types: all the primitive types plus String, and DateTime. Besides that you have to tell how to convert other types to a String. To do that provide ArgumentParser implementations annotated with \verb|GroupArgumentParser|. A good example to look at is how the UserGroup defines it's Group argument type:

 ```java
@GroupArgumentParser
public class UserArgumentParser implements ArgumentParser<User> {
    @Override public User parse(String argument) {
        return User.findByUsername(argument);
    }
 
 
    @Override public String serialize(User argument) {
        return argument.getUsername();
    }
 
 
    @Override public Class<User> type() {
        return User.class;
    }
}
```

For CustomGroups you also have to define a persistent counterpart. That is a domain object extending PersistentGroup with slots or domain relations, one for each argument. Then implement in both the group and its persistent counterpart methods to obtain the other. That is: implement Group's \verb|toPersistentGroup()| and PersistentGroup's \verb|toGroup()|. Again taking the UserGroup example:

 ```java
//in UserGroup:
@GroupArgument("") private Set<User> members;
  
@Override public PersistentGroup toPersistentGroup() {
    return PersistentUserGroup.getInstance(members);
}
  
//in dml:
public class groups.PersistentUserGroup extends groups.PersistentGroup {}
relation UserGroupMembers {
    protected groups.PersistentUserGroup playsRole userGroup {
        multiplicity *;
    }
    protected User playsRole member {
        multiplicity 1..*;
    }
}
  
//in PersistentUserGroup:
@Override public Group toGroup() {
    return Group.users(getMemberSet().stream());
}
```
Putting it all together lets take an example group of user of a specific department. Assume a Department entity in the system linked to users, that represent it's members. The group should support a department argument. The expected usage of the group in the language should be:

```C
dep(<dep acronym>)
```

First lets implement the Group:

```java
package org.fenixedu.bennu.example;
 
 
import java.util.stream.Stream;
 
import org.fenixedu.bennu.core.annotation.GroupArgument;
import org.fenixedu.bennu.core.annotation.GroupOperator;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.CustomGroup;
import org.joda.time.DateTime;
 
@GroupOperator("dep")
public class DepartmentMemberGroup extends CustomGroup {
    @GroupArgument private Department department;
 
    public DepartmentMemberGroup(Department department) {
        this.department = department;
    }
 
    @Override public String getPresentationName() {
        return "Members of department " + department.getAcronym();
    }
 
 
    @Override public PersistentGroup toPersistentGroup() {
        return PersistentDepartmentGroup.getInstance(department);
    }
 
    @Override public Stream<User> getMembers() {
        return department.getMembers();
    }
 
    @Override public Stream<User> getMembers(DateTime when) {
        return department.getMembers(when);
    }
 
    @Override public boolean isMember(User user) {
        return department.equals(user.getDepartment());
    }
 
    @Override public boolean isMember(User user, DateTime when) {
        return department.equals(user.getDepartment(when));
    }
 
    @Override public boolean equals(Object object) {
        if (object instanceof DepartmentMemberGroup) {
            return department.equals(((DepartmentMemberGroup) object).department);
        }
        return false;
    }
 
    @Override public int hashCode() {
        return department.hashCode();
    }
}
```

To tell the system how to go from acronyms to Department instances do this:

```java
@GroupArgumentParser
public class DepartmentArgumentParser implements ArgumentParser<Department> {
    @Override public Department parse(String argument) {
        return Department.findByAcronym(argument);
    }
 
 
    @Override public String serialize(Department argument) {
        return argument.getAcronym();
    }
 
 
    @Override public Class<Department> type() {
        return Department.class;
    }
}
```
Then define the persistent group entity:

```java

public class PersistentDepartmentGroup extends .org.fenixedu.bennu.core.domain.groups.PersistentGroup {}
 
 
relation PersistentDepartmentDepartment {
    protected PersistentDepartmentGroup playsRole departmentGroup {
        multiplicity 0..1;
    }
    protected Department playsRole department {
        multiplicity 1..1;
    }
}
```

And implement it:

```java
package org.fenixedu.bennu.example;
 
import java.util.*;
import java.util.stream.Stream;
 
import org.fenixedu.bennu.core.domain.BennuGroupIndex;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
 
import pt.ist.fenixframework.dml.runtime.Relation;
 
public final class PersistentDepartmentGroup extends PersistentDepartmentGroup_Base {
    protected PersistentDepartmentGroup(Department department) {
        super();
        setDepartment(department);
    }
    @Override public Group toGroup() {
        return new DepartmentMemberGroup(getDepartment());
    }
 
    @Override protected Collection<Relation<?, ?>> getContextRelations() {
        return Collections.singleton(getRelationPersistentDepartmentDepartment());
    }
    public static PersistentDepartmentGroup getInstance(Department department) {
        return singleton(Department::getDepartmentGroup, () -> new PersistentDepartmentGroup(department));
    }
}
```