import socket

client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client_socket.connect(('localhost', 12345))

a = int(input("Enter the number of names: "))
for i in range(a):
    name = input("Enter the name: ")
    client_socket.send(name.encode()) 

client_socket.close()
