package APITest;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import APITest.utils.DataFactory;
import APITest.utils.Order;
import APITest.utils.OrderResponse;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;


public class TokenAuthTest {

    private String token;
    private DataFactory dataFactory;

    @BeforeClass
    public void setup() {

        RestAssured.baseURI = "https://simple-books-api.click";
        this.dataFactory = new DataFactory();
        this.token = this.dataFactory.generateToken();
        
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

    @Test
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
