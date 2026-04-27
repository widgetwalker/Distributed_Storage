import socket
import threading
import os

def setup_node_dir(node_name):
    folder = f"node_{node_name}_files"
    if not os.path.exists(folder):
        os.makedirs(folder)
    return folder

def search_in_files(folder, keyword):
    results = []
    for filename in os.listdir(folder):
        path = os.path.join(folder, filename)
        if os.path.isfile(path):
            with open(path, "r") as f:
                for line in f:
                    if keyword.lower() in line.lower():
                        results.append(f"{filename}: {line.strip()}")
    return results

def handle_client(conn, folder, node_name):
    try:
        data = conn.recv(1024).decode()
        if not data:
            return
        
        parts = data.split("|")
        command = parts[0]
        
        if command == "SEARCH":
            keyword = parts[1]
            found = search_in_files(folder, keyword)
            if found:
                reply = "\n".join(found)
            else:
                reply = "NOT_FOUND"
            conn.send(reply.encode())
            
        elif command == "UPDATE":
            fname = parts[1]
            content = parts[2]
            with open(os.path.join(folder, fname), "w") as f:
                f.write(content)
            print(f"\n[NETWORK] File '{fname}' was updated by a peer.")
            print("Choice (1-Search, 2-Update, 3-Exit): ", end="")
            
    except Exception as e:
        print(f"Error handling request: {e}")
    finally:
        conn.close()

def server_loop(port, folder, node_name):
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind(('0.0.0.0', port))
    s.listen(5)
    while True:
        conn, addr = s.accept()
        threading.Thread(target=handle_client, args=(conn, folder, node_name), daemon=True).start()

def main():
    node_name = input("Enter Node Name (e.g., A, B): ")
    my_port = int(input(f"Enter port for Node {node_name}: "))
    peer_ports_input = input("Enter peer ports (comma separated, e.g., 8001,8002): ")
    
    peer_ports = []
    if peer_ports_input.strip():
        peer_ports = [int(p.strip()) for p in peer_ports_input.split(",")]
    
    folder = setup_node_dir(node_name)
    
    threading.Thread(target=server_loop, args=(my_port, folder, node_name), daemon=True).start()
    
    while True:
        print(f"\n--- Node {node_name} Menu ---")
        print("1. Search (Local + Peers)")
        print("2. Update/Sync File to Peers")
        print("3. Exit")
        choice = input("Choice: ")
        
        if choice == '1':
            keyword = input("Enter keyword to search: ")

            print("Searching locally...")
            local_results = search_in_files(folder, keyword)
            for r in local_results:
                print(f"[LOCAL] {r}")
            
            for p in peer_ports:
                try:
                    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                    s.connect(('localhost', p))
                    s.send(f"SEARCH|{keyword}".encode())
                    reply = s.recv(4096).decode()
                    if reply != "NOT_FOUND":
                        print(f"[PEER {p}] {reply}")
                    s.close()
                except:
                    print(f"Could not reach peer at port {p}")
                    
        elif choice == '2':
            fname = input("Enter filename to update: ")
            content = input("Enter new content: ")

            with open(os.path.join(folder, fname), "w") as f:
                f.write(content)

            for p in peer_ports:
                try:
                    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                    s.connect(('localhost', p))
                    s.send(f"UPDATE|{fname}|{content}".encode())
                    s.close()
                except:
                    print(f"Could not sync with peer at port {p}")
            print("File updated and synced with peers.")
            
        elif choice == '3':
            break

if __name__ == "__main__":
    main()
