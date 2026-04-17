package com.quizapp.server;

import com.quizapp.shared.Question;
import com.quizapp.shared.QuizResult;
import com.quizapp.shared.RequestData;
import com.quizapp.shared.ResponseData;
import com.quizapp.shared.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class QuizServlet extends HttpServlet {
    private DatabaseManager db = DatabaseManager.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (ObjectInputStream ois = new ObjectInputStream(req.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(resp.getOutputStream())) {
            
            RequestData requestData = (RequestData) ois.readObject();
            ResponseData responseData = processRequest(requestData);
            oos.writeObject(responseData);

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseData processRequest(RequestData req) {
        String action = req.getAction();
        Object payload = req.getPayload();

        switch (action) {
            case "LOGIN":
                return handleLogin(payload);
            case "START_SESSION":
                return handleStartSession(payload);
            case "SYNC_SESSION":
                return handleSyncSession(payload);
            case "END_SESSION":
                return handleEndSession(payload);
            case "GET_ALL_QUESTIONS":
                return handleGetAllQuestions();
            case "GET_QUESTIONS_BY_CATEGORY":
                return handleGetQuestionsByCategory(payload);
            case "GET_CATEGORIES":
                return handleGetCategories();
            case "SAVE_RESULT":
                return handleSaveResult(payload);
            case "ADMIN_DASHBOARD":
                return handleAdminDashboard();
            default:
                return new ResponseData(false, null, "Unknown action");
        }
    }

    private ResponseData handleGetQuestionsByCategory(Object payload) {
        String category = (String) payload;
        List<Question> list = db.getQuestionsByCategory(category);
        return new ResponseData(true, list, "Fetched questions for " + category);
    }

    private ResponseData handleGetCategories() {
        List<String> list = db.getCategories();
        return new ResponseData(true, list, "Fetched categories");
    }

    private ResponseData handleLogin(Object payload) {
        Map<String, String> data = (Map<String, String>) payload;
        String username = data.get("username");
        String role = data.get("role"); // "admin" or "user"
        
        if ("admin".equals(role)) {
            // Hardcoded admin for simplicity, but could be in DB
            if ("admin".equals(username)) {
                return new ResponseData(true, new User(0, "admin", "", 0, 0), "Admin login successful");
            } else {
                return new ResponseData(false, null, "Invalid admin credentials");
            }
        } else {
            User user = null;
            try {
                user = db.getUserByUsername(username);
                if (user == null) {
                    user = db.createUser(username, "");
                }
            } catch (Exception e) {
                return new ResponseData(false, null, "DB Exception: " + e.getMessage());
            }
            if (user != null) {
                return new ResponseData(true, user, "User login successful");
            }
            return new ResponseData(false, null, "Failed: Both fetch and create returned null");
        }
    }

    private ResponseData handleStartSession(Object payload) {
        Map<String, Object> data = (Map<String, Object>) payload;
        int userId = (Integer) data.get("userId");
        String username = (String) data.get("username");
        int timeRemaining = (Integer) data.get("timeRemaining");
        
        boolean success = db.createSession(userId, username, timeRemaining);
        return new ResponseData(success, null, success ? "Session started" : "Failed to start session");
    }

    private ResponseData handleSyncSession(Object payload) {
        Map<String, Object> data = (Map<String, Object>) payload;
        int userId = (Integer) data.get("userId");
        int score = (Integer) data.get("score");
        int timeRemaining = (Integer) data.get("timeRemaining");
        
        boolean success = db.updateSession(userId, score, timeRemaining);
        return new ResponseData(success, null, success ? "Session synced" : "Failed to sync session");
    }

    private ResponseData handleEndSession(Object payload) {
        int userId = (Integer) payload;
        boolean success = db.endSession(userId);
        return new ResponseData(success, null, success ? "Session ended" : "Failed to end session");
    }

    private ResponseData handleGetAllQuestions() {
        List<Question> list = db.getAllQuestions();
        return new ResponseData(true, list, "Fetched questions");
    }

    private ResponseData handleSaveResult(Object payload) {
        QuizResult result = (QuizResult) payload;
        boolean success = db.saveQuizResult(result);
        db.endSession(result.getUserId());
        return new ResponseData(success, null, success ? "Result saved" : "Failed to save result");
    }

    private ResponseData handleAdminDashboard() {
        return new ResponseData(true, db.getActiveSessions(), "Fetched active sessions");
    }
}
