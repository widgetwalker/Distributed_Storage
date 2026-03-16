from gui_base import TerminalGUI
import socket
import threading

gui = TerminalGUI("Simple Sender")

state = {'count': None, 'names': []}

def handle_input(value):
    if state['count'] is None:
        try:
            state['count'] = int(value)
            state['names'] = []
            gui.print(f"\nEnter {state['count']} names:")
        except:
            gui.print("Please enter a valid number")
    else:
        state['names'].append(value)
        
        if len(state['names']) >= state['count']:
            # Send to server
            gui.print("\nConnecting to server...")
            try:
                sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                sock.connect(('localhost', 12345))
                gui.print("Connected!")
                
                for name in state['names']:
                    sock.send(name.encode())
                    gui.print(f"Sent: {name}")
                
                sock.close()
                gui.print("All names sent. Connection closed.\n")
            except Exception as e:
                gui.print(f"Error: {e}\n")
            
            # Reset
            gui.print("=" * 40)
            gui.print("Enter the number of names:")
            state['count'] = None
            state['names'] = []

gui.print("Simple Sender - GUI Version")
gui.print("=" * 40)
gui.print("Enter the number of names:")
gui.set_callback(handle_input)
gui.run()
