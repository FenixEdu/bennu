/**
 * Copyright © 2014 Instituto Superior Técnico
 *
 * This file is part of Bennu Spring.
 *
 * Bennu Spring is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bennu Spring is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Bennu Spring.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.spring.mvc;

import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/test/")
public class TestController {

    @RequestMapping("/404")
    public String throw404() {
        throw BennuSpringTestDomainException.status(404);
    }

    @RequestMapping("/403")
    public String throw403() {
        throw BennuSpringTestDomainException.status(403);
    }

    @RequestMapping("/412")
    public String throw412() {
        throw BennuSpringTestDomainException.status(412);
    }

    @RequestMapping("/500")
    public String throw500() {
        throw BennuSpringTestDomainException.status(500);
    }

    @ResponseBody
    @RequestMapping("/localized")
    public String localized(@RequestParam LocalizedString localized) {
        return localized.json().toString();
    }

}
