package APITest;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;

/**
 * Lightweight authentication tests demonstrating basic and token-based
 * authentication flows against external test endpoints.
 *
 * <p>The class contains a basic-auth example using Postman's echo service
 * and a token/client-registration example targeting the Simple Books API.</p>
 */
public class AuthTest {
    
    /**
     * Base URI for the Postman echo basic-auth endpoint used in
     * {@link #basicAuthTest()}.
     */
    private final String BASE_URI = "https://postman-echo.com/basic-auth";

    /**
     * Base URI for the Simple Books API used in {@link #tokenAuthTest()}.
     */
    private final String BASE_URI_2 = "https://simple-books-api.click";
    
    /**
     * TestNG setup placeholder. Left intentionally minimal because each test
     * configures its own target URIs as needed.
     */
    @BeforeClass
    public static void setup() {
        // Configuramos la URL base para no repetirla
        //RestAssured.baseURI = "https://simple-books-api.glitch.me";
    }

    @Test
    /**
     * Demonstrates HTTP Basic authentication by calling the Postman echo
     * basic-auth endpoint and asserting a successful {@code 200 OK} response.
     */
    public void basicAuthTest() {
        given()
            //.header("Authorization", "Basic cG9zdG1hbjpwYXNzd29yZA==")
            .auth().basic("postman", "password")
        .when()
            .get(this.BASE_URI)
        .then()
            .log().all()
            .statusCode(200);
    }

    @Test
    /**
     * Demonstrates client registration for token-based access by POSTing
     * generated client credentials to {@code /api-clients} on the Simple
     * Books API and logging the service response.
     */
    public void tokenAuthTest() {

         String requestBody = """
            {
                "clientName": "Postman",
                "clientEmail": "fatencio@example11.com"
            }
        """;
        
        given()
            .baseUri(this.BASE_URI_2)
            .contentType("application/json")
            .accept(ContentType.ANY)
            .body(requestBody)
        .log().all()
        .when()
            .post("/api-clients/")
        .then()
            .log().all();
 
    }
}
