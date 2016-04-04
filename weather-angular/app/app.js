'use strict';
// Declare app level module which depends on views, and components
var weatherApp = angular.module('weatherApp', ['datePicker', 'leaflet-directive', 'ngSanitize'])
    .controller('SearchCtrl', ['$filter', '$scope', '$http', 'leafletData',
        function ($filter, $scope, $http, leafletData) {
            // benchmark variables
            var before, after;

            // request controllers
            $scope.pageNum = 0;
            $scope.pageLength = 30;

            $scope.noDataStyle = {'display': 'none'};
            // $scope.mapStyle = {'visibility': 'hidden'};
            $scope.mapStyle = {'visibility': 'hidden'};
            $scope.dataStyle = {'display': 'none'};
            $scope.merticsStyle = {'visibility': 'hidden'};
            $scope.searchingStyle = {'display': 'none'};
            $scope.highlight = {'color': 'red'};

            var geoData = {
                "type": "FeatureCollection",
                "features": []
            };

            var geojsonMarkerOptions = {
                radius: 3,
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

            var mapbox = {
                name: 'Mapbox',
                url: 'https://api.tiles.mapbox.com/v4/{mapid}/{z}/{x}/{y}.png?access_token={apikey}',
                type: 'xyz',
                options: {
                    /*                    apikey: 'pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpandmbXliNDBjZWd2M2x6bDk3c2ZtOTkifQ._QA7i5Mpkd_m30IGElHziw',
                     mapid: 'mapbox.streets'*/
                    apikey: 'pk.eyJ1IjoiYXJpZWxsZXYwMiIsImEiOiIwZGVmOWI1NmYzMzE3ZmI3MWUwNmQzNDk1NDMyYzE5OSJ9.BTrDAcc81lL5FReG_ljgag',
                    mapid: 'ariellev02.md09o8kh',
                    attribution: 'Maps by <a href="http://www.mapbox.com/">Mapbox</a>'
                }
            };

            angular.extend($scope, {

                center: usa,
                tiles: mapbox,

                markers: {},

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
                    scrollWheelZoom: false
                }
            });


            var processData = function (response) {
                var min_lat = Number.MAX_VALUE, min_lng = Number.MAX_VALUE, max_lat = Number.MIN_VALUE, max_lng = -Number.MAX_VALUE;

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
                            "coordinates": [e.longitude, e.latitude]
                        },
                        "properties": {
                            "GPSId": e.id,
                            "GPSUserName": e.id,
                            "GPSUserColor": "#FF5500",
                            "description": "<b>" + e.event_type + "</b><br/>" + $filter('date')(e.date, 'longDate')
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
                var maxbounds = {
                    southWest: L.GeoJSON.coordsToLatLng([min_lng, min_lat]),
                    northEast: L.GeoJSON.coordsToLatLng([max_lng, max_lat])
                };

                var southWest = L.latLng(min_lat, min_lng),
                    northEast = L.latLng(max_lat, max_lng),
                    bounds = L.latLngBounds(southWest, northEast);

                console.log(JSON.stringify(bounds));

                leafletData.getMap().then(function (map) {
                    console.log("leafletData.getMap");
                    console.log(JSON.stringify(bounds));

                    map.fitBounds(bounds);
                });

                //console.log(JSON.stringify(response));
                $scope.results = response.data.map(function (e) {
                    if ((typeof e.snippet === 'undefined') || e.snippet.length < "<span class='highlight'></span>".length + 20) {
                        e.snippet = e.remarks;
                    }
                    return e;
                });

                $scope.merticsStyle = {'visibility': 'visible'};
                $scope.dataStyle = {'display': 'inline'};
                $scope.noDataStyle = {'display': 'none'};
                $scope.mapStyle = {'visibility': 'visible'};
                $scope.totalResults = response.data.length;
                $scope.totalSeconds = (after - before) / 1000;
            };

            var processNoData = function (response) {
                $scope.geojson.data = [];
                $scope.dataStyle = {'display': 'none'};
                $scope.noDataStyle = {'display': 'inline'};
            };

            var processEvents = function (response) {
                after = Date.now();
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
                console.log('event.target.id=' + event.target.id);
                console.log('event.keyCode=' + event.keyCode);

                if (event.keyCode === 13 || event.target.id == "searchButton") {
                    $scope.pageNum = 0;
                    var str = JSON.stringify($scope.form);
                    var data = JSON.parse(str);
                    data.fromDate = $scope.form.fromDate.getTime();
                    data.toDate = $scope.form.toDate.getTime();
                    $scope.payload = data;

                    $scope.merticsStyle = {'visibility': 'hidden'};

                    performSearch(data, $scope.pageNum, $scope.pageLength);
                }
            };

            var performSearch = function (payload, pageN, pageL) {
                var uri = 'http://localhost:8080/weather/v1/events/search/?pageLength=' + pageL + '&pageNum=' + pageN;
                console.log('performing search.., uri=' + uri);
                console.log('data=' + JSON.stringify(payload));
                $scope.dataStyle = {'display': 'none'};
                $scope.noDataStyle = {'display': 'none'};
                $scope.searchingStyle = {'display': 'inline'};

                before = Date.now();
                //$http.get('response.json').
                $http.post(uri, payload).
                then(processEvents);
            };

            $scope.nextPage = function (event) {
                if ($scope.totalResults == $scope.pageLength) {
                    $scope.pageNum++;
                    console.log("nextPage, pageNum=" + $scope.pageNum);
                    performSearch($scope.payload, $scope.pageNum, $scope.pageLength);
                }
            };

            $scope.previousPage = function (event) {
                if ($scope.pageNum > 0) {
                    $scope.pageNum--;
                    console.log("nextPage, pageNum=" + $scope.pageNum);
                    performSearch($scope.payload, $scope.pageNum, $scope.pageLength);
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
