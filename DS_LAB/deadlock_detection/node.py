# 16.Develop a program for Distributed Deadlock Detection (wait-for graph).
import socket
def run_node():
    d_ip = input("Detector IP: ").strip(); d_port = int(input("Detector Port: ").strip())
    name = input("My Node Name: ").strip()
    while True:
        target = input("I am waiting for (Node Name): ").strip()
        try:
            s = socket.socket(socket.AF_INET, socket.SOCK_STREAM); s.connect((d_ip, d_port))
            s.sendall(f"{name},{target}".encode()); s.close()
        except Exception as e: print(f"Error connecting to detector: {e}")
        if input("Add another dependency? (y/n): ").strip().lower() == 'n': break
if __name__ == "__main__": run_node()
