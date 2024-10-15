# Orders App

## Requirements

- Java 21
- Docker

## Description

* APP URL: http://localhost:8080
* API DOCS: http://localhost:8080/swagger-ui/index.html

## Further Steps

* Expand domain model, e.g. add invoice implementation
* Write more unit tests
* Add more logging
* Add cache
* Optimize performance
* Improve error handling and validation
* Add security and restrict API
  * E.g delete/update endpoints only for admin, other users can update only status
* Add roles for different actions e.g., Customer, Admin, Warehouse Worker, Courier
* Add auth implementation using JWT token, store it in HTTP_ONLY cookie
  * Then Customer ID field in 'place order dialog' can be removed and id can be taken from authentication
* Write frontend tests
* Write integration tests using test containers
* Add application properties for prod
* Implement add/edit product action in angular (currently buttons are disabled)
* Other ideas:
  * Add home and 'not found' page
  * Integration with payment services
  * Support more cases like refunds/discounts
  * Support placing orders as guest (without registration)
  * Implement search for angular tables
  * Implement user management

