# 🌐 DNS Implementation via Java RMI

This project contains a simple Domain Name System (DNS) built using Java Remote Method Invocation (RMI).

## Features
- Resolves any valid domain name to its corresponding IPv4/IPv6 address.
- Configured to bind custom IP addresses (`java.rmi.server.hostname`) to accept external client connections seamlessly.
- Real-time resolution via `java.net.InetAddress`.

## Components
- `DNSInterface.java`: The remote interface defining the `lookup` method.
- `DNSServerImpl.java`: Remote object implementation that executes the real DNS lookup logic.
- `RMIDNSServer.java`: Registers the DNS service to the specified RMI registry.
- `RMIDNSClient.java`: The client application to interactively query the server.

## How to Run

1. **Compile all files:**
   ```bash
   javac *.java
   ```
2. **Start the Server:**
   ```bash
   java RMIDNSServer
   ```
3. **Start the Client (open a new terminal):**
   ```bash
   java RMIDNSClient localhost
   ```
   *(If running on different machines, replace `localhost` with the server's IP address).*
