# 🌐 Distributed Systems Lab (DS_LAB)
![Distributed Systems](https://img.shields.io/badge/Laboratory-Distributed%20Systems-blueviolet?style=for-the-badge&logo=git)
![Python](https://img.shields.io/badge/Language-Python%203.11+-blue?style=for-the-badge&logo=python)
![Status](https://img.shields.io/badge/Lab%20Status-10%2F10%20Completed-success?style=for-the-badge)

A comprehensive implementation of core Distributed Systems algorithms refactored for **Full-Duplex**, **Multi-Node**, and **Network-Ready** execution.

---

## 🚀 Lab Dashboard

| # | Experiment Name | Focus Area | Status |
|---|---|---|---|
| 03 | [Message Passing](./DS_LAB/message_passing) | Full-Duplex Messenger | ✅ Done |
| 04 | [Lamport Clock](./DS_LAB/lamport_clock) | Event Ordering | ✅ Done |
| 08 | [Berkeley Sync](./DS_LAB/berkeley_sync) | Clock Synchronization | ✅ Done |
| 10 | [Lamport Mutex](./DS_LAB/lamport_mutex) | Mutual Exclusion | ✅ Done |
| 11 | [Ricart-Agrawala](./DS_LAB/ricart_agrawala) | Op. Mutual Exclusion | ✅ Done |
| 13 | [Chat App](./DS_LAB/chat_app) | Async Networking | ✅ Done |
| 16 | [Deadlock Detection](./DS_LAB/deadlock_detection) | Wait-For Graphs | ✅ Done |
| 19 | [Distributed Sorting](./DS_LAB/distributed_sorting) | Data Parallelism | ✅ Done |
| 20 | [MapReduce](./DS_LAB/mapreduce) | Distributed Processing | ✅ Done |
| 21 | [Prime Calculation](./DS_LAB/prime_calculation) | Resource Splitting | ✅ Done |

---

## 🛠️ General Setup
Each experiment is designed to run across multiple physical machines or local terminals.

### **Prerequisites**
- **Networking**: Ensure all machines are on the same LAN/Wi-Fi.
- **Firewall**: Temporarily allow the chosen ports (default is usually `5000+` or `9000+`).
- **Python**: Version 3.8+ recommended.

### **Execution Model**
Most programs follow the **Master/Worker** or **P2P Node** model:
1. **Start the Master/Server** first to bind the port.
2. **Start the Workers/Nodes** and provide the Master's LAN IP address.
3. Every program will prompt you for a **Name** to make logs readable.

---

## 🏗️ Technical Architecture
```mermaid
graph TD
    A[Master/Server] -- Assignments --> B[Worker 1]
    A -- Assignments --> C[Worker 2]
    B -- Results --> A
    C -- Results --> A
    D[Node A] <-> E[Node B]
    E <-> F[Node C]
```

*Created by Antigravity AI*
