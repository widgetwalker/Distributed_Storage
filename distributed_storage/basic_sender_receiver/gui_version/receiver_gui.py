from gui_base import TerminalGUI
import socket
import threading

gui = TerminalGUI("Simple Receiver")

def start_server():
    gui.print("Simple Receiver - GUI Version")
    gui.print("=" * 40)
    gui.print("Starting server on port 12345...")
    
    try:
        server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server.bind(('localhost', 12345))
        server.listen(1)
        gui.print("Server listening...")
        
        conn, addr = server.accept()
        gui.print(f"Connected by {addr}")
        
        while True:
            data = conn.recv(1024).decode()
            if not data:
                break
            gui.print(f"Name received: {data}")
        
        conn.close()
        server.close()
        gui.print("\nConnection closed.")
    except Exception as e:
        gui.print(f"Error: {e}")

threading.Thread(target=start_server, daemon=True).start()
gui.run()
