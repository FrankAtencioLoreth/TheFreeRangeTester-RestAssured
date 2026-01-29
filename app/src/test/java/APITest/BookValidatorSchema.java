package APITest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import APITest.utils.DataFactory;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;

/**
 * Test suite for validating the "Simple Books" API behavior and JSON schema.
 *
 * <p>Uses RestAssured to exercise endpoints under {@code https://simple-books-api.click}.
 * Tests include JSON schema validation, status-code checks for common error
 * conditions, client registration flows, and a simple response-time assertion.</p>
 *
 * <p>This class is intended to be executed as part of the project test phase
 * (TestNG).</p>
 */
public class BookValidatorSchema {

    /**
     * Bearer token used for authenticated requests. Generated in {@link #setup()}.
     */
    private String token;

    /**
     * Test data factory helper used to produce tokens and random values.
     */
    private DataFactory dataFactory;

    
    @BeforeClass
    /**
     * TestNG setup method run once before the test methods in this class.
     *
     * <p>Configures the RestAssured base URI and initializes helpers including
     * the {@link DataFactory} and a valid bearer token used by authenticated
     * test cases.</p>
     */
    public void setup() {
        RestAssured.baseURI = "https://simple-books-api.click";
        this.dataFactory = new DataFactory();
        this.token = this.dataFactory.generateToken();
    }

    @Test
    /**
     * Validates that the list-books endpoint returns payloads matching the
     * JSON schema located at {@code classpath:schemas/books-schema.json}.
     */
    public void validateBookSchema() {
        given()
            .contentType(ContentType.JSON)
            .when()
            .get("/books")
            .then()
            .assertThat()
            .statusCode(200)
            .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/books-schema.json"));
    }

    @Test
    /**
     * Sends an order request with an invalid token and asserts the API
     * responds with {@code 401 Unauthorized}.
     */
    public void validateStatusCode401() {

        String _token = "holaMundo";

        String body = """
            {
                "bookId": "1",
                "customerName": "Columbina"
            }        
        """;

        given()
            .headers("Authorization", "Bearer " + _token)
            .contentType(ContentType.JSON)
            .body(body)
            .when()
            .post("/orders")
            .then()
            .statusCode(401)
            .log().all();
    }

    @Test
    /**
     * Posts an empty order body using a valid token and expects a
     * {@code 400 Bad Request} response.
     */
    public void validateStatusCode400BaddRequest() {

        String emptyBody = "{}";

        given()
            .headers("Authorization", "Bearer " + this.token)
            .contentType(ContentType.JSON)
            .body(emptyBody)
            .when()
            .post("/orders")
            .then()
            .statusCode(400)
            .log().all();
    }


    @Test
    /**
     * Requests a non-existing book id and asserts the service returns
     * {@code 404 Not Found}.
     */
    public void validateStatusCode404() {
    
        given()
            .headers("Authorization", "Bearer " + this.token)
            .contentType(ContentType.JSON)
            .when()
            .get("/books/999")
            .then()
            .assertThat()
            .statusCode(404)
            .log().all();
    }

    @Test
    /**
     * Tries to create an API client with an empty payload and verifies the
     * expected error message for missing client name.
     */
    public void sendEmptyCredentials() {
        
        String emptyCredentials = "{}";

        given()
            .contentType(ContentType.JSON)
            .body(emptyCredentials)
            .when()
            .post("/api-clients")
            .then().log().all()
            .assertThat()
            .body("error", equalTo("Invalid or missing client name."))
            .log().all();
    }

    @Test
    /**
     * Attempts to register a client with only the {@code clientName} field
     * and verifies the API returns an error indicating a missing email.
     */
    public void sendOnlyClientNameField() {
        
        String onlyClientNameBiody = """
            {
                "clientName": "fatencio"
            }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(onlyClientNameBiody)
            .when()
            .post("/api-clients")
            .then().log().all()
            .assertThat()
            .body("error", equalTo("Invalid or missing client email."))
            .log().all();
    }

    @Test
    /**
     * Registers a new client with a generated email and asserts the
     * registration returns HTTP {@code 201 Created}.
     */
    public void sendCorrectCredentials() {
        
        String requestBody = """
            {
                "clientName": "Postman",
                "clientEmail": "%s"
            }
        """.formatted(this.dataFactory.generateRandomEmail());

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post("/api-clients")
            .then().log().all()
            .assertThat()
            .statusCode(201)
            .log().all();
    }

    @Test
    /**
     * Checks that the response time for fetching the books list is below
     * the configured threshold (2 seconds in this test).
     */
    public void validResponseTime() {

        given()
            .contentType(ContentType.JSON)
            .when()
            .get("/books")
            .then().log().all()
            .assertThat()
            .statusCode(200)
            .time(lessThan(2000L))
            .log().all();
    }
}
