var Firebase = require('firebase');
var configyaspas = require('./configyaspas');
var OTPPassword = require('./OTPPassword');
var PushNotification = require('./PushNotification');
var VisitorAnalytics = require('./VisitorAnalytics');

//Create reference to Firebase
var rootref = new Firebase(configyaspas.firebaseroot);
var authDbRef = new Firebase(configyaspas.authDbRoot);
var yasPasRef = new Firebase(configyaspas.yasPasRoot);
var yasPaseesRef = new Firebase(configyaspas.yasPaseesRoot);
var bulletinBoardPushNotificationRef = new Firebase(configyaspas.bulletinBoardPushNotificationRoot);
var yasPasPushNotificationRef = new Firebase(configyaspas.yasPasPushNotificationRoot);
var tempAnalyticsVisitorRef = new Firebase(configyaspas.tempAnalyticsVisitorRoot);
var analyticsSytesRef = new Firebase(configyaspas.analyticsSyteRoot);
var chatPushNotificationRef = new Firebase(configyaspas.chatPushNotificationRoot);
var OTP_TIMEOUT = 300       // Seconds before it is too late to honor OTP

//SMSClient interface
var SMSClient = new OTPPassword.SMSClient(configyaspas.accountSid, configyaspas.authToken, configyaspas.fromPhone);

//SendPushNotification interface
var PushNotificationClient = new PushNotification.PushNotificationClient(configyaspas.gcmApiKey);
var VisitorAnalyticsClient = new VisitorAnalytics.VisitorAnalyticsClient();

var debug = configyaspas.debug;

function Users() {
    this._newUser = function (snapshot) {
        newUser(authDbRef, snapshot);
    };
    this._verifyUser = function (snapshot) {
        verifyUser(authDbRef, snapshot);
    };
    this._bulletinBoardSendPushNotificationNew = function (snapshot) {
        sendBulletinPushMsgNew(bulletinBoardPushNotificationRef, snapshot);
    };
    this._yasPasSendPushNotificationNew = function (snapshot) {
        sendFollowingPushMsgNew(yasPasPushNotificationRef, snapshot);
    };
    this._tempAnalyticsVisitorAdded = function(snapshot)
    {
        AnalyticsVisitorInitiation(tempAnalyticsVisitorRef, snapshot);
    };
    this._chatPushNotificationNew = function(snapshot)
    {
        sendChatPushMsg(chatPushNotificationRef, snapshot);
    };

    //Setup event listener for new user registering to the service
    authDbRef.on('child_added', this._newUser);
    authDbRef.on('child_changed', this._verifyUser);
    bulletinBoardPushNotificationRef.on('child_added', this._bulletinBoardSendPushNotificationNew);
    yasPasPushNotificationRef.on('child_added', this._yasPasSendPushNotificationNew);
    tempAnalyticsVisitorRef.on('child_added', this._tempAnalyticsVisitorAdded);
    chatPushNotificationRef.on('child_added', this._chatPushNotificationNew);
    console.log("Starting server...");
}

// Generate OTP and return as string
function generateOTP() {
    var otp = Math.random().toPrecision(6);
    otp = otp * 1000000;
    otp = '000' + Math.round(otp).toString();
    otp = otp.substr(otp.length - 6);
    console.log("Generated OTP: " + otp);
    return (otp);
}

// Utility function to update authDB child
function updateChild(childref, otp, verificationStatus) {
    var status = {};
    status.createdAt = Firebase.ServerValue.TIMESTAMP;
    status.verificationStatus = verificationStatus;
    if(otp) status.generatedOtp = otp;

    childref.update(status);
}

//Handle new user registration event
function newUser(authDbRef, snapshot) {
    var telno = snapshot.key();
    var authDbData = snapshot.val();
    var countryCode = authDbData.countryCode;
    var childref = authDbRef.child(telno);
    console.log("newUser telno " + telno);

    //Getting deviceId of an existing user, if he exists in YasPasees
    var yasPaseeRef = new Firebase(yasPaseesRef + "/" + telno);
    var yasPaseesDeviceId = "new user";
    yasPaseeRef.once("value", function (snapshot1) {
        var yasPaseesData = snapshot1.val();
        //console.log("hj"+yasPaseesData);
        if (yasPaseesData !== null) {
            if (yasPaseesData.hasOwnProperty('deviceId')) {
                yasPaseesDeviceId = yasPaseesData.deviceId;
                // If user is already verfied and trying to login from same device, then his "verificationStatus" is verfied
                // and can go to client home screen with OTP verification
                if (authDbData.deviceId.localeCompare(yasPaseesDeviceId) == 0 && yasPaseesData.authenticated) {
                    console.log("Device already authenticated: " + telno);
                    // Just update something so that the client can continue. The client should check the verification status
                    // before trying to register
                    updateChild(childref, null, "VERIFIED");
                }
                else {
                    // Handle server startup scenario. Ignore too old attempts
                    if (Math.floor((Date.now() - authDbData.createdAt)/1000) > OTP_TIMEOUT) return;
                    // Either user is not verfied or user is trying to login from another device
                    console.log("User is trying to login from another device: " + telno);
                    var otp = generateOTP();
                    updateChild(childref, otp, "PENDING");
                    //Send the SMS to the registered phone
                    SMSClient.SendSMS(countryCode + telno, 'Your GOSYTE verification code is ' + otp);
                }
            }
            else {
                // Error handling - if particular mobile number is present in YasPasees, but deviceId is missing from it - Very very rare to occur.
                console.log("Number in YasPasees but missing from authDB" + telno);
                var otp = generateOTP();
                updateChild(childref, otp, "PENDING");
                //Send the SMS to the registered phone
                SMSClient.SendSMS(countryCode + telno, 'Your GOSYTE verification code is ' + otp);
            }
        }
        else {
            // Handle the case when starting up and there new registration entries still in PENDING state
            if(authDbData.hasOwnProperty('generatedOtp')) {
                console.log('Skipping pending: ' + telno);
                return;
            }
            // If entered mobile number is not present in YasPasees, it is a first time login from that number
            console.log("First time login for: " +telno);
            var otp = generateOTP();
            updateChild(childref, otp, "PENDING");
            //Send the SMS to the registered phone
            SMSClient.SendSMS(countryCode + telno, 'Your GOSYTE verification code is ' + otp);
        }
    });
}

//Gets called when the user enters OTP
function verifyUser(authDbRef, snapshot) {
    var telno = snapshot.key();
    var authDbData = snapshot.val();
    var childref = authDbRef.child(telno);

    if (authDbData.hasOwnProperty('recievedOtp')
        && (authDbData.verificationStatus == "PENDING" || authDbData.verificationStatus == "WRONG")
        && authDbData.recievedOtp.localeCompare("0") != 0) {
        // Read the modified time from server
        var timeDiff = Math.floor((authDbData.modified - authDbData.createdAt) / 1000);
        console.log("timeDiff : " + timeDiff);
        if (timeDiff > OTP_TIMEOUT) {
            childref.update({verificationStatus: "EXPIRED"});
            console.log("OTP Expired for: " + telno);
        } else {
            if (authDbData.generatedOtp == authDbData.recievedOtp) {
                childref.update({
                    verificationStatus: "VERIFIED"
                });
                console.log("Authentication successful for: " + telno);
                var yasPaseeRef = yasPaseesRef.child(telno);
                yasPaseeRef.update({
                    authenticated: true,
                    deviceId: authDbData.deviceId,
                    deviceToken: authDbData.deviceToken
                });
            } else {
                console.log("Wrong OTP entered for: " + telno);
                childref.update({
                    verificationStatus: "WRONG"
                });
            }
        }
    }
}

// Function to send bulletins' notifications to Sytes' followers and
// to initimate Syte's claimer if his request is been accepted or rejected
function sendBulletinPushMsgNew(bulletinBoardPushNotificationRef, paramSnapshot) {
    var PushMessageKey = paramSnapshot.key();
    var PushMessageData = paramSnapshot.val();

    var syteId=PushMessageData.syteId;
    var syteImageUrl="";
    var syteName="";
    var pushType = PushMessageData.pushType;
    var bulletinDateTime = PushMessageData.bulletinDateTime;

    var SyteRef = yasPasRef.child(syteId);
    // Checking if Push notification is already sent or not
    if (!PushMessageData.hasOwnProperty('isSent')) {
        if (pushType == 1) {
            // Push Notification is a bulletin
            var bulletinId=PushMessageData.bulletinId;
            var bulletinSubject=PushMessageData.bulletinSubject;
            var bulletinImageUrl=PushMessageData.bulletinImageUrl;

            SyteRef.once("value", function(snapsht) {
                var syteData = snapsht.val();
                syteImageUrl = syteData ? syteData.imageUrl : "";
                syteName = syteData ? syteData.name : "";

                // Checking if there is any follower for the Syte
                if (syteData && snapsht.val().hasOwnProperty('followerYasPasees')) {
                    var follwerYasPasees = snapsht.child('followerYasPasees');
                    var follwerYasPaseesSize = follwerYasPasees.numChildren()
                    if (follwerYasPaseesSize > 0) {
                        var deviceTokens = Array();
                        var yasPasses = Array();
                        follwerYasPasees.forEach(function(childSnapshot) {
                            var key = childSnapshot.key();
                            var childData = childSnapshot.val();
                            deviceTokens.push(childData.deviceToken);
                            yasPasses.push(key);
                            var pushMessage = syteName+" has posted a new bulletin "+bulletinSubject;
                            follwerYasPaseesSize=follwerYasPaseesSize-1;

                            if (follwerYasPaseesSize == 0) {
                                for (var i = 0; i < yasPasses.length; i++) {
                                    // Writing Notification to each YasPasee's bulletinBoardNotificationInbox
                                    var yasPaseeBulletinBoardNotificationInbox = yasPaseesRef.child(yasPasses[i])
                                        .child(configyaspas.bulletinBoardNotificationInbox).child(bulletinId);
                                    yasPaseeBulletinBoardNotificationInbox.set({
                                        syteId: syteId,
                                        syteImageUrl: syteImageUrl,
                                        syteName: syteName,
                                        bulletinId: bulletinId,
                                        bulletinSubject: bulletinSubject,
                                        bulletinImageUrl: bulletinImageUrl,
                                        bulletinDateTime: bulletinDateTime,
                                        pushType: pushType

                                    });
                                    yasPaseeBulletinBoardNotificationInbox.setPriority(0 - bulletinDateTime);
                                    if (i == yasPasses.length - 1) {
                                        PushNotificationClient.PushMessage(pushMessage, deviceTokens, bulletinImageUrl,
                                            syteImageUrl, syteName, pushType);
                                        var t = bulletinBoardPushNotificationRef.child(PushMessageKey);
                                        t.update({
                                            isSent:0
                                        });
                                    }
                                }
                            }
                        });
                    }
                }
            });
        } else if (pushType==4 || pushType ==5) {
            var claimerKey = PushMessageData.registeredNum;

            // Syte claim is either accepted or rejected
            SyteRef.once("value", function(snapsht) {
                var syteData = snapsht.val();
                syteImageUrl = syteData ? syteData.imageUrl : "";
                syteName = syteData ? syteData.name : "";
                // Checking if Syte, its existance -highly unlikely, is deleted or not. If not then proceeding with further action
                if (syteData && syteData.isDeleted == 0) {
                    var deviceTokens1 = Array();
                    var yasPaseesRef2 = yasPaseesRef.child(claimerKey);
                    yasPaseesRef2.once("value", function(snapshotClaimer) {
                        deviceTokens1.push(snapshotClaimer.val().deviceToken);
                        var claimerBulletinBoardNotificationInbox = yasPaseesRef2.child(configyaspas.bulletinBoardNotificationInbox)
                            .child(PushMessageKey);
                        claimerBulletinBoardNotificationInbox.set({
                            syteId: syteId,
                            syteImageUrl: syteImageUrl,
                            syteName: syteName,
                            bulletinDateTime: bulletinDateTime,
                            pushType: pushType
                        });
                        claimerBulletinBoardNotificationInbox.setPriority(0-bulletinDateTime);
                        // Send Notification
                        var pushMessage1="";
                        if (pushType == 4) {
                            pushMessage1="Your syte claim request for "+syteName+" has been rejected";
                        } else if (pushType==5) {
                            pushMessage1="Your syte claim request for "+syteName+" has been accepted";
                        }
                        PushNotificationClient.PushMessage(pushMessage1, deviceTokens1, "", syteImageUrl, syteName, pushType);
                        var t = bulletinBoardPushNotificationRef.child(PushMessageKey);
                        t.update({
                            isSent:0
                        });
                    });
                }
            });
        }else if(pushType==8||pushType==9){ //start modified on 9/6/16 for invitefriends by kasi
						
						//var syteownerkey = PushMessageData.registeredNum;
						// Syte invite to follow is either accepted or rejected
						SyteRef.once("value", function(snapsht)
							{
								var syteData = snapsht.val();
								syteImageUrl = syteData ? syteData.imageUrl : "";
								syteName = syteData ? syteData.name : "";
								var syteownerkey=syteData ? syteData.owner :"";
								// Checking if Syte, its existance -highly unlikely, is deleted or not. If not then proceeding with further action
								if(syteData && syteData.isDeleted==0)
									{
										var deviceTokens1 = Array();
										var yasPaseesRef2 = new Firebase(configyaspas.yasPaseesRoot).child(syteownerkey);
										yasPaseesRef2.once("value", function(snapshotClaimer)
											{
												deviceTokens1.push(snapshotClaimer.val().deviceToken);
												var claimerBulletinBoardNotificationInbox = yasPaseesRef2.child(configyaspas.bulletinBoardNotificationInbox).child(PushMessageKey);
												claimerBulletinBoardNotificationInbox.set(
													{
														syteId: syteId,
														syteImageUrl: syteImageUrl,
														syteName: syteName,
														bulletinDateTime: bulletinDateTime,
														pushType: pushType
													});
												claimerBulletinBoardNotificationInbox.setPriority(0-bulletinDateTime);
												// Send Notification
												var pushMessage1="";
												if(pushType==8)
													{
														pushMessage1="Your syte follow request for "+syteName+" has been accepted";
													}
												else if(pushType==9)
													{
														pushMessage1="Your syte follow request for "+syteName+" has been rejected";
													}
														
												PushNotificationClient.PushMessage(pushMessage1,deviceTokens1,"",syteImageUrl,syteName,pushType);
												var t = bulletinBoardPushNotificationRef.child(PushMessageKey);
												t.update
													({
														isSent:0
													});
											});
									}
							}); 
		}//end modified on 9/6/16 for invite friends by kasi
    }
} // END sendBulletinPushMsg()

//Function to send push notification to Syte's owner for a new follower
// and to initimate him for any of his Syte claim
function sendFollowingPushMsgNew(yasPasPushNotificationRef, paramSnapshot) {
    var paramSnapshotKey = paramSnapshot.key();
    var paramSnapshotData = paramSnapshot.val();
    var syteId = paramSnapshotData.syteId;
    var syteName = paramSnapshotData.syteName;
    var syteOwner = paramSnapshotData.syteOwner;
    var registeredNum = paramSnapshotData.registeredNum;
    var userName = paramSnapshotData.userName;
    var userProfilePic = paramSnapshotData.userProfilePic;
    var pushType = paramSnapshotData.pushType;
    var dateTime = paramSnapshotData.dateTime;

    // Checking if Push message is being sent already, as "child_added" will be called if server is re-started again
    if (!paramSnapshotData.hasOwnProperty('isSent')) {
		if(pushType==2||pushType==3){ //add modified on 9/6/16 for invite friends by kasi
        // Creating YasPasees - owner reference
        var yasPaseesOwnerRef = yasPaseesRef.child(syteOwner);
        yasPaseesOwnerRef.once("value", function(ownerSnapshot) {
            var ownerData = ownerSnapshot.val();
            var ownerDeviceToken = ownerData ? ownerData.deviceToken : "";
            var deviceTokens = Array();
            deviceTokens.push(ownerDeviceToken);

            //Checking if Owner exists
            if (ownerData) {
                // Writing Notification to owner's myYasPasesNotificationInbox
                var myYasPasesNotificationInboxRef = yasPaseesOwnerRef.child(configyaspas.myYasPasesNotificationInbox).child(paramSnapshotKey);
                myYasPasesNotificationInboxRef.set({
                    syteId: syteId,
                    syteName: syteName,
                    registeredNum: registeredNum,
                    userName: userName,
                    userProfilePic: userProfilePic,
                    dateTime: dateTime,
                    pushType: pushType
                });
                myYasPasesNotificationInboxRef.setPriority(0-dateTime);
                var pushMessage="";
                if (pushType==2) {
                    pushMessage = syteName+" has a new follower"
                } else if (pushType==3) {
                    pushMessage = userName +" has claimed "+syteName;
                }

                PushNotificationClient.PushMessage(pushMessage, deviceTokens, "", userProfilePic, syteName, pushType);
                var t = yasPasPushNotificationRef.child(paramSnapshotKey);
                t.update({
                    isSent:0
                });
            }
        });
	}else if(pushType==7){//start modified on 9/6/16 for invite friends by kasi
					// Creating YasPasees - registeredNum reference
				var yasPaseesRegNumRef = yasPaseesRef.child(registeredNum);
				yasPaseesRegNumRef.once("value", function(registeredNumSnapshot)
					{
						var registeredNumData = registeredNumSnapshot.val();
						var registeredNumDeviceToken = registeredNumData ? registeredNumData.deviceToken : "";
						var deviceTokens = Array();
						deviceTokens.push(registeredNumDeviceToken);
						//Checking if Owner exists
						if(registeredNumData)
							{
								// Writing Notification to owner's myYasPasesNotificationInbox
								var myYasPasesNotificationInboxRef = yasPaseesRegNumRef.child(configyaspas.myYasPasesNotificationInbox).child(paramSnapshotKey);
								myYasPasesNotificationInboxRef.set
									({
										syteId: syteId,
										syteName: syteName,
										registeredNum: registeredNum,
										userName: userName,
										userProfilePic: userProfilePic,
										dateTime: dateTime,
										pushType: pushType
									});
								myYasPasesNotificationInboxRef.setPriority(0-dateTime);
								var pushMessage="";
								
								if(pushType==7)
									{
										pushMessage = userName +" has invited to follow "+ syteName;
									}
								PushNotificationClient.PushMessage(pushMessage,deviceTokens,"",userProfilePic,syteName,pushType);
								var t = yasPasPushNotificationRef.child(paramSnapshotKey);
									t.update
											({
												isSent:0
											});
							}
					});
		}//end modified on 9/6/16 for invite friends by kasi
    }
}

//Function/injection to start Visitor Analytics proceeding
function AnalyticsVisitorInitiation(tempAnalyticsVisitorRef, snapshot) {
    VisitorAnalyticsClient.processAnalyticsVisitor(tempAnalyticsVisitorRef,snapshot);
}

// Function to send chatPushNotification
function sendChatPushMsg(chatPushNotificationRef, snapshot) {
    var paramSnapshotKey = snapshot.key();
    var paramSnapshotData = snapshot.val();
    var syteId = paramSnapshotData.syteId;
    var cMessage = paramSnapshotData.cMessage;
    var cSenderRegisteredNum =paramSnapshotData.cSenderRegisteredNum;
    var cChatId = paramSnapshotData.cChatId;
    var cSenderName = paramSnapshotData.cSenderName;
    var imgUrl = paramSnapshotData.imgUrl;
    var cSenderType = paramSnapshotData.cSenderType;

    // Checking if Push message is being sent already, as "child_added" will be called if server is re-started again
    if (!paramSnapshotData.hasOwnProperty('isSent')) {
        /* cSenderType = 0, it means msg is sent by yaspasee
         and push msg is supposed to be sent to yaspas owner */
        if (cSenderType == 0) {
            // Getting yaspas data
            var yasPas = yasPasRef.child(syteId);
            yasPas.once("value", function(yasPasSnapshot) {
                var yasPasData = yasPasSnapshot.val();
                //Checking for null yasPasData
                if (yasPasData) {
                    var yasPasOwnerRegNum = yasPasData.owner;
                    var yasPasName = yasPasData.name;
                    // Getting YasPas Owner data
                    var yasPasOwner = yasPaseesRef.child(yasPasOwnerRegNum).child('deviceToken');
                    yasPasOwner.once("value", function(yasPasOwnerSnapshot) {
                        var yasPasOwnerDeviceToken = yasPasOwnerSnapshot.val();
                        // Checking for yasPasOwnerDeviceToken null
                        if (yasPasOwnerDeviceToken) {
                            var deviceTokens = Array();
                            deviceTokens.push(yasPasOwnerDeviceToken);
                            // Sending Push Msg
                            PushNotificationClient.PushMessage(cMessage,
                                deviceTokens,
                                "",
                                imgUrl,
                                cSenderName,
                                6,
                                syteId,
                                cChatId,
                                cSenderType,
                                cSenderRegisteredNum);
                            var t = chatPushNotificationRef.child(paramSnapshotKey);
                            t.update({
                                isSent:0
                            });
                        }
                    });
                }
            });
        }

        /* cSenderType = 1, it means msg is sent by Syte owner
         and push msg is supposed to be sent to yaspas user*/
        if(cSenderType == 1) {
            // Getting yaspas data
            var yasPas = yasPasRef.child(syteId);
            yasPas.once("value", function(yasPasSnapshot) {
                var yasPasData = yasPasSnapshot.val();
                //Checking for null yasPasData
                if (yasPasData) {
                    var yasPasName = yasPasData.name;
                    var yasPasImgUrl = (yasPasData.imageUrl? yasPasData.imageUrl:"");
                    // Getting YasPas User data
                    var firebaseUser = yasPaseesRef.child(cChatId).child('deviceToken');
                    firebaseUser.once("value", function(yasPasUserSnapshot) {
                        var yasPasUserDeviceToken = yasPasUserSnapshot.val();
                        // Checking for yasPasUserDeviceToken null
                        if (yasPasUserDeviceToken) {
                            var deviceTokens = Array();
                            deviceTokens.push(yasPasUserDeviceToken);
                            // Sending Push Msg
                            PushNotificationClient.PushMessage(cMessage,
                                deviceTokens,
                                "",
                                yasPasImgUrl,
                                yasPasName,
                                6,
                                syteId,
                                cChatId,
                                cSenderType,
                                cSenderRegisteredNum);
                            var t = chatPushNotificationRef.child(paramSnapshotKey);
                            t.update({
                                isSent:0
                            });
                        }
                    });
                }
            });
        }
    }
}

//Create main object
var users = new Users();