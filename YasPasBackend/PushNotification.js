/**
 * This module implements Push Notification being sent using GCM.
 */
var gcm = require('node-gcm');

function PushNotificationClient(gcmApiKey) {
    this._gcmApiKey = gcmApiKey;
} // END PushNotificationClient

PushNotificationClient.prototype.PushMessage = function(paramMsgTitle, paramDeviceTokens, bigImage, bigIconImage, syteName, pushType) {
    var message = new gcm.Message();
    message.addData('title', paramMsgTitle);
    message.addData('message', paramMsgTitle);
    message.addData('bigImage', bigImage);
    message.addData('bigIconImage', bigIconImage);
    message.addData('pushType', pushType);
    var regTokens = paramDeviceTokens;

    // Set up the sender with you API key
    var sender = new gcm.Sender(this._gcmApiKey);
    // Now the sender can be used to send messages
    sender.send(message, {registrationTokens: regTokens}, function (err, response) {
        if (err) {
            console.error(err);
        } else {
            console.log(response);
        }
    });
    // Send to a topic, with no retry this time
    sender.sendNoRetry(message, {topic: '/topics/global'}, function (err, response) {
        if (err) {
            console.error(err);
        } else {
            console.log(response);
        }
    });
};

// Function to send chatPushNotification
// This function is for sending the push notification for Chat
PushNotificationClient.prototype.PushMessage = function(paramMsgTitle, paramDeviceTokens,
                                                        bigImage, bigIconImage, syteName, pushType,
                                                        syteId, chatId, paramSenderType, paramSenderNum) {
    // Here chatId is user,not owner, Registered number
    var message = new gcm.Message();
    message.addData('title', syteName);
    message.addData('message', paramMsgTitle);
    message.addData('bigImage', bigImage);
    message.addData('bigIconImage', bigIconImage);
    message.addData('pushType', pushType);
    message.addData('syteId', syteId);
    message.addData('chatId', chatId);
    message.addData('senderType', paramSenderType);
    message.addData('senderNum', paramSenderNum);
    var regTokens = paramDeviceTokens;
    // Set up the sender with you API key
    var sender = new gcm.Sender(this._gcmApiKey);
    // Now the sender can be used to send messages
    sender.send(message, {registrationTokens: regTokens}, function (err, response) {
        if(err) {
            console.error(err);
        } else {
            console.log(response);
        }
    });
    // Send to a topic, with no retry this time
    sender.sendNoRetry(message, {topic: '/topics/global'}, function (err, response) {
        if(err) {
            console.error(err);
        } else {
            console.log(response);
        }
    });
};

exports.PushNotificationClient = PushNotificationClient;