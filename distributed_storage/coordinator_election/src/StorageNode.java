import java.util.ArrayList;
import java.util.List;

public class StorageNode {
    private final String nodeId;
    private final int CAPACITY = 5;
    private List<String> files;

    public StorageNode(String nodeId) {
        this.nodeId = nodeId;
        this.files = new ArrayList<>();
    }

    public boolean isFull() {
        return files.size() >= CAPACITY;
    }

    public boolean addFile(String fileName) {
        if (isFull()) return false;
        if (!files.contains(fileName)) {
            files.add(fileName);
            return true;
        }
        return false;
    }

    public boolean deleteFile(String fileName) {
        return files.remove(fileName);
    }

    public List<String> getFiles() {
        return new ArrayList<>(files);
    }

    public String getNodeId() { return nodeId; }
    public int getCount() { return files.size(); }
    public int getCapacity() { return CAPACITY; }
}
