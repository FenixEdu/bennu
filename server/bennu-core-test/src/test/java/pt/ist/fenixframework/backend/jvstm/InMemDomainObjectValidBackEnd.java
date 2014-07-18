package pt.ist.fenixframework.backend.jvstm;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.backend.jvstm.repository.NoRepository;

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

}