/**
 * This module process visitors' analytics
 * for a Syte or a bulletin KP
 */
var Firebase = require('firebase');
var configyaspas = require('./configyaspas');

function VisitorAnalyticsClient() { } // END PushNotificationClient

//Function to accept day of the week and returns day name or weekday - KP
function getWeekDay(dayNumber) {
    return ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"][dayNumber];
}

//Function to accept month and returns month name - KP
function getMonthName(monthNumber) {
    return ["January", "February", "March", "April", "May", "June", "July", "August", "September",
            "October", "November", "December"][monthNumber];
}

//Function to create daily visitor analytics key
function getDailyVisitorAnalyticsKey(date, month, year) {
    var updatedMonth = month + 1;
    if (updatedMonth <= 9) {
        updatedMonth = "0" + updatedMonth;
    }
    return (year + updatedMonth + date);
}

//Function to create monthly visitor analytics key
function getMonthlyVisitorAnalyticsKey(month, year) {
    return year + getMonthName(month);
}

function getCountry(visitorLatitude, visitorLongitude, tempAnalyticsVisitorRef, snapshot) {
    var extra = {
        apiKey: configyaspas.gcmApiKey,
        formatter: null
    };
    var geocoder = require('node-geocoder')("google", "https", extra);
    geocoder.reverse({lat: visitorLatitude, lon: visitorLongitude})
        .then(function (res) {
            if (res[0]) {
                console.log(res);
                var geocoderResponse = res[0];
                var geoCity = "Unknown";
                var geoState = "Unknown";
                var geoCountry = "Unknown";
                if (geocoderResponse.city) {
                    geoCity = geocoderResponse.city;
                }
                if (geocoderResponse.country) {
                    geoCountry = geocoderResponse.country;
                }
                if (geocoderResponse.administrativeLevels) {
                    var geoAdministrativeLevels = geocoderResponse.administrativeLevels;
                    if (geoAdministrativeLevels.level1long) {
                        geoState = geoAdministrativeLevels.level1long;
                    }
                }
                insertAnalyticsData(tempAnalyticsVisitorRef, snapshot, geoCity, geoState, geoCountry);
            } else {
                insertAnalyticsData(tempAnalyticsVisitorRef, snapshot, "Unknown", "Unknown", "Unknown");
            }
        })
        .catch(function (err) {
            insertAnalyticsData(tempAnalyticsVisitorRef, snapshot, "Unknown", "Unknown", "Unknown");
        });
}

//Function to process a new vistor for either a Syte or a Bulletin - KP
VisitorAnalyticsClient.prototype.processAnalyticsVisitor = function (tempAnalyticsVisitorRef, snapshot) {
    var paramSnapshotKey = snapshot.key();
    var paramSnapshotData = snapshot.val();
    var visitorRegisteredNum = paramSnapshotData.visitorRegisteredNum;
    var visitorGender = paramSnapshotData.visitorGender;
    var visitedSyteId = paramSnapshotData.visitedSyteId;
    var visitedBulletinId = paramSnapshotData.visitedBulletinId;
    var visitorLatitude = paramSnapshotData.visitorLatitude;
    var visitorLongitude = paramSnapshotData.visitorLongitude;
    var visitedTime = paramSnapshotData.visitedTime;

    if (!paramSnapshotData.hasOwnProperty('isProcessed')) {
        getCountry(visitorLatitude, visitorLongitude, tempAnalyticsVisitorRef, snapshot);
    }
}; // END processAnalyticsVisitor()

//Function to insert a new vistor for either a Syte or a Bulletin - KP
function insertAnalyticsData(tempAnalyticsVisitorRef, snapshot, paramCity, paramState, paramCountry) {
    var paramSnapshotKey = snapshot.key();
    var paramSnapshotData = snapshot.val();
    var visitorRegisteredNum = paramSnapshotData.visitorRegisteredNum;
    var visitorGender = paramSnapshotData.visitorGender;
    var visitedSyteId = paramSnapshotData.visitedSyteId;
    var visitedBulletinId = paramSnapshotData.visitedBulletinId;
    var visitorLatitude = paramSnapshotData.visitorLatitude;
    var visitorLongitude = paramSnapshotData.visitorLongitude;
    var visitedTime = paramSnapshotData.visitedTime;
    var converteddate = new Date(visitedTime);
    var date = converteddate.getDate(); // Returns the day of the month (from 1-31)
    var dayNumber = converteddate.getDay(); // Returns the day of the week (from 0-6)
    var monthNumber = converteddate.getMonth(); // Returns the month (from 0-11)
    var year = converteddate.getFullYear(); // Returns the year
    var dayName = getWeekDay(dayNumber);
    var monthName = getMonthName(monthNumber);
    var dailyAnalyticsKey = getDailyVisitorAnalyticsKey(date, monthNumber, year);
    var monthlyAnalyticsKey = getMonthlyVisitorAnalyticsKey(monthNumber, year);

    //Checking which visit is it, bulletin or syte?
    if (visitedBulletinId == "") {
        var analyticsSytesRefRoot = new Firebase(configyaspas.analyticsSyteRoot);
        var analyticsSytesRef = analyticsSytesRefRoot.child(visitedSyteId);
    } else {
        var analyticsSytesRefRoot = new Firebase(configyaspas.analyticsBulletinsRoot);
        var analyticsSytesRef = analyticsSytesRefRoot.child(visitedSyteId).child(visitedBulletinId);

    }
    // DAILY
    analyticsSytesRef.child("daily/").child(dailyAnalyticsKey).child("allVisits/")
        .orderByChild("visitorRegisteredNum").startAt(visitorRegisteredNum).endAt(visitorRegisteredNum).once("value", function (querySnapshot) {
        var queryData = querySnapshot.val() == null ? true : false;
        var firebaseDailyAnalyticsRef = analyticsSytesRef.child("daily/").child(dailyAnalyticsKey);
        firebaseDailyAnalyticsRef.transaction(function (currentData) {
            if (currentData === null) {
                return {dayName: dayName, totalVisits: 1, uniqueVisits: 1};
            } else {
                currentData.totalVisits = currentData.totalVisits + 1;
                if (queryData) {
                    currentData.uniqueVisits = currentData.uniqueVisits + 1;
                }
                return currentData;
            }
        }, function (error, committed, snapshot) {
            if (error) {
                //console.log('Transaction failed abnormally on ' + visitedSyteId, error);
            } else if (!committed) {
                console.log('Transaction was not committed for ' + visitedSyteId);
                console.log("error " + error);
            } else {
                console.log('Updated count for ' + visitedSyteId);
                var firebaseDailyAnalyticsAllRef = analyticsSytesRef.child("daily/").child(dailyAnalyticsKey).child("allVisits/");
                firebaseDailyAnalyticsAllRef.push().set({
                    visitorRegisteredNum: visitorRegisteredNum,
                    visitorGender: visitorGender,
                    visitorLatitude: visitorLatitude,
                    visitorLongitude: visitorLongitude,
                    visitedTime: visitedTime,
                    city: paramCity,
                    state: paramState,
                    country: paramCountry
                });
            }
        });
    }); // END DAILY
    // MONTHLY -------------------------------------
    analyticsSytesRef.child("monthly/").child(monthlyAnalyticsKey).child("allVisits/")
        .orderByChild("visitorRegisteredNum").startAt(visitorRegisteredNum).endAt(visitorRegisteredNum).once("value", function (querySnapshot1) {
        var queryData1 = querySnapshot1.val() == null ? true : false;
        var firebaseMonthlyAnalyticsRef = analyticsSytesRef.child("monthly/").child(monthlyAnalyticsKey);
        firebaseMonthlyAnalyticsRef.transaction(function (currentData1) {
            if (currentData1 === null) {
                return {month: monthName, year: year, totalVisits: 1, uniqueVisits: 1};
            } else {
                currentData1.totalVisits = currentData1.totalVisits + 1;
                if (queryData1) {
                    currentData1.uniqueVisits = currentData1.uniqueVisits + 1;
                }
                return currentData1;
            }
        }, function (error, committed, snapshot) {
            if (error) {
                console.log('Transaction failed abnormally on ' + visitedSyteId, error);
            } else if (!committed) {
                console.log('Transaction was not committed for ' + visitedSyteId);
                console.log("error " + error);
            } else {
                console.log('Updated count for ' + visitedSyteId);
                var firebaseMonthlyAnalyticsAllRef = analyticsSytesRef.child("monthly/").child(monthlyAnalyticsKey).child("allVisits/");
                firebaseMonthlyAnalyticsAllRef.push().set({
                    visitorRegisteredNum: visitorRegisteredNum,
                    visitorGender: visitorGender,
                    visitorLatitude: visitorLatitude,
                    visitorLongitude: visitorLongitude,
                    visitedTime: visitedTime,
                    city: paramCity,
                    state: paramState,
                    country: paramCountry
                });
                var t = tempAnalyticsVisitorRef.child(paramSnapshotKey);
                t.update({
                    isProcessed: 1
                });
                t.remove();
            }
        });
    }); // END MONTHLY
}

exports.VisitorAnalyticsClient = VisitorAnalyticsClient;