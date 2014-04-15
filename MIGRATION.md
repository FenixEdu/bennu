# Migration

This file contains the steps required to update from one version to another. The following sections represent the steps required to update from the previous to that version. 

## Migrating from 2.x to 3.0

Migrate groups

```
rename table `GROUP` to `PERSISTENT_GROUP`;
alter table `PERSISTENT_GROUP` add `OID_FIRST` bigint unsigned, add index (OID_FIRST);

create table `INTERSECTION_GROUP_COMPOSITION` (`OID_PERSISTENT_GROUP` bigint unsigned, `OID_PERSISTENT_INTERSECTION_GROUP` bigint unsigned, primary key (OID_PERSISTENT_GROUP, OID_PERSISTENT_INTERSECTION_GROUP), index (OID_PERSISTENT_GROUP), index (OID_PERSISTENT_INTERSECTION_GROUP)) ENGINE=InnoDB, character set utf8;
insert into INTERSECTION_GROUP_COMPOSITION (OID_PERSISTENT_GROUP, OID_PERSISTENT_INTERSECTION_GROUP)
	select OID_GROUP, OID_COMPOSITION_GROUP
	from GROUP_COMPOSITION
	join FF$DOMAIN_CLASS_INFO on OID_COMPOSITION_GROUP >> 32 = DOMAIN_CLASS_ID
	where DOMAIN_CLASS_NAME = 'org.fenixedu.bennu.core.domain.groups.IntersectionGroup';

create table `UNION_GROUP_COMPOSITION` (`OID_PERSISTENT_GROUP` bigint unsigned, `OID_PERSISTENT_UNION_GROUP` bigint unsigned, primary key (OID_PERSISTENT_GROUP, OID_PERSISTENT_UNION_GROUP), index (OID_PERSISTENT_GROUP), index (OID_PERSISTENT_UNION_GROUP)) ENGINE=InnoDB, character set utf8;
insert into UNION_GROUP_COMPOSITION (OID_PERSISTENT_GROUP, OID_PERSISTENT_UNION_GROUP)
	select OID_GROUP, OID_COMPOSITION_GROUP
	from GROUP_COMPOSITION
	join FF$DOMAIN_CLASS_INFO on OID_COMPOSITION_GROUP >> 32 = DOMAIN_CLASS_ID
	where DOMAIN_CLASS_NAME = 'org.fenixedu.bennu.core.domain.groups.UnionGroup';

create table `DIFFERENCE_GROUP_REST` (`OID_PERSISTENT_GROUP` bigint unsigned, `OID_PERSISTENT_DIFFERENCE_GROUP` bigint unsigned, primary key (OID_PERSISTENT_GROUP, OID_PERSISTENT_DIFFERENCE_GROUP), index (OID_PERSISTENT_GROUP), index (OID_PERSISTENT_DIFFERENCE_GROUP)) ENGINE=InnoDB, character set utf8;
insert into DIFFERENCE_GROUP_REST (OID_PERSISTENT_GROUP, OID_PERSISTENT_DIFFERENCE_GROUP)
	select OID_GROUP, OID_COMPOSITION_GROUP
	from GROUP_COMPOSITION
	join FF$DOMAIN_CLASS_INFO on OID_COMPOSITION_GROUP >> 32 = DOMAIN_CLASS_ID
	where DOMAIN_CLASS_NAME = 'org.fenixedu.bennu.core.domain.groups.DifferenceGroup';

alter table `USER_GROUP_MEMBERS` change `OID_USER_GROUP` `OID_PERSISTENT_USER_GROUP` bigint unsigned;

update FF$DOMAIN_CLASS_INFO set DOMAIN_CLASS_NAME = 'org.fenixedu.bennu.core.domain.groups.PersistentGroup' where DOMAIN_CLASS_NAME = 'org.fenixedu.bennu.core.domain.groups.Group';
update FF$DOMAIN_CLASS_INFO set DOMAIN_CLASS_NAME = 'org.fenixedu.bennu.core.domain.groups.PersistentUserGroup' where DOMAIN_CLASS_NAME = 'org.fenixedu.bennu.core.domain.groups.UserGroup';
update FF$DOMAIN_CLASS_INFO set DOMAIN_CLASS_NAME = 'org.fenixedu.bennu.core.domain.groups.PersistentUnionGroup' where DOMAIN_CLASS_NAME = 'org.fenixedu.bennu.core.domain.groups.UnionGroup';
update FF$DOMAIN_CLASS_INFO set DOMAIN_CLASS_NAME = 'org.fenixedu.bennu.core.domain.groups.PersistentIntersectionGroup' where DOMAIN_CLASS_NAME = 'org.fenixedu.bennu.core.domain.groups.IntersectionGroup';
update FF$DOMAIN_CLASS_INFO set DOMAIN_CLASS_NAME = 'org.fenixedu.bennu.core.domain.groups.PersistentDifferenceGroup' where DOMAIN_CLASS_NAME = 'org.fenixedu.bennu.core.domain.groups.DifferenceGroup';
update FF$DOMAIN_CLASS_INFO set DOMAIN_CLASS_NAME = 'org.fenixedu.bennu.core.domain.groups.PersistentDynamicGroup' where DOMAIN_CLASS_NAME = 'org.fenixedu.bennu.core.domain.groups.DynamicGroup';
update FF$DOMAIN_CLASS_INFO set DOMAIN_CLASS_NAME = 'org.fenixedu.bennu.core.domain.groups.PersistentNegationGroup' where DOMAIN_CLASS_NAME = 'org.fenixedu.bennu.core.domain.groups.NegationGroup';
update FF$DOMAIN_CLASS_INFO set DOMAIN_CLASS_NAME = 'org.fenixedu.bennu.core.domain.groups.PersistentNobodyGroup' where DOMAIN_CLASS_NAME = 'org.fenixedu.bennu.core.domain.groups.NobodyGroup';
update FF$DOMAIN_CLASS_INFO set DOMAIN_CLASS_NAME = 'org.fenixedu.bennu.core.domain.groups.PersistentAnyoneGroup' where DOMAIN_CLASS_NAME = 'org.fenixedu.bennu.core.domain.groups.AnyoneGroup';
update FF$DOMAIN_CLASS_INFO set DOMAIN_CLASS_NAME = 'org.fenixedu.bennu.core.domain.groups.PersistentAnonymousGroup' where DOMAIN_CLASS_NAME = 'org.fenixedu.bennu.core.domain.groups.AnonymousGroup';
update FF$DOMAIN_CLASS_INFO set DOMAIN_CLASS_NAME = 'org.fenixedu.bennu.core.domain.groups.PersistentLoggedGroup' where DOMAIN_CLASS_NAME = 'org.fenixedu.bennu.core.domain.groups.LoggedGroup';
```

Migrate FileSupport: ensure default storage

```
alter table `FILE_SUPPORT` add `OID_DEFAULT_STORAGE` bigint unsigned;
alter table `FILE_STORAGE` add `OID_FILE_SUPPORT_AS_DEFAULT` bigint unsigned;
update FILE_STORAGE set OID_FILE_SUPPORT_AS_DEFAULT = (select OID from FILE_SUPPORT) where PATH is not null and PATH not like '%tmp%' limit 1;
update FILE_SUPPORT set OID_DEFAULT_STORAGE = (select OID from FILE_STORAGE where OID_FILE_SUPPORT_AS_DEFAULT is not null);
```
