# 4. Write a program to simulate Lamport Logical Clock Algorithm.
import socket
import threading
class LamportNode:
    def __init__(self, port, name):
        self.port, self.name, self.clock, self.lock = port, name, 0, threading.Lock()
    def listen(self):
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM); s.bind(('0.0.0.0', self.port)); s.listen(5)
        while True:
            conn, _ = s.accept(); data = conn.recv(1024).decode().split(":")
            s_name, s_clock = data[0], int(data[1])
            with self.lock:
                self.clock = max(self.clock, s_clock) + 1
                print(f"\n[EVENT] '{s_name}' sent message. My New Clock: {self.clock}\nChoice (1-Event, 2-Send): ", end="")
            conn.close()
    def start(self):
        threading.Thread(target=self.listen, daemon=True).start()
        while True:
            choice = input("\n1: Internal Event, 2: Send Message\nChoice: ").strip()
            if choice == '1':
                with self.lock: self.clock += 1; print(f"Internal Event. Clock: {self.clock}")
            elif choice == '2':
                t_ip = input("Target IP: ").strip(); t_port = int(input("Target Port: ").strip())
                with self.lock: self.clock += 1; msg = f"{self.name}:{self.clock}"
                try:
                    sc = socket.socket(socket.AF_INET, socket.SOCK_STREAM); sc.connect((t_ip, t_port))
                    sc.sendall(msg.encode()); sc.close()
                except: print("Target unreachable.")
if __name__ == "__main__":
    p = LamportNode(int(input("Listen Port: ").strip()), input("Node Name: ").strip()); p.start()
