(function() {
    'use strict';

    angular
        .module('app.webApp')
        .config(configFunction);

    configFunction.$inject = ['$routeProvider'];

    function configFunction($routeProvider) {
        $routeProvider.when('/webApp', {
            templateUrl: 'app/webApp/webAppMainView.html',
            controller: 'WebAppMainViewController',
            controllerAs: 'vm',
            resolve: {user: resolveUser}
        });

        $routeProvider.when('/syteHome', {
            templateUrl: 'app/webApp/syteHome.html',
            controller: 'WebAppMainViewController',
            controllerAs: 'vm',
            resolve: {user: resolveUser}
        });

    }

    resolveUser.$inject = ['authService'];

    function resolveUser(authService) {
        return authService.firebaseAuthObject.$requireAuth();
    }

})();
