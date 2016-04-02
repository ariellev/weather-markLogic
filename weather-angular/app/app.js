'use strict';
// Declare app level module which depends on views, and components
var weatherApp = angular.module('weatherApp', ['datePicker', 'leaflet-directive', 'ngSanitize'])
    .controller('SearchCtrl', ['$filter', '$scope', '$http', 'leafletData',
        function ($filter, $scope, $http, leafletData) {
            $scope.noDataStyle = {'display': 'none'};
            $scope.mapStyle = {'visibility': 'hidden'};
            $scope.dataStyle = {'display': 'none'};
            $scope.searchingStyle = {'display': 'none'};
            $scope.highlight = {'color': 'red'};

            var geoData = {
                "type": "FeatureCollection",
                "features": []
            };

            var geojsonMarkerOptions = {
                radius: 10,
                fillColor: 'red',
                color: 'white',
                weight: 1,
                opacity: 0.7,
                fillOpacity: 0.8
            };

            var usa = {
                lat: 45.19,
                lng: -96.27,
                zoom: 6
            };

            angular.extend($scope, {

                center: usa,
                tiles: {
                    name: 'Mapbox',
                    url: 'https://api.tiles.mapbox.com/v4/{mapid}/{z}/{x}/{y}.png?access_token={apikey}',
                    type: 'xyz',
                    options: {
                        apikey: 'pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpandmbXliNDBjZWd2M2x6bDk3c2ZtOTkifQ._QA7i5Mpkd_m30IGElHziw',
                        mapid: 'mapbox.streets'
                    }
                },

                markers: {
                },

                geojson: {
                    data: geoData,
                    pointToLayer: function (feature, latlng) {
                        return L.circleMarker(latlng, geojsonMarkerOptions);
                    },
                    onEachFeature: function (feature, layer) {
                        layer.bindPopup(feature.properties.description);
                    }
                },

                defaults: {
                    scrollWheelZoom: true
                }
            });


            var processData = function(response) {
                var min_lat = Number.MAX_VALUE, min_lng = Number.MAX_VALUE, max_lat = Number.MIN_VALUE, max_lng= -Number.MAX_VALUE;

                var geoFeatures = response.data.map(function (e) {
                    e.latitude = parseFloat(e.latitude);
                    e.longitude = parseFloat(e.longitude);

                    min_lat = Math.min(e.latitude, min_lat);
                    min_lng = Math.min(e.longitude, min_lng);

                    max_lat = Math.max(e.latitude, max_lat);
                    max_lng = Math.max(e.longitude, max_lng);

                    return {
                        "type": "Feature",
                        "geometry": {
                            "type": "Point",
                            "coordinates": [ e.longitude, e.latitude]
                        },
                        "properties": {
                            "GPSId": e.id,
                            "DateTime": "7/3/2013 4:47:15 PM",
                            "GPSUserName": e.id,
                            "GPSUserColor": "#FF5500",
                            "description" : "<b>" + e.event_type + "</b><br/>" + $filter('date')(e.date, 'longDate')
                        }
                    }
                });

                var geoData = {
                    "type": "FeatureCollection",
                    "features": geoFeatures
                };

                $scope.geojson.data = geoData;
                console.log(JSON.stringify(geoData));

                // max_lng = -101.4;
                $scope.maxbounds = {
                    southWest: {
                        lat: min_lat,
                        lng: min_lng
                    },
                    northEast: {
                        lat: max_lat,
                        lng: max_lng
                    }
                };

                console.log(JSON.stringify($scope.maxbounds));
                //console.log(JSON.stringify(response));
                $scope.results = response.data;

                $scope.dataStyle = {'display': 'inline'};
                $scope.noDataStyle = {'display': 'none'};
                $scope.mapStyle = {'visibility': 'visible'};


            };

            var processNoData = function(response) {
                $scope.dataStyle = {'display': 'none'};
                $scope.noDataStyle = {'display': 'inline'};
            };

            var processEvents = function (response) {
                $scope.searchingStyle = {'display': 'none'};

                if (response.data.length > 0) {
                    processData(response);
                } else processNoData(response);
            };


            $scope.form = {
                'query': "",
                'fromDate': new Date(1950, 1, 1),
                'toDate': new Date(),
                'state': "",
                'eventType': ""
            };

            $scope.search = function (event) {
                if (event.keyCode === 13) {
                    console.log('performing search..');
                    $scope.dataStyle = {'display': 'none'};
                    $scope.noDataStyle = {'display': 'none'};
                    $scope.searchingStyle = {'display': 'inline'};
                    $scope.mapStyle = {'visibility': 'hidden'};

                    var str = JSON.stringify($scope.form);
                    console.log('data=' + str);
                    var data = JSON.parse(str);
                    data.fromDate = $scope.form.fromDate.getTime();
                    data.toDate = $scope.form.toDate.getTime();

                    var query = $scope.form.query.trim();
                    //$http.get('response.json').
                    $http.post('http://localhost:8080/weather/v1/events/search/?pageLength=30', data).
                    then(processEvents);
                }
            };

        }])
    /*    .controller('EventCtrl', [function () {
     var vm = this;
     // The model object that we reference
     // on the  element in index.html
     vm.rental = {};

     // An array of our form fields with configuration
     // and options set. We make reference to this in
     // the 'fields' attribute on the  element
     vm.rentalFields = [
     {
     key: 'first_name',
     type: 'input',
     templateOptions: {
     type: 'text',
     label: 'First Name',
     placeholder: 'Enter your first name',
     required: true
     }
     },
     {
     key: 'last_name',
     type: 'input',
     templateOptions: {
     type: 'text',
     label: 'Last Name',
     placeholder: 'Enter your last name',
     required: true
     }
     },
     {
     key: 'email',
     type: 'input',
     templateOptions: {
     type: 'email',
     label: 'Email address',
     placeholder: 'Enter email',
     required: true
     }
     }
     ];
     }])*/
    .directive('resultItem', function () {
        return {
            replace: 'true',
            restrict: 'AE',
            scope: {item: '='},
            templateUrl: 'result-item.html'
        }
    });
