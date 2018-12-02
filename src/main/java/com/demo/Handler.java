/**
 * 
 */
package com.demo;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.codedeploy.AmazonCodeDeploy;
import com.amazonaws.services.codedeploy.AmazonCodeDeployClientBuilder;
import com.amazonaws.services.codedeploy.model.CreateDeploymentRequest;
import com.amazonaws.services.codedeploy.model.RevisionLocation;
import com.amazonaws.services.codedeploy.model.S3Location;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;

/**
 * @author Aniruddha.Raje
 *
 */
public class Handler implements RequestHandler<S3Event, String> {

	@Override
	public String handleRequest(S3Event input, Context context) {

		try {

			S3EventNotificationRecord s3EventNotificationRecord = input.getRecords().get(0);
			String s3BucketName = s3EventNotificationRecord.getS3().getBucket().getName();
			String s3BucketKey = s3EventNotificationRecord.getS3().getObject().getKey();

			System.out.println("s3BucketName => " + s3BucketName + "\n s3BucketKey => " + s3BucketKey);
			deployApplication(s3BucketName, s3BucketKey);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void deployApplication(String s3BucketName, String s3BucketKey) throws Exception {

		System.out.println("deployApplication called!");
		try {
			AmazonCodeDeploy codeDeployClient = AmazonCodeDeployClientBuilder.standard().withRegion(Regions.AP_SOUTHEAST_1)
					.build();
			CreateDeploymentRequest request = new CreateDeploymentRequest();

			RevisionLocation revision = new RevisionLocation();

			S3Location s3Location = new S3Location();
			s3Location.setBucket(s3BucketName);
			s3Location.setKey(s3BucketKey);
			s3Location.setBundleType("tgz");

			revision.setRevisionType("S3");
			revision.setS3Location(s3Location);

			request.setApplicationName("test");
			request.setDeploymentGroupName("test");
			request.setIgnoreApplicationStopFailures(true);

			request.setRevision(revision);

			System.out.println("Creating deployment!");
			codeDeployClient.createDeployment(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
