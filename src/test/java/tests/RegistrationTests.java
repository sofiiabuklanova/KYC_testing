package tests;

import core.BaseTest;
import domain.client.UserClient;
import domain.model.UserRequest;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static tests.TestDataUtil.generateValidPhoneNumber;
import static tests.TestDataUtil.getValidEmail;

public class RegistrationTests extends BaseTest {

    private static final String LONG_STRING = StringUtils.repeat('a', 256);
    UserClient userClient = new UserClient();

    @Test
    public void successfulRegistration() {
        UserRequest user = new UserRequest(
                getValidEmail(),
                "password123",
                generateValidPhoneNumber());
        Response response = userClient.registerUser(user);
        assertEquals(response.getStatusCode(), HttpStatus.SC_CREATED);
        assertNotNull(response.jsonPath().getString("data.userId"));
    }

    @Test
    public void duplicateAfterSuccessfulRegistrationShouldFail() {
        //valid registration
        String email = getValidEmail();
        UserRequest user = new UserRequest(
                email,
                "password123",
                generateValidPhoneNumber());
        Response response = userClient.registerUser(user);
        assertEquals(response.getStatusCode(), HttpStatus.SC_CREATED);
        assertNotNull(response.jsonPath().getString("data.userId"));
        //duplicate registration
        user = new UserRequest(
                email,
                "password123",
                generateValidPhoneNumber());
        response = userClient.registerUser(user);
        assertEquals(response.getStatusCode(), HttpStatus.SC_CONFLICT);
    }

    @Test
    public void missingEmailShouldFail() {
        UserRequest user = new UserRequest(
                null,
                "password123",
                generateValidPhoneNumber());
        Response response = userClient.registerUser(user);
        assertEquals(response.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    //currently failing due to the bug
    @Test
    public void tooLongEmailShouldFail() {
        String email = LONG_STRING + (System.currentTimeMillis()) + "@example.com";
        UserRequest user = new UserRequest(
                email,
                "password123",
                generateValidPhoneNumber());
        Response response = userClient.registerUser(user);
        assertEquals(response.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void invalidEmailShouldFail() {
        UserRequest user = new UserRequest(
                "invalid-email",
                "password123",
                generateValidPhoneNumber());
        Response response = userClient.registerUser(user);
        assertEquals(response.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void invalidPhoneShouldFail() {
        UserRequest user = new UserRequest(
                getValidEmail(),
                "password123",
                "invalid-phone");
        Response response = userClient.registerUser(user);
        assertEquals(response.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void missingPhoneShouldFail() {
        UserRequest user = new UserRequest(
                getValidEmail(),
                "password123",
                null);
        Response response = userClient.registerUser(user);
        assertEquals(response.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void tooShortPasswordShouldFail() {
        UserRequest user = new UserRequest(
                getValidEmail(),
                "short",
                generateValidPhoneNumber());
        Response response = userClient.registerUser(user);
        assertEquals(response.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void missingPasswordShouldFail() {
        UserRequest user = new UserRequest(
                getValidEmail(),
                null,
                generateValidPhoneNumber());
        Response response = userClient.registerUser(user);
        assertEquals(response.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }
}
