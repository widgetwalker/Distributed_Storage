# Distributed Transactions (Two-Phase Commit)

This project implements a distributed transaction system using the Two-Phase Commit (2PC) protocol. It includes a Transaction Coordinator, multiple Participants (Partitions), and a Client/Server architecture.

## Architecture
- **TransactionCoordinator**: Orchestrates the 2PC flow (Prepare -> Commit/Abort).
- **Participant**: Manages a local data partition and handles vote requests.
- **TransactionServer**: A multi-threaded server that handles incoming client connections and coordinates transactions.
- **TransactionClient**: An interactive client that allows users to build and commit transactions.

## How to Run

### 1. Compile the Source
Run the following command in the `transactions` directory:
```powershell
& "C:\Program Files\Java\jdk-21\bin\javac.exe" *.java
```

### 2. Start the Server
Open a terminal and run:
```powershell
& "C:\Program Files\Java\jdk-21\bin\java.exe" TransactionServerLauncher
```
The server will initialize with three partitions: `PARTITION-A`, `PARTITION-B`, and `PARTITION-C`.

### 3. Start the Client
Open another terminal and run:
```powershell
& "C:\Program Files\Java\jdk-21\bin\java.exe" TransactionClientLauncher
```

### 4. Perform Transactions
In the client terminal, you can use the following commands:
- `WRITE <key> <value>`: Add a write operation (e.g., `WRITE user:1 Alice`).
- `READ <key>`: Add a read operation.
- `DELETE <key>`: Add a delete operation.
- `COMMIT`: Submit the transaction to the server for 2PC processing.
- `STATUS`: See your current pending operations.
- `EXIT`: Disconnect.

## Features
- **Atomic Commits**: Ensures either all operations in a transaction succeed or none do.
- **Dynamic Partitioning**: Operations are automatically routed to the correct partition based on the key.
- **Interactive Shell**: Easy-to-use CLI for testing transaction flows.
