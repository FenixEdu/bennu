/*
 * group.js
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

    Bennu.group = Bennu.group || {};
    Bennu.group.attr = "bennu-group"
    Bennu.group.selectionClass = "btn-info";

    Bennu.group.template = '<div class="btn-group bennu-group-widget"></div>';
    Bennu.group.allow = {};
    Bennu.group.allow['public'] = {
        name: "Public",
        group: "anyone",
        icon: "glyphicon glyphicon-globe"
    };

    Bennu.group.allow['users'] = {
        name: "Users",
        group: "logged",
        icon: "glyphicon glyphicon-user"
    };

    Bennu.group.allow['managers'] = {
        name: "Managers",
        group: "#managers",
        icon: "glyphicon glyphicon-eye-open"
    };

    Bennu.group.allow['custom'] = {
        name: "Custom",
        group: "custom",
        icon: "glyphicon glyphicon-wrench"
    };

    Bennu.group.modalTemplate = '<div class="bennu-group-custom-modal modal fade">' +
        '<div class="modal-dialog">' +
        '<div class="modal-content">' +
        '<div class="modal-header">' +
        '<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>' +
        '<h4 class="modal-title">Custom Group</h4>' +
        '</div>' +
        '<p><div class="modal-body">' +
        '<p><small><b>Add User:</b></small></p>' +
        '<div class="row">' +
        '<div class="col-sm-12">' +
        '<input placeholder="Username" class="user-search form-control"/>' +
        '</div>' +
        '</div></p>' +
        '<p><small><b>Group users:</b></small></p>' +
        '<div class="bennu-group-list" style="height:149px; overflow:scroll; border:1px solid #ddd; margin-top:20px;">' +
        '<table class="table table-striped">' +
        '</table>' +
        '</div>' +
        '</div>' +
        '<div class="modal-footer">' +
        '<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>' +
        '</div>' +
        '</div><!-- /.modal-content -->' +
        '</div><!-- /.modal-dialog -->' +
        '</div><!-- /.modal -->'

    Bennu.group.modalClearUsers = function () {
        $(".bennu-group-list table", Bennu.group.customModal).empty();
    }

    Bennu.group.modalAddUserToModal = function (obj) {
        var line = $('<tr class="bennu-group-custom-user" data-username="' + obj.username + '">' +
            '<td class="col-xs-1"><img class="img-circle" src="' + obj.avatar + '?s=32" alt="" /></td>' +
            '<td class="col-xs-9">' +
            '<div><b>' + obj.displayName + '</b></div>' +
            '<div><small>' + obj.username + '</small></div>' +
            '</td>' +
            '<td class="col-xs-2">' +
            '<button class="btn btn-sm btn-warning">Remove</button>' +
            '</td>' +
            '</tr>');

        $("button", line).on('click', function (e) {
            e = $(e.target).closest(".bennu-group-custom-user");
            var username = e.data("username");
            e.remove();
            var dom = Bennu.group.customModal.data("input");
            var list = group(dom);

            for (var i = list.length - 1; i >= 0; i--) {
                if (list[i] === username) {
                    list.splice(i, 1);
                }
            }

            storeGroup(list, dom);
        });

        $(".bennu-group-list table", Bennu.group.customModal).append(line);
    };

    function setSelected(dom) {
        var e = dom.data("related");
        var val = e.val();
        $("." + Bennu.group.selectionClass, dom).removeClass(Bennu.group.selectionClass);

        if ($("[data-role='" + val + "']", dom).length) {
            $("[data-role='" + val + "']", dom).addClass(Bennu.group.selectionClass);
        } else if (val.substring(0, 2) === "U(" || val === "nobody" || val === "" ) {
            $("[data-role='custom']", dom).addClass(Bennu.group.selectionClass);
        } else {
            $("button", dom).addClass("disabled");
            dom.attr("data-trigger", "hover").attr("data-placement", "right").data("title", "Disabled, group is too complex.").tooltip({})
        }

    }

    Bennu.group.setupModal = function () {
        $(document.body).append(Bennu.group.modalTemplate);
        // Bennu.group.customModal = $(".bennu-group-custom-modal");
        // Bennu.group.customModal.modal('show');
// -------------------------------------
// -------------------------------------
// -------------------------------------
        var substringMatcher = function (strs) {
            return function findMatches(q, cb) {
                cb(states);
            };
        };

        var example = new Bloodhound({
            datumTokenizer: function (d) {
                return Bloodhound.tokenizers.whitespace(d.value);
            },
            queryTokenizer: Bloodhound.tokenizers.whitespace,
            remote: {
                url: Bennu.contextPath + "/api/bennu-core/users/findAutoComplete",

                replace: function (url, query) {
                    return url + "?query=" + query + "&maxHits=10";
                },

                ajax: {
                    beforeSend: function (jqXhr, settings) {
                        //settings.data = $.param({q: queryInput.val()});
                    },
                    type: "POST"

                },

                filter: function (response) {
                    return response.users;
                }
            }
        });

        example.initialize();

        $('.bennu-group-custom-modal .user-search').typeahead({
            hint: true,
            highlight: true,
            minLength: 1,
        }, {
            name: 'custom-group-username',
            displayKey: 'value',
            source: example.ttAdapter(),
            templates: {
                empty: [
                    '<div class="empty-message">',
                    'No User Found',
                    '</div>'
                ].join('\n'),
                suggestion: function (x) {
                    Bennu.group.userCache[x.username] = x;
                    return '<p><div class="row">' +
                        '<div class="col-xs-1"><img class="img-circle" src="' + x.avatar + '?s=32" alt="" /></div>' +
                        '<div class="col-sm-11">' +
                        '<div>' + x.displayName + '</div>' +
                        '<div>' + x.username + '</div>' +
                        '</div></div></p>';
                }
            }
        }).on("typeahead:autocompleted typeahead:selected", function (x, y, z) {
            Bennu.group.modalAddUserToModal(y);
            var dom = Bennu.group.customModal.data("input");
            var list = group(dom);
            list.push(y.username);
            storeGroup(list, dom);
            dom.data("related").trigger("change");
        });
// -------------------------------------
// -------------------------------------
// -------------------------------------

    }

    function group(dom) {
        var list = dom.data("customCache");
        if (list == ""  || list[0] != "U") {
            return [];
        }

        list = list.substring(2, list.length - 1).split(",");

        list = $.map(list, function (e) {
            return e.trim();
        });

        if (list.length == 1 && list[0] === "") {
            list = [];
        }

        return list;
    }

    function storeGroup(list, dom) {
        var result = "";
        for (var i = 0; i < list.length; i++) {
            result += list[i];


            if (i < list.length - 1) {
                result += ", ";
            }
        }
        var s;
        if (result)
            s="U(" + result + ")";
        else {
            s =  "nobody"
        }
        dom.data("customCache", s);
        dom.data("related").val(s);
    }

    function showModal(dom) {
        Bennu.group.customModal = $(".bennu-group-custom-modal");
        Bennu.group.customModal.data("input", dom);

        var list = group(dom);

        $(".bennu-group-list table").empty();
        for (var i = 0; i < list.length; i++) {
            var user = Bennu.group.userCache[list[i]];
            Bennu.group.modalAddUserToModal(user);
        }

        Bennu.group.customModal.modal('show');
    }

    function cacheCustom(dom) {
        var e = dom.data("related");

        if (e.val().substring(0, 2) == "U(") {
            dom.data("customCache", e.val());
        } else {
            dom.data("customCache", "nobody");
        }

        $.map(group(dom), prefetch);
    }

    Bennu.group.userCache = {};
    function prefetch(username) {
        $.post(Bennu.contextPath + "/api/bennu-core/users/findAutoComplete?query=" + username + "&maxHits=10", {}, function (e) {
            Bennu.group.userCache[username] = e.users[0];
        });
    }

    Bennu.group.createWidget = function(input){
        input = $(input);
        var dom = $(Bennu.group.template);
        var allow = input.attr("allow") || "public,users,custom";
        allow = allow.split(",");
        for (var i = 0; i < allow.length; i++) {
            var perm = Bennu.group.allow[allow[i]];
            if (perm){
                var temp = '<button type="button" data-role="' + perm.group + '" class="btn btn-sm btn-default"><span class="' + perm.icon + '"></span> ' + perm.name + '</button>';
                dom.append(temp);
            }else{
                console.warn("Unknown permission type " + allow[i]);
            }
        }

        dom.data("related", input);
        input.data("input", dom);

        $("button", dom).click(function (e) {
            e = $(e.target).closest("button");
            var dom = e.closest(".bennu-group-widget");
            if (e.data("role") != "custom") {
                dom.data("related").val(e.data("role"));
            } else {
                dom.data("related").val(dom.data("customCache") || "U");
                showModal(dom);
            }
            setSelected(dom);
            dom.data("related").trigger("change");
        });

        input.after(dom);
        cacheCustom(dom);
        setSelected(dom);

        input.on("change.bennu", function(ev){
            cacheCustom(dom);
            setSelected(dom);
            input.data("handler").trigger();
        });

        return Bennu.widgetHandler.makeFor(input);
    }
})();

