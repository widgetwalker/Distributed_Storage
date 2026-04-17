# 21.Implement a Distributed Prime Number Calculation (range split across nodes).
import socket
import pickle
def run_master():
    port = int(input("Master Port: ").strip()); count = int(input("Worker Count: ").strip())
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM); s.bind(('0.0.0.0', port)); s.listen(count)
    start = int(input("Start: ").strip()); end = int(input("End: ").strip())
    step, primes = (end-start)//count, []
    for i in range(count):
        conn, _ = s.accept(); name = conn.recv(1024).decode()
        p_start = start + i*step; p_end = (start + (i+1)*step) if i<count-1 else end
        print(f"[TASK] Assigning range {p_start}-{p_end} to Worker '{name}'")
        conn.sendall(pickle.dumps((p_start, p_end))); primes.extend(pickle.loads(conn.recv(4096))); conn.close()
    print(f"All Primes: {primes}"); s.close()
if __name__ == "__main__": run_master()
