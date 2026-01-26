package APITest;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;

public class BookValidatorSchema {
    
    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://simple-books-api.click";
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
}
