# 11.Implement Ricart–Agrawala Algorithm for distributed mutual exclusion.
import socket
import threading
import time
class RANode:
    def __init__(self, id, name, peers):
        self.id, self.name, self.port, self.peers = id, name, 9000+id, peers
        self.clock, self.req_time, self.is_req, self.replies, self.deferred = 0, 0, False, 0, []
    def listen(self):
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM); s.bind(('0.0.0.0', self.port)); s.listen(5)
        while True:
            try:
                conn, _ = s.accept(); msg = conn.recv(1024).decode().split(':')
                t, s_id, s_name, s_clock = msg[0], int(msg[1]), msg[2], int(msg[3])
                self.clock = max(self.clock, s_clock) + 1
                if t == "REQ":
                    prio = self.is_req and (self.req_time < s_clock or (self.req_time == s_clock and self.id < s_id))
                    if prio: self.deferred.append((s_id, s_name))
                    else:
                        sc = socket.socket(socket.AF_INET, socket.SOCK_STREAM); sc.connect(('localhost', 9000+s_id))
                        sc.sendall(f"REP:{self.id}:{self.name}:{self.clock}".encode()); sc.close()
                elif t == "REP": self.replies += 1
                conn.close()
            except: pass
    def request_cs(self):
        self.is_req, self.req_time, self.replies = True, self.clock + 1, 0
        for p in self.peers:
            try:
                sc = socket.socket(socket.AF_INET, socket.SOCK_STREAM); sc.connect(('localhost', 9000+p))
                sc.sendall(f"REQ:{self.id}:{self.name}:{self.req_time}".encode()); sc.close()
            except: pass
        while self.replies < len(self.peers): time.sleep(0.1)
        print(">>> IN CRITICAL SECTION <<<"); time.sleep(2)
        print(">>> OUT OF CRITICAL SECTION <<<"); self.is_req = False
        for s_id, _ in self.deferred:
            try:
                sc = socket.socket(socket.AF_INET, socket.SOCK_STREAM); sc.connect(('localhost', 9000+s_id))
                sc.sendall(f"REP:{self.id}:{self.name}:{self.clock}".encode()); sc.close()
            except: pass
        self.deferred = []
if __name__ == "__main__":
    i = int(input("My ID: ").strip()); n = input("Node Name: ").strip()
    peers = [int(x) for x in input("Peer IDs: ").strip().split()]
    node = RANode(i, n, peers); threading.Thread(target=node.listen, daemon=True).start()
    while True:
        if input("\nType 'req' for CS: ").strip().lower() == 'req': node.request_cs()
