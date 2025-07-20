package tests;

import core.BaseTest;
import domain.client.KYCClient;
import domain.client.UserClient;
import domain.model.UserRequest;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import java.io.File;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static tests.TestDataUtil.generateValidPhoneNumber;
import static tests.TestDataUtil.getValidEmail;

public class KYCTests extends BaseTest {

    public static final int RESPONSE_TIMEOUT_SECONDS = 20;
    private static final int ALLOWANCE = 5;
    public static final int MAXIMUM_TIMEOUT_SECONDS = RESPONSE_TIMEOUT_SECONDS + ALLOWANCE;
    private final UserClient userClient = new UserClient();
    private final KYCClient kycClient = new KYCClient();

    @Test
    public void kycStatusTransitionsToVerified() {
        // Register user
        UserRequest user = new UserRequest(
                getValidEmail(),
                "pass123",
                generateValidPhoneNumber());

        String userId = userClient.registerUser(user)
                .jsonPath()
                .getString("data.userId");

        // Initial KYC status
        Response status = kycClient.getKycStatus(userId);
        String kycStatus = status.jsonPath().getString("data.kycStatus");
        assertEquals(kycStatus.toLowerCase(), "no_documents");

        // Upload KYC document
        File doc = new File("src/test/resources/valid.png");
        Response upload = kycClient.uploadKyc(userId, doc);
        assertEquals(upload.getStatusCode(), HttpStatus.SC_OK);

        // Wait until KYC status transitions to "valid"
        await()
                .atMost(MAXIMUM_TIMEOUT_SECONDS, SECONDS)
                .pollInterval(1, SECONDS)
                .untilAsserted(() -> {
                    Response polledStatus = kycClient.getKycStatus(userId);
                    String polledKycStatus = polledStatus.jsonPath().getString("data.kycStatus");
                    assertEquals(polledKycStatus.toLowerCase(), "valid");
                });
    }

    @Test
    public void kycStatusInvalidAndValidFiles() {
        // Register user
        UserRequest user = new UserRequest(
                getValidEmail(),
                "pass123",
                generateValidPhoneNumber());
        String userId = userClient.registerUser(user).jsonPath().getString("data.userId");

        Response statusResponse = kycClient.getKycStatus(userId);
        String kycStatus = statusResponse.jsonPath().getString("data.kycStatus");

        assertTrue(kycStatus.equalsIgnoreCase("no_documents"));

        // Upload invalid KYC document
        File doc = new File("src/test/resources/incorrect.png");
        Response uploadResponse = kycClient.uploadKyc(userId, doc);
        assertEquals(uploadResponse.getStatusCode(), HttpStatus.SC_OK);

        // Wait for verification to complete (simulate polling)
        await()
                .atMost(MAXIMUM_TIMEOUT_SECONDS, SECONDS)
                .pollInterval(1, SECONDS)
                .untilAsserted(() -> {
                    Response polledStatus = kycClient.getKycStatus(userId);
                    String polledKycStatus = polledStatus.jsonPath().getString("data.kycStatus");
                    assertEquals(polledKycStatus.toLowerCase(), "invalid");
                });

        // Upload valid KYC document
        doc = new File("src/test/resources/valid.png");
        uploadResponse = kycClient.uploadKyc(userId, doc);
        assertEquals(uploadResponse.getStatusCode(), HttpStatus.SC_OK);

        // Wait for verification to complete (simulate polling)
        await()
                .atMost(MAXIMUM_TIMEOUT_SECONDS, SECONDS)
                .pollInterval(1, SECONDS)
                .untilAsserted(() -> {
                    Response polledStatus = kycClient.getKycStatus(userId);
                    String polledKycStatus = polledStatus.jsonPath().getString("data.kycStatus");
                    assertEquals(polledKycStatus.toLowerCase(), "valid");
                });
    }

    @Test
    public void kycWithInvalidDocument() {
        // Register user
        UserRequest user = new UserRequest(
                getValidEmail(),
                "pass123",
                generateValidPhoneNumber());
        String userId = userClient.registerUser(user).jsonPath().getString("data.userId");

        Response statusResponse = kycClient.getKycStatus(userId);
        String kycStatus = statusResponse.jsonPath().getString("data.kycStatus");

        assertTrue(kycStatus.equalsIgnoreCase("no_documents"));

        // Upload invalid KYC document
        File doc = new File("src/test/resources/incorrect.png");
        Response uploadResponse = kycClient.uploadKyc(userId, doc);
        assertEquals(uploadResponse.getStatusCode(), HttpStatus.SC_OK);

        // Wait for verification to complete (simulate polling)
        await()
                .atMost(MAXIMUM_TIMEOUT_SECONDS, SECONDS)
                .pollInterval(1, SECONDS)
                .untilAsserted(() -> {
                    Response polledStatus = kycClient.getKycStatus(userId);
                    String polledKycStatus = polledStatus.jsonPath().getString("data.kycStatus");
                    assertEquals(polledKycStatus.toLowerCase(), "invalid");
                });
    }

    @Test
    public void kycWithTooBigDocument() {
        // Register user
        UserRequest user = new UserRequest(
                getValidEmail(),
                "pass123",
                generateValidPhoneNumber());
        String userId = userClient.registerUser(user).jsonPath().getString("data.userId");

        Response statusResponse = kycClient.getKycStatus(userId);
        String kycStatus = statusResponse.jsonPath().getString("data.kycStatus");

        assertTrue(kycStatus.equalsIgnoreCase("no_documents"));

        // Upload invalid KYC document
        File doc = new File("src/test/resources/toobigvalid.pdf");
        Response uploadResponse = kycClient.uploadKyc(userId, doc);
        assertEquals(uploadResponse.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void kycWithWrongFormatDocument() {
        // Register user
        UserRequest user = new UserRequest(
                getValidEmail(),
                "pass123",
                generateValidPhoneNumber());
        String userId = userClient.registerUser(user).jsonPath().getString("data.userId");

        Response statusResponse = kycClient.getKycStatus(userId);
        String kycStatus = statusResponse.jsonPath().getString("data.kycStatus");

        assertTrue(kycStatus.equalsIgnoreCase("no_documents"));

        // Upload invalid KYC document
        File doc = new File("src/test/resources/validtext.txt");
        Response uploadResponse = kycClient.uploadKyc(userId, doc);
        assertEquals(uploadResponse.getStatusCode(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }
}
