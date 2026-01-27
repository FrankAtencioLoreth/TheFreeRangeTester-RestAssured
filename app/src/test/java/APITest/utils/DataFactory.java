package APITest.utils;

import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.datafaker.Faker;

public class DataFactory {

    private String token;
    private Faker faker;

    public DataFactory() {
        this.faker = new Faker();
    }

    public String generateRandomEmail() {
        return faker.internet().emailAddress(); 
    }

    public String generateToken() {
        
        String requestBody = """
            {
                "clientName": "Postman",
                "clientEmail": "%s"
            }
        """.formatted(this.generateRandomEmail());

        Response response = given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post("/api-clients")
            .then()
            .statusCode(201)
            .log().all()
            .extract()
            .response();

        this.token = response.jsonPath().getString("accessToken");

        return this.token;
    }
    
}
