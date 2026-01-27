package APITest;

import static org.hamcrest.Matchers.equalTo;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import APITest.utils.DataFactory;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;

public class BookValidatorSchema {

    private String token;
    private DataFactory dataFactory;

    
    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://simple-books-api.click";
        this.dataFactory = new DataFactory();
        this.token = this.dataFactory.generateToken();
    }

    @Test
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
}
