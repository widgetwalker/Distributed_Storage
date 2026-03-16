# 📄 Collaborative Document Editing System

A real-time, multi-client document editing platform using **Java Sockets**. This system implements a simplified token-based/mutex mechanism to ensure document consistency during concurrent edits.

## ✨ Features
- **Real-Time Sync**: Changes made by one client are immediately broadcast to all others.
- **Access Control**: Request `WRITE` access to lock the document for editing.
- **Persistent Storage**: Edits are automatically saved to the server's filesystem.
- **Detailed Logging**: Every action is timestamped and logged in `edit_log.txt`.

## 🛠️ Architecture
The system follows a **Client-Server Architecture**:
1. **Server**: Manages the master document and synchronizes client updates.
2. **Handlers**: Individual threads for each connected client to manage concurrent requests.
3. **Clients**: Interactive CLI terminals for reading and editing.

## 🚀 How to Run

### Step 1: Start the Server
```bash
java CollaborativeEditingServer
```
- Enter Port (Default: `5001`)
- Provide path to a `.txt` file (or use default `shared_document.txt`)

### Step 2: Connect Clients
```bash
java CollaborativeEditingClient
```
- Connect to `localhost` or Server IP.

## ⌨️ Command Reference
| Command | Action |
| :--- | :--- |
| `READ` | View current document state. |
| `APPEND <text>` | Add a new line at the end. |
| `REPLACE <n> <t>` | Replace line `<n>` with text `<t>`. |
| `DELETE <n>` | Remove line `<n>`. |
| `MODE WRITE` | Acquire editing lock. |
| `EXIT` | Terminate connection. |

---
*Part of the Distributed Systems Repository*
