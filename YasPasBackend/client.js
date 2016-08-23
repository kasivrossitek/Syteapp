/**
 * Client to simulate mobile user interactions.
 */

var path = require('path');
var Firebase = require('firebase');
var config = require('./config');

//Create reference to Firebase
var rootref = new Firebase(config.firebaseroot);
var usersref = new Firebase(config.authDbRoot);

var debug = config.debug;

function usage() {
	var argv = process.argv;
	console.log("\nUsage: " + path.basename(argv[0]) + " " + path.basename(argv[1]) + " <country code> <mobile #> [OTP code]");
};

if (process.argv.length < 4) {
	console.log("Need minimum 4 arguments");
	usage();
	process.exit(0);
}

var countryCode = process.argv[2];
var telno = process.argv[3];
var otp = null;
if (process.argv.length > 4) {
	otp = process.argv[4];
}

//Save data to Firebase
if (otp) {
	var childref = usersref.child(telno);
	childref.update({
		receivedOtp: otp,
		modified: Firebase.ServerValue.TIMESTAMP
	});
	//Setup child updated
	childref.on('value', function(snapshot, prevSnapshot) {
		var data = snapshot.val();
		
		if (data.verificationStatus == "VERIFIED") {
			console.log("Verification Successful");
			process.exit(0);
		} 
	});
} else {
	//Register as new user
	var newuser = {};
	newuser[telno] = {
        registeredNum: telno,
        deviceId: "123" + telno,
        countryCode: countryCode,
        deviceToken: "Moto X"
    };

	usersref.update(newuser);

	//Receive OTP from server
	usersref.child(telno).on('value', function(snapshot, prevSnapshot) {
		var data = snapshot.val();

		if (data.hasOwnProperty('generatedOtp')) {
			console.log("OTP From server: ", data.generatedOtp);
			process.exit(0);
		} 
	});
}