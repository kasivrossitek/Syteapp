/**
 * Created by nachiket_bahekar on 3/3/16.
 * Test script to test all analytics reporting.
 * This script populates fake stats and generate reports that can be displayed by the clients.
 */

var path = require('path');
var Firebase = require('firebase');
var configyaspas = require('../configyaspas');

//Create reference to Firebase
//var rootref = new Firebase(configyaspas.firebaseroot);
//var authDbRef = new Firebase(configyaspas.authDbRoot);
var yasPasRootRef = new Firebase(configyaspas.yasPasRoot);
var dailyReportingRootRef = new Firebase(configyaspas.dailyReportingRoot);
var monthlyReportingRootRef = new Firebase(configyaspas.monthlyReportingRoot);

var debug = configyaspas.debug;

function usage() {
    var argv = process.argv;
    console.log("\nUsage: " + path.basename(argv[0]) + " " + path.basename(argv[1]) + " -p or -g <offset days>");
    console.log("\n           -p Polulate dummy stats");
    console.log("\n           -g Generate reporting data");
    console.log("\n<offset days>: Offset Date from today");
    console.log("\n");
};

if (process.argv.length < 3) {
    console.log("Need minimum 3 arguments");
    usage();
    process.exit(0);
}

function getTodayKey(offset) {
    var today = new Date();
    today.setUTCDate(today.getUTCDate() - offset);
    var day = '00' + today.getUTCDate();
    var month = '00' + today.getUTCMonth();     // Month automatically adjusts if rolled into last month

    return month.substr(month.length - 2) + day.substr(day.length - 2) + today.getUTCFullYear();
}

var mode = process.argv[2].trim();
var offset = 0;
var syte_key = '-K8rX1lxMxVxfwBMa_VZ';
if (process.argv.length == 4) {
    if (mode == '-p') {
        offset = parseInt(process.argv[3], 10);
        if (offset > 30) offset = 30;
    } else {
        syte_key = process.argv[3];
    }
}

if (mode == '-p') {
    // Populate random stats for first 100 registered Sytes
    yasPasRootRef.limitToFirst(100).on('child_added', function(data) {
        var syte_key = data.key();
        var today = getTodayKey(offset);
        var syteReportingRoot = dailyReportingRootRef.child(syte_key).child(today);
        syteReportingRoot.transaction(function(currentData) {
            if (currentData === null) {
                return {visits: 1};
            } else {
                currentData.visits += Math.round(Math.random().toPrecision(1)*10);
                return currentData;
            }
        }, function(error, committed, snapshot) {
            if (error) {
                console.log('Transaction failed abnormally on ' + syte_key, error);
            } else if (!committed) {
                console.log('Transaction was not committed for ' + syte_key);
            } else {
                console.log('Updated count for ' + syte_key);
            }
        });
    });
} else {
    // Generate the daily/monthly report
    // For daily report, get last 7 days stats for the sponsored sytes
    dailyReportingRootRef.child(syte_key)
        .orderByKey()
        .limitToFirst(7)
        .once("value", function (data) {
            data.forEach(function (child_snap) {
                console.log(child_snap.key(), ':', child_snap.val().visits);
            })
        });
}