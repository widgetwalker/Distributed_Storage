# 8. Implement Berkeley Clock Synchronization Algorithm.
import socket
import time
def run_master():
    port = int(input("Master Port: ").strip()); count = int(input("Slave Count: ").strip())
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM); s.bind(('0.0.0.0', port)); s.listen(count)
    slaves = []; names = {}; print(f"Waiting for {count} slaves...")
    while len(slaves) < count:
        conn, addr = s.accept(); name = conn.recv(1024).decode()
        slaves.append(conn); names[conn] = name; print(f"[JOIN] Slave '{name}' connected from {addr}")
    times = [time.time()]
    for c in slaves:
        c.sendall(b"GET"); times.append(float(c.recv(1024).decode()))
    avg = sum(times) / len(times); print(f"Average System Time: {avg}")
    for i, c in enumerate(slaves):
        c.sendall(str(avg - times[i+1]).encode()); c.close()
    print("Sync finished."); s.close()
if __name__ == "__main__": run_master()
