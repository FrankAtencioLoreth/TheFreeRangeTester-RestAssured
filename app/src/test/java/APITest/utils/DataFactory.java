package APITest.utils;

import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.datafaker.Faker;

/**
 * Helper that produces test data and performs lightweight setup actions used
 * across API tests.
 *
 * <p>Responsibilities include generating random, realistic values (emails)
 * and creating an API client to obtain a reusable bearer token for
 * authenticated requests.</p>
 */
public class DataFactory {

    /**
     * Cached bearer token created by {@link #generateToken()}.
     */
    private String token;

    /**
     * Faker instance used to generate realistic random data.
     */
    private final Faker faker;

    /**
     * Constructs a new DataFactory and initializes the internal faker.
     */
    public DataFactory() {
        this.faker = new Faker();
    }

    /**
     * Returns a randomly generated, valid-looking email address.
     *
     * @return a random email address
     */
    public String generateRandomEmail() {
        return faker.internet().emailAddress(); 
    }

    /**
     * Registers a new API client and returns an authentication token.
     *
     * <p>This method issues a POST to {@code /api-clients} with generated
     * credentials, asserts the creation {@code 201} response, extracts the
     * {@code accessToken} field and caches it in {@link #token}.</p>
     *
     * @return the bearer token string extracted from the registration response
     */
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
