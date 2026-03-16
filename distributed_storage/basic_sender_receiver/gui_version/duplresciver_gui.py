from gui_base import TerminalGUI
import socket
import threading

gui = TerminalGUI("Duplex Receiver (Server)")

names = []
state = {'waiting_name': False}

def handle_client(conn, addr):
    gui.print(f"Client connected: {addr}")
    while True:
        try:
            data = conn.recv(1024).decode()
            if not data:
                break
            
            parts = data.split(":", 1)
            cmd = parts[0]
            
            if cmd == "CREATE":
                name = parts[1]
                names.append(name)
                resp = f"Name added: {name}\nCurrent names: {names}"
                gui.print(f"Client added: {name}")
                conn.send(resp.encode())
            elif cmd == "EXIT":
                conn.send("Connection closed".encode())
                gui.print("Client disconnected")
                break
        except:
            break
    conn.close()

def handle_input(value):
    if not state['waiting_name']:
        if value == "1":
            state['waiting_name'] = True
            gui.print("Enter name:")
        elif value == "2":
            gui.close()
        else:
            gui.print("Invalid choice\n")
            gui.print("Enter choice:")
    else:
        names.append(value)
        gui.print(f"Name added: {value}")
        gui.print(f"Current names: {names}\n")
        state['waiting_name'] = False
        gui.print("Server Menu:")
        gui.print("1. Enter name")
        gui.print("2. Exit")
        gui.print("\nEnter choice:")

def start_server():
    gui.print("Duplex Receiver - GUI Version")
    gui.print("=" * 40)
    gui.print("Starting server on port 12345...")
    
    try:
        server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server.bind(('localhost', 12345))
        server.listen(1)
        gui.print("Server listening...\n")
        
        conn, addr = server.accept()
        threading.Thread(target=handle_client, args=(conn, addr), daemon=True).start()
        
        gui.print("Server Menu:")
        gui.print("1. Enter name")
        gui.print("2. Exit")
        gui.print("\nEnter choice:")
    except Exception as e:
        gui.print(f"Error: {e}")

threading.Thread(target=start_server, daemon=True).start()
gui.set_callback(handle_input)
gui.run()
