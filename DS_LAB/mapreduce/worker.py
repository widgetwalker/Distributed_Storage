# 20.Write a program to simulate MapReduce for Word Count.
import socket
import pickle
def run_worker():
    m_ip = input("Master IP: ").strip(); m_port = int(input("Master Port: ").strip())
    name = input("Worker Name: ").strip()
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    try:
        s.connect((m_ip, m_port)); s.sendall(name.encode())
        words = pickle.loads(s.recv(4096)); res = {}
        for w in words: res[w] = res.get(w, 0) + 1
        print(f"Mapped {len(words)} words."); s.sendall(pickle.dumps(res))
    except Exception as e: print(f"Error: {e}")
    finally: s.close()
if __name__ == "__main__": run_worker()
