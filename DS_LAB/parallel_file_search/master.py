# 22.Develop a program for Parallel File Search across distributed nodes.
import socket
import pickle
def run_master():
    port = int(input("Master Port: ").strip()); count = int(input("Worker Count: ").strip())
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM); s.bind(('0.0.0.0', port)); s.listen(count)
    request = input("Search Keyword (request): ").strip()
    print(f"Broadcasting search request: '{request}' to {count} workers...")
    for i in range(count):
        conn, _ = s.accept(); name = conn.recv(1024).decode()
        print(f"[SEARCH] Sending request to Worker '{name}'"); conn.sendall(request.encode())
        receive = pickle.loads(conn.recv(4096))
        if receive:
            for reply in receive: print(f"[RESULT] From {name}: {reply}")
        else: print(f"[RESULT] From {name}: No matches found.")
        conn.close()
    print("Search complete."); s.close()
if __name__ == "__main__": run_master()
