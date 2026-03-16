import tkinter as tk
from tkinter import scrolledtext
import threading
import queue

class TerminalGUI:
    """Simple terminal-like GUI - completely reworked"""
    
    def __init__(self, title="Terminal"):
        self.root = tk.Tk()
        self.root.title(title)
        self.root.geometry("700x500")
        
        # Output text area
        self.output = scrolledtext.ScrolledText(
            self.root,
            wrap=tk.WORD,
            font=("Consolas", 10),
            bg='black',
            fg='white',
            state='disabled'
        )
        self.output.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)
        
        # Input frame
        input_frame = tk.Frame(self.root, bg='black')
        input_frame.pack(fill=tk.X, padx=5, pady=5)
        
        tk.Label(input_frame, text=">", font=("Consolas", 10), bg='black', fg='white').pack(side=tk.LEFT)
        
        self.input_field = tk.Entry(
            input_frame,
            font=("Consolas", 10),
            bg='black',
            fg='white',
            insertbackground='white'
        )
        self.input_field.pack(side=tk.LEFT, fill=tk.X, expand=True, padx=5)
        self.input_field.bind('<Return>', self._on_enter)
        
        self.callback = None
        self.msg_queue = queue.Queue()
        
        # Process queue periodically
        self._process_queue()
    
    def _process_queue(self):
        """Process messages from queue (for thread-safe printing)"""
        try:
            while True:
                msg = self.msg_queue.get_nowait()
                self.output.config(state='normal')
                self.output.insert(tk.END, msg + "\n")
                self.output.see(tk.END)
                self.output.config(state='disabled')
        except queue.Empty:
            pass
        self.root.after(100, self._process_queue)
    
    def print(self, text):
        """Print text to output (thread-safe)"""
        self.msg_queue.put(str(text))
    
    def _on_enter(self, event):
        """Handle Enter key"""
        value = self.input_field.get()
        if value:
            self.print(f"> {value}")
            self.input_field.delete(0, tk.END)
            
            if self.callback:
                # Run callback in thread
                threading.Thread(target=self.callback, args=(value,), daemon=True).start()
    
    def set_callback(self, callback):
        """Set input callback"""
        self.callback = callback
    
    def run(self):
        """Start GUI"""
        self.input_field.focus_set()
        self.root.mainloop()
    
    def close(self):
        """Close GUI"""
        self.root.quit()

# Test
if __name__ == "__main__":
    gui = TerminalGUI("Test")
    gui.print("Type something and press Enter")
    
    def handle(value):
        gui.print(f"You entered: {value}")
    
    gui.set_callback(handle)
    gui.run()
