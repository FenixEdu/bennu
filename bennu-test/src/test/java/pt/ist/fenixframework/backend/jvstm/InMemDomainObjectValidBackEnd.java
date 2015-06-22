package pt.ist.fenixframework.backend.jvstm;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.backend.jvstm.repository.NoRepository;

import com.google.common.base.Strings;

/***
 * 
 * This backend is necessary since {@link JVSTMBackEnd} throws {@link UnsupportedOperationException} when invoking
 * {@link FenixFramework#isDomainObjectValid(DomainObject)}
 * 
 * @author Sérgio Silva (sergio.silva@tecnico.ulisboa.pt)
 * @see JVSTMBackEnd#isDomainObjectValid(DomainObject)
 * 
 */
class InMemDomainObjectValidBackEnd extends JVSTMBackEnd {

    public InMemDomainObjectValidBackEnd() {
        super(new NoRepository());
    }

    @Override
    public boolean isDomainObjectValid(DomainObject object) {
        return object != null;
    }

    @Override
    public <T extends DomainObject> T getDomainObject(String externalId) {
        if (Strings.isNullOrEmpty(externalId)) {
            return null;
        }
        try {
            return super.getDomainObject(externalId);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}