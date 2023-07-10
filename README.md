# Product Package Service 
This application provides a RESTful API to for a Product Package Service.

## Building The Product Package Service
The product package service was built with Maven version 3.8.4. To build it do the following.

1. Go to the directory containing the maven build file (pom.xml).
2. Type the command ">mvn clean" to remove old build files.
3. Type the command ">mvn package" to create the new build.

### Build Requirements.
You must use **Java 17.0.7** to build the project. JDK Details of the JDK used are,

1.  IMPLEMENTOR=Oracle Corporation
2.  JAVA_VERSION=17.0.7
3.  JAVA_VERSION_DATE=2023-04-18

## Alternative JAR
If you don't want to build the JAR file using mvn. Then there is a prebuilt JAR file in the folder **\prebuilttarget**.
You must use Java 17.0.7 when running this jar file.

## Using The Application
If you build the application using the maven, then the jar file **codingexercise-0.0.1-SNAPSHOT.jar** will be created in target directory.
You can execute this jar file by changing to the target folder and typing the following at the command line.

**>java -jar codingexercise-0.0.1-SNAPSHOT.jar**

The application uses OpenApi 3.0 to provide documentation and access to its REST API. To access the OpenAPI documentation use the following url in your browser [OpenAPI Swagger UI]http://localhost:8080/swagger-ui/index.html

Note the above link assumes you are running on localhost.

# RESTful API Supported
The application supports the following rest api (note these are doucmented in the OpenAPI described above).

| HTTP Command  | URL               | Description                                   | Notes                             |
| ------------- | ----------------- | --------------------------------------------- | --------------------------------- |
| GET           | /packages         | Gets a list of all Product Packages           | Optional parameter currencyToUse  |
| GET           | /packages/{id}    | Gets a specific package using the package id  | Optional parameter currencyToUse  |
| POST          | /packages         | Creates a new package with a new package id   |                                   |
| PUT           | /packages/{id}    | Updates an existing package using its id      |                                   |
| DELETE        | /packages/{id}    | Deletes the package with the given id         | Omnipotent delete.                |

## Rest API response Codes
Successful requests return HTTP Status code OK (200).
Unsuccessful requests return HTTP Status code BAD_REQUEST (400).
Note as delete is omnipotent and attempt to delete a package using a non-exiting id, will return HTTP Status code NO_CONTENT (204).

## Package Structure
A Package contains the following fields.
1.  id : a unique string identifying the package.
2.  name : a non-null string naming the package.
3.  description : a string describing the package.
4.  productids : a list of strings each containing a the unique product id of a product in the package.
5.  totalprice : the total prices of all products included in the package.

An example schema for the GET /packages/{id} API is,
    {
        "id": "string",
        "name": "string",
        "description": "string",
        "productIds": [
            "string"
            ],
        "totalPrice": 0
    }
**The Schema and documnetation for each API can be found using the link** [OpenAPI Swagger UI]http://localhost:8080/swagger-ui/index.html.

## Validation of API Requests
1.  **name cannot be null or empty**
2.  **productids cannot be null and cannot contain invalid product ids. But the productids field may be empty.**
**The REST API does not allow the creation of packages that contain invalid product ids.** It checks product ids are valid using [the product service]https://user:pass@product-service.herokuapp.com/api/v1/products. An attempt to add a package with an invalid product id will result in a BAD_REQUEST response.

## Currency Handling
The field **totalPrice** gives its value in a base currency, which is current USD.
For HTTP GET commands (ie. /packages and /packages/{id}) there is an optional paramter **currencyToUse**.
This parameter can specific the currency to use for the totalPrice field. CurrencyTouse is a String value that must match
the currency string used by the [exchange service used]https://www.frankfurter.app/latest.
An example value for currencyToUse is "GBP" to convert the base currency to British Pounds.

# Notes on Testing
Spring Boot tests have been created in the source code to test each API call. All tests currently pass.
