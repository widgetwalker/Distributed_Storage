# 19.Develop a program for Distributed Sorting (Merge Sort / Quick Sort) across nodes.
import socket
import pickle
def run_master():
    port = int(input("Master Port: ").strip()); count = int(input("Worker Count: ").strip())
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM); s.bind(('0.0.0.0', port)); s.listen(count)
    nums = [int(x) for x in input("Numbers to sort: ").strip().split()]; step = len(nums)//count; results = []
    print(f"Waiting for {count} workers...")
    for i in range(count):
        conn, _ = s.accept(); name = conn.recv(1024).decode()
        chunk = nums[i*step:(i+1)*step] if i<count-1 else nums[i*step:]
        print(f"[TASK] Sending chunk to Worker '{name}'"); conn.sendall(pickle.dumps(chunk))
        results.extend(pickle.loads(conn.recv(4096))); conn.close()
    print(f"Final Sorted Result: {sorted(results)}"); s.close()
if __name__ == "__main__": run_master()
