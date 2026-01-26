package APITest;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;

public class AuthTest {
    
    private final String BASE_URI = "https://postman-echo.com/basic-auth";
    private final String BASE_URI_2 = "https://simple-books-api.click";
    
    @BeforeClass
    public static void setup() {
        // Configuramos la URL base para no repetirla
        //RestAssured.baseURI = "https://simple-books-api.glitch.me";
    }

    @Test
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
