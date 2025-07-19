package tests;

import core.BaseTest;
import domain.client.UserClient;
import domain.model.UserRequest;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class RegistrationTests extends BaseTest {

    UserClient userClient = new UserClient();

    @Test
    public void successfulRegistration() {
        UserRequest user = new UserRequest("user+" + System.currentTimeMillis() + "@example.com", "password123", "+1234567890");
        Response response = userClient.registerUser(user);
        assertEquals(response.getStatusCode(), 200);
        assertNotNull(response.jsonPath().getString("id"));
    }

    @Test
    public void missingEmailShouldFail() {
        UserRequest user = new UserRequest(null, "password123", "+1234567890");
        Response response = userClient.registerUser(user);
        assertEquals(response.getStatusCode(), 400);
    }

    @Test
    public void invalidPhoneShouldFail() {
        UserRequest user = new UserRequest("user+" + System.currentTimeMillis() + "@example.com", "password123", "invalid-phone");
        Response response = userClient.registerUser(user);
        assertEquals(response.getStatusCode(), 400);
    }
}
