/*
 * codeEditor.js
 * 
 * Copyright (c) 2014, Instituto Superior Técnico. All rights reserved.
 * 
 * This file is part of Bennu Toolkit.
 * 
 * Bennu Toolkit is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * Bennu Toolkit is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Bennu Toolkit. If not, see
 * <http://www.gnu.org/licenses/>.
 */

(function(){
  Bennu.codeEditor = Bennu.codeEditor || {};
  Bennu.codeEditor.require = function(){
    if (!window.ace){
      $.ajaxSetup({cache: true});
      $(document.body).append("<script src='" + Bennu.contextPath + "/bennu-toolkit/js/libs/ace/ace.js'></script>");
    }
  };
})();
