from gui_base import TerminalGUI
import socket

gui = TerminalGUI("Duplex Sender (Client)")

state = {'connected': False, 'sock': None, 'waiting_name': False}

def handle_input(value):
    if not state['connected']:
        gui.print("Connecting to server...")
        try:
            state['sock'] = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            state['sock'].connect(('localhost', 12345))
            state['connected'] = True
            gui.print("Connected!\n")
            gui.print("Client Menu:")
            gui.print("1. Enter name")
            gui.print("2. Exit")
            gui.print("\nEnter choice:")
        except Exception as e:
            gui.print(f"Error: {e}")
        return
    
    if not state['waiting_name']:
        if value == "1":
            state['waiting_name'] = True
            gui.print("Enter name:")
        elif value == "2":
            try:
                state['sock'].send("EXIT:".encode())
                resp = state['sock'].recv(1024).decode()
                gui.print(resp)
                state['sock'].close()
            except:
                pass
            gui.close()
        else:
            gui.print("Invalid choice\n")
            gui.print("Enter choice:")
    else:
        # Send name
        try:
            state['sock'].send(f"CREATE:{value}".encode())
            resp = state['sock'].recv(1024).decode()
            gui.print(resp)
            state['waiting_name'] = False
            gui.print("\nClient Menu:")
            gui.print("1. Enter name")
            gui.print("2. Exit")
            gui.print("\nEnter choice:")
        except Exception as e:
            gui.print(f"Error: {e}")

gui.print("Duplex Sender - GUI Version")
gui.print("=" * 40)
gui.print("Press Enter to connect...")
gui.set_callback(handle_input)
gui.run()
