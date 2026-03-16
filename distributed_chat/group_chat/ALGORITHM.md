# ALGORITHM: GROUP CHAT APPLICATION

## 📋 ALGORITHM 1: SERVER PROGRAM (GroupChatServer.java)

### **Input:** None (runs on startup)
### **Output:** Multi-client chat with message broadcasting and monitoring
### **Data Structures:**
- `ServerSocket`: Listens for incoming connections
- `List<ClientHandler>`: Thread-safe list of all connected clients
- `ClientHandler`: Inner class managing individual client connections
- `BufferedReader`: For reading input streams
- `PrintWriter`: For writing output streams
- `Thread`: One thread per client connection

### **Algorithm Steps:**

```
ALGORITHM: GroupChatServer
BEGIN
    1. INITIALIZE
       - Create ServerSocket on PORT 12351
       - Create synchronized ArrayList for clients
       - Initialize clientCounter = 0
       - Display "Server started on port 12351. Waiting for clients..."
    
    2. ACCEPT_CLIENTS_LOOP
       - WHILE true DO
           - clientSocket ← serverSocket.accept()
           - INCREMENT clientCounter
           - Display "Client-<N> connected: <IP_ADDRESS>"
           - CREATE ClientHandler(clientSocket, clientCounter)
           - ADD ClientHandler to clients list
           - START new Thread with ClientHandler
       - END WHILE
    
    3. BROADCAST_FUNCTION(message, sender)
       - SYNCHRONIZED on clients list:
           - FOR each client IN clients DO
               - IF client ≠ sender THEN
                   - client.sendMessage(message)
               - END IF
           - END FOR
       - Display message on server console
    
    4. REMOVE_CLIENT_FUNCTION(client)
       - REMOVE client from clients list
       - Display "Client-<N> disconnected. Active clients: <count>"
    
    ON IOException:
       - Print error stack trace
       - END program
END

ALGORITHM: ClientHandler (Inner Class)
BEGIN
    1. INITIALIZE
       - Store socket and clientId
       - Create input/output streams
    
    2. GET_USERNAME
       - username ← read from client
       - IF username is null OR empty THEN
           - username ← "Client-<clientId>"
       - END IF
       - joinMessage ← "*** <username> has joined the chat ***"
       - CALL broadcast(joinMessage, this)
    
    3. MESSAGE_LOOP
       - WHILE true DO
           - message ← clientReader.readLine()
           - IF message == null THEN
               - BREAK
           - END IF
           - formattedMessage ← "<username>: " + message
           - CALL broadcast(formattedMessage, this)
       - END WHILE
    
    4. CLEANUP
       - leaveMessage ← "*** <username> has left the chat ***"
       - CALL broadcast(leaveMessage, this)
       - CALL removeClient(this)
       - Close socket and streams
    
    ON IOException:
       - Display error message
       - CALL cleanup()
END
```

---

## 📋 ALGORITHM 2: CLIENT PROGRAM (GroupChatClient.java)

### **Input:** Server IP address and username from user
### **Output:** Bidirectional chat with all connected clients
### **Data Structures:**
- `Socket`: Connection to server
- `BufferedReader`: For reading input streams (console and server)
- `PrintWriter`: For writing to server
- `Thread`: For concurrent message receiving

### **Algorithm Steps:**

```
ALGORITHM: GroupChatClient
BEGIN
    1. GET_SERVER_ADDRESS
       - PROMPT "Enter server IP address (or 'localhost' for local): "
       - serverAddress ← read from console
       - IF serverAddress is null OR empty THEN
           - serverAddress ← "localhost"
       - END IF
    
    2. CONNECT_TO_SERVER
       - CREATE socket ← new Socket(serverAddress, 12351)
       - Display "Connected to server at <address>:<port>"
    
    3. SETUP_IO_STREAMS
       - Create serverReader ← BufferedReader(socket.inputStream)
       - Create serverWriter ← PrintWriter(socket.outputStream)
       - Create consoleReader ← BufferedReader(System.in)
    
    4. GET_USERNAME
       - PROMPT "Enter your username: "
       - username ← read from console
       - IF username is null OR empty THEN
           - username ← "Anonymous"
       - END IF
       - SEND username to server
       - Display "=== Group Chat Started ==="
    
    5. START_LISTENER_THREAD
       - CREATE new Thread:
           - WHILE true DO
               - incoming ← serverReader.readLine()
               - IF incoming == null THEN
                   - BREAK
               - END IF
               - DISPLAY incoming
           - END WHILE
           - ON IOException:
               - DISPLAY "*** Disconnected from server ***"
       - START thread
    
    6. MAIN_SENDING_LOOP
       - WHILE true DO
           - message ← consoleReader.readLine()
           - IF message == null THEN
               - BREAK
           - END IF
           - serverWriter.println(message)
       - END WHILE
    
    7. CLEANUP
       - Close socket and all streams
       - END program
    
    ON UnknownHostException:
       - Display "Error: Could not find server at the specified address."
       - END program
    
    ON IOException:
       - Display "Error: Could not connect to server."
       - END program
END
```

---

## 🔄 ALGORITHM 3: COMPLETE SYSTEM INTERACTION

### **Combined Algorithm for Multi-Client Communication:**

```
ALGORITHM: GroupChatSystem
BEGIN
    // PHASE 1: SERVER INITIALIZATION
    1. SERVER_STARTUP
       - Server binds to port 12351
       - Server enters listening state
       - Initialize empty clients list
    
    // PHASE 2: CLIENT CONNECTIONS
    2. CLIENT_1_CONNECTS
       - Client-1 prompts for server IP
       - Client-1 connects to server
       - Server accepts connection
       - Server creates ClientHandler-1 thread
       - Client-1 sends username
       - Server broadcasts "*** <username-1> has joined ***"
    
    3. CLIENT_2_CONNECTS
       - Client-2 prompts for server IP
       - Client-2 connects to server
       - Server accepts connection
       - Server creates ClientHandler-2 thread
       - Client-2 sends username
       - Server broadcasts "*** <username-2> has joined ***"
       - Client-1 receives join notification
    
    4. CLIENT_N_CONNECTS
       - Repeat for each additional client
       - Each new client receives all join notifications
    
    // PHASE 3: MESSAGE BROADCASTING
    5. MESSAGE_FLOW
       - PARALLEL execution for each client:
       
           // Client-1 sends message
           Client-1 Main Thread:
               - User types message
               - Send to server
           
           Server ClientHandler-1:
               - Receive message from Client-1
               - Format: "<username-1>: <message>"
               - Broadcast to Client-2, Client-3, ..., Client-N
               - Display on server console
           
           Client-2, Client-3, ..., Client-N Listener Threads:
               - Receive broadcasted message
               - Display "<username-1>: <message>"
    
    6. CONCURRENT_MESSAGING
       - All clients can send messages simultaneously
       - Server handles each client in separate thread
       - Messages are broadcast to all OTHER clients
       - Server monitors all messages
    
    // PHASE 4: CLIENT DISCONNECTION
    7. CLIENT_LEAVES
       - IF Client-N disconnects THEN
           - ClientHandler-N detects disconnection
           - Broadcast "*** <username-N> has left ***"
           - Remove ClientHandler-N from clients list
           - Close Client-N socket and streams
           - Remaining clients continue normally
       - END IF
    
    // PHASE 5: CROSS-NETWORK COMMUNICATION
    8. REMOTE_CLIENT_CONNECTION
       - Remote client prompts for server IP
       - Remote client enters server's network IP (e.g., 192.168.1.100)
       - Connection established over network
       - Same message flow as local clients
       - Server displays remote client's IP address
END
```

---

## 📊 COMPLEXITY ANALYSIS

### **Time Complexity:**
- **Server Initialization:** O(1)
- **Client Connection:** O(1) per client
- **Message Broadcasting:** O(n) where n = number of connected clients
- **Message Sending:** O(1) per message
- **Message Receiving:** O(1) per message
- **Overall:** O(n × m) where n = clients, m = messages

### **Space Complexity:**
- **Server:** O(n) where n = number of connected clients
- **Client:** O(1) - Fixed streams and buffers
- **Message Buffer:** O(k) where k = message length
- **Total Server Memory:** O(n × k) for n clients with k-length messages

### **Concurrency:**
- **Server Threads:** 1 main + n client handlers (where n = number of clients)
- **Client Threads:** 2 per client (Main + Listener)
- **Total System Threads:** 1 + n + 2n = 1 + 3n

---

## 🎯 KEY ALGORITHMIC FEATURES

### **1. Thread-Safe Broadcasting**
```
CONCEPT: Synchronized Client List
- clients list uses Collections.synchronizedList()
- Broadcast method synchronizes on clients list
- Prevents race conditions during iteration
- Safe concurrent add/remove operations
```

### **2. Multi-Threaded Server**
```
CONCEPT: One Thread Per Client
- Main thread accepts new connections
- Each client runs in dedicated thread
- Independent message handling
- No blocking between clients
```

### **3. Message Distribution**
```
CONCEPT: Broadcast to All Except Sender
- Server receives message from Client-A
- Iterates through all clients
- Sends to everyone except Client-A
- Sender sees their own message locally
```

### **4. Dynamic Client Management**
```
CONCEPT: Runtime Client List Updates
- Clients added when connected
- Clients removed when disconnected
- No limit on number of clients
- Graceful handling of disconnections
```

### **5. Network Flexibility**
```
CONCEPT: IP Address Configuration
- Server binds to 0.0.0.0 (all interfaces)
- Client prompts for server IP
- Supports localhost for local testing
- Supports network IP for remote clients
```

---

## 🔢 PSEUDOCODE COMPARISON

### **Server Pseudocode:**
```
function startServer():
    socket = listen(12351)
    clients = []
    
    while true:
        client = accept()
        spawn thread(handleClient, client):
            username = receive(client)
            broadcast("*** " + username + " joined ***")
            
            while message = receive(client):
                broadcast(username + ": " + message)
            
            broadcast("*** " + username + " left ***")
            remove(client)

function broadcast(message, sender):
    for each client in clients:
        if client != sender:
            send(client, message)
    print(message)  // Server monitoring
```

### **Client Pseudocode:**
```
function startClient():
    serverIP = prompt("Enter server IP: ")
    socket = connect(serverIP, 12351)
    
    username = prompt("Enter username: ")
    send(socket, username)
    
    spawn thread:
        while message = receive(socket):
            print(message)
    
    while input = readConsole():
        send(socket, input)
```

---

## 📈 ALGORITHM PROPERTIES

| Property | Value |
|----------|-------|
| **Type** | Multi-Client Server Architecture |
| **Communication** | Synchronous TCP/IP |
| **Concurrency Model** | Multi-threaded (1 thread per client) |
| **Message Protocol** | Text-based, line-delimited |
| **Connection Type** | Persistent, bidirectional |
| **Scalability** | 1-to-many (multiple clients) |
| **Broadcasting** | Server-mediated message distribution |
| **Reliability** | TCP guarantees delivery |
| **Network Support** | Local and cross-network |
| **Port** | 12351 |

---

## 🧮 MATHEMATICAL MODEL

### **Message Flow Equation:**
```
Let:
- S = Server
- C₁, C₂, ..., Cₙ = Clients
- M(x→y) = Message from x to y
- B(x) = Broadcast from x
- T = Time

Message Broadcast:
M(C₁→S) at time T₁
B(S→{C₂, C₃, ..., Cₙ}) at time T₁ + δ

Where δ = network latency + processing time
```

### **Thread State Diagram:**
```
Server Main Thread:
RUNNING → BLOCKED(accept) → RUNNING → SPAWN_THREAD → BLOCKED(accept)

Server ClientHandler Thread (per client):
RUNNING → BLOCKED(readLine) → RUNNING → BROADCAST → RUNNING

Client Main Thread:
RUNNING → BLOCKED(readLine) → RUNNING → SEND → RUNNING

Client Listener Thread:
RUNNING → BLOCKED(readLine) → RUNNING → DISPLAY → RUNNING
```

### **Broadcasting Complexity:**
```
For n clients:
- Time to broadcast 1 message: O(n)
- Messages per second: m
- Total operations: O(n × m)
- Bandwidth: O(n × m × k) where k = message size
```

---

## 🔐 KEY DIFFERENCES FROM ONE-TO-ONE CHAT

| Feature | One-to-One | Group Chat |
|---------|-----------|------------|
| **Clients** | 1 | Multiple (unlimited) |
| **Server Threads** | 2 (main + listener) | 1 + n (main + n handlers) |
| **Message Flow** | Direct bidirectional | Broadcast to all |
| **Client List** | Not needed | Thread-safe list |
| **Scalability** | Fixed (1 client) | Dynamic (n clients) |
| **Monitoring** | Server sees 1 conversation | Server sees all conversations |
| **Join/Leave** | Not applicable | Broadcast notifications |
| **IP Configuration** | Hardcoded localhost | User-provided IP |
