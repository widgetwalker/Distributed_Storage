package com.quizapp.client;

import com.quizapp.shared.RequestData;
import com.quizapp.shared.ResponseData;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClientNetwork {
    private static final String SERVER_URL = "http://localhost:8080/api/";

    public static ResponseData sendRequest(RequestData requestData) {
        try {
            URL url = new URL(SERVER_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/octet-stream");

            try (ObjectOutputStream oos = new ObjectOutputStream(conn.getOutputStream())) {
                oos.writeObject(requestData);
                oos.flush();
            }

            if (conn.getResponseCode() == 200) {
                try (ObjectInputStream ois = new ObjectInputStream(conn.getInputStream())) {
                    return (ResponseData) ois.readObject();
                }
            } else {
                return new ResponseData(false, null, "Server err: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseData(false, null, "Connection err: " + e.getMessage());
        }
    }
}
