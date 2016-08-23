/**
 * Demonstrate Node working with Firebase
 */
var Firebase = require('firebase');
var config = require('./config');
var OTPPassword = require('./OTPPassword');

//Create reference to Firebase
var rootref = new Firebase(config.firebaseroot);
var usersref = new Firebase(config.authDbRoot);
var yasPaseesRef = new Firebase(config.yasPaseesRoot);

//SMSClient interface
var SMSClient = new OTPPassword.SMSClient(config.accountSid, config.authToken, config.fromPhone);

//Handle for debug
var debug = config.debug;

function Users(usersref) {
	this._usersref = usersref;
	var self = this;
	this._newUser = function (snapshot) {
		newUser(self._usersref, snapshot);
	};
	this._verifyUser = function (snapshot, prevSnapshot) {
		verifyUser(self._usersref, snapshot, prevSnapshot);
	};
	
	//Setup event listener for new user registering to the service
	this._usersref.on('child_added', this._newUser);
	this._usersref.on('child_changed', this._verifyUser);
	
	console.log("Users: Object created");
}

function generateOTP() {
    var otp = Math.random().toPrecision(6);
    var otp = otp * 1000000;
    return (Math.round(otp).toString());
}

//Handle new user registration event
function newUser(authDbRef, snapshot) {
	var telno = snapshot.key();
	var authDbData = snapshot.val();
	
	debug("newUSer", authDbData);
    //Get deviceId of an existing user, if he/she exists in YasPasees
    var yasPaseeRef = yasPaseesRef.child(telno);
    yasPaseeRef.once("value", function(yassnap) {
        var yasdata = yassnap.val();
        if (yasdata !== null) {
            if (yasdata.hasOwnProperty('deviceId')) {
                var yasPaseesDeviceId = yasdata.deviceId;
                // If user is already verfied and trying to login from same device, then his "verificationStatus" is
                // verfied and can go to client home screen with OTP verification
                var verificationStatus;
                if (authDbData.deviceId.localeCompare(yasPaseesDeviceId) == 0 && yasdata.authenticated) {
                    verificationStatus = "VERIFIED";
                    console.log("Device already authenticated");
                } else {
                    // Either user is not verfied or user is trying to login from another device
                    verificationStatus = "PENDING";
                }
                var childref = authDbRef.child(telno);
                var otp = generateOTP();
                childref.update({
                    generatedOtp: otp,
                    createdAt: Firebase.ServerValue.TIMESTAMP,
                    verificationStatus: verificationStatus
                });
            } else {
                // Error handling - if particular mobile number is present in YasPasees, but deviceId is missing
                // from it - Very very rare to occur.
                var childref = authDbRef.child(telno);
                var otp = generateOTP();
                childref.update({
                    generatedOtp: otp,
                    createdAt: Firebase.ServerValue.TIMESTAMP,
                    verificationStatus: "PENDING"
                });
            }
        } else {
            // If entered mobile number is not present in YasPasees, it is a first time login from that number
            var childref = usersref.child(telno);
            var otp = generateOTP();
            childref.update({
                generatedOtp: otp,
                createdAt: Firebase.ServerValue.TIMESTAMP,
                verificationStatus: "PENDING"
            });
            //Send the SMS to the registered phone
            SMSClient.SendSMS(telno, 'Your YasPas code is ' + otp);
        }
    });
}

//Gets called when the user enters OTP
function verifyUser(usersref, snapshot, prevSnapshot) {
	var telno = snapshot.key();
	var data = snapshot.val();
	var childref = usersref.child(telno);
	
	debug("verifyUser", data);
	if (data.hasOwnProperty('OTPReceived') 
			&& (data.Status == "PENDING")) {
		if (data.OTPSent == data.OTPReceived) {
			//Matches the user input received
			childref.update({
				Status: "VERIFIED",
				modified: Firebase.ServerValue.TIMESTAMP
			});
			console.log("verifyUser: Verified user: " + telno);
		} else {
			console.log("verifyUser: Wrong input from user: " + data.OTPReceived);
		}
	}
}

//Create main object
var users = new Users(usersref);