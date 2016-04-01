'use strict';

// Declare app level module which depends on views, and components
var weatherApp = angular.module('weatherApp', ['datePicker'])
    .controller('SearchCtrl', ['$scope', '$http',
        function ($scope, $http) {

            var processEvents = function (response) {
                var results = response.data.map(function (e) {
                    e.remarks = 'EPISODE NARRATIVE: A thunderstorm produced nickel size hail and estimated 40 mph wind gust.';
                    return e;
                });
                console.log("data=" + JSON.stringify(results));
                $scope.results = results;
            };


            $scope.form = {
                'query': "",
                'fromDate': new Date(1950, 1, 1),
                'toDate': new Date(),
                'state': "",
                'eventType': ""
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
