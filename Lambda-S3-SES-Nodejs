var aws = require('aws-sdk');
var ses = new aws.SES({
    region: 'ap-south-1'
});
const s3 = new aws.S3({
    apiVersion: '2006-03-01'
});
exports.handler = function(event, context, callback) {
    console.log("Incoming: ", event);
    const bucket = event.Records[0].s3.bucket.name;
    const key = decodeURIComponent(event.Records[0].s3.object.key.replace(/\+/g, ' '));
    const news = `Event took place in - ${bucket} -> ${key}`;
    const params = {
        Bucket: bucket,
        Key: key,
    };
    var eParams = {
        Destination: {
            ToAddresses: ["test@gmail.com"]
        },
        Message: {
            Body: {
                Text: {
                    Data: `${news}`
                }
            },
            Subject: {
                Data: "Email Notification"
            }
        },
        Source: "test@gmail.com"
    };
    console.log('===SENDING EMAIL===');
    var email = ses.sendEmail(eParams, function(err, data) {
        if (err) console.log(err);
        else {
            console.log("===EMAIL SENT===");
            // console.log(data);
            console.log("EMAIL CODE END");
            console.log('EMAIL: ', email);
            context.succeed(event);
        }
    });
};
