package APITest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

/**
 * Integration tests for the RESTful API at https://api.restful-api.dev.
 * <p>
 * Tests use RestAssured to perform CRUD operations on the /objects endpoint
 * and assert expected HTTP status codes and response payload contents.
 * </p>
 *
 * <p>Test methods are annotated with TestNG's {@code @Test}.</p>
 */
public class APIRESTTest {

    /**
     * Base URI of the API under test.
     * <p>
     * This URL is used as the base for all requests performed by the test methods.
     * </p>
     */
    private final String BASE_URI = "https://api.restful-api.dev";

    /**
     * Creates a new object via POST /objects and returns the created object's id.
     *
     * <p>The payload represents a device with name and nested data fields.
     * The method logs request and response details, asserts a 200 status code,
     * and extracts the generated {@code id} field from the response.</p>
     *
     * @return the id of the newly-created object as a String
     */
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

    /**
     * Retrieves all records from the /objects endpoint and asserts the collection is non-empty.
     *
     * <p>Verifies that the server responds with HTTP 200 and that the returned array
     * contains at least one element.</p>
     */
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

    /**
     * Retrieves the object with id = 1 and validates specific fields.
     *
     * <p>Asserts HTTP 200 and checks that the response contains expected values for
     * id, name, data.color and data.capacity.</p>
     */
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

    
    /**
     * Creates a new record by sending a POST to /objects with a sample payload.
     *
     * <p>Verifies that the server returns HTTP 200 on successful creation.</p>
     */
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

    /**
     * Creates a temporary object and updates it via PUT /objects/{id}.
     *
     * <p>The method first creates a new item (using {@link #createItem()}), then
     * sends an updated payload to the PUT endpoint and verifies HTTP 200.</p>
     */
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

    /**
     * Creates and deletes a record, then verifies that the deletion message references the id.
     *
     * <p>Ensures delete operation returns HTTP 200 and the expected confirmation message.</p>
     */
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

    /**
     * Retrieves object with id=1, extracts fields, prints them, and asserts expected values.
     *
     * <p>This method demonstrates extracting a full Response object and using JsonPath
     * to access nested fields before asserting their expected values.</p>
     */
    @Test
    public void validateFieldsInRecordWithJsonPath() {

        Response response = 
            given()
                .baseUri(this.BASE_URI)
                .accept(ContentType.ANY)
                .get("/objects/1")
            .then()
                .statusCode(200)
                .extract()
                .response();

        
        String name = response.jsonPath().getString("name");
        String capacity = response.jsonPath().getString("data.capacity");

        System.out.println("Name: " + name);
        System.out.println("Capacity: " + capacity);

        response
            .then()
                .body("name", equalTo("Google Pixel 6 Pro"))
                .body("data.capacity", equalTo("128 GB"));
    }
}
