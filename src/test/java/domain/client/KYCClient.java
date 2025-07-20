package domain.client;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.io.File;
import java.net.URLConnection;

import static io.restassured.RestAssured.given;

public class KYCClient {
    public Response uploadKyc(String userId, File document) {
            return given()
                    .contentType(ContentType.MULTIPART)
                    .multiPart("document", document, URLConnection.guessContentTypeFromName(document.getName())).log().all()
                    .when()
                    .post("/kyc/" + userId);
    }

    public Response getKycStatus(String userId) {
        return given().get("/kyc/" + userId);
    }
}