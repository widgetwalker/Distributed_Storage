# 21.Implement a Distributed Prime Number Calculation (range split across nodes).
import socket
import pickle
def is_prime(n):
    if n < 2: return False
    for i in range(2, int(n**0.5)+1):
        if n % i == 0: return False
    return True
def run_worker():
    m_ip = input("Master IP: ").strip(); m_port = int(input("Master Port: ").strip())
    name = input("Worker Name: ").strip()
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    try:
        s.connect((m_ip, m_port)); s.sendall(name.encode())
        r_start, r_end = pickle.loads(s.recv(4096)); res = [x for x in range(r_start, r_end) if is_prime(x)]
        print(f"Found {len(res)} primes."); s.sendall(pickle.dumps(res))
    except Exception as e: print(f"Error: {e}")
    finally: s.close()
if __name__ == "__main__": run_worker()
