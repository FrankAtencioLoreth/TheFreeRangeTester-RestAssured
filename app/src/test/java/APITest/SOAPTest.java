package APITest;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * Tests SOAP NumberConversion service (NumberToWords operation) using RestAssured.
 *
 * <p>This test class sends a SOAP request to the NumberConversion web service
 * hosted at dataaccess.com and verifies that the response contains the expected
 * textual number representation.</p>
 */
public class SOAPTest {

    /**
     * Base URI for the SOAP NumberConversion service used in tests.
     */
    private final String BASE_URI = "https://www.dataaccess.com/webservicesserver/NumberConversion.wso";

    /**
     * Sends a SOAP NumberToWords request for the integer 500 and verifies:
     * <ul>
     *   <li>HTTP response status code is 200</li>
     *   <li>Response body contains the phrase "five hundred"</li>
     * </ul>
     *
     * <p>The test uses RestAssured to build and post an XML SOAP envelope and
     * extracts the NumberToWordsResult from the SOAP response for assertion.</p>
     */
    @Test(groups = {"api-soap"})
    public void sampleSOAPTest() {
        
        String requestBody = """
        <?xml version="1.0" encoding="utf-8"?>
        <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
            <soap:Body>
                <NumberToWords xmlns="http://www.dataaccess.com/webservicesserver/">
                    <ubiNum>500</ubiNum>
                </NumberToWords>
            </soap:Body>
        </soap:Envelope>
        """;

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
