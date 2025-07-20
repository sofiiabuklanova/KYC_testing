package domain.client;

import domain.model.UserRequest;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserClient {
    public Response registerUser(UserRequest user) {
        return given()
                .header("Content-Type", "application/json")
                .body(user)
                .log().all()
                .when()
                .post("/users");
    }

    public Response getUser(String userId) {
        return given().get("/users/" + userId);
    }
}
