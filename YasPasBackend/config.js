/**
 * Global configuration settings
 */

var process = require('process');
var fs = require('fs');

var globalconfigfile = '/etc/yaspassettings.js';
var config = {};

//Check if the global configuration settings file exists under /etc
//Else, assume devmode and use environment variables
try {
    fs.access(globalconfigfile, fs.R_OK);
    //Load global configuration settings
    config = require('/etc/yaspassettings.js');
} catch (err) {
    //No global config gile exists.
    //Firebase root reference
    console.log('Using environment settings');
    config.firebaseroot = process.env.FIREBASEROOT;
    config.usersroot = config.firebaseroot + "users/";
    config.authDbRoot = config.firebaseroot + "authDB/";
    config.yasPaseesRoot = config.firebaseroot + "yaspasees/";
    config.accountSid = process.env.ACCOUNTSID;
    config.authToken = process.env.AUTHTOKEN;
    config.fromPhone = process.env.FROMPHONE;
}

config.debug = function (id, obj) {
	console.log(id + ":");
	Object.getOwnPropertyNames(obj).forEach(function(val, idx, array) {
		console.log(val + ' -> ' + obj[val]);
	});
};

// Export as an object
module.exports = config;