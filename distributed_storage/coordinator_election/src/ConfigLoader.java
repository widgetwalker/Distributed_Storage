import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    private static final String CONFIG_FILE = "src/config.properties";
    private static Properties properties = new Properties();

    public static void load() throws IOException {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
        }
    }

    public static String getIp(String nodeId) {
        return properties.getProperty("node." + nodeId + ".ip");
    }

    public static int getPort(String nodeId) {
        String port = properties.getProperty("node." + nodeId + ".port");
        return port != null ? Integer.parseInt(port) : -1;
    }

    public static int getPriority(String nodeId) {
        String priority = properties.getProperty("node." + nodeId + ".priority");
        return priority != null ? Integer.parseInt(priority) : -1;
    }
}
