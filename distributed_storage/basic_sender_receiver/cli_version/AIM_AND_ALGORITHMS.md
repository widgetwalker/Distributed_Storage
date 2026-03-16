# Aim and Algorithms for Distributed Systems Programs

This document provides the aim and detailed algorithms for all programs in the originals folder.

---

## 1. Simple Name Transfer (sender.py & receiver.py)

### **Aim**
To demonstrate basic client-server communication using TCP sockets, where a client sends multiple names to a server for storage and display.

### **Algorithm**

#### **Sender (Client)**
```
START
1. Import socket module
2. Create TCP socket using AF_INET and SOCK_STREAM
3. Connect to server at localhost:12345
4. Prompt user: "Enter the number of names"
5. Read count 'a' from user
6. FOR i = 0 to a-1:
   a. Prompt user: "Enter the name"
   b. Read name from user
   c. Encode name to bytes
   d. Send encoded name to server
7. Close client socket
END
```

#### **Receiver (Server)**
```
START
1. Import socket module
2. Create TCP socket using AF_INET and SOCK_STREAM
3. Bind socket to localhost:12345
4. Listen for incoming connections (max 1)
5. Display "Server is on port 12345..."
6. Accept connection from client
7. Store connection object and address
8. Display "Connected by {address}"
9. WHILE True:
   a. Receive data (1024 bytes) from client
   b. Decode data from bytes to string
   c. IF data is empty:
      - BREAK loop
   d. Display "Name received: {data}"
10. Close connection
11. Close server socket
END
```

**Time Complexity**: O(n) where n is the number of names
**Space Complexity**: O(1) - no persistent storage

---

## 2. Duplex Name Management (duplsender.py & duplresciver.py)

### **Aim**
To implement a bidirectional client-server system where both client and server can add names to a shared list, demonstrating concurrent operations using threading.

### **Algorithm**

#### **Duplex Sender (Client)**
```
START
1. Import socket module
2. Create TCP socket
3. Connect to server at localhost:12345
4. WHILE True:
   a. Display menu:
      - 1. Enter name
      - 2. Exit
   b. Read user choice
   c. IF choice == "1":
      - Prompt "Enter name"
      - Read name from user
      - Create message "CREATE:{name}"
      - Encode and send message to server
   d. ELSE IF choice == "2":
      - Send "EXIT:" message to server
      - BREAK loop
   e. ELSE:
      - Display "Invalid choice"
      - CONTINUE to next iteration
   f. Receive response (1024 bytes) from server
   g. Decode and display response
5. Close client socket
END
```

#### **Duplex Receiver (Server)**
```
GLOBAL: names = [] (empty list)

FUNCTION handle_client(conn, addr):
  WHILE True:
    TRY:
      1. Receive data (1024 bytes) from connection
      2. Decode data to string
      3. IF data is empty: BREAK
      4. Split data by ":" into [command, payload]
      5. Initialize response = ""
      6. IF command == "CREATE":
         a. Extract name from payload
         b. Append name to global names list
         c. Set response = "Name added: {name}\nCurrent names: {names}"
      7. ELSE IF command == "EXIT":
         a. Set response = "Connection closed"
         b. Send encoded response
         c. BREAK loop
      8. Send encoded response
    CATCH any exception:
      BREAK
  Close connection

FUNCTION server_menu():
  WHILE True:
    1. Display menu:
       - 1. Enter name
       - 2. Exit
    2. Read user choice
    3. IF choice == "1":
       a. Prompt "Enter name"
       b. Read name from user
       c. Append name to global names list
       d. Display "Name added: {name}\nCurrent names: {names}"
    4. ELSE IF choice == "2":
       BREAK loop

FUNCTION main():
  1. Create TCP socket
  2. Bind to localhost:12345
  3. Listen for connections (max 1)
  4. Accept connection, store conn and addr
  5. Create daemon thread running handle_client(conn, addr)
  6. Start thread
  7. Call server_menu() (blocks until exit)
  8. Close server socket

IF __name__ == "__main__":
  Call main()
END
```

**Time Complexity**: O(1) for each operation
**Space Complexity**: O(n) where n is the number of names stored
**Concurrency**: Uses threading for simultaneous client and server operations

---

## 3. Two-Way CRUD Operations (2waysender.py & 2wayreceiver.py)

### **Aim**
To implement a complete CRUD (Create, Read, Update, Delete) system for name management with bidirectional communication, where both client and server can independently perform all operations on a shared data structure.

### **Algorithm**

#### **2-Way Sender (Client)**
```
START
1. Import socket module
2. Create TCP socket
3. Connect to server at localhost:12345
4. WHILE True:
   a. Display menu:
      - 1. Create
      - 2. Edit
      - 3. Delete
      - 4. Show
      - 5. Exit
   b. Read user choice
   c. SWITCH choice:
      CASE "1": (Create)
        - Prompt "Enter name"
        - Read name
        - Send "CREATE:{name}"
      CASE "2": (Edit)
        - Prompt "Enter old name"
        - Read old_name
        - Prompt "Enter new name"
        - Read new_name
        - Send "EDIT:{old_name},{new_name}"
      CASE "3": (Delete)
        - Prompt "Enter name"
        - Read name
        - Send "DELETE:{name}"
      CASE "4": (Show)
        - Send "SHOW:"
      CASE "5": (Exit)
        - Send "EXIT:"
        - BREAK loop
      DEFAULT:
        - Display "Invalid choice"
        - CONTINUE to next iteration
   d. Receive response (1024 bytes)
   e. Decode and display response
5. Close client socket
END
```

#### **2-Way Receiver (Server)**
```
GLOBAL: names = [] (empty list)

FUNCTION handle_client(conn, addr):
  WHILE True:
    TRY:
      1. Receive data (1024 bytes) from connection
      2. Decode data to string
      3. IF data is empty: BREAK
      4. Split data by ":" (max 1 split) into [command, payload]
      5. Initialize response = ""
      6. SWITCH command:
         CASE "CREATE":
           a. Extract name from payload
           b. Append name to global names list
           c. response = "Name created: {name}"
         
         CASE "EDIT":
           a. Split payload by "," into [old, new]
           b. IF old exists in names:
              - Find index of old name
              - Replace names[index] with new
              - response = "Name edited: {old} -> {new}"
           c. ELSE:
              - response = "Name {old} not found"
         
         CASE "DELETE":
           a. Extract name from payload
           b. IF name exists in names:
              - Remove name from list
              - response = "Name deleted: {name}"
           c. ELSE:
              - response = "Name {name} not found"
         
         CASE "SHOW":
           a. response = "Current names: {names}"
         
         CASE "EXIT":
           a. response = "Connection closed"
           b. Send encoded response
           c. BREAK loop
      
      7. Send encoded response
    CATCH any exception:
      BREAK
  Close connection

FUNCTION server_menu():
  WHILE True:
    1. Display menu:
       - 1. Create
       - 2. Edit
       - 3. Delete
       - 4. Show
       - 5. Exit
    2. Read user choice
    3. SWITCH choice:
       CASE "1": (Create)
         a. Prompt "Enter name"
         b. Read name
         c. Append name to global names list
         d. Display "Name created: {name}"
       
       CASE "2": (Edit)
         a. Prompt "Enter old name"
         b. Read old_name
         c. Prompt "Enter new name"
         d. Read new_name
         e. IF old_name exists in names:
            - Find index of old_name
            - Replace names[index] with new_name
            - Display "Name edited: {old_name} -> {new_name}"
         f. ELSE:
            - Display "Name {old_name} not found"
       
       CASE "3": (Delete)
         a. Prompt "Enter name"
         b. Read name
         c. IF name exists in names:
            - Remove name from list
            - Display "Name deleted: {name}"
         d. ELSE:
            - Display "Name {name} not found"
       
       CASE "4": (Show)
         a. Display "Current names: {names}"
       
       CASE "5": (Exit)
         BREAK loop

FUNCTION main():
  1. Create TCP socket
  2. Bind to localhost:12345
  3. Listen for connections (max 1)
  4. Accept connection, store conn and addr
  5. Create daemon thread running handle_client(conn, addr)
  6. Start thread
  7. Call server_menu() (blocks until exit)
  8. Close server socket

IF __name__ == "__main__":
  Call main()
END
```

**Time Complexity**: 
- Create: O(1)
- Edit: O(n) - linear search for name
- Delete: O(n) - linear search and removal
- Show: O(n) - display all names

**Space Complexity**: O(n) where n is the number of names
**Concurrency**: Uses threading for simultaneous client and server operations

---

## 4. Standalone Name Collection (takeandsend.py)

### **Aim**
To demonstrate basic input/output operations by collecting multiple names from the user and displaying them, without any network communication.

### **Algorithm**
```
START
1. Prompt user: "enter the number of names"
2. Read count 'a' from user and convert to integer
3. Initialize empty list 'names'
4. FOR i = 0 to a-1:
   a. Prompt user: "enter the name"
   b. Read name from user
   c. Append name to 'names' list
5. Display "\nYou entered:"
6. FOR i = 0 to a-1:
   a. Display names[i]
END
```

**Time Complexity**: O(n) where n is the number of names
**Space Complexity**: O(n) for storing names in list

---

## Summary Table

| Program | Type | Communication | Operations | Threading |
|---------|------|---------------|------------|-----------|
| sender.py / receiver.py | Client-Server | One-way | Send only | No |
| duplsender.py / duplresciver.py | Client-Server | Bidirectional | Create | Yes |
| 2waysender.py / 2wayreceiver.py | Client-Server | Bidirectional | CRUD | Yes |
| takeandsend.py | Standalone | None | Input/Output | No |

---

## Key Concepts Demonstrated

1. **TCP Socket Programming**: All networked programs use TCP sockets for reliable communication
2. **Client-Server Architecture**: Clear separation between client and server roles
3. **Protocol Design**: Custom text-based protocols (e.g., "CREATE:name", "EDIT:old,new")
4. **Concurrency**: Threading enables simultaneous client and server operations
5. **Data Persistence**: Shared global data structure (list) for name storage
6. **Error Handling**: Try-catch blocks for robust network communication
7. **Menu-Driven Interface**: User-friendly console interfaces for all operations

---

## Network Protocol Format

All networked programs use a simple text-based protocol:
```
COMMAND:PAYLOAD
```

**Commands**:
- `CREATE:name` - Add a new name
- `EDIT:old,new` - Update existing name
- `DELETE:name` - Remove a name
- `SHOW:` - Display all names
- `EXIT:` - Close connection

**Response Format**: Plain text messages indicating success/failure
