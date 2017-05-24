package ch.arbitrary.cloudscale.sample1;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.UUID;

public class Main {
    private static final String ACCESS_KEY = "ACCESS_KEY_HERE";
    private static final String SECRET_KEY = "SECRET_KEY_HERE";
    private static final String BUCKET_NAME = "sample1";
    private static final String ENDPOINT = "https://objects.cloudscale.ch";
    private static final String CONTENT = "object content";

    public static void main(String arg[]) throws IOException, UnirestException {
        String objectKey = UUID.randomUUID().toString();

        // create client
        AWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
        AmazonS3ClientBuilder amazonS3ClientBuilder = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(ENDPOINT, null))
                .withCredentials(new AWSStaticCredentialsProvider(credentials));
        AmazonS3 s3client = amazonS3ClientBuilder.build();

        // create object
        {
            System.out.println(String.format("creating object %s", objectKey));
            ObjectMetadata metadata = new ObjectMetadata();
            byte[] bytes = CONTENT.getBytes();
            metadata.setContentLength(bytes.length);
            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, objectKey, stream, metadata);
            s3client.putObject(request);
            System.out.println("ok");
        }

        // get object
        {
            System.out.println("getting object");
            GetObjectRequest request = new GetObjectRequest(BUCKET_NAME, objectKey);
            S3Object response = s3client.getObject(request);
            StringWriter writer = new StringWriter();
            IOUtils.copy(response.getObjectContent(), writer, "UTF-8");
            String body = writer.toString();
            if (!(CONTENT.equals(body))) {
                throw new AssertionError(String.format("unexpected content: %s", body));
            }
            System.out.println("ok");
        }

        // get request url
        {
            System.out.println("generating presigned URL");
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(BUCKET_NAME, objectKey);
            URL url = s3client.generatePresignedUrl(request);
            System.out.println(String.format("ok, presigned URL is: %s", url));
            System.out.println("requesting presigned URL");
            String body = Unirest.get(url.toString()).asString().getBody();
            if (!CONTENT.equals(body)) {
                throw new AssertionError(String.format("unexpected content: %s", body));
            }
            System.out.println("ok");
        }
    }
}
