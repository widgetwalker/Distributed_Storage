import socket

c = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
c.connect(('localhost', 12345))

while True:
    print("\nClient Menu:\n1. Create\n2. Edit\n3. Delete\n4. Show\n5. Exit")
    choice = input("Enter choice: ")
    if choice == "1":
        name = input("Enter name: ")
        c.send(f"CREATE:{name}".encode())
    elif choice == "2":
        old = input("Enter old name: ")
        new = input("Enter new name: ")
        c.send(f"EDIT:{old},{new}".encode())
    elif choice == "3":
        name = input("Enter name: ")
        c.send(f"DELETE:{name}".encode())
    elif choice == "4":
        c.send("SHOW:".encode())
    elif choice == "5":
        c.send("EXIT:".encode())
        break
    else:
        print("Invalid choice")
        continue
    resp = c.recv(1024).decode()
    print(resp)

c.close()
