# 🌌 Distributed Systems Lab & Core Implementations

Welcome to the **Ultimate Distributed Systems Repository**. This project is a curated collection of core distributed computing concepts, ranging from network-level communication to high-level system coordination.

![Logo](https://img.shields.io/badge/Distributed_Systems-Workspace-blue?style=for-the-badge&logo=apache-spark&logoColor=white) 
![Status](https://img.shields.io/badge/Maintenance-Active-green?style=for-the-badge)
![Language](https://img.shields.io/badge/Language-Java_%7C_Python-orange?style=for-the-badge&logo=java)

---

## 🏗️ Repository Architecture

This repository is organized into distinct categories, each representing a major pillar of distributed computing:

| Category | Description | Key Technologies |
| :--- | :--- | :--- |
| [📂 **Collaborative Editing**](./collaborative_editing) | Mutex-controlled real-time document editing system. | Java Sockets, Multi-threading |
| [📂 **Distributed Chat**](./distributed_chat) | Hybrid communication platform for 1-to-1 and group chats. | TCP Sockets, Socket Handlers |
| [📂 **Distributed Storage**](./distributed_storage) | Fault-tolerant storage with Coordinator Election. | Bully Algorithm, Python GUI |
| [📂 **RMI Systems**](./rmi_systems) | High-level procedure calls for arithmetic and authentication. | Java RMI, Registry |
| [📂 **Utility Scripts**](./scripts) | Automation tools for compilation and deployment. | Batch, PowerShell |

---

## 🚀 Quick Navigation

> [!TIP]
> Each folder contains its own specialized README with deep-dive technical documentation and usage instructions.

### 👥 Collaborative Editing
Build to demonstrate real-time synchronization and concurrency control.
- [Documentation](./collaborative_editing/README.md)
- [Server Implementation](./collaborative_editing/CollaborativeEditingServer.java)

### 💬 Distributed Chat 
Sophisticated message routing across distributed nodes.
- [Group Chat](./distributed_chat/group_chat)
- [One-to-One](./distributed_chat/one_to_one)
- [Hybrid Mode](./distributed_chat/hybrid_chat)

### 📦 Distributed Storage
Focuses on the **Bully Algorithm** for leader election and storage resilience.
- [Coordinator Election](./distributed_storage/coordinator_election)
- [Storage GUI](./distributed_storage/basic_sender_receiver)

### 🔌 RMI Systems
Implementing remote procedure calls for complex computations.
- [Arithmetic Engine](./rmi_systems/arithmetic_core)
- [Auth System](./rmi_systems/login)

---

## 🛠️ Global Setup

To compile all Java projects in the main root (Legacy Batch):
```bash
./scripts/compile.bat
```

To start individual modules, refer to the category-specific documentation.

---

## ✨ Features Breakdown

- ⚡ **Real-time Synchronization** in collaborative tools.
- 🗳️ **Leader Election Algorithms** (Bully Algorithm) for cluster management.
- 🔐 **Authentication Layers** using RMI.
- 💬 **Multi-threaded Handlers** for high-concurrency chat systems.

---

<div align="center">
  <img src="https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/rainbow.png" width="100%">
  <p><i>Crafted for Distributed Systems Excellence</i></p>
</div>
