package APITest;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import APITest.utils.DataFactory;
import APITest.utils.Order;
import APITest.utils.OrderResponse;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;


/**
 * Tests that exercise authenticated endpoints of the "Simple Books" API.
 *
 * <p>This test class uses RestAssured and TestNG. It acquires a bearer token
 * via {@link APITest.utils.DataFactory} in the {@link #setup()} method and
 * reuses it for authenticated requests against endpoints such as
 * {@code /orders} and {@code /books}.</p>
 */
public class TokenAuthTest {

    /**
     * Bearer token retrieved from the API to authorize requests.
     */
    private String token;

    /**
     * Helper used to generate test data and to obtain the authentication token.
     */
    private DataFactory dataFactory;

    @BeforeClass
    /**
     * TestNG setup executed once before tests in this class.
     *
     * <p>Configures the RestAssured base URI and initializes the
     * {@link DataFactory} and authentication token used by the test methods.</p>
     */
    public void setup() {

        RestAssured.baseURI = "https://simple-books-api.click";
        this.dataFactory = new DataFactory();
        this.token = this.dataFactory.generateToken();
        
    }
    
    @Test
    /**
     * Requests the authenticated {@code /orders} endpoint and asserts a
     * successful {@code 200 OK} response when a valid token is provided.
     */
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

    @Test
    /**
     * Fetches the list of books from {@code /books} using the authorized token
     * and verifies the call succeeds with HTTP {@code 200}.
     */
    public void getAllBooks() {
       
        given()
            .headers("Authorization", "Bearer " + this.token)
            .contentType(ContentType.JSON)
            .when()
            .get("/books")
            .then()
            .statusCode(200)
            .log().all();

    }

    @Test
    /**
     * Submits an order for a book and asserts the API responds with
     * {@code 201 Created}. The method deserializes the response into an
     * {@link APITest.utils.OrderResponse} for further inspection.
     */
    public void submitOrder() {
        Order newOrder = new Order(1, "Columbina");

        OrderResponse orderResponse = given()
            .headers("Authorization", "Bearer " + this.token)
            .contentType(ContentType.JSON)
            .body(newOrder)
            .when()
            .post("/orders")
            .then()
            .statusCode(201)
            .extract()
            .as(OrderResponse.class);

        System.out.println("Order response data: " + orderResponse.toString());
    }

   
}
