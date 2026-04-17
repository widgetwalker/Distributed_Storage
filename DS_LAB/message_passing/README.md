# 📩 Experiment 03: Message Passing
### **Aim**: Message Passing between two processes.
### **Result**: Successfully implemented and executed the Full-Duplex Message Passing program.
---
## How it Works
This program implements **Bi-directional communication** between two processes. Both nodes run a background listener thread, allowing either process to send a message at any time.
### Flowchart
```mermaid
sequenceDiagram
    participant Node A
    participant Node B
    Note over Node A, Node B: Threads Started (Listen & Input)
    Node A->>Node B: Sends Message "Hello"
    Node B-->>Node B: Thread intercepts RECV
    Node B->>Node B: Displays [FROM A]: Hello
    Note over Node B: Node B types response
    Node B->>Node A: Sends Message "Hi there"
    Node A-->>Node A: Displays [FROM B]: Hi there
```
## Setup
1. Run `messenger.py`.
2. Enter your name and listening port.
3. Enter the peer's IP and port. 
4. Type messages freely.
![Output Demo](./output_demo.png)
