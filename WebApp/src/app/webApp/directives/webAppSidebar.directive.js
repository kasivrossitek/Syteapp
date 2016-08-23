(function() {
  'use strict';

  angular
    .module('app.webApp')
    .directive('webAppSidebar', webAppSidebar);

  function webAppSidebar() {
    return {
      templateUrl: 'app/webApp/directives/webAppSidebar.html',
      restrict: 'E',
      controller: WebAppSidebarController,
      controllerAs: 'vm',
      bindToController: true,
      scope: {
        parties: '='
      }
    }
  }

  WebAppSidebarController.$inject = ['yaspasService'];

  function WebAppSidebarController(yaspasService) {
    var vm = this;

    
    }
  

})();
