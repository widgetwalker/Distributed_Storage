# 10.Develop a program to demonstrate Mutual Exclusion using Lamport’s Algorithm.
import socket
import threading
import time
class Node:
    def __init__(self, id, name, peers):
        self.id, self.name, self.port, self.peers = id, name, 9000+id, peers
        self.clock, self.queue, self.replies = 0, [], 0
    def listen(self):
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM); s.bind(('0.0.0.0', self.port)); s.listen(5)
        while True:
            try:
                conn, _ = s.accept(); msg = conn.recv(1024).decode().split(':')
                t, s_id, s_name, s_clock = msg[0], int(msg[1]), msg[2], int(msg[3])
                self.clock = max(self.clock, s_clock) + 1
                if t == "REQ":
                    print(f"[RECV] REQ from '{s_name}'"); self.queue.append((s_clock, s_id, s_name)); self.queue.sort()
                    sc = socket.socket(socket.AF_INET, socket.SOCK_STREAM); sc.connect(('localhost', 9000+s_id))
                    sc.sendall(f"REP:{self.id}:{self.name}:{self.clock}".encode()); sc.close()
                elif t == "REP": self.replies += 1
                elif t == "REL": print(f"[RECV] REL from '{s_name}'"); self.queue = [x for x in self.queue if x[1] != s_id]
                conn.close()
            except: pass
    def request_cs(self):
        self.clock += 1; self.replies = 0; self.queue.append((self.clock, self.id, self.name)); self.queue.sort()
        for p in self.peers:
            try:
                sc = socket.socket(socket.AF_INET, socket.SOCK_STREAM); sc.connect(('localhost', 9000+p))
                sc.sendall(f"REQ:{self.id}:{self.name}:{self.clock}".encode()); sc.close()
            except: pass
        while self.replies < len(self.peers) or self.queue[0][1] != self.id: time.sleep(0.1)
        print(">>> IN CRITICAL SECTION <<<"); time.sleep(2)
        self.queue.pop(0); self.clock += 1
        for p in self.peers:
            try:
                sc = socket.socket(socket.AF_INET, socket.SOCK_STREAM); sc.connect(('localhost', 9000+p))
                sc.sendall(f"REL:{self.id}:{self.name}:{self.clock}".encode()); sc.close()
            except: pass
        print(">>> OUT OF CRITICAL SECTION <<<")
if __name__ == "__main__":
    i = int(input("My ID: ").strip()); n = input("Node Name: ").strip()
    peers = [int(x) for x in input("Peer IDs: ").strip().split()]
    node = Node(i, n, peers); threading.Thread(target=node.listen, daemon=True).start()
    while True:
        if input("\nType 'req' for CS: ").strip().lower() == 'req': node.request_cs()
