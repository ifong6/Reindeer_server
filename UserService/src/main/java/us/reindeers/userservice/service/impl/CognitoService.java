package us.reindeers.userservice.service.impl;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;

import java.util.UUID;

public class CognitoService {

    private final CognitoIdentityProviderClient cognitoClient;
    private final String userPoolId;

    public CognitoService(String userPoolId) {
        this.userPoolId = userPoolId;

        this.cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.US_WEST_2)
                .build();
    }

    public void setCognitoGroup(UUID sub, String role) {
        try {
            AdminAddUserToGroupRequest request = AdminAddUserToGroupRequest.builder()
                    .userPoolId(userPoolId)
                    .username(sub.toString())
                    .groupName(role)
                    .build();

            cognitoClient.adminAddUserToGroup(request);

            System.out.println("User added to group successfully!");

        } catch (CognitoIdentityProviderException e) {
            System.err.println("Failed to add user to group: " + e.awsErrorDetails().errorMessage());
        }
    }
}
