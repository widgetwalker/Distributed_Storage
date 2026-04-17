# 20.Write a program to simulate MapReduce for Word Count.
import socket
import pickle
def run_master():
    port = int(input("Master Port: ").strip()); count = int(input("Worker Count: ").strip())
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM); s.bind(('0.0.0.0', port)); s.listen(count)
    text = input("Sentence: ").strip().split(); step = len(text)//count; final_counts = {}
    print(f"Waiting for {count} workers...")
    for i in range(count):
        conn, _ = s.accept(); name = conn.recv(1024).decode()
        chunk = text[i*step:(i+1)*step] if i<count-1 else text[i*step:]
        print(f"[MAP] Sending words to Worker '{name}'"); conn.sendall(pickle.dumps(chunk))
        res = pickle.loads(conn.recv(4096))
        for k, v in res.items(): final_counts[k] = final_counts.get(k, 0) + v
        conn.close()
    print(f"Final Word Counts: {final_counts}"); s.close()
if __name__ == "__main__": run_master()
