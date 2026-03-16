# Step By Step RMI Authentication System

**Group 6:**
Dheeraj P
Manha A K
Trinayani D

---

## RMI Login Module (Login.java, LoginImpl.java, LoginServer.java, LoginClient.java)

### Aim
To implement a secure client-server authentication system using Java RMI (Remote Method Invocation), where a client can register a username and password on a remote server, and subsequently authenticate using those credentials. The server persists user data to a file so credentials are retained across server restarts.

---

### Algorithm

**START**

#### 1. Remote Interface (Login.java)
   a. Import `java.rmi.Remote` and `java.rmi.RemoteException`
   b. Declare interface `Login` extending `Remote`
   c. Define two remote methods:
      - `register(username, password)` → returns `String` response
      - `authenticate(username, password)` → returns `boolean`

---

#### 2. Server Implementation (LoginImpl.java)
   a. Import `UnicastRemoteObject`, `RemoteServer`, file I/O libraries
   b. Declare class `LoginImpl` extending `UnicastRemoteObject` and implementing `Login`
   c. Initialise `HashMap<String, String> userDatabase`
   d. On startup, call `loadDatabase()`:
      - Read `users.txt` line by line
      - For each line, split by `,` to get `[username, password]`
      - Put into `userDatabase`
   e. **register(username, password)**:
      - Print client IP and action to server console
      - IF username or password is empty → RETURN error message
      - IF username already exists in map → RETURN "already taken" message
      - ELSE → put into map, append to `users.txt`, RETURN success message
   f. **authenticate(username, password)**:
      - Print client IP and action to server console
      - IF username or password is null → RETURN false
      - Lookup stored password in map
      - IF match → print SUCCESS on server, RETURN true
      - ELSE → print FAILED on server, RETURN false

---

#### 3. Server Startup (LoginServer.java)
   a. Create instance of `LoginImpl`
   b. Start RMI registry on port `1099`
   c. Bind `LoginImpl` to name `"rmi://localhost/LoginService"`
   d. Print server IP address on console

---

#### 4. Client Program (LoginClient.java)
   a. Prompt user for Server IP (default: `localhost`)
   b. Look up `LoginService` from RMI registry
   c. WHILE connected:
      - Display menu:
        1. Register
        2. Login
        3. Exit
      - Read user choice
      - IF choice == 1 (Register):
        - Prompt for new username and password
        - Call remote `register(username, password)`
        - Display server response
      - IF choice == 2 (Login):
        - Prompt for username and password
        - Call remote `authenticate(username, password)`
        - IF true → Display "Authentication Successful! Welcome, {username}."
        - ELSE → Display "Authentication Failed. Invalid username or password."
      - IF choice == 3 → BREAK
   d. Close scanner
   e. Exit

**END**

---

### Flowchart

```
[START]
   |
   v
[Server Starts → Loads users.txt → Binds LoginService to RMI Registry]
   |
   v
[Client Connects: rmi://host/LoginService]
   |
   v
   +---------- [Display Menu: 1.Register  2.Login  3.Exit]
   |                            |              |          |
   v                            v              v          v
[choice=1]             [Get username,    [Get username,  [BREAK]
                        password]         password]
   |                            |              |
   v                            v              v
[Call register()]    [Call authenticate()]
   |                            |
   v                            v
[Server: Check if   [Server: Check if
 username exists]    password matches]
   |                            |
   +---[EXISTS]---+     +---[MATCH]---+
   |              |     |             |
   v              v     v             v
[Save to     [Return  [Return      [Return
 file +       error]   true +       false +
 Return               Log SUCCESS]  Log FAILED]
 success]
   |                            |
   v                            v
[Client prints server response]
   |
   v
[Back to Menu]
   |
[EXIT] → [STOP]
```

---

### Files Created

| File | Role |
|---|---|
| `Login.java` | Remote interface with `register()` and `authenticate()` method signatures |
| `LoginImpl.java` | Server-side implementation; handles user data and file persistence |
| `LoginServer.java` | Main server; starts RMI registry and binds the service |
| `LoginClient.java` | Client; connects via RMI and provides interactive menu to user |
| `users.txt` | Auto-generated flat file database; stores `username,password` per line |

---

### Commands to Run

**Step 1 – Compile all files (from `rmi/` directory):**
```bash
javac login/*.java
```

**Step 2 – Start the Server:**
```bash
java login.LoginServer
```

**Step 3 – Start the Client (in a new terminal):**
```bash
java login.LoginClient
```

---

### Output

**Server Terminal:**
```
LoginService is running on 10.10.129.109
>> Loaded 1 users from database file.

--------------------------------------------------
>> Client [10.10.129.109] connected
>> Action Selected : REGISTER
>> Username Details: dheeraj
>> Password Details: qwerty
>> Result          : SUCCESS - User 'dheeraj' successfully registered

--------------------------------------------------
>> Client [10.10.129.109] connected
>> Action Selected : LOGIN
>> Username Details: dheeraj
>> Password Details: qwerty
>> Result          : SUCCESS - User 'dheeraj' authenticated

--------------------------------------------------
>> Client [10.10.129.109] connected
>> Action Selected : LOGIN
>> Username Details: dheeraj
>> Password Details: qwertt
>> Result          : FAILED - Invalid credentials for 'dheeraj'
```

**Client Terminal:**
```
Enter Server IP Address (leave empty for localhost): 10.10.129.109
Connected to LoginService.

Options:
1. Register
2. Login
3. Exit
Choose an option: 1
Enter new username: dheeraj
Enter new password: qwerty
Server Response: Registration successful for user 'dheeraj'.

Options:
1. Register
2. Login
3. Exit
Choose an option: 2
Enter username: dheeraj
Enter password: qwerty
Server Response: Authentication successful! Welcome, dheeraj.

Options:
1. Register
2. Login
3. Exit
Choose an option: 2
Enter username: dheeraj
Enter password: qwertt
Server Response: Authentication failed. Invalid username or password.

Options:
1. Register
2. Login
3. Exit
Choose an option: 3
Exiting.
```
