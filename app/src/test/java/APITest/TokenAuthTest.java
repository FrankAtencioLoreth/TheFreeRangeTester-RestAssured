package APITest;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.datafaker.Faker;


public class TokenAuthTest {

    private String token;
    private String randomEmail;
    private Faker faker;

    @BeforeClass
    public void setup() {

        this.faker = new Faker();
        this.randomEmail = faker.internet().emailAddress();

        RestAssured.baseURI = "https://simple-books-api.click";

        String requestBody = """
            {
                "clientName": "Postman",
                "clientEmail": "%s"
            }
        """.formatted(this.randomEmail);

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
    }
    
    @Test
    public void testTokenBooks() {
       
        given()
            .headers("Authorization", "Bearer " + this.token)
            .contentType(ContentType.JSON)
            .when()
            .get("/orders")
            .then()
            .statusCode(200)
            .log().all();

    }
}
