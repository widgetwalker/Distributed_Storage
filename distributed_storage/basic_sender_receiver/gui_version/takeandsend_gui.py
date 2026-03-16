from gui_base import TerminalGUI

gui = TerminalGUI("Take and Send Names")

# State variables
state = {'count': None, 'names': []}

def handle_input(value):
    if state['count'] is None:
        # Getting count
        try:
            state['count'] = int(value)
            state['names'] = []
            gui.print(f"\nEnter {state['count']} names:")
        except:
            gui.print("Please enter a valid number")
            gui.print("Enter the number of names:")
    else:
        # Getting names
        state['names'].append(value)
        
        if len(state['names']) >= state['count']:
            gui.print("\nYou entered:")
            for name in state['names']:
                gui.print(f"  {name}")
            
            # Reset
            gui.print("\n" + "=" * 40)
            gui.print("Enter the number of names:")
            state['count'] = None
            state['names'] = []

gui.print("Take and Send Names - GUI Version")
gui.print("=" * 40)
gui.print("Enter the number of names:")
gui.set_callback(handle_input)
gui.run()
