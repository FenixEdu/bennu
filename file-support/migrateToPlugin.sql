UPDATE FF$DOMAIN_CLASS_INFO SET DOMAIN_CLASS_NAME='pt.ist.fenixframework.plugins.fileSupport.domain.FileRawData' WHERE DOMAIN_CLASS_NAME='module.fileSupport.domain.FileRawData';
UPDATE FF$DOMAIN_CLASS_INFO SET DOMAIN_CLASS_NAME='pt.ist.fenixframework.plugins.fileSupport.domain.GenericFile' WHERE DOMAIN_CLASS_NAME='module.fileSupport.domain.GenericFile';
UPDATE FF$DOMAIN_CLASS_INFO SET DOMAIN_CLASS_NAME='pt.ist.fenixframework.plugins.fileSupport.domain.DBStorage' WHERE DOMAIN_CLASS_NAME='module.fileSupport.domain.DBStorage';
UPDATE FF$DOMAIN_CLASS_INFO SET DOMAIN_CLASS_NAME='pt.ist.fenixframework.plugins.fileSupport.domain.LocalFileSystemStorage' WHERE DOMAIN_CLASS_NAME='module.fileSupport.domain.LocalFileSystemStorage';
UPDATE FF$DOMAIN_CLASS_INFO SET DOMAIN_CLASS_NAME='pt.ist.fenixframework.plugins.fileSupport.domain.FileStorageConfiguration' WHERE DOMAIN_CLASS_NAME='module.fileSupport.domain.FileStorageConfiguration';
UPDATE FF$DOMAIN_CLASS_INFO SET DOMAIN_CLASS_NAME='pt.ist.fenixframework.plugins.fileSupport.domain.DomainStorage' WHERE DOMAIN_CLASS_NAME='module.fileSupport.domain.DomainStorage';
UPDATE FF$DOMAIN_CLASS_INFO SET DOMAIN_CLASS_NAME='pt.ist.fenixframework.plugins.fileSupport.domain.FileStorage' WHERE DOMAIN_CLASS_NAME='module.fileSupport.domain.FileStorage';
UPDATE FF$DOMAIN_CLASS_INFO SET DOMAIN_CLASS_NAME='pt.ist.fenixframework.plugins.fileSupport.domain.FileSupport' WHERE DOMAIN_CLASS_NAME='module.fileSupport.domain.FileSupport';
UPDATE FF$DOMAIN_CLASS_INFO SET DOMAIN_CLASS_NAME='pt.ist.fenixframework.plugins.fileSupport.domain.LocalFileToDelete' WHERE DOMAIN_CLASS_NAME='module.fileSupport.domain.LocalFileToDelete';

select @FILE_SUPPORT_OID := OID_FILE_SUPPORT FROM MY_ORG;

insert into FF$PERSISTENT_ROOT(OID,ID_INTERNAL,OID_NEXT,OID_PREVIOUS,ROOT_KEY,OID_ROOT_OBJECT)
values('3','3',NULL,'2','pt.ist.fenixframework.plugins.fileSupport.domain.FileSupport',@FILE_SUPPORT_OID);

update FF$PERSISTENT_ROOT SET OID_NEXT='3' WHERE OID='2';
 
alter table FILE_RAW_DATA add column OID_FILE_SUPPORT bigint(20);
update FILE_RAW_DATA SET OID_FILE_SUPPORT=@FILE_SUPPORT_OID;

alter table GENERIC_FILE add column OID_FILE_SUPPORT bigint(20);
update GENERIC_FILE SET OID_FILE_SUPPORT=@FILE_SUPPORT_OID;

alter table FILE_STORAGE add column OID_FILE_SUPPORT bigint(20);
update FILE_STORAGE SET OID_FILE_SUPPORT=@FILE_SUPPORT_OID;
update FILE_STORAGE SET OJB_CONCRETE_CLASS='pt.ist.fenixframework.plugins.fileSupport.domain.DomainStorage' WHERE OJB_CONCRETE_CLASS='module.fileSupport.domain.DomainStorage';
update FILE_STORAGE SET OJB_CONCRETE_CLASS='pt.ist.fenixframework.plugins.fileSupport.domain.LocalFileSystemStorage' WHERE OJB_CONCRETE_CLASS='module.fileSupport.domain.LocalFileSystemStorage';

alter table FILE_STORAGE_CONFIGURATION add column OID_FILE_SUPPORT bigint(20);
update FILE_STORAGE_CONFIGURATION SET OID_FILE_SUPPORT=@FILE_SUPPORT_OID;

alter table LOCAL_FILE_TO_DELETE add column OID_FILE_SUPPORT bigint(20);
update LOCAL_FILE_TO_DELETE SET OID_FILE_SUPPORT=@FILE_SUPPORT_OID;
