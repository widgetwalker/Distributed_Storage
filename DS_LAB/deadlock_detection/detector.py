# 16.Develop a program for Distributed Deadlock Detection (wait-for graph).
import socket
def run_detector():
    port = int(input("Listen Port: ").strip()); s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind(('0.0.0.0', port)); s.listen(5); graph = {}; print(f"Deadlock Detector started on {port}...")
    def has_cycle(u, visited, path):
        visited.add(u); path.add(u)
        for v in graph.get(u, []):
            if v not in visited:
                if has_cycle(v, visited, path): return True
            elif v in path: return True
        path.remove(u); return False
    while True:
        try:
            conn, _ = s.accept(); u, v = conn.recv(1024).decode().split(",")
            graph.setdefault(u, []).append(v); print(f"[UPDATE] Edge Added: {u} -> {v}")
            visited, path, deadlock = set(), set(), False
            for node in list(graph.keys()):
                if node not in visited and has_cycle(node, visited, path): deadlock = True; break
            if deadlock: print(">>> ALERT: DEADLOCK DETECTED IN THE SYSTEM <<<")
            conn.close()
        except: pass
if __name__ == "__main__": run_detector()
