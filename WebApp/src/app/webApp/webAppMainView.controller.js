(function () {
    'use strict';

    angular
        .module('app.webApp')
        .controller('WebAppMainViewController', WebAppMainViewController);

    WebAppMainViewController.$inject = ['$scope', 'NgMap', 'firebaseDataService', 'geofireService'];

    function WebAppMainViewController($scope, NgMap, firebaseDataService, geofireService) {

        var vm = this;


        var markerList = {};

        vm.mapIdle = mapIdle;
        vm.addMarker = addMarker;
        vm.removeMarker = removeMarker;
        vm.onReadyRegistration =  onReadyRegistration;
        vm.onKeyEnteredRegistration =  onKeyEnteredRegistration;
        vm.onKeyExitedRegistration = onKeyExitedRegistration;
        vm.onKeyMovedRegistration = onKeyMovedRegistration;


        $scope.mycallback = function (map) {

            vm.mymap = map;
            vm.geoQuery = geofireService.getGeoQuery(map.getCenter(), 10);
            console.log('****Inside mycall back. Center is: '+ map.getCenter());
            vm.geoQuery.on("ready", vm.onReadyRegistration);
            vm.geoQuery.on("key_entered", vm.onKeyEnteredRegistration);
            vm.geoQuery.on("key_exited", vm.onKeyExitedRegistration);
            vm.geoQuery.on("key_moved", vm.onKeyMovedRegistration);

            //vm.geoQuery = geofireService.getGeoQuery(this.localMap.getCenter(),10);
            map.addListener('load', vm.initialize);
            //mapRef.addListener('bounds_changed',vm.centerChanged);
            map.addListener('idle', vm.mapIdle);
            $scope.$apply();
        };



        function addMarker(key, location) {
            var marker = new google.maps.Marker({
                position: new google.maps.LatLng(location[0], location[1]),
                optimized: true,
                map: vm.mymap
            });

            markerList[key] = marker;
            //console.log('******** number of Markers A = '+ Object.keys(markerList).length);
        }

        function removeMarker(key) {
            var marker = markerList[key];
            marker.setMap(null);
            delete markerList[key];
        }

        function mapIdle() {

                var newCenter = new google.maps.LatLng(vm.mymap.getCenter().lat(), vm.mymap.getCenter().lng(), false);
                console.log('Google LatLng value' + newCenter);
                var lat = newCenter.lat();
                var lon = newCenter.lng();

                var bounds = vm.mymap.getBounds();

                var center = bounds.getCenter();
                var ne = bounds.getNorthEast();

                // r = radius of the earth in statute miles
               var r = 3963.0;

                // Convert lat or lng from decimal degrees into radians (divide by 57.2958)
                var lat1 = center.lat() / 57.2958;
                var lon1 = center.lng() / 57.2958;
                var lat2 = ne.lat() / 57.2958;
                var lon2 = ne.lng() / 57.2958;

                // distance = circle radius from center to Northeast corner of bounds
                var dis = r * Math.acos(Math.sin(lat1) * Math.sin(lat2) +
                        Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1));

                var disInKm = dis * 1.6;
                vm.geoQuery.updateCriteria({
                    center: [lat, lon],
                    radius: 30
                });
        }




        var yaspasListRoot = firebaseDataService.yaspasses;

        function onReadyRegistration(){

        }

        function onKeyEnteredRegistration (key, location, distance) {
            console.log(key + " entered query at " + location + " (" + distance + " km from center)");

            vm.addMarker(key, location);

            /*
             var yaspasRef = yaspasListRoot.child(key);
             yaspasRef.once('value',function(snapshot){
             console.log('****Name returned :' + snapshot.val().name);

             })
             */

        };

         function onKeyExitedRegistration (key, location, distance) {
            console.log(key + " exited query to " + location + " (" + distance + " km from center)");
            vm.removeMarker(key);

        };

        function onKeyMovedRegistration (key, location, distance) {
                console.log(key + " moved within query to " + location + " (" + distance + " km from center)");

        };


    };

})();



