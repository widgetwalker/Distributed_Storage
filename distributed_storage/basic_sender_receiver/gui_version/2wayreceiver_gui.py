from gui_base import TerminalGUI
import socket
import threading

gui = TerminalGUI("Two-Way Receiver (CRUD Server)")

names = []
state = {'mode': None, 'old_name': None}

def handle_client(conn, addr):
    gui.print(f"Client connected: {addr}")
    while True:
        try:
            data = conn.recv(1024).decode()
            if not data:
                break
            
            parts = data.split(":", 1)
            cmd = parts[0]
            resp = ""
            
            if cmd == "CREATE":
                name = parts[1]
                names.append(name)
                resp = f"Name created: {name}"
                gui.print(f"Client created: {name}")
            elif cmd == "EDIT":
                old, new = parts[1].split(",")
                if old in names:
                    names[names.index(old)] = new
                    resp = f"Name edited: {old} -> {new}"
                    gui.print(f"Client edited: {old} -> {new}")
                else:
                    resp = f"Name {old} not found"
            elif cmd == "DELETE":
                name = parts[1]
                if name in names:
                    names.remove(name)
                    resp = f"Name deleted: {name}"
                    gui.print(f"Client deleted: {name}")
                else:
                    resp = f"Name {name} not found"
            elif cmd == "SHOW":
                resp = f"Current names: {names}"
                gui.print("Client requested show")
            elif cmd == "EXIT":
                resp = "Connection closed"
                conn.send(resp.encode())
                gui.print("Client disconnected")
                break
            
            conn.send(resp.encode())
        except:
            break
    conn.close()

def handle_input(value):
    if state['mode'] is None:
        if value == "1":
            state['mode'] = 'create'
            gui.print("Enter name:")
        elif value == "2":
            state['mode'] = 'edit_old'
            gui.print("Enter old name:")
        elif value == "3":
            state['mode'] = 'delete'
            gui.print("Enter name:")
        elif value == "4":
            gui.print(f"Current names: {names}")
            show_menu()
        elif value == "5":
            gui.close()
        else:
            gui.print("Invalid choice")
            show_menu()
    
    elif state['mode'] == 'create':
        names.append(value)
        gui.print(f"Name created: {value}")
        state['mode'] = None
        show_menu()
    
    elif state['mode'] == 'edit_old':
        if value in names:
            state['old_name'] = value
            state['mode'] = 'edit_new'
            gui.print("Enter new name:")
        else:
            gui.print(f"Name {value} not found")
            state['mode'] = None
            show_menu()
    
    elif state['mode'] == 'edit_new':
        names[names.index(state['old_name'])] = value
        gui.print(f"Name edited: {state['old_name']} -> {value}")
        state['mode'] = None
        show_menu()
    
    elif state['mode'] == 'delete':
        if value in names:
            names.remove(value)
            gui.print(f"Name deleted: {value}")
        else:
            gui.print(f"Name {value} not found")
        state['mode'] = None
        show_menu()

def show_menu():
    gui.print("\nServer Menu:")
    gui.print("1. Create")
    gui.print("2. Edit")
    gui.print("3. Delete")
    gui.print("4. Show")
    gui.print("5. Exit")
    gui.print("\nEnter choice:")

def start_server():
    gui.print("Two-Way Receiver - GUI Version")
    gui.print("=" * 40)
    gui.print("Starting server on port 12345...")
    
    try:
        server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server.bind(('localhost', 12345))
        server.listen(1)
        gui.print("Server listening...\n")
        
        conn, addr = server.accept()
        threading.Thread(target=handle_client, args=(conn, addr), daemon=True).start()
        
        show_menu()
    except Exception as e:
        gui.print(f"Error: {e}")

threading.Thread(target=start_server, daemon=True).start()
gui.set_callback(handle_input)
gui.run()
