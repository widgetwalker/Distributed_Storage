# 13.Develop a simple Distributed Chat Application using sockets.
import socket
import threading
clients = {}
def handle(conn, addr):
    try:
        name = conn.recv(1024).decode(); clients[conn] = name
        print(f"[JOIN] User '{name}' ({addr}) connected.")
        while True:
            data = conn.recv(1024)
            if not data: break
            msg = data.decode(); print(f"[{name}] {msg}")
            for c, n in list(clients.items()):
                if c != conn:
                    try: c.sendall(f"{name}: {msg}".encode())
                    except: del clients[c]
    except: pass
    name = clients.pop(conn, "Unknown"); print(f"[LEAVE] User '{name}' disconnected."); conn.close()
def run_server():
    port = int(input("Listen Port: ").strip()); s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind(('0.0.0.0', port)); s.listen(5); print(f"Chat Server started on {port}...")
    while True:
        conn, addr = s.accept()
        threading.Thread(target=handle, args=(conn, addr), daemon=True).start()
if __name__ == "__main__": run_server()
