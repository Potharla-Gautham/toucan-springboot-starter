# Transaction Management API

This project is a simple **Transaction Management REST API** built using Spring Boot.

The application allows users to:

* Create a transaction
* Get a transaction using its ID
* Update the transaction status
* Get all transactions of a customer
* Validate transaction data
* Handle errors such as duplicate transactions and invalid status changes

## Technologies Used

* Java 17
* Spring Boot 3.5.5
* Spring Web
* Spring Data JPA
* H2 Database
* Maven
* JUnit and MockMvc for testing

## Project Structure

The main transaction code is inside:

`src/main/java/com/example/transactionstarter/transaction`

Important files:

* `Transaction.java` – Stores transaction details
* `TransactionController.java` – Handles API requests
* `TransactionService.java` – Contains the main business logic
* `TransactionRepository.java` – Communicates with the database
* `CreateTransactionRequest.java` – Handles transaction creation data
* `UpdateTransactionStatusRequest.java` – Handles status updates
* `GlobalExceptionHandler.java` – Handles errors

## Transaction Status

The project supports three statuses:

* `PENDING`
* `COMPLETED`
* `FAILED`

A transaction can change only in the following way:

`PENDING → COMPLETED`

or

`PENDING → FAILED`

Once a transaction is `COMPLETED` or `FAILED`, its status cannot be changed again.

## API Endpoints

### 1. Create Transaction

**POST**

`/api/transactions`

Example request:

```json
{
  "transactionId": "tx-1001",
  "customerId": "customer-42",
  "amount": 125.50,
  "currency": "USD",
  "transactionType": "PURCHASE",
  "transactionStatus": "PENDING"
}
```

A successful request returns **201 Created**.

The API also checks that:

* Transaction ID is not empty
* Customer ID is not empty
* Amount is greater than zero
* Required fields are provided
* Transaction ID is unique
* Status is supported

### 2. Get Transaction

**GET**

`/api/transactions/{transactionId}`

Example:

`/api/transactions/tx-1001`

If the transaction exists, its details are returned.

If it does not exist, the API returns **404 Not Found**.

### 3. Update Transaction Status

**PATCH**

`/api/transactions/{transactionId}/status`

Example request:

```json
{
  "transactionStatus": "COMPLETED"
}
```

This changes the transaction from `PENDING` to `COMPLETED` or `FAILED`.

Invalid status changes are rejected.

### 4. Get Customer Transactions

**GET**

`/api/customers/{customerId}/transactions`

Example:

`/api/customers/customer-42/transactions`

This returns all transactions belonging to the specified customer.

If the customer has no transactions, an empty list is returned.

## Database

The project uses an **H2 in-memory database**.

The database is temporary, so the transaction data is cleared when the application is restarted.

The H2 console is available at:

`http://localhost:8080/h2-console`

## How to Run the Project

### Step 1: Check Java

Make sure Java 17 or later is installed.

```bash
java -version
```

### Step 2: Run the application

On Windows:

```bash
mvnw.cmd spring-boot:run
```

On Linux/Mac:

```bash
./mvnw spring-boot:run
```

The application will start on:

`http://localhost:8080`

## How to Run Tests

Run:

```bash
mvnw.cmd clean test
```

The tests check important cases such as:

* Creating a valid transaction
* Rejecting invalid amounts
* Rejecting duplicate transaction IDs
* Getting a transaction
* Getting customer transactions
* Updating transaction status
* Preventing invalid status changes
* Rejecting unsupported statuses
* Handling customers with no transactions

## Error Handling

The application provides proper error responses for common problems, including:

* Transaction not found
* Duplicate transaction ID
* Invalid transaction status
* Invalid status transition
* Invalid request data

## Future Improvements

Some features that could be added in the future are:

* Authentication and authorization
* Pagination
* Permanent database such as MySQL or PostgreSQL
* Swagger/OpenAPI documentation
* Database migrations using Flyway or Liquibase
* Better validation for currency and transaction type
* Optimistic locking for handling simultaneous updates

## Conclusion

This project demonstrates how to build a basic transaction management backend using **Spring Boot, REST APIs, JPA, and H2**.

It also demonstrates validation, exception handling, database operations, status management, and automated testing.
