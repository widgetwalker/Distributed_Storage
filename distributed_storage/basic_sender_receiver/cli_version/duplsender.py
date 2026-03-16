import socket

c = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
c.connect(('localhost', 12345))

while True:
    print("\nClient Menu:\n1. Enter name\n2. Exit")
    choice = input("Enter choice: ")
    if choice == "1":
        name = input("Enter name: ")
        c.send(f"CREATE:{name}".encode())
    elif choice == "2":
        c.send("EXIT:".encode())
        break
    else:
        print("Invalid choice")
        continue
    resp = c.recv(1024).decode()
    print(resp)

c.close()
