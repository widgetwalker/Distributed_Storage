# 8. Implement Berkeley Clock Synchronization Algorithm.
import socket
import time
def run_slave():
    m_ip = input("Master IP: ").strip(); m_port = int(input("Master Port: ").strip())
    name = input("Slave Name: ").strip(); drift = float(input("Drift (secs): ").strip())
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    try:
        s.connect((m_ip, m_port)); s.sendall(name.encode())
        while True:
            data = s.recv(1024).decode()
            if data == "GET": s.sendall(str(time.time() + drift).encode())
            else: print(f"Adjustment from Master: {data}"); break
    except Exception as e: print(f"Error: {e}")
    finally: s.close()
if __name__ == "__main__": run_slave()
