# 🔌 RMI Systems (Remote Method Invocation)

Implementation of high-level remote procedures, moving away from low-level socket management into object-oriented distributed objects.

## 🚀 Projects

### 🧮 [Arithmetic Engine](./arithmetic_core)
Efficient remote computation engine for basic and scientific calculations.
- **Client-Server Bindings**: Uses `Naming.rebind` and `LocateRegistry`.

### 🏦 [Calculator](./calculator)
An advanced RMI calculator for financial and simple interest calculations.

### 🔐 [Login & Authentication](./login)
A distributed authentication module where the server manages credentials and authenticates remote clients.

### 🌐 [DNS Resolver](./dns)
A Domain Name System (DNS) application using Java RMI that efficiently resolves domain names to IP addresses across distributed networks.

## 🏃 Running RMI

1. **Start RMI Registry**:
   ```bash
   rmiregistry &
   ```
2. **Start Server**:
   ```bash
   java Server
   ```
3. **Run Client**:
   ```bash
   java Client
   ```

---
*Distributed Systems Suite*
