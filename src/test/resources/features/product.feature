Feature: Product API Testing
  As an API user
  I want to perform CRUD operations on products
  So that I can manage the product inventory

  Background:
    Given the API is running at http://localhost:8080

  @create
  Scenario: Create a new product
    Given I have a product with following details:
      | name           | description                | price  |
      | Gaming Laptop  | High-performance laptop    | 1299.99|
    When I send POST request to "/api/products"
    Then the response status code should be 201
    And the response should contain the product details

  @read
  Scenario: Get all products
    When I send GET request to "/api/products"
    Then the response status code should be 200
    And the response should contain a list of products

  @read
  Scenario: Get a specific product
    Given I have a product with following details:
      | name           | description                | price  |
      | Test Product   | Test Description          | 99.99  |
    When I send POST request to "/api/products"
    And I send GET request to "/api/products/1"
    Then the response status code should be 200
    And the response should contain the product details

  @update
  Scenario: Update a product
    Given I have a product with following details:
      | name           | description                | price  |
      | Initial Product| Initial Description       | 99.99  |
    When I send POST request to "/api/products"
    And I have a product with following details:
      | name              | description               | price  |
      | Updated Product   | Updated Description      | 149.99 |
    And I send PUT request to "/api/products/1"
    Then the response status code should be 200
    And the response should contain the product details

  @delete
  Scenario: Delete a product
    Given I have a product with following details:
      | name           | description                | price  |
      | Delete Product | To be deleted             | 49.99  |
    When I send POST request to "/api/products"
    And I send DELETE request to "/api/products/1"
    Then the response status code should be 204
    And the product should not exist