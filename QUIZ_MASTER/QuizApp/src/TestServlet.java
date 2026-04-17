import com.quizapp.client.ClientNetwork;
import com.quizapp.shared.RequestData;
import com.quizapp.shared.ResponseData;
import java.util.HashMap;
import java.util.Map;

public class TestServlet {
    public static void main(String[] args) {
        System.out.println("Testing LOGIN...");
        Map<String, String> creds = new HashMap<>();
        creds.put("username", "dheer");
        creds.put("role", "user");
        
        ResponseData res = ClientNetwork.sendRequest(new RequestData("LOGIN", creds));
        System.out.println("Success: " + res.isSuccess());
        System.out.println("Message: " + res.getMessage());
        if (res.getPayload() != null) {
            System.out.println("Payload: " + res.getPayload().toString());
        }
    }
}
