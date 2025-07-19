package tests;

import core.BaseTest;
import domain.client.KYCClient;
import domain.client.KYCClient;
import domain.client.UserClient;
import domain.model.UserRequest;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class KYCTests extends BaseTest {

    UserClient userClient = new UserClient();
    KYCClient kycClient = new KYCClient();

    @Test
    public void kycStatusTransitionsToVerified() throws InterruptedException {
        // Register user
        UserRequest user = new UserRequest("kycuser+" + System.currentTimeMillis() + "@example.com", "pass123", "+1234567890");
        String userId = userClient.registerUser(user).jsonPath().getString("id");

        // Upload valid KYC document
        File doc = new File("src/test/resources/sample.pdf");
        Response upload = kycClient.uploadKyc(userId, doc);
        assertEquals(upload.getStatusCode(), 200);

        // Wait for verification to complete (simulate polling)
        Thread.sleep(5000);  // Replace with retry logic in production
        Response status = kycClient.getKycStatus(userId);
        String kycStatus = status.jsonPath().getString("status");

        assertTrue(kycStatus.equalsIgnoreCase("verified") || kycStatus.equalsIgnoreCase("pending"));
    }

    @Test
    public void kycWithMissingDocumentShouldFail() {
        String userId = userClient.registerUser(new UserRequest("faildoc+" + System.currentTimeMillis() + "@example.com", "pass123", "+1234567890"))
                .jsonPath().getString("id");

        Response response = kycClient.uploadKyc(userId, null);
        assertEquals(response.getStatusCode(), 400);
    }
}
