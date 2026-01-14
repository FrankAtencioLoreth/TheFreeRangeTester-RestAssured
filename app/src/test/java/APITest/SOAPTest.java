package APITest;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class SOAPTest {

    private final String BASE_URI = "https://www.dataaccess.com/webservicesserver/NumberConversion.wso";
    
    @Test
    public void sampleSOAPTest() {
        
        String requestBody = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
            "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                    "<NumberToWords xmlns=\"http://www.dataaccess.com/webservicesserver/\">" +
                    "<ubiNum>500</ubiNum>" +
                    "</NumberToWords>" +
                "</soap:Body>" +
            "</soap:Envelope>";

        Response response = RestAssured.given()
            .contentType("text/xml; charset=utf-8")
            .body(requestBody)
            .post(this.BASE_URI)
            .then()
            .statusCode(200)
            .log().all()
            .extract().response();

        System.out.println("Response SOAP Body: " + response.getBody().asString());

        //Extracting value from the response XML
        String result = response.xmlPath().getString("Envelope.Body.NumberToWordsResponse.NumberToWordsResult").toLowerCase().trim();
        System.out.println("Extracted Result: " + result);

        Assert.assertTrue(response.getBody().asString().contains("five hundred"), "Response body does not contain the expected result.");

    }
}   
