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


            $scope.form = {
                'query': "",
                'fromDate' : new Date(1950, 1, 1),
                'toDate' : new Date(),
                'state' : "",
                'eventType' : ""
            };

/*            $http.get('http://localhost:8080/weather/v1/events/TORNADO/?').
            then(processEvents);*/


            $scope.search = function (event) {
                if (event.keyCode === 13) {
                    console.log('performing search..');
                    var str = JSON.stringify($scope.form);
                    console.log('data=' + str);
                    var data = JSON.parse(str);
                    data.fromDate = $scope.form.fromDate.getTime();
                    data.toDate = $scope.form.toDate.getTime();

                    var query = $scope.form.query.trim();
                        $http.post('http://localhost:8080/weather/v1/events/search/?', data).
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
