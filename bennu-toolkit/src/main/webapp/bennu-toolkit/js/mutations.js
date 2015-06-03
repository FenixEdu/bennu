/*
 * mutations.js
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

(function () {
    Bennu.monitor = Bennu.monitor || {};

    Bennu.monitor.checkFor = function (tag, actions) {
        var target = document.querySelector('body');

        var observer = new MutationObserver(function (mutations) {

            $.map(mutations, function (e) {
                $.map(e.addedNodes, function (e) {
                    var contains = $("[" + tag + "]", $(e)).length !== 0;
                    var is = $(e).attr(tag) === "";
                    if (is) {
                        actions.add && actions.add(e);
                    } else if (contains) {
                        $("[" + tag + "]", $(e)).map(function (i, x) {
                            actions.add && actions.add(x);
                        })

                    }
                });

                $.map(e.removedNodes, function (e) {
                    actions.remove && actions.remove(e);
                });
            });
        });

	var config = {
            attributes: true,
            subtree: true,
            childList: true,
            characterData: true
        };
        observer.observe(target, config);
    };

    Bennu.on("load", function () {

        // Datetime
        // --------
        $("[bennu-datetime],[bennu-time],[bennu-date]").each(function (i, e) {
            Bennu.datetime.createWidget(e);
        });

        Bennu.monitor.checkFor("bennu-date", {
            add: function (e) {
                Bennu.datetime.createWidget(e);
            },
            remove: function (e) {
                if ($(e).attr("bennu-date") === "") {
                    $(e).data("handler").remove();
                }
            }
        });

	Bennu.monitor.checkFor("bennu-time", {
            add: function (e) {
                Bennu.datetime.createWidget(e);
            },
            remove: function (e) {
                if ($(e).attr("bennu-time") === "") {
                    $(e).data("handler").remove();
                }
            }
        });

	Bennu.monitor.checkFor("bennu-datetime", {
            add: function (e) {
                Bennu.datetime.createWidget(e);
            },
            remove: function (e) {
                if ($(e).attr("bennu-datetime") === "") {
                    $(e).data("handler").remove();
                }
            }
        });

        // Groups
        // ------
        var inputs = $("[" + Bennu.group.attr + "]");

        if (inputs.size() > 0) {
            Bennu.group.setupModal();
        }

        inputs.map(function (i, e) {
            Bennu.group.createWidget(e);
        });

        Bennu.monitor.checkFor(Bennu.group.attr, {
            add: function (e) {
                Bennu.group.createWidget(e);
            },
            remove: function (e) {
                e = $(e);
                if ($(e).attr(Bennu.group.attr) === "") {
                    $(e).data("handler").remove();
                }
            }
        });

        // Localized String
        // ----------------
        $("[bennu-localized-string]").map(function (i, e) {
            Bennu.localizedString.createWidget(e);
        });

        Bennu.monitor.checkFor(Bennu.localizedString.attr, {
            add: function (e) {
                Bennu.localizedString.createWidget(e);
            },
            remove: function (e) {
                if ($(e).attr(Bennu.localizedString.attr) === "") {
                    $(e).data("handler").remove();
                }
            }
        });

        // HTML Editor
        // -----------
        $("[" + Bennu.htmlEditor.attr + "]").map(function (i, e) {
            Bennu.htmlEditor.createWidget(e);
        });

        Bennu.monitor.checkFor(Bennu.htmlEditor.attr, {
            add: function (e) {
                Bennu.htmlEditor.createWidget(e);
            },
            remove: function (e) {
                if ($(e).attr(Bennu.htmlEditor.attr) === "") {
                    $(e).data("handler").remove();
                }
            }
        });
        
        
        // User Autocomplete
        // -----------------
        var autocompletes = $("[" + Bennu.userAutocomplete.attr + "]");
        
        autocompletes.map(function (i, e) {
            Bennu.userAutocomplete.createWidget(e);
        });
        
        Bennu.monitor.checkFor(Bennu.userAutocomplete.attr, {
            add: function (e) {
                Bennu.userAutocomplete.createWidget(e);
            },
            remove: function (e) {
                if ($(e).attr(Bennu.userAutocomplete.attr) === "") {
                    $(e).data("handler").remove();
                }
            }
        });
        
    });
})();
