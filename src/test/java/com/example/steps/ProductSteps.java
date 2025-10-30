package com.example.steps;

import com.example.model.Product;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.given;

@Slf4j
public class ProductSteps {

    @Autowired
    private RequestSpecification requestSpec;

    private Response response;
    private Product testProduct;

    @Given("I have a product with following details:")
    public void i_have_a_product_with_following_details(io.cucumber.datatable.DataTable dataTable) {
        var data = dataTable.asMaps().get(0);
        Long currentId = testProduct != null ? testProduct.getId() : null;
        testProduct = Product.builder()
                .id(currentId) // preserve the ID if it exists
                .name(data.get("name"))
                .description(data.get("description"))
                .price(Double.parseDouble(data.get("price")))
                .build();
        log.info("Created test product: {}", testProduct);
    }

    @Given("the API is running at http:\\/\\/localhost:{int}")
    public void the_api_is_running_at_http_localhost(Integer port) {
        String baseUrl = String.format("http://localhost:%d", port);
        log.info("Checking if API is running at {}", baseUrl);
        Response healthResponse = given()
                .spec(requestSpec)
                .baseUri(baseUrl)
                .when()
                .get("/api/products");
        log.info("Health check response status: {}", healthResponse.getStatusCode());
        assertEquals(200, healthResponse.getStatusCode(), "API is not running at " + baseUrl);
    }

    @When("I send POST request to {string}")
    public void i_send_post_request_to(String endpoint) {
        log.info("Sending POST request to {} with body: {}", endpoint, testProduct);
        response = given()
                .spec(requestSpec)
                .body(testProduct)
                .when()
                .post(endpoint);
        log.info("Received response with status code: {}", response.getStatusCode());
        // Extract and store the ID from the response
        Product created = response.as(Product.class);
        testProduct.setId(created.getId());
    }

    @When("I send GET request to {string}")
    public void i_send_get_request_to(String endpoint) {
        if (endpoint.contains("{id}")) {
            endpoint = endpoint.replace("{id}", String.valueOf(testProduct.getId()));
        }
        log.info("Sending GET request to {}", endpoint);
        response = given()
                .spec(requestSpec)
                .when()
                .get(endpoint);
        log.info("Received response with status code: {}", response.getStatusCode());
    }

    @When("I send PUT request to {string}")
    public void i_send_put_request_to(String endpoint) {
        if (endpoint.contains("{id}")) {
            endpoint = endpoint.replace("{id}", String.valueOf(testProduct.getId()));
        }
        log.info("Sending PUT request to {} with body: {}", endpoint, testProduct);
        response = given()
                .spec(requestSpec)
                .body(testProduct)
                .when()
                .put(endpoint);
        log.info("Received response with status code: {}", response.getStatusCode());
    }

    @When("I send DELETE request to {string}")
    public void i_send_delete_request_to(String endpoint) {
        if (endpoint.contains("{id}")) {
            endpoint = endpoint.replace("{id}", String.valueOf(testProduct.getId()));
        }
        log.info("Sending DELETE request to {}", endpoint);
        response = given()
                .spec(requestSpec)
                .when()
                .delete(endpoint);
        log.info("Received response with status code: {}", response.getStatusCode());
    }

    @Then("the response status code should be {int}")
    public void the_response_status_code_should_be(Integer expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        log.info("Verifying status code. Expected: {}, Actual: {}", expectedStatusCode, actualStatusCode);
        assertEquals(expectedStatusCode, actualStatusCode);
    }

    @Then("the response should contain the product details")
    public void the_response_should_contain_the_product_details() {
        Product responseProduct = response.as(Product.class);
        log.info("Verifying product details. Response product: {}", responseProduct);
        
        assertNotNull(responseProduct.getId(), "Product ID should not be null");
        assertEquals(testProduct.getName(), responseProduct.getName(), "Product name should match");
        assertEquals(testProduct.getDescription(), responseProduct.getDescription(), "Product description should match");
        assertEquals(testProduct.getPrice(), responseProduct.getPrice(), 0.001, "Product price should match");
    }

    @Then("the response should contain a list of products")
    public void the_response_should_contain_a_list_of_products() {
        var products = response.jsonPath().getList(".", Product.class);
        log.info("Retrieved {} products from response", products.size());
        assertFalse(products.isEmpty(), "Product list should not be empty");
    }

    @Then("the product should not exist")
    public void the_product_should_not_exist() {
        response = given()
                .spec(requestSpec)
                .when()
                .get("/api/products/" + testProduct.getId());
        
        assertEquals(404, response.getStatusCode(), "Product should not be found");
    }
}