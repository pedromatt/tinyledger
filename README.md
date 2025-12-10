# Tiny Ledger – Spring Boot Coding Assignment

A backend-only ledger application built with Java and Spring Boot.

This solution implements the required features:
1.  Recording money movements (deposits and withdrawals).
2.  Viewing the current balance.
3.  Viewing the transaction history.

Interaction is done entirely through REST API endpoints using the `curl` command-line tool.

---

##  Setup



### Requirements

* **Java 17+**
* **Git Bash** (Recommended on Windows for native `curl` and `bash` support)
* **`jq`** (Optional, but highly recommended for formatted JSON output)

### Clone and Run

1.  **Clone the repository**
 
2.  **Start the Spring Boot application:**
    ```bash
    ./mvnw spring-boot:run
    ```
    OR
    ```
    ./mvnw clean package
    
    java -jar target/tiny-ledger-0.0.1-SNAPSHOT.jar
    ```
 

When started successfully, the API will be available at:

**`http://localhost:8080`**



### Optional: Install `jq`

`jq` is a powerful command-line JSON processor that makes the API outputs readable. If you don't install this, JSON responses will appear in just 1 single line, making it harder to interpret.

1.  Download `jq-win64.exe` from [here](https://github.com/jqlang/jq/releases).
2.  Rename it to `jq.exe`.
3.  Place it in a folder (e.g., `C:\Tools\jq\`).
4.  Add that folder to your system `PATH` environment variable.
5.  Restart your Git Bash terminal.

You can verify the installation by running the following command on Git Bash and obtaining a response:

```bash
jq --version
```

---

##  Test Cases

### Step 1: Deposit 100

```
curl -s -X POST http://localhost:8080/transactions/deposit \
  -H "Content-Type: application/json" \
  -d '{"amount":100,"description":"Initial deposit"}' | jq
```

Expected Response:
```
{
  "id": 1,
  "type": "DEPOSIT",
  "amount": 100,
  "timestamp": "2025-12-10T03:32:37.610031",
  "description": "Initial deposit"
}


```
---
### Step 2: Withdraw 300 (Expected to Fail - Insufficient Funds)
```
curl -s -X POST http://localhost:8080/transactions/withdraw \
  -H "Content-Type: application/json" \
  -d '{"amount":300,"description":"Cheeseburguer"}' | jq
```

Expected Response (HTTP 400 Bad Request):
```
{
  "status": 400,
  "timestamp": "2025-12-10T03:33:11.6550918",
  "error": "Cannot withdraw more money than you have in your account balance."
}

```
---
### Step 3: Check Current Balance
```
curl -s http://localhost:8080/transactions/balance | jq
```

Expected Response (HTTP 400 Bad Request):
```
100
```

---
### Step 4: Withdraw 30
```
curl -s -X POST http://localhost:8080/transactions/withdraw \
  -H "Content-Type: application/json" \
  -d '{"amount":30,"description":"Videogames"}' | jq
```

Expected Response (HTTP 400 Bad Request):
```
{
  "id": 2,
  "type": "WITHDRAWAL",
  "amount": 30,
  "timestamp": "2025-12-10T03:34:05.355972",
  "description": "Videogames"
}

```
---
### Step 5: View transaction history
```
curl -s http://localhost:8080/transactions | jq
```

Expected Response (HTTP 400 Bad Request):
```
[
  {
    "id": 1,
    "type": "DEPOSIT",
    "amount": 100,
    "timestamp": "2025-12-10T03:32:37.610031",
    "description": "Initial deposit"
  },
  {
    "id": 2,
    "type": "WITHDRAWAL",
    "amount": 30,
    "timestamp": "2025-12-10T03:34:05.355972",
    "description": "Videogames"
  }
]

```
---
### Step 6: Check Final Balance
```
curl -s http://localhost:8080/transactions/balance | jq
```

Expected Response:
```
70
```
---
### Extra test: Try to deposit or withdraw a negative or zero value
```
curl -s -X POST http://localhost:8080/transactions/deposit \
  -H "Content-Type: application/json" \
  -d '{"amount":0,"description":"Netflix"}' | jq
```

Expected Response (HTTP 400 Bad Request):
```
{
  "status": 400,
  "timestamp": "2025-12-10T03:35:09.7366916",
  "error": "Amount must be a positive value."
}


```

---

## Design and Implementation Details
### Assumptions
As permitted by the assignment, the following simplifications were made:
 - In-Memory Store: All data is stored in a simple List within the LedgerService. Data is lost when the application stops.
 - No need for account register or login. This tinyledger app is used just for one person and for a single runtime use.
 - No different types of currency. BigDecimal is used as it's the standard in fintech services.

### Error Handling
A dedicated GlobalExceptionHandler component handles application-wide exceptions. This ensures that validation errors, such as insufficient funds during a withdrawal, are converted into a structured JSON response with the appropriate HTTP 400 Bad Request status, providing a good API experience.
Without this feature, when getting an error, user would only be shown a generic response as the logs are shown in the Sprint Boot terminal.