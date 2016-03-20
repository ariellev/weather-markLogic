'use strict';

// Declare app level module which depends on views, and components
var weatherApp = angular.module('weatherApp', ['datePicker'])
    .controller('SearchCtrl', ['$scope', '$http',
        function ($scope, $http) {

            var processEvents = function (response) {
                var results = response.data.map(function (e) {
                    e.totalProp = e.propDmg * Math.pow(10, e.propDmgExp);
                    e.totalCrop = e.cropDmg * Math.pow(10, e.cropDmgExp);
                    e.remarks = 'EPISODE NARRATIVE: A thunderstorm produced nickel size hail and estimated 40 mph wind gust.';
                    return e;
                });
                console.log("data=" + JSON.stringify(results));
                $scope.results = results;
            };



            $scope.date = new Date(2013, 9, 22);

            $http.get('http://localhost:8080/weather/v1/events/TORNADO/?').
            then(processEvents);


            $scope.search = function (event) {
                console.log('performing search..')
                var query = event.target.value.trim();

                if (query !== '' && event.keyCode === 13) {
                    $http.get('http://localhost:8080/weather/v1/events/TORNADO/?').
                    then(processEvents);
                }
            };

        }])
    .directive('resultItem', function () {
        return {
            replace: 'true',
            restrict: 'AE',
            scope: {item: '='},
            templateUrl: 'result-item.html'
        }
    });
