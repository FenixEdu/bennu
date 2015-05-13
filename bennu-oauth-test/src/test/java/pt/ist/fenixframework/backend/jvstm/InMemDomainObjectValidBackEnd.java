/**
 * Copyright © 2015 Instituto Superior Técnico
 *
 * This file is part of Bennu OAuth Test.
 *
 * Bennu OAuth Test is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bennu OAuth Test is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Bennu OAuth Test.  If not, see <http://www.gnu.org/licenses/>.
 */
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