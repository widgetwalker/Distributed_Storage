# GROUP CHAT APPLICATION
## AIM AND ALGORITHM

---

## 🎯 AIM

**To design and implement a multi-client group chat application using Java socket programming that enables multiple users to communicate simultaneously through a central server with message broadcasting capabilities and support for cross-network connectivity.**

### Objectives:

1. **Multi-Client Communication**: Enable multiple clients to connect to a single server simultaneously
2. **Message Broadcasting**: Implement a mechanism to broadcast messages from one client to all other connected clients
3. **Server Monitoring**: Allow the server to monitor and display all chat activity
4. **User Identification**: Implement username-based identification for each client
5. **Cross-Network Support**: Enable clients to connect from different computers using IP addresses
6. **Graceful Connection Management**: Handle client connections and disconnections gracefully
7. **Thread-Safe Operations**: Ensure thread-safe operations for concurrent client handling

---

## 📋 ALGORITHM

### ALGORITHM 1: GROUP CHAT SERVER

**Input:** None (runs on startup)  
**Output:** Multi-client chat with message broadcasting and monitoring  
**Port:** 12351

#### Steps:

```
BEGIN
    1. Initialize server socket on PORT 12351
    2. Create synchronized list to store client handlers
    3. Set clientCounter = 0
    4. Display "Server started on port 12351. Waiting for clients..."
    
    5. WHILE (true) DO
        a. Wait for client connection (serverSocket.accept())
        b. Increment clientCounter
        c. Display "Client-N connected: <IP_ADDRESS>"
        d. Create new ClientHandler object with (socket, clientId)
        e. Add ClientHandler to clients list
        f. Start new thread for ClientHandler
    END WHILE
END
```

#### BROADCAST Function:
```
FUNCTION broadcast(message, sender)
BEGIN
    SYNCHRONIZED (clients list)
        FOR each client IN clients DO
            IF client ≠ sender THEN
                Send message to client
            END IF
        END FOR
    END SYNCHRONIZED
    
    Display message on server console
END FUNCTION
```

#### CLIENT HANDLER (Inner Class):
```
BEGIN
    1. Create input/output streams for client socket
    
    2. Read username from client
    3. IF username is empty THEN
        username = "Client-<clientId>"
    END IF
    
    4. Broadcast "*** <username> has joined the chat ***"
    
    5. WHILE (true) DO
        a. Read message from client
        b. IF message is null THEN
            BREAK
        END IF
        c. Format message as "<username>: <message>"
        d. Broadcast formatted message to all other clients
    END WHILE
    
    6. Broadcast "*** <username> has left the chat ***"
    7. Remove client from clients list
    8. Close socket and streams
END
```

---

### ALGORITHM 2: GROUP CHAT CLIENT

**Input:** Server IP address and username from user  
**Output:** Bidirectional chat with all connected clients

#### Steps:

```
BEGIN
    1. Prompt user: "Enter server IP address (or 'localhost' for local): "
    2. Read serverAddress from console
    3. IF serverAddress is empty THEN
        serverAddress = "localhost"
    END IF
    
    4. Connect to server at (serverAddress, PORT 12351)
    5. Display "Connected to server at <address>:<port>"
    
    6. Create input/output streams
    
    7. Prompt user: "Enter your username: "
    8. Read username from console
    9. IF username is empty THEN
        username = "Anonymous"
    END IF
    
    10. Send username to server
    11. Display "=== Group Chat Started ==="
    
    12. START LISTENER THREAD:
        WHILE (true) DO
            a. Read message from server
            b. IF message is null THEN
                BREAK
            END IF
            c. Display message on console
        END WHILE
        Display "*** Disconnected from server ***"
    
    13. MAIN THREAD (Sending):
        WHILE (true) DO
            a. Read message from console
            b. IF message is null THEN
                BREAK
            END IF
            c. Send message to server
        END WHILE
    
    14. Close socket and streams
END
```

---

### ALGORITHM 3: COMPLETE SYSTEM FLOW

```
SYSTEM INITIALIZATION:
    1. Server starts and listens on port 12351
    2. Server initializes empty client list

CLIENT CONNECTION:
    3. Client-1 connects to server
    4. Server accepts connection and creates ClientHandler-1
    5. Client-1 sends username
    6. Server broadcasts "*** <username-1> joined ***"
    
    7. Client-2 connects to server
    8. Server accepts connection and creates ClientHandler-2
    9. Client-2 sends username
    10. Server broadcasts "*** <username-2> joined ***" to Client-1
    11. Client-1 receives join notification

MESSAGE BROADCASTING:
    12. Client-1 sends message "Hello"
    13. Server receives message in ClientHandler-1
    14. Server formats as "<username-1>: Hello"
    15. Server broadcasts to Client-2, Client-3, ..., Client-N
    16. Server displays message on console
    17. All clients (except Client-1) receive and display message

CLIENT DISCONNECTION:
    18. Client-1 disconnects
    19. ClientHandler-1 detects disconnection
    20. Server broadcasts "*** <username-1> left ***"
    21. Server removes ClientHandler-1 from list
    22. Remaining clients continue normally
```

---

## 📊 COMPLEXITY ANALYSIS

### Time Complexity:
- **Server Initialization:** O(1)
- **Client Connection:** O(1) per client
- **Message Broadcasting:** O(n) where n = number of connected clients
- **Message Sending:** O(1) per message
- **Overall System:** O(n × m) where n = clients, m = messages

### Space Complexity:
- **Server:** O(n) where n = number of connected clients
- **Client:** O(1) - Fixed streams and buffers
- **Total:** O(n)

### Concurrency:
- **Server Threads:** 1 main + n client handlers
- **Client Threads:** 2 per client (main + listener)
- **Total System Threads:** 1 + 3n

---

## 🔑 KEY FEATURES

1. **Thread-Safe Broadcasting**
   - Uses synchronized list for client management
   - Synchronized block during message broadcasting
   - Prevents race conditions

2. **Multi-Threaded Architecture**
   - One thread per client on server
   - Two threads per client application
   - Non-blocking concurrent operations

3. **Dynamic Client Management**
   - Clients can join/leave at any time
   - Automatic cleanup on disconnection
   - No limit on number of clients

4. **Cross-Network Communication**
   - Server binds to all network interfaces
   - Client accepts IP address input
   - Supports both local and remote connections

5. **User-Friendly Interface**
   - Username-based identification
   - Join/leave notifications
   - Clear message formatting

---

## 📈 ADVANTAGES

1. **Scalability**: Supports unlimited number of clients
2. **Reliability**: TCP ensures message delivery
3. **Flexibility**: Works on local network and across networks
4. **Monitoring**: Server can monitor all conversations
5. **Thread Safety**: Synchronized operations prevent data corruption
6. **Graceful Degradation**: Handles disconnections without affecting other clients

---

## 🎓 LEARNING OUTCOMES

1. Understanding of **socket programming** in Java
2. Implementation of **multi-threaded applications**
3. Knowledge of **client-server architecture**
4. Experience with **thread synchronization**
5. Understanding of **TCP/IP networking**
6. Implementation of **broadcasting mechanisms**
7. Handling **concurrent operations** safely

---

## 🔧 TECHNICAL SPECIFICATIONS

| Specification | Value |
|--------------|-------|
| **Language** | Java (JDK 21) |
| **Protocol** | TCP/IP |
| **Port** | 12351 |
| **Architecture** | Client-Server |
| **Concurrency** | Multi-threaded |
| **Message Format** | Text-based, line-delimited |
| **Connection Type** | Persistent, bidirectional |
| **Network Support** | Local and cross-network |

---

## 📝 CONCLUSION

This group chat application demonstrates the implementation of a robust multi-client communication system using Java socket programming. The system efficiently handles multiple concurrent connections, broadcasts messages to all participants, and supports both local and cross-network communication. The use of thread-safe operations ensures data integrity, while the multi-threaded architecture provides responsive and non-blocking communication for all users.
