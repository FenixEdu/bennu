bennuAdmin.controller('MenuController', [ '$scope', '$routeParams', '$http', function($scope, $routeParams, $http) {
  $scope.id = $routeParams.id;
  $scope.deleteSelected = function() {
    $scope.selected.node.remove();
    if($scope.selected.id) {
      $http.delete("../api/bennu-portal/menu/" + $scope.selected.id);
    }
    $scope.selected = null;
  }
  $scope.saveSelected = function() {
    var data = { title: $scope.selected.title, description: $scope.selected.description, visible: $scope.selected.visible,
                 layout: $scope.selected.layout, accessExpression: $scope.selected.accessExpression };
    var promise;
    if($scope.selected.id) {
      promise = $http.put("../api/bennu-portal/menu/" + $scope.selected.id, data);
    } else {
      data.path = $scope.selected.path; data.parent = $scope.selected.parent;
      promise = $http.post("../api/bennu-portal/menu", data);
    }
    promise.success(function (data) {
      var node = $scope.selected.node;
      $scope.selected = data;
      data.node = node;
      node.setTitle(i18n(data.title));
    });
  }
  $scope.loadApps = function() {
    if(!$scope.apps) {
      $http.get("../api/bennu-portal/menu/applications/").success(function (data) {
        $scope.apps = data;
      });
    }
  }
  $scope.installApplication = function() {
    $http.post("../api/bennu-portal/menu/applications/", { root: $scope.selected.id, key: $scope.newApp.key }).success(function (item) {
      add(item, $scope.selected.node);
    });
  }
  $scope.createChild = function() {
    var mls = {}; 
    mls[BennuPortal.locale.tag] = 'New Entry';
    var newChild = { title: mls, description: {}, visible: true, accessExpression: 'anyone', functionality: false, parent: $scope.selected.id };
    add(newChild, $scope.selected.node).setActive(true);
  }
  $scope.saveOrder = function() {
    $http.post("../api/bennu-portal/menu/order", $scope.changes).success(function () {
      $scope.changes = null;
    });
  }

  var add = function(item, parent) {
    var node = parent.addChildren({ title: i18n(item.title), key: item.id, folder: !item.functionality, item: item });
    item.node = node;
    if(item.menu) {
      item.menu.forEach(function (entry) {
        add(entry, node);
      });
    }
    return node;
  }

  $http.get('../api/bennu-portal/menu/' + $scope.id).success(function (data) {
    $scope.locales = BennuPortal.locales;
    $("#tree").fancytree({ source: [ ], extensions: ["dnd"],
      dnd: {
        preventRecursiveMoves: true,
        dragStart: function(node, data) { return true; },
        dragEnter: function(node, data) {
          if(node.parent !== data.otherNode.parent) return false;
          return ["before", "after"];
        },
        dragDrop: function(node, data) {
          $scope.changes = $scope.changes || {};
          data.otherNode.moveTo(node, data.hitMode);
          node.parent.children.forEach(function (node) {
            $scope.changes[node.key] = node.getIndex();
          });
          $scope.$apply();
        }
      },
     });
    var tree = $("#tree").fancytree("getTree");
    $("#tree").bind("fancytreeactivate", function(event, data) {
      $scope.selected = data.node.data.item;
      if(!$scope.$$phase) {
        $scope.$apply();
      }
    });
    add(data, tree.rootNode);
    tree.rootNode.children[0].setActive(true);
    $("#tree ul").focus();
  });
}]);

