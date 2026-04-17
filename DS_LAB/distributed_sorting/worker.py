# 19.Develop a program for Distributed Sorting (Merge Sort / Quick Sort) across nodes.
import socket
import pickle
def run_worker():
    m_ip = input("Master IP: ").strip(); m_port = int(input("Master Port: ").strip())
    name = input("Worker Name: ").strip()
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    try:
        s.connect((m_ip, m_port)); s.sendall(name.encode())
        data = pickle.loads(s.recv(4096)); print(f"Sorting chunk of size {len(data)}...")
        data.sort(); s.sendall(pickle.dumps(data))
    except Exception as e: print(f"Error: {e}")
    finally: s.close()
if __name__ == "__main__": run_worker()
