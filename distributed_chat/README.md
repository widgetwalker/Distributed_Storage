# 💬 Distributed Chat Network

An integrated suite of communication tools implementing various network topologies and messaging patterns in Java.

## 📂 Sub-Modules

### 1. [One-to-One Chat](./one_to_one)
Dedicated point-to-point communication between a single server and a single client. Focuses on low-latency stream processing.

### 2. [Group Chat](./group_chat)
A multi-client broadcast network. Features unique algorithms for message distribution and client handler management.

### 3. [Hybrid Chat](./hybrid_chat)
A versatile implementation that supports switching between 1-to-1 and group modes dynamically from the server terminal.

## 🔧 Technical Details
- **Protocol**: TCP (Transmission Control Protocol)
- **Concurrency**: `Thread-per-client` model using `Socket` and `ServerSocket`.
- **Formatting**: ANSI-colored console outputs for better readability.

## 🚀 Quick Launch
Navigate to a submodule directory to find specific launch instructions. Typical usage:
```bash
# Start Server
java OneToOneGroupChatServer

# Start Client
java OneToOneGroupChatClient
```

---
*Distributed Systems Suite*
