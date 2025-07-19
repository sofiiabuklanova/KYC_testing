package domain.client;

import io.restassured.response.Response;

import java.io.File;

import static io.restassured.RestAssured.given;

public class KYCClient {
    public Response uploadKyc(String userId, File document) {
        return given()
                .multiPart("document", document)
                .when()
                .post("/kyc/" + userId);
    }

    public Response getKycStatus(String userId) {
        return given().get("/kyc/" + userId);
    }
}
