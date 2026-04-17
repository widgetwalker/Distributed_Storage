# 3. Develop a program to demonstrate Message Passing between two processes.
import socket
import threading
def receive_loop(port):
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM); s.bind(('0.0.0.0', port)); s.listen(5)
    while True:
        try:
            conn, _ = s.accept(); data = conn.recv(1024).decode().split(":", 1)
            print(f"\n[FROM {data[0]}] {data[1]}\nMessage: ", end=""); conn.close()
        except: pass
def run_messenger():
    l_port = int(input("My Listen Port: ").strip()); name = input("My Name: ").strip()
    threading.Thread(target=receive_loop, args=(l_port,), daemon=True).start()
    r_ip = input("Peer IP: ").strip(); r_port = int(input("Peer Port: ").strip())
    print("-" * 30); print(f"Full Duplex Mode Started. Type messages below.")
    while True:
        msg = input("Message: ")
        try:
            s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            s.settimeout(2); s.connect((r_ip, r_port)); s.sendall(f"{name}:{msg}".encode()); s.close()
        except: print("Peer is currently unreachable.")
if __name__ == "__main__": run_messenger()
