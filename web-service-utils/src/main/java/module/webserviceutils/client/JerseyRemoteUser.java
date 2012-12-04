package module.webserviceutils.client;

import pt.ist.bennu.core.domain.User;

import module.webserviceutils.domain.HostSystem;

public class JerseyRemoteUser {

    private User user;
    private JerseyClient client;

    public JerseyRemoteUser(final User user) {
	this.user = user;
	this.client = HostSystem.getFenixJerseyClient();
    }

    public User getLocalUser() {
	return user;
    }

    private String readRemoteMethod(String method, Object object) {
	return client.method("remotePerson").arg("username", user.getUsername()).arg("method", method).get();
    }

    public String getUserAliass() {
	return readRemoteMethod("getUserAliass", null);
    }

    public String getEmailForSendingEmails() {
	return readRemoteMethod("getEmailForSendingEmails", null);
    }

    public String getWorkingPlaceCostCenter() {
	return readRemoteMethod("getWorkingPlaceCostCenter", null);
    }

    public String getEmployeeRoleDescription() {
	return readRemoteMethod("getEmployeeRoleDescription", null);
    }

    public String readAllEmployerRelations() {
	return readRemoteMethod("readAllEmployerRelations", null);
    }

    public String readAllTeacherInformation() {
	return readRemoteMethod("readAllTeacherInformation", null);
    }

    public String readAllResearcherInformation() {
	return readRemoteMethod("readAllResearcherInformation", null);
    }

    public String readAllEmployeeInformation() {
	return readRemoteMethod("readAllEmployeeInformation", null);
    }

    public String readAllGrantOwnerInformation() {
	return readRemoteMethod("readAllGrantOwnerInformation", null);
    }

    public String readAllExternalResearcherInformation() {
	return readRemoteMethod("readAllExternalResearcherInformation", null);
    }

    public String getWorkingPlaceForAnyRoleType() {
	return readRemoteMethod("getWorkingPlaceForAnyRoleType", null);
    }

    public String getName() {
	return readRemoteMethod("getName", null);
    }

    public String getRemoteOid() {
	return readRemoteMethod("getExternalId", null);
    }

    public static String readAllUserData() {
	return HostSystem.getFenixJerseyClient().method("readAllUserData").arg("types", "STUDENT-EMPLOYEE-TEACHER").get();
    }

    public static String readAllUserDataForSotis() {
	return HostSystem.getFenixJerseyClient().method("readAllUserData").get();
    }

    public static String readAllEmails() {
	return HostSystem.getFenixJerseyClient().method("readAllEmails").get();
    }

    public Boolean hasRemotePerson() {
	return client.method("hasRemotePerson").get(Boolean.class);
    }

    public Boolean hasStudent() {
	return Boolean.parseBoolean(readRemoteMethod("hasStudent", null));
    }
}
