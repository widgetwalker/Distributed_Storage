import socket
import threading
clock = 0
name = ""
lock = threading.Lock()
def receiver(port):
    global clock
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind(('0.0.0.0', port))
    s.listen(5)
    
    while True:
        conn, addr = s.accept()
        data = conn.recv(1024).decode()
        
        if data:
            sender_name, sender_clock = data.split(":")
            sender_clock = int(sender_clock)
            
            # Update clock: max(local, received) + 1
            with lock:
                clock = max(clock, sender_clock) + 1
                print(f"\n[Received] Message from {sender_name} (their clock: {sender_clock})")
                print(f"My New Clock: {clock}")
                print("1: Event, 2: Send, 3: Exit -> ", end="")
        conn.close()
def main():
    global clock, name
    my_port = int(input("Enter your port: "))
    name = input("Enter your name: ")
    threading.Thread(target=receiver, args=(my_port,), daemon=True).start()
    
    while True:
        print(f"\n--- {name}'s Lamport Clock: {clock} ---")
        print("1. Internal Event")
        print("2. Send Message")
        print("3. Exit")
        choice = input("Choice: ")
        
        if choice == '1':
            with lock:
                clock += 1
                print(f"Internal event occurred. Clock is now {clock}")
        
        elif choice == '2':
            target_port = int(input("Enter target port: "))
            with lock:
                clock += 1 
                msg = f"{name}:{clock}"
            try:
                s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                s.connect(('localhost', target_port))
                s.send(msg.encode())
                s.close()
                print(f"Message sent! My clock: {clock}")
            except:
                print("Could not connect to target.")
                
        elif choice == '3':
            break

if __name__ == "__main__":
    main()
