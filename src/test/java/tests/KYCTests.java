package tests;

import core.BaseTest;
import domain.client.KYCClient;
import domain.client.UserClient;
import domain.model.UserRequest;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static tests.TestDataUtil.generateValidPhoneNumber;
import static tests.TestDataUtil.getValidEmail;

public class KYCTests extends BaseTest {

    public static final int RESPONSE_TIMEOUT_MILLIS = 20000;
    UserClient userClient = new UserClient();
    KYCClient kycClient = new KYCClient();

    @Test
    public void kycStatusTransitionsToVerified() throws InterruptedException {
        // Register user
        UserRequest user = new UserRequest(
                getValidEmail(),
                "pass123",
                generateValidPhoneNumber());
        String userId = userClient.registerUser(user).jsonPath().getString("data.userId");

        Response status = kycClient.getKycStatus(userId);
        String kycStatus = status.jsonPath().getString("data.kycStatus");

        assertTrue(kycStatus.equalsIgnoreCase("no_documents"));

        // Upload valid KYC document
        File doc = new File("src/test/resources/valid.png");
        Response upload = kycClient.uploadKyc(userId, doc);
        assertEquals(upload.getStatusCode(), HttpStatus.SC_OK);

        // Wait for verification to complete (simulate polling)
        Thread.sleep(RESPONSE_TIMEOUT_MILLIS);
        status = kycClient.getKycStatus(userId);
        kycStatus = status.jsonPath().getString("data.kycStatus");

        assertTrue(kycStatus.equalsIgnoreCase("valid"));
    }

    @Test
    public void kycStatusInvalidAndValidFiles() throws InterruptedException {
        // Register user
        UserRequest user = new UserRequest(
                getValidEmail(),
                "pass123",
                generateValidPhoneNumber());
        String userId = userClient.registerUser(user).jsonPath().getString("data.userId");

        Response status = kycClient.getKycStatus(userId);
        String kycStatus = status.jsonPath().getString("data.kycStatus");

        assertTrue(kycStatus.equalsIgnoreCase("no_documents"));

        // Upload invalid KYC document
        File doc = new File("src/test/resources/incorrect.png");
        Response upload = kycClient.uploadKyc(userId, doc);
        assertEquals(upload.getStatusCode(), HttpStatus.SC_OK);

        // Wait for verification to complete (simulate polling)
        Thread.sleep(RESPONSE_TIMEOUT_MILLIS);
        status = kycClient.getKycStatus(userId);
        kycStatus = status.jsonPath().getString("data.kycStatus");

        assertTrue(kycStatus.equalsIgnoreCase("invalid"));

        // Upload valid KYC document
        doc = new File("src/test/resources/valid.png");
        upload = kycClient.uploadKyc(userId, doc);
        assertEquals(upload.getStatusCode(), HttpStatus.SC_OK);

        // Wait for verification to complete (simulate polling)
        Thread.sleep(RESPONSE_TIMEOUT_MILLIS);
        status = kycClient.getKycStatus(userId);
        kycStatus = status.jsonPath().getString("data.kycStatus");

        assertTrue(kycStatus.equalsIgnoreCase("valid"));
    }

    @Test
    public void kycWithInvalidDocument() throws InterruptedException {
        // Register user
        UserRequest user = new UserRequest(
                getValidEmail(),
                "pass123",
                generateValidPhoneNumber());
        String userId = userClient.registerUser(user).jsonPath().getString("data.userId");

        Response status = kycClient.getKycStatus(userId);
        String kycStatus = status.jsonPath().getString("data.kycStatus");

        assertTrue(kycStatus.equalsIgnoreCase("no_documents"));

        // Upload invalid KYC document
        File doc = new File("src/test/resources/incorrect.png");
        Response upload = kycClient.uploadKyc(userId, doc);
        assertEquals(upload.getStatusCode(), HttpStatus.SC_OK);

        // Wait for verification to complete (simulate polling)
        Thread.sleep(RESPONSE_TIMEOUT_MILLIS);
        status = kycClient.getKycStatus(userId);
        kycStatus = status.jsonPath().getString("data.kycStatus");

        assertTrue(kycStatus.equalsIgnoreCase("invalid"));
    }

    @Test
    public void kycWithTooBigDocument() {
        // Register user
        UserRequest user = new UserRequest(
                getValidEmail(),
                "pass123",
                generateValidPhoneNumber());
        String userId = userClient.registerUser(user).jsonPath().getString("data.userId");

        Response status = kycClient.getKycStatus(userId);
        String kycStatus = status.jsonPath().getString("data.kycStatus");

        assertTrue(kycStatus.equalsIgnoreCase("no_documents"));

        // Upload invalid KYC document
        File doc = new File("src/test/resources/toobigvalid.pdf");
        Response upload = kycClient.uploadKyc(userId, doc);
        assertEquals(upload.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void kycWithWrongFormatDocument() {
        // Register user
        UserRequest user = new UserRequest(
                getValidEmail(),
                "pass123",
                generateValidPhoneNumber());
        String userId = userClient.registerUser(user).jsonPath().getString("data.userId");

        Response status = kycClient.getKycStatus(userId);
        String kycStatus = status.jsonPath().getString("data.kycStatus");

        assertTrue(kycStatus.equalsIgnoreCase("no_documents"));

        // Upload invalid KYC document
        File doc = new File("src/test/resources/validtext.txt");
        Response upload = kycClient.uploadKyc(userId, doc);
        assertEquals(upload.getStatusCode(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }
}
