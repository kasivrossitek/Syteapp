(function() {
    'use strict';

    angular
        .module('app.core')
        .factory('yaspasService', yaspasService);

    yaspasService.$inject = ['$firebaseArray', 'firebaseDataService'];

    function yaspasService($firebaseArray, firebaseDataService) {

        var service = {
            getYasPasList: getYasPasList,
            yaspasObj: yaspasObj
        };

        return service;

        ////////////

        function getYasPasList() {
            return $firebaseArray(firebaseDataService.yaspasses);
        }

        function yaspasObj() {
            this.city = '';
            this.country = '';
            this.dateCreated = '' ;
            this.name = '';
        }
    }

})();