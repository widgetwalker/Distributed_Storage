# 13.Develop a simple Distributed Chat Application using sockets.
import socket
import threading
def receive_msgs(s):
    while True:
        try:
            msg = s.recv(1024).decode()
            if not msg: break
            print(f"\n{msg}\nInput: ", end="")
        except: break
def run_client():
    ip = input("Server IP: ").strip(); port = int(input("Server Port: ").strip())
    name = input("Enter your Username: ").strip()
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    try:
        s.connect((ip, port)); s.sendall(name.encode())
        print(f"Connected to Chat as '{name}'")
        threading.Thread(target=receive_msgs, args=(s,), daemon=True).start()
        while True:
            msg = input("Input: ")
            if msg.lower() == 'exit': break
            s.sendall(msg.encode())
    except Exception as e: print(f"Error: {e}")
    finally: s.close()
if __name__ == "__main__": run_client()
