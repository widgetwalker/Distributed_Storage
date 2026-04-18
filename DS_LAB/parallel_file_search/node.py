# 22.Develop a program for Parallel File Search & Sync (Collaborative style).
import socket
import threading
import os
import time
class Node:
    def __init__(self, id, name, peers):
        self.id, self.name, self.port, self.peers = id, name, 8000+id, peers
        self.files_dir = f"node_{name}_files"
        if not os.path.exists(self.files_dir): os.makedirs(self.files_dir)
    def receive_loop(self):
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM); s.bind(('0.0.0.0', self.port)); s.listen(5)
        while True:
            try:
                conn, _ = s.accept(); request = conn.recv(4096).decode().split('|')
                type, content = request[0], request[1:]
                if type == "SEARCH":
                    keyword, receive = content[0], []
                    for f_name in os.listdir(self.files_dir):
                        with open(os.path.join(self.files_dir, f_name), "r") as f:
                            for line in f:
                                if keyword.lower() in line.lower(): receive.append(f"{f_name}: {line.strip()}")
                    reply = ":".join(receive) if receive else "NONE"
                    conn.sendall(reply.encode())
                elif type == "SYNC":
                    f_name, f_content = content[0], content[1]
                    with open(os.path.join(self.files_dir, f_name), "w") as f: f.write(f_content)
                    print(f"\n[SYNC] File '{f_name}' updated by peer.\nChoice: ", end="")
                conn.close()
            except: pass
    def broadcast_edit(self, f_name, f_content):
        for p_port in self.peers:
            try:
                s = socket.socket(socket.AF_INET, socket.SOCK_STREAM); s.connect(('localhost', p_port))
                s.sendall(f"SYNC|{f_name}|{f_content}".encode()); s.close()
            except: pass
    def global_search(self, keyword):
        results = []
        for f_name in os.listdir(self.files_dir):
            with open(os.path.join(self.files_dir, f_name), "r") as f:
                for line in f:
                    if keyword.lower() in line.lower(): results.append(f"Local->{f_name}: {line.strip()}")
        for p_port in self.peers:
            try:
                s = socket.socket(socket.AF_INET, socket.SOCK_STREAM); s.connect(('localhost', p_port))
                s.sendall(f"SEARCH|{keyword}".encode()); reply = s.recv(4096).decode()
                if reply != "NONE":
                    for r in reply.split(":"): results.append(f"Peer({p_port})->{r}")
                s.close()
            except: pass
        return results
def run_node():
    id = int(input("Node ID (e.g. 0, 1): ").strip()); name = input("Node Name (A, B): ").strip()
    peers = [int(p) for p in input("Peer Ports (e.g. 8000 8001): ").strip().split()]
    node = Node(id, name, peers); threading.Thread(target=node.receive_loop, daemon=True).start()
    while True:
        choice = input("\n1: Search, 2: Edit/Sync File\nChoice: ").strip()
        if choice == '1':
            key = input("Keyword: ").strip(); res = node.global_search(key)
            for r in res: print(f"[RESULT] {r}")
        elif choice == '2':
            fn = input("Filename: ").strip(); ct = input("Content: ").strip()
            with open(os.path.join(node.files_dir, fn), "w") as f: f.write(ct)
            node.broadcast_edit(fn, ct); print("File updated and synced to all peers.")
if __name__ == "__main__": run_node()
