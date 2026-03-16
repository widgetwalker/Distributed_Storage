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
                response = f"Name created: {name}"
            elif command == "EDIT":
                old, new = parts[1].split(",")
                if old in names:
                    idx = names.index(old)
                    names[idx] = new
                    response = f"Name edited: {old} -> {new}"
                else:
                    response = f"Name {old} not found"
            elif command == "DELETE":
                name = parts[1]
                if name in names:
                    names.remove(name)
                    response = f"Name deleted: {name}"
                else:
                    response = f"Name {name} not found"
            elif command == "SHOW":
                response = f"Current names: {names}"
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
        print("\nServer Menu:\n1. Create\n2. Edit\n3. Delete\n4. Show\n5. Exit")
        choice = input("Enter choice: ")
        if choice == "1":
            name = input("Enter name: ")
            names.append(name)
            print(f"Name created: {name}")
        elif choice == "2":
            old = input("Enter old name: ")
            new = input("Enter new name: ")
            if old in names:
                idx = names.index(old)
                names[idx] = new
                print(f"Name edited: {old} -> {new}")
            else:
                print(f"Name {old} not found")
        elif choice == "3":
            name = input("Enter name: ")
            if name in names:
                names.remove(name)
                print(f"Name deleted: {name}")
            else:
                print(f"Name {name} not found")
        elif choice == "4":
            print("Current names:", names)
        elif choice == "5":
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
