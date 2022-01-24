## Summary

TUI DX Backend technical Test v2 Federico Conoci


######Endpoints:
- POST /client/create will create a new client with an address.
- POST /client/search will look for clients and their orders.
- POST /order/create will create a new order given a client and an address.
- POST /order/update will update an existing order.
- POST /authenticate will generate the jwt token.

######How to use it:
-create client /client/create (response will provide the address, and the client id needed for the next request)
-place an order /order/create 
-update it
-authentication 
-search for the customer

######DB:
There are 3 entities Client, Address and PilotesOrder.
There is an assumption: an order can't exist if it has not been linked to an address and to a client.
So tables relationships have been modeled as the following:
-client and address are mapped as one to many
-address and customer are mapped as many to one
-order and client are mapped as many to one

######Swagger
Swagger has been added as per recommendations: visit http://localhost:8080/swagger-ui/index.html and then search for http://localhost:8080/v2/api-docs.

######Docker:
mvn install
docker-compose up --build

######Security
In order to use the search orders operation authenticate via JWT authentication (/authentication) with the following user:
username: user
password: password

then add the authentication header for the search endpoint.

######Improvements
-Add an endpoint to add address for a customer
-Allow search via addresses
-Store configurations in DB (e.g: number of pilotes and price)
######Notes
Code wise I followed the domain driven design thus separating the concerns between business logic, presentation and persistence.

I took care to handle also the possibles errors e.g:
trying to insert an order for a customer that doesn't exist, or for a non-existing address,
sending invalid requests.

Everything has been developed in a TDD fashion (you can check the commits).

All tests are integration tests that covers most of the scenarios, and the business requirements; furthermore each of them is
independent by the others since for every test there's a database clean.

Tests coverage asses to 97% classes and 95% lines covered (though this is not the only relevant metric).

Tests check: requests validation, business logic, database persistence layer and data integrity.

In this project you will find a postman collection with an environment useful to test the project.

Search is case-sensitive.

Relevant parameters are configurable from the application.properties file (price and number of pilotes)
