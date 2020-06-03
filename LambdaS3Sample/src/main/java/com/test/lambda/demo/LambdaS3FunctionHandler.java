package com.test.lambda.demo;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

public class LambdaS3FunctionHandler implements RequestHandler<S3Event, String> {

    private AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();
    static final String FROM = "akhilparjun@gmail.com";
    static final String TO = "akhilparjun@gmail.com";
    static final String SUBJECT = "Test mail from Lambda";
    static final String HTMLBODYSTART = "The following File was added to your S3 Bucket<br><h1 style='color:red'>";
    static final String HTMLBODYEND = "</h1>";

    public LambdaS3FunctionHandler() {}

    // Test purpose only.
    LambdaS3FunctionHandler(AmazonS3 s3) {
        this.s3 = s3;
    }

    @Override
    public String handleRequest(S3Event event, Context context) {
        context.getLogger().log("Received event: " + event);

        // Get the object from the event and show its content type
        String bucket = event.getRecords().get(0).getS3().getBucket().getName();
        String key = event.getRecords().get(0).getS3().getObject().getKey();
        try {
        	context.getLogger().log("Bucket "+bucket+" Key "+key);
        	AmazonSimpleEmailService client = 
        	          AmazonSimpleEmailServiceClientBuilder.standard()
        	            .withRegion(Regions.US_EAST_1).build();
        	      SendEmailRequest request = new SendEmailRequest()
        	          .withDestination(
        	              new Destination().withToAddresses(TO))
        	          .withMessage(new Message()
        	              .withBody(new Body()
        	                  .withHtml(new Content()
        	                      .withCharset("UTF-8").withData(HTMLBODYSTART+key+HTMLBODYEND))
        	                      )
        	              .withSubject(new Content()
        	                  .withCharset("UTF-8").withData(SUBJECT)))
        	          .withSource(FROM);
        	      client.sendEmail(request);
        	return key;
        } catch (Exception e) {
            e.printStackTrace();
            context.getLogger().log(String.format(
                "Error getting object %s from bucket %s. Make sure they exist and"
                + " your bucket is in the same region as this function.", key, bucket));
            throw e;
        }
    }
}