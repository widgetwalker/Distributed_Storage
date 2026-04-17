# 🔢 Experiment 19: Distributed Sorting
### **Aim**: Distributed Sorting (Merge Sort / Quick Sort) across nodes.
### **Result**: Successfully implemented and executed the Distributed Sorting across nodes.
---
## How it Works
1. **Master** splits a list into `N` equal chunks.
2. **Workers** sort chunks locally and send them back.
3. Master merges/sorts the results into the final list.
### Flowchart
```mermaid
graph TD
    M[Master: Big List] --> S[Split into Chunks]
    S --> W1[Worker A: Sort]
    S --> W2[Worker B: Sort]
    W1 --> R[Master: Merge Results]
    W2 --> R
    R --> F[Final Sorted List]
```
## Setup
1. Run `master.py` and provide input numbers.
2. Run `worker.py` on remote machines.
![Output Demo](./output_demo.png)
