/**
 * Global configuration settings
 */
var process = require('process');
var fs = require('fs');

var globalconfigfile = '/etc/yaspassettings.js';
var configyaspas = {};

//Check if the global configuration settings file exists under /etc
//Else, assume devmode and use environment variables
try {
    fs.accessSync(globalconfigfile, fs.R_OK);
    //Load global configuration settings
    configyaspas = require(globalconfigfile);
} catch (err) {
    //No global config gile exists.
    console.log('Using environment settings');
    //configyaspas.firebaseroot = "https://yaspasbeta.firebaseio.com/";
    //configyaspas.firebaseroot = "https://shining-inferno-1102.firebaseio.com/";
    //configyaspas.firebaseroot = "https://glaring-heat-6985.firebaseIO.com/";
    //configyaspas.authDbRoot = configyaspas.firebaseroot + "authDB/";
    //configyaspas.yasPaseesRoot = configyaspas.firebaseroot + "yaspasees/";
    //configyaspas.accountSid = "AC6fefb90f4b0d5a01e2a8b4ca0c218b2d";
    //configyaspas.authToken = "f2c60fa06bde71dc8ea20aef15a6ae60";
    //configyaspas.fromPhone = "+18554651802";
    configyaspas.firebaseroot = process.env.FIREBASEROOT;
    configyaspas.usersroot = configyaspas.firebaseroot + "users/";
    configyaspas.authDbRoot = configyaspas.firebaseroot + "authDB/";
    configyaspas.yasPaseesRoot = configyaspas.firebaseroot + "yaspasees/";
    configyaspas.yasPasRoot = configyaspas.firebaseroot + "yaspas/";
    configyaspas.reportingRoot = configyaspas.firebaseroot + "reporting/";
    configyaspas.dailyReportingRoot = configyaspas.reportingRoot + "daily/";
    configyaspas.monthlyReportingRoot = configyaspas.reportingRoot + "monthly/";
    configyaspas.bulletinBoardPushNotificationRoot = configyaspas.firebaseroot + "bulletinBoardPushNotification/";
    configyaspas.bulletinBoardNotificationInbox = "bulletinBoardNotificationInbox/";
    configyaspas.yasPasPushNotificationRoot = configyaspas.firebaseroot + "yasPasPushNotification/";
    configyaspas.myYasPasesNotificationInbox = "myYasPasesNotificationInbox/";
    configyaspas.tempAnalyticsVisitorRoot = configyaspas.firebaseroot + "tempAnalyticsVisitor/";
    configyaspas.analyticsSyteRoot = configyaspas.firebaseroot + "analyticsSytes/";
    configyaspas.analyticsBulletinsRoot = configyaspas.firebaseroot + "analyticsBulletins/";
    configyaspas.chatPushNotificationRoot = configyaspas.firebaseroot + "chatPushNotification/";
    configyaspas.accountSid = process.env.ACCOUNTSID;
    configyaspas.authToken = process.env.AUTHTOKEN;
    configyaspas.fromPhone = process.env.FROMPHONE;
    configyaspas.gcmApiKey = process.env.GCMAPIKEY;
}

configyaspas.debug = function (id, obj) {
    console.log(id + ":");
    Object.getOwnPropertyNames(obj).forEach(function (val, idx, array) {
        console.log(val + ' -> ' + obj[val]);
    });
};

// Export as an object
module.exports = configyaspas;