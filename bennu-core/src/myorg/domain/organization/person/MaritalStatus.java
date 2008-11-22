package myorg.domain.organization.person;

public enum MaritalStatus {

    SINGLE,

    MARRIED,

    DIVORCED,

    WIDOWER,

    SEPARATED,

    CIVIL_UNION,

    UNKNOWN;
    
    public String getName() {
	return name();
    }
}
