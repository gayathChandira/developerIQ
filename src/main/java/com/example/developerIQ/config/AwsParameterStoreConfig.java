package com.example.developerIQ.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;


@Configuration
public class AwsParameterStoreConfig {

    @Value("${AWS_ACCESS_KEY_ID}")
    private String accessKey;

    @Value("${AWS_SECRET_ACCESS_KEY}")
    private String secretKey;

    @Autowired
    private AwsCredentialsProvider awsCredentialsProvider;

    @Autowired
    private SsmClient ssmClient;

    public AwsParameterStoreConfig(
            @Value("${AWS_ACCESS_KEY_ID}") String accessKey,
            @Value("${AWS_SECRET_ACCESS_KEY}") String secretKey,
            AwsCredentialsProvider awsCredentialsProvider,
            SsmClient ssmClient) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.awsCredentialsProvider = awsCredentialsProvider;
        this.ssmClient = ssmClient;
    }

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
    }

    @Bean
    public SsmClient ssmClient(AwsCredentialsProvider awsCredentialsProvider) {
        return SsmClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }

    public String dbRetreiverUrl() {
        GetParameterRequest parameterRequest = GetParameterRequest.builder()
                .name("/developeriq/db-retreival-url")
                .build();

        GetParameterResponse parameterResponse = ssmClient.getParameter(parameterRequest);
        return parameterResponse.parameter().value();
    }
}

