package myorg.domain.organization.person;

public enum IDDocumentType {

    IDENTITY_CARD,

    PASSPORT,

    NAVY_IDENTITY_CARD,

    AIR_FORCE_IDENTITY_CARD,

    MILITARY_IDENTITY_CARD;

    public String getName() {
	return name();
    }

}
