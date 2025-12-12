# Tiny Ledger – Spring Boot Assignment

Backend-only ledger application built with **Java + Spring Boot**.

The application supports:
- Deposits and withdrawals
- Per-account balances and transaction history
- Transfers between accounts (modeled as two ledger entries)

Interaction is done via REST APIs using `curl`.

---

## Setup

### Requirements
- **Java 17+**
- **Git Bash** (recommended on Windows)
- **jq** (optional, for readable JSON output)

### Run the Application

From the project root:

```bash
./mvnw spring-boot:run
```
Or build and run the JAR:
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

### Step 1: Deposit 100 on Account 1

```
curl -s -X POST http://localhost:8080/ledger/1/deposit \
  -H "Content-Type: application/json" \
  -d '{"amount":100,"description":"Initial deposit"}' | jq
```

Expected Response:
```
{
  "id": 1,
  "type": "DEPOSIT",
  "amount": 100,
  "timestamp": "2025-12-12T21:12:53.3246639",
  "description": "Initial deposit"
}
```
---
### Step 2: Withdraw 30 from Account 1
```
curl -s -X POST http://localhost:8080/transactions/withdraw \
  -H "Content-Type: application/json" \
  -d '{"amount":300,"description":"Cheeseburguer"}' | jq
```

Expected Response:
```
{
  "id": 2,
  "type": "WITHDRAWAL",
  "amount": 30,
  "timestamp": "2025-12-12T21:14:34.0669924",
  "description": "Videogames"
}
```

---
### Step 3: Withdraw 275 from Account 1 (Expected to Fail - Insufficient Funds)
```
curl -s -X POST http://localhost:8080/ledger/1/withdraw \
  -H "Content-Type: application/json" \
  -d '{"amount":275,"description":"House"}' | jq
```

Expected Response (HTTP 400 Bad Request):
```
{
  "timestamp": "2025-12-12T21:15:54.7309985",
  "status": 400,
  "error": "Cannot withdraw more money than you have in your account balance."
}
```
---
### Step 4: View transaction history for Account 1
```
curl -s http://localhost:8080/ledger/1/transactions | jq
```

Expected Response:
```
[
  {
    "id": 1,
    "type": "DEPOSIT",
    "amount": 100,
    "timestamp": "2025-12-12T21:12:53.3246639",
    "description": "Initial deposit"
  },
  {
    "id": 2,
    "type": "WITHDRAWAL",
    "amount": 30,
    "timestamp": "2025-12-12T21:14:34.0669924",
    "description": "Videogames"
  }
]
```
---
### Step 5 (New Implementation): Transfer 25 from account 1 to account 2
```
curl -s -X POST http://localhost:8080/ledger/1/transfer \
  -H "Content-Type: application/json" \
  -d '{"amount":25,"description":"mbway","receiverId":2}' | jq
```

Expected Response:
```
[
  {
    "id": 3,
    "type": "WITHDRAWAL",
    "amount": 25,
    "timestamp": "2025-12-12T21:19:36.3425049",
    "description": "Transfer to Account with ID 2---mbway"
  },
  {
    "id": 4,
    "type": "DEPOSIT",
    "amount": 25,
    "timestamp": "2025-12-12T21:19:36.3425049",
    "description": "Received money from Account with ID 1---mbway"
  }
]

```
---
### Validation test 1: Try to deposit or withdraw a negative or zero value
```
curl -s -X POST http://localhost:8080/ledger/1/deposit \
  -H "Content-Type: application/json" \
  -d '{"amount":0,"description":"Invalid"}' | jq
```

Expected Response (HTTP 400 Bad Request):
```
{
  "timestamp": "2025-12-12T21:20:52.9985202",
  "status": 400,
  "error": "Amount must be a positive value."
}
```

---
### Validation test 2: Try to transfer to own account.
```
curl -s -X POST http://localhost:8080/ledger/1/transfer \
  -H "Content-Type: application/json" \
  -d '{"amount":25,"description":"sametransfer","receiverId":1}' | jq
```

Expected Response (HTTP 400 Bad Request):
```
{
  "timestamp": "2025-12-12T21:22:22.2090211",
  "status": 400,
  "error": "Cannot transfer money to the same account."
}
```

---

## How to run unit tests:
(in project source)
```
./mvnw test
```

## Design and Implementation Details
### Assumptions
As permitted by the assignment, the following simplifications were made:
 - In-Memory Store: All data is stored in a simple List within the LedgerService. Data is lost when the application stops.
 - No need for account register or login. This tinyledger app is used just for one person and for a single runtime use.
 - No different types of currency. BigDecimal is used as it's the standard in fintech services.

#### Update:
 - AccountID added to enable transfer of money between accounts. Ledger can control each account to operate on based on the id given in the first parameter.
 - List used before for all transactions is now a Map<accountId, List<Transaction>>

### Error Handling
 - A dedicated GlobalExceptionHandler component handles application-wide exceptions. This ensures that validation errors, such as insufficient funds during a withdrawal, are converted into a structured JSON response with the appropriate HTTP 400 Bad Request status, providing a good API experience.
 - Without this feature, when getting an error, user would only be shown a generic response as the logs are shown in the Sprint Boot terminal.

#### Update:
 - New error handling to prevent accounts from transfering to their own wallet.

## Final Notes & Possible Improvements

The following aspects were intentionally kept out of scope for this exercise but would be considered in a production-ready system:

- **Concurrency & atomicity**: transfers and balance updates are not synchronized; in a real system this would require transactional guarantees.
- **Persistence**: data is stored in memory only - this is not aplicable in real-life
- **Validation**: additional error handling checking for account existence and account ID constraints should be added.
- **Security**: authentication, authorization, and input validation are not implemented.

These trade-offs were made to keep the implementation aligned with the scope of the assignment.
