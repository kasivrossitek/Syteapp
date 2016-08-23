(function() {
    'use strict';

    angular
        .module('app.core')
        .factory('geofireService', geofireService);

    geofireService.$inject = ['$firebaseArray', 'firebaseDataService'];

    function geofireService($firebaseArray, firebaseDataService) {

        
        var service = {
            getGeoQuery: getGeoQuery,
           
        };

        return service;

        ////////////
        var vm = this;
        var lat = 37.363947;
        var lon = -121.928938;
        var rad = 10;
        

        function getGeoQuery(center, radius) {
            var geoFireRef =  new GeoFire(firebaseDataService.geoFire);
            
            return geoFireRef.query({
                center: [37.363947,-121.928938],
                radius: 10
            });
        }

        
    }

})();