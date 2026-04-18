# 22.Develop a program for Parallel File Search across distributed nodes.
import socket
import pickle
import os
def run_worker():
    m_ip = input("Master IP: ").strip(); m_port = int(input("Master Port: ").strip())
    name, file_path = input("Worker Name: ").strip(), "search_data.txt"
    if not os.path.exists(file_path):
        with open(file_path, "w") as f: f.write("Distributed systems are powerful.\nParallel search is efficient.\nSocket programming is fun.")
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    try:
        s.connect((m_ip, m_port)); s.sendall(name.encode())
        request = s.recv(1024).decode(); receive = []
        with open(file_path, "r") as f:
            for line in f:
                if request.lower() in line.lower():
                    reply = line.strip(); receive.append(reply)
        print(f"Searched file. Found {len(receive)} matches for '{request}'.")
        s.sendall(pickle.dumps(receive))
    except Exception as e: print(f"Error: {e}")
    finally: s.close()
if __name__ == "__main__": run_worker()
