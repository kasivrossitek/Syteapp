/**
 * This module implements OTP being sent using
 * Twilio messaging API.
 */

var Twilio = require('twilio');

function SMSClient(accountSid, authToken, from) {
    this._from = from;
    this._client = Twilio(accountSid, authToken);
}

SMSClient.prototype.SendSMS = function(to, message) {
    this._client.messages.create({
        to: to,
        from: this._from,
        body: message
    }, function(err, message) {
        console.log(message);
    });
};

exports.SMSClient = SMSClient;