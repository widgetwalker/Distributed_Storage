# 📦 Distributed Storage & Resilience

This module explores the critical aspects of distributed databases: **Reliability, Fault Tolerance, and Election Algorithms.**

## 🌟 Featured Implementations

### 🗳️ [Coordinator Election (Bully Algorithm)](./coordinator_election)
A full Java implementation of the **Bully Algorithm**.
- **Self-Healing**: Automatically detects if a coordinator (Leader) fails.
- **Priority Based**: Nodes elect the highest-priority member as the new leader.
- **Global State**: Synchronizes state across nodes to ensure consistency.

### 🖼️ [Basic Sender-Receiver (Python GUI)](./basic_sender_receiver)
Visualizes packet distribution and file transfer in a distributed storage environment.
- **GUI Versions**: Tkinter-based interfaces for senders and receivers.
- **Packetized Transfer**: Demonstrates how files are chunked and distributed across nodes.

## 🛠️ Setup Instructions

### Coordinator Election (Java)
1. Navigate to `coordinator_election/src`.
2. Update `config.properties` with node IPs and priorities.
3. Run `Main.java`.

### Storage GUI (Python)
```bash
pip install tkinter # Usually pre-installed
python basic_sender_receiver/gui_version/sender_gui.py
```

---
*Distributed Systems Suite*
