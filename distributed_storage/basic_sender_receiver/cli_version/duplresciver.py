import socket, threading
names = []
def handle_client(conn, addr):
    while True:
        try:
            data = conn.recv(1024).decode()
            if not data:
                break
            parts = data.split(":", 1)
            command = parts[0]
            response = ""
            if command == "CREATE":
                name = parts[1]
                names.append(name)
                response = f"Name added: {name}\nCurrent names: {names}"
            elif command == "EXIT":
                response = "Connection closed"
                conn.send(response.encode())
                break
            conn.send(response.encode())
        except:
            break
    conn.close()
def server_menu():
    while True:
        print("\nServer Menu:\n1. Enter name\n2. Exit")
        choice = input("Enter choice: ")
        if choice == "1":
            name = input("Enter name: ")
            names.append(name)
            print(f"Name added: {name}\nCurrent names: {names}")
        elif choice == "2":
            break
def main():
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind(('localhost', 12345))
    s.listen(1)
    conn, addr = s.accept()
    threading.Thread(target=handle_client, args=(conn, addr), daemon=True).start()
    server_menu()
    s.close()

if __name__ == "__main__":
    main()
