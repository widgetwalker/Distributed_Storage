# ALGORITHM: ONE-TO-ONE CHAT APPLICATION

## 📋 ALGORITHM 1: SERVER PROGRAM (oneToOneServer.java)

### **Input:** None (runs on startup)
### **Output:** Bidirectional chat communication with one client
### **Data Structures:**
- `ServerSocket`: Listens for incoming connections
- `Socket`: Represents client connection
- `BufferedReader`: For reading input streams
- `PrintWriter`: For writing output streams
- `Thread`: For concurrent message handling

### **Algorithm Steps:**

```
ALGORITHM: OneToOneServer
BEGIN
    1. INITIALIZE
       - Create ServerSocket on PORT 12350
       - Display "Server started. Waiting for a client..."
    
    2. WAIT_FOR_CONNECTION
       - Call serverSocket.accept()
       - WAIT until client connects
       - Store client connection in clientSocket
       - Display "Client connected: <IP_ADDRESS>"
    
    3. SETUP_IO_STREAMS
       - Create clientReader ← BufferedReader(clientSocket.inputStream)
       - Create clientWriter ← PrintWriter(clientSocket.outputStream)
       - Create consoleReader ← BufferedReader(System.in)
    
    4. START_LISTENER_THREAD
       - CREATE new Thread:
           WHILE true DO
               message ← clientReader.readLine()
               IF message == null THEN
                   BREAK
               END IF
               DISPLAY "Client: " + message
           END WHILE
           ON IOException:
               DISPLAY "Client disconnected."
       - START thread
    
    5. MAIN_SENDING_LOOP
       - WHILE true DO
           serverMessage ← consoleReader.readLine()
           IF serverMessage == null THEN
               BREAK
           END IF
           clientWriter.println("Server: " + serverMessage)
       END WHILE
    
    6. CLEANUP
       - Close all streams and sockets
       - END program
       
    ON IOException:
       - Print error stack trace
       - END program
END
```

---

## 📋 ALGORITHM 2: CLIENT PROGRAM (oneToOneClient.java)

### **Input:** None (runs on startup)
### **Output:** Bidirectional chat communication with server
### **Data Structures:**
- `Socket`: Connection to server
- `BufferedReader`: For reading input streams
- `PrintWriter`: For writing output streams
- `Thread`: For concurrent message handling

### **Algorithm Steps:**

```
ALGORITHM: OneToOneClient
BEGIN
    1. INITIALIZE
       - SET SERVER_ADDRESS ← "localhost"
       - SET SERVER_PORT ← 12350
    
    2. CONNECT_TO_SERVER
       - Create socket ← new Socket(SERVER_ADDRESS, SERVER_PORT)
       - Display "Connected to server. Start chatting!"
    
    3. SETUP_IO_STREAMS
       - Create serverReader ← BufferedReader(socket.inputStream)
       - Create serverWriter ← PrintWriter(socket.outputStream)
       - Create consoleReader ← BufferedReader(System.in)
    
    4. START_LISTENER_THREAD
       - CREATE new Thread:
           WHILE true DO
               incoming ← serverReader.readLine()
               IF incoming == null THEN
                   BREAK
               END IF
               DISPLAY incoming
           END WHILE
           ON IOException:
               DISPLAY "Disconnected from server."
       - START thread
    
    5. MAIN_SENDING_LOOP
       - WHILE true DO
           message ← consoleReader.readLine()
           IF message == null THEN
               BREAK
           END IF
           serverWriter.println(message)
       END WHILE
    
    6. CLEANUP
       - Close all streams and socket
       - END program
       
    ON IOException:
       - Print error stack trace
       - END program
END
```

---

## 🔄 ALGORITHM 3: COMPLETE SYSTEM INTERACTION

### **Combined Algorithm for Full Communication Flow:**

```
ALGORITHM: OneToOneChatSystem
BEGIN
    // PHASE 1: INITIALIZATION
    1. SERVER_INIT
       - Server binds to port 12350
       - Server enters listening state
    
    2. CLIENT_INIT
       - Client attempts connection to localhost:12350
    
    3. HANDSHAKE
       - IF connection successful THEN
           - Server accepts connection
           - Both create I/O streams
       - ELSE
           - Client displays error
           - EXIT
       - END IF
    
    // PHASE 2: THREAD CREATION
    4. SERVER_THREADS
       - Create ServerListenerThread
       - ServerListenerThread monitors clientReader
       - Main thread monitors consoleReader
    
    5. CLIENT_THREADS
       - Create ClientListenerThread
       - ClientListenerThread monitors serverReader
       - Main thread monitors consoleReader
    
    // PHASE 3: BIDIRECTIONAL COMMUNICATION
    6. MESSAGE_EXCHANGE_LOOP
       - PARALLEL execution:
       
           // Server Side
           THREAD ServerMain:
               WHILE connected DO
                   input ← read from server console
                   send "Server: " + input to client
               END WHILE
           
           THREAD ServerListener:
               WHILE connected DO
                   message ← read from client
                   display "Client: " + message
               END WHILE
           
           // Client Side
           THREAD ClientMain:
               WHILE connected DO
                   input ← read from client console
                   send input to server
               END WHILE
           
           THREAD ClientListener:
               WHILE connected DO
                   message ← read from server
                   display message
               END WHILE
    
    // PHASE 4: TERMINATION
    7. DISCONNECT
       - IF connection lost OR user exits THEN
           - Catch IOException
           - Display disconnection message
           - Close all resources
           - Terminate threads
       - END IF
END
```

---

## 📊 COMPLEXITY ANALYSIS

### **Time Complexity:**
- **Connection Setup:** O(1)
- **Message Sending:** O(1) per message
- **Message Receiving:** O(1) per message
- **Overall:** O(n) where n = number of messages exchanged

### **Space Complexity:**
- **Server:** O(1) - Fixed number of streams and buffers
- **Client:** O(1) - Fixed number of streams and buffers
- **Message Buffer:** O(m) where m = message length

### **Concurrency:**
- **Threads per Process:** 2 (Main + Listener)
- **Total System Threads:** 4 (2 server + 2 client)

---

## 🎯 KEY ALGORITHMIC FEATURES

### **1. Asynchronous I/O**
```
CONCEPT: Non-blocking Communication
- Sending and receiving happen independently
- No waiting for response before sending next message
```

### **2. Thread Synchronization**
```
CONCEPT: Concurrent Execution
- Main thread handles OUTPUT (console → network)
- Listener thread handles INPUT (network → console)
- No shared mutable state = No race conditions
```

### **3. Stream-Based Communication**
```
CONCEPT: Buffered I/O
- BufferedReader for efficient line reading
- PrintWriter for automatic flushing
- Text-based protocol (newline-delimited)
```

### **4. Error Handling**
```
CONCEPT: Graceful Degradation
- IOException caught on disconnection
- Resources closed in try-with-resources
- User notified of connection status
```

---

## 🔢 PSEUDOCODE COMPARISON

### **Server Pseudocode:**
```
function startServer():
    socket = listen(12350)
    client = accept()
    
    spawn thread:
        while message = receive(client):
            print("Client:", message)
    
    while input = readConsole():
        send(client, "Server: " + input)
```

### **Client Pseudocode:**
```
function startClient():
    socket = connect("localhost", 12350)
    
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
| **Type** | Client-Server Architecture |
| **Communication** | Synchronous TCP/IP |
| **Concurrency Model** | Multi-threaded |
| **Message Protocol** | Text-based, line-delimited |
| **Connection Type** | Persistent, bidirectional |
| **Scalability** | 1-to-1 only (single client) |
| **Reliability** | TCP guarantees delivery |
| **Latency** | Low (localhost) |

---

## 🧮 MATHEMATICAL MODEL

### **Message Flow Equation:**
```
Let:
- S = Server
- C = Client
- M(x→y) = Message from x to y
- T = Time

Message Exchange:
M(S→C) at time T₁: S sends, C receives at T₁ + δ
M(C→S) at time T₂: C sends, S receives at T₂ + δ

Where δ = network latency (≈0 for localhost)
```

### **Thread State Diagram:**
```
Main Thread States:
RUNNING → BLOCKED(readLine) → RUNNING → BLOCKED(println) → RUNNING

Listener Thread States:
RUNNING → BLOCKED(readLine) → RUNNING → DISPLAY → RUNNING
```
