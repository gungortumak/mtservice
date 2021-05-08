# Gungor Tumak Technical Assesment Money Transfer

A simple service for P2P money transfer

####  Tech Stack:

- java 11
- Spring Framework 5.3.6 
- Spring Boot 2.4.5
- Maven
- H2 db

####  Run Application:

java -jar target/money-transfer-service-0.0.1-SNAPSHOT.jar


####  URLS:

    Transfer Service : http://localhost:8080/v1/transfers
    
    H2 Console : http://localhost:8080/h2-console


SERVICE USAGE

    http://localhost:8080/v1/transfers **POST** 
    Sample Request :
    {
    "sourceAccountId": "101",
    "targetAccountId": "102",
    "amount": "100"
    }

    Success Response
        HTTP 201 success
        No content


    Error Response
        HTTP 400 -> Insufficant Funds, Transfer Between Same Account, Currency Mismatch,
        HTTP 404 -> Source or Target Account not found
        HTTP 500 -> internal server errors, concurrent modify error
        Error Content For Client :
        Sample JSON RESPONSE Content
        {
            message: "Validation rules are not match Insufficent Balance For : 101"
        }

##TESTS
    - Controller tests are verifing controller logic and mocking service layer
    - Service tests are verifing service logic and mocking repository layer
    - Repository tests are using h2 in memory database. 
    - Integration tests are verfing E2E functionality without mocking and using data.sql as initial data set      

### ASSUMPTIONS
    - Limited currency option GBP TRY USD EUR IND
    - Transfer between different currencies is not allowed 
    - Concurent changes on account doesnot need to block reads, optimistic lock is used         

### IMPROVEMENTS
    - Service Security should be handled
    - Account rest services can be added to manage accounts
    - TransferTransaction get service can be added to track transactions  
    - Validations needs to be added to models
    - Currency entity model and rest services can be added to manage currencies
    - Performance and consistency trade-off for concurrent transfers and account changes. 
    - Transfer between different currency accounts can be enabled by integrating acurrency converter service 
    - Repository test with test profile