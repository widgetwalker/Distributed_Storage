from gui_base import TerminalGUI
import socket

gui = TerminalGUI("Two-Way Sender (CRUD Client)")

state = {'connected': False, 'sock': None, 'mode': None, 'old_name': None}

def handle_input(value):
    if not state['connected']:
        gui.print("Connecting to server...")
        try:
            state['sock'] = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            state['sock'].connect(('localhost', 12345))
            state['connected'] = True
            gui.print("Connected!\n")
            show_menu()
        except Exception as e:
            gui.print(f"Error: {e}")
        return
    
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
            try:
                state['sock'].send("SHOW:".encode())
                resp = state['sock'].recv(1024).decode()
                gui.print(resp)
                show_menu()
            except Exception as e:
                gui.print(f"Error: {e}")
        elif value == "5":
            try:
                state['sock'].send("EXIT:".encode())
                resp = state['sock'].recv(1024).decode()
                gui.print(resp)
                state['sock'].close()
            except:
                pass
            gui.close()
        else:
            gui.print("Invalid choice")
            show_menu()
    
    elif state['mode'] == 'create':
        try:
            state['sock'].send(f"CREATE:{value}".encode())
            resp = state['sock'].recv(1024).decode()
            gui.print(resp)
            state['mode'] = None
            show_menu()
        except Exception as e:
            gui.print(f"Error: {e}")
    
    elif state['mode'] == 'edit_old':
        state['old_name'] = value
        state['mode'] = 'edit_new'
        gui.print("Enter new name:")
    
    elif state['mode'] == 'edit_new':
        try:
            state['sock'].send(f"EDIT:{state['old_name']},{value}".encode())
            resp = state['sock'].recv(1024).decode()
            gui.print(resp)
            state['mode'] = None
            show_menu()
        except Exception as e:
            gui.print(f"Error: {e}")
    
    elif state['mode'] == 'delete':
        try:
            state['sock'].send(f"DELETE:{value}".encode())
            resp = state['sock'].recv(1024).decode()
            gui.print(resp)
            state['mode'] = None
            show_menu()
        except Exception as e:
            gui.print(f"Error: {e}")

def show_menu():
    gui.print("\nClient Menu:")
    gui.print("1. Create")
    gui.print("2. Edit")
    gui.print("3. Delete")
    gui.print("4. Show")
    gui.print("5. Exit")
    gui.print("\nEnter choice:")

gui.print("Two-Way Sender - GUI Version")
gui.print("=" * 40)
gui.print("Press Enter to connect...")
gui.set_callback(handle_input)
gui.run()
