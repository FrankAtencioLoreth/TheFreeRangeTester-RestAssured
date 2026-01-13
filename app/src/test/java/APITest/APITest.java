package APITest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;

public class APITest {

    private final String BASE_URI = "https://api.restful-api.dev";

    private String createItem() {
        String requestBody = """
            {
                "name": "Apple MacBook Pro 16",
                "data": {
                    "year": 2019,
                    "price": 1849.99,
                    "CPU model": "Intel Core i9",
                    "Hard disk size": "1 TB"
                }
            }
        """;

        String id = given()
            .baseUri(this.BASE_URI)
            .accept(ContentType.ANY)
            .contentType(ContentType.JSON)
            .log().all()
            .body(requestBody)
        .when()
            .post("/objects")
        .then()
            .statusCode(200).log().all()
            .extract()
            .path("id");
        System.out.println("Created record ID: " + id);
        return id;
    }

    @Test
    public void getRecords() {
        given()
            .baseUri(this.BASE_URI)
            .accept(ContentType.ANY)
            .log().all()
        .when()
            .get("/objects")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .log().all();
    }

    @Test
    public void getRecordById() {

        given()
            .baseUri(this.BASE_URI)
            .accept(ContentType.ANY)
            .log().all()
        .when()
            .get("/objects/1")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("id", equalTo("1"))
            .body("name", equalTo("Google Pixel 6 Pro"))
            .body("data.color", equalTo("Cloudy White"))
            .body("data.capacity", equalTo("128 GB"))
            .log().all();
    }

    
    @Test
    public void createNewRcord() {
        String requestBody = """
            {
                "name": "Apple MacBook Pro 16",
                "data": {
                    "year": 2019,
                    "price": 1849.99,
                    "CPU model": "Intel Core i9",
                    "Hard disk size": "1 TB"
                }
            }
        """;
        
        given()
            .baseUri(this.BASE_URI)
            .accept(ContentType.ANY)
            .contentType(ContentType.JSON)
            .log().all()
            .body(requestBody)
        .when()
            .post("/objects")
        .then()
            .statusCode(200)
            .log().all();
    }

    @Test
    public void updateRecord() {

        String createdId = this.createItem();

        String requestBody = """
            {
                "name": "Apple MacBook Pro 16",
                "data": {
                    "year": 2019,
                    "price": 2049.99,
                    "CPU model": "Intel Core i9",
                    "Hard disk size": "1 TB",
                    "color": "silver"
                }
            }
        """;

        given()
            .baseUri(this.BASE_URI)
            .accept(ContentType.ANY)
            .contentType(ContentType.JSON)
            .log().all()
            .body(requestBody)
        .when()
            .put("/objects/"+createdId)
        .then()
            .statusCode(200)
            .log().all();

    }

    @Test
    public void deleteRecord() {

        String createdId = this.createItem();

        given()
            .baseUri(this.BASE_URI)
            .accept(ContentType.ANY)
            .contentType(ContentType.JSON)
            .log().all()
        .when()
            .delete("/objects/"+createdId)
        .then()
            .statusCode(200)
            .body("message", equalTo("Object with id = "+createdId+" has been deleted."))
            .log().all(); 
    }
}
