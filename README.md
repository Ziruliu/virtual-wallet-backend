# Virtual Wallet - A RESTful API Backend

This project creates a RESTful API backend using Java, Spring Framework, Hibernate, Maven and H2 (In-memory store) datebase, which is able to provide service for a virtual wallet to track users transaction account.


# How to Run

1. Clone this repository via `git clone https://github.com/Ziruliu/virtual-wallet-backend.git`.

2. Run `mvn clean package` on the commond line to download all the maven depencies and build the excutable jar file.

3. Run `java -jar target/virtual-wallet-backend-0.0.1-SNAPSHOT.jar` to start the application.

4. By default, application will run on `localhost:8080`, you can change the port number by going to `/src/main/resources/application.properties`, and modify the `server.port` property.


# About the Service

Following public end points are provided by the Wallet Service:

### Create a new wallet for a user
```
GET /user/wallet

{
    "response": "Wallet successfully created!"
}
```
Each user can only have single wallet, calling the end point again will result in the following response:
```
{
    "status": 400,
    "message": "User already has a wallet",
    "timeStamp": "2018-10-14 19:49:47"
}
```

### Return current account balance
Initial account balance is 0.
```
GET /user/balance

{
    "account balance": 0
}
```
If the wallet has not been created yet, it will result in the following response:
```
{
    "status": 400,
    "message": "Wallet Not Exist",
    "timeStamp": "2018-10-14 20:00:20"
}
```
And it applyes to other requests as well.

### Perform a deposit transaction on the account
```
GET /user/deposit/100

{
    "id": 1,
    "type": "Deposit",
    "amount": 100,
    "timestamp": "2018-10-14 20:03:07"
}
```
Only positive amount will be accepted, a negative amount will result in the following response:
```
{
    "status": 400,
    "message": "Please enter a positive amount",
    "timeStamp": "2018-10-14 20:04:23"
}
```
A deposit transaction will add the amount to the account balance:
```
GET /user/balance

{
    "account balance": 100
}
```

### Perform a withdrawal transaction on the account
```
GET /user/withdrawal/50

{
    "id": 2,
    "type": "Withdrawal",
    "amount": 50,
    "timestamp": "2018-10-14 20:09:13"
}
```
If withdrawal amount make the account balance goes beyond zero, it will result in the following response:
```
GET /user/withdrawal/100

{
    "status": 400,
    "message": "Withdrawal amount cannot exceed 50",
    "timeStamp": "2018-10-14 20:11:06"
}
```

### Return last N transactions
```
GET /user/history/2

[
    {
        "id": 2,
        "type": "Withdrawal",
        "amount": 50,
        "timestamp": "2018-10-14 20:09:13"
    },
    {
        "id": 1,
        "type": "Deposit",
        "amount": 100,
        "timestamp": "2018-10-14 20:03:07"
    }
]
```
If N is large than the total transactions, all transactions will be returned:
```
GET /user/history/100

[
    {
        "id": 2,
        "type": "Withdrawal",
        "amount": 50,
        "timestamp": "2018-10-14 20:09:13"
    },
    {
        "id": 1,
        "type": "Deposit",
        "amount": 100,
        "timestamp": "2018-10-14 20:03:07"
    }
]
```

## Exceptions

Following custom exceptions are implemented in this project:
```
HasWalletException
NegativeAccountBalanceException
NegativeAmountException
WalletNotException
```

## Concurrency

Concurrency is enforced by adding `Async` annotation to the two methos: `getBalance()`, `get_last_N_transactions(int N)` at `\src\main\java\com\ziruliu\backend\service\WalletService.java`.

Aysnc Executor Confirguation:
```
@Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("WalletService-");
        executor.initialize();
        return executor;
    }
```

## Testing

There are a total of 8 custom test cases at `\src\test\java\com\ziruliu\backend\VirtualWalletBackendApplicationTests.java`.
