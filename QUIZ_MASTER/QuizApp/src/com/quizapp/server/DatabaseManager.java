package com.quizapp.server;

import com.quizapp.shared.Question;
import com.quizapp.shared.User;
import com.quizapp.shared.QuizResult;
import com.quizapp.shared.QuizSession;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Database manager for quiz application
 * Handles all database operations using SQLite
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:QuizApp.db";
    private static DatabaseManager instance;

    private DatabaseManager() {
        initializeDatabase();
    }

    /**
     * Singleton pattern to get database manager instance
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Initialize database with tables if they don't exist
     */
    private void initializeDatabase() {
        try {
            // Try to load SQLite driver
            Class.forName("org.sqlite.JDBC");

            try (Connection conn = DriverManager.getConnection(DB_URL);
                    Statement stmt = conn.createStatement()) {

                // Create questions table
                stmt.execute("CREATE TABLE IF NOT EXISTS questions (" +
                        "id INTEGER PRIMARY KEY," +
                        "question_text TEXT NOT NULL," +
                        "option_a TEXT NOT NULL," +
                        "option_b TEXT NOT NULL," +
                        "option_c TEXT NOT NULL," +
                        "option_d TEXT NOT NULL," +
                        "correct_option TEXT NOT NULL," +
                        "difficulty TEXT" +
                        ")");

                // Migration: Add category if not exists (SQLite doesn't have IF NOT EXISTS for columns, so we try-catch)
                try {
                    stmt.execute("ALTER TABLE questions ADD COLUMN category TEXT");
                } catch (SQLException ignored) {}

                // Migration: Default existing null categories to 'Anime'
                stmt.execute("UPDATE questions SET category = 'Anime' WHERE category IS NULL");

                // Create users table
                stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username TEXT UNIQUE NOT NULL," +
                        "email TEXT," +
                        "total_score INTEGER DEFAULT 0," +
                        "quizzes_attempted INTEGER DEFAULT 0," +
                        "created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                        ")");

                // Create results table
                stmt.execute("CREATE TABLE IF NOT EXISTS results (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_id INTEGER NOT NULL," +
                        "username TEXT NOT NULL," +
                        "score INTEGER NOT NULL," +
                        "total_questions INTEGER NOT NULL," +
                        "time_taken INTEGER NOT NULL," +
                        "attempted_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "FOREIGN KEY (user_id) REFERENCES users(id)" +
                        ")");

                // Create active_sessions table
                stmt.execute("CREATE TABLE IF NOT EXISTS active_sessions (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_id INTEGER UNIQUE NOT NULL," +
                        "username TEXT NOT NULL," +
                        "score INTEGER DEFAULT 0," +
                        "time_remaining INTEGER NOT NULL," +
                        "is_active BOOLEAN DEFAULT 1," +
                        "FOREIGN KEY (user_id) REFERENCES users(id)" +
                        ")");

                System.out.println("Database initialized successfully!");
                seedSampleQuestions();
            }
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver not found. Running in memory-only mode.");
            System.out.println("Install sqlite-jdbc to enable persistent storage.");
        } catch (SQLException e) {
            System.err.println("Database initialization warning: " + e.getMessage());
            System.out.println("App will use in-memory sample data.");
        }
    }

    // ==================== QUESTION OPERATIONS ====================

    /**
     * Get all questions from database
     */
    public List<Question> getAllQuestions() {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Question q = new Question(
                        rs.getInt("id"),
                        rs.getString("question_text"),
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d"),
                        rs.getString("correct_option"));
                q.setDifficulty(rs.getString("difficulty"));
                q.setCategory(rs.getString("category"));
                questions.add(q);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching questions: " + e.getMessage());
        }

        return questions;
    }

    /**
     * Get questions by category
     */
    public List<Question> getQuestionsByCategory(String category) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE category = ?";

        if (category == null || category.equalsIgnoreCase("All")) {
            return getAllQuestions();
        }

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Question q = new Question(
                        rs.getInt("id"),
                        rs.getString("question_text"),
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d"),
                        rs.getString("correct_option"));
                q.setDifficulty(rs.getString("difficulty"));
                q.setCategory(rs.getString("category"));
                questions.add(q);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching categorized questions: " + e.getMessage());
        }

        return questions;
    }

    /**
     * Get a specific question by ID
     */
    public Question getQuestionById(int id) {
        String sql = "SELECT * FROM questions WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Question q = new Question(
                        rs.getInt("id"),
                        rs.getString("question_text"),
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d"),
                        rs.getString("correct_option"));
                q.setDifficulty(rs.getString("difficulty"));
                q.setCategory(rs.getString("category"));
                return q;
            }

        } catch (SQLException e) {
            System.err.println("Error fetching question: " + e.getMessage());
        }

        return null;
    }

    /**
     * Add a new question to database
     */
    public boolean addQuestion(Question q) {
        String sql = "INSERT INTO questions(question_text, option_a, option_b, option_c, option_d, " +
                "correct_option, difficulty, category) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, q.getQuestionText());
            pstmt.setString(2, q.getOptionA());
            pstmt.setString(3, q.getOptionB());
            pstmt.setString(4, q.getOptionC());
            pstmt.setString(5, q.getOptionD());
            pstmt.setString(6, q.getCorrectOption());
            pstmt.setString(7, q.getDifficulty());
            pstmt.setString(8, q.getCategory());

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error adding question: " + e.getMessage());
        }

        return false;
    }

    // ==================== USER OPERATIONS ====================

    /**
     * Get or create user
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getInt("total_score"),
                        rs.getInt("quizzes_attempted"));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching user: " + e.getMessage());
        }

        return null;
    }

    /**
     * Create a new user
     */
    public User createUser(String username, String email) {
        String sql = "INSERT INTO users(username, email) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.executeUpdate();

            // SQLite JDBC driver doesn't support getGeneratedKeys reliably,
            // so we'll just fetch the user that we just inserted by username.
            return getUserByUsername(username);

        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
        }

        return null;
    }

    /**
     * Get all users sorted by score
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY total_score DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getInt("total_score"),
                        rs.getInt("quizzes_attempted")));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }

        return users;
    }

    // ==================== RESULT OPERATIONS ====================

    /**
     * Save quiz result
     */
    public boolean saveQuizResult(QuizResult result) {
        String sql = "INSERT INTO results(user_id, username, score, total_questions, time_taken) " +
                "VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, result.getUserId());
            pstmt.setString(2, result.getUsername());
            pstmt.setInt(3, result.getScore());
            pstmt.setInt(4, result.getTotalQuestions());
            pstmt.setInt(5, (int) result.getTimeTaken());

            pstmt.executeUpdate();

            // Update user's total score and quizzes attempted
            updateUserStats(result.getUserId(), result.getScore());
            return true;

        } catch (SQLException e) {
            System.err.println("Error saving quiz result: " + e.getMessage());
        }

        return false;
    }

    /**
     * Get all results for a user
     */
    public List<QuizResult> getUserResults(int userId) {
        List<QuizResult> results = new ArrayList<>();
        String sql = "SELECT * FROM results WHERE user_id = ? ORDER BY attempted_at DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                results.add(new QuizResult(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getInt("score"),
                        rs.getInt("total_questions"),
                        rs.getInt("time_taken"),
                        rs.getTimestamp("attempted_at").toLocalDateTime()));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching user results: " + e.getMessage());
        }

        return results;
    }

    /**
     * Update user stats after quiz
     */
    private void updateUserStats(int userId, int score) {
        String sql = "UPDATE users SET total_score = total_score + ?, quizzes_attempted = quizzes_attempted + 1 WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, score);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating user stats: " + e.getMessage());
        }
    }

    /**
     * Get leaderboard (top users)
     */
    public List<User> getLeaderboard(int limit) {
        List<User> leaderboard = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY total_score DESC LIMIT ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                leaderboard.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getInt("total_score"),
                        rs.getInt("quizzes_attempted")));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching leaderboard: " + e.getMessage());
        }

        return leaderboard;
    }

    // ==================== SESSION OPERATIONS ====================

    /**
     * Start a new active session for a user or replace an existing one
     */
    public boolean createSession(int userId, String username, int timeRemaining) {
        String deleteSql = "DELETE FROM active_sessions WHERE user_id = ?";
        String insertSql = "INSERT INTO active_sessions(user_id, username, time_remaining, is_active) VALUES(?, ?, ?, 1)";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            try (PreparedStatement pstmtDelete = conn.prepareStatement(deleteSql)) {
                pstmtDelete.setInt(1, userId);
                pstmtDelete.executeUpdate();
            }
            try (PreparedStatement pstmtInsert = conn.prepareStatement(insertSql)) {
                pstmtInsert.setInt(1, userId);
                pstmtInsert.setString(2, username);
                pstmtInsert.setInt(3, timeRemaining);
                pstmtInsert.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating section: " + e.getMessage());
        }
        return false;
    }

    /**
     * Update an active session with current score and remaining time
     */
    public boolean updateSession(int userId, int score, int timeRemaining) {
        String sql = "UPDATE active_sessions SET score = ?, time_remaining = ? WHERE user_id = ? AND is_active = 1";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, score);
            pstmt.setInt(2, timeRemaining);
            pstmt.setInt(3, userId);
            
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating session: " + e.getMessage());
        }
        return false;
    }

    /**
     * End an active session
     */
    public boolean endSession(int userId) {
        String sql = "UPDATE active_sessions SET is_active = 0 WHERE user_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error ending session: " + e.getMessage());
        }
        return false;
    }

    /**
     * Get all active sessions (for Admin dashboard)
     */
    public List<QuizSession> getActiveSessions() {
        List<QuizSession> sessions = new ArrayList<>();
        String sql = "SELECT * FROM active_sessions WHERE is_active = 1 ORDER BY score DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                sessions.add(new QuizSession(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getInt("score"),
                        rs.getInt("time_remaining"),
                        rs.getBoolean("is_active")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching active sessions: " + e.getMessage());
        }

        return sessions;
    }

    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM questions ORDER BY category";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String cat = rs.getString("category");
                if (cat != null) categories.add(cat);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching categories: " + e.getMessage());
        }
        return categories;
    }

    private int getQuestionCountByCategory(String category) {
        String sql = "SELECT COUNT(*) FROM questions WHERE category = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {}
        return 0;
    }

    private void seedSampleQuestions() {
        System.out.println("Checking database for sample questions...");
        
        // Anime
        if (getQuestionCountByCategory("Anime") < 10) {
            addQuestion(new Question(0, "In 'Naruto', who is the Fourth Hokage?", "Jiraiya", "Minato", "Kakashi", "Hiruzen", "B", "Anime", "Medium"));
            addQuestion(new Question(0, "What is the name of the main dragon in 'Spirited Away'?", "Haku", "Tohru", "Falcor", "Mushu", "A", "Anime", "Easy"));
            addQuestion(new Question(0, "Who wrote 'Dragon Ball'?", "Oda", "Kishimoto", "Toriyama", "Tite Kubo", "C", "Anime", "Medium"));
            addQuestion(new Question(0, "What is the name of Goku's signature move?", "Chidori", "Kamehameha", "Rasengan", "Getsuga Tensho", "B", "Anime", "Easy"));
            addQuestion(new Question(0, "In 'Death Note', what is the name of the Shinigami who likes apples?", "Ryuk", "Rem", "Gelus", "Sidoh", "A", "Anime", "Medium"));
            addQuestion(new Question(0, "How many Dragon Balls are there?", "5", "6", "7", "8", "C", "Anime", "Easy"));
            addQuestion(new Question(0, "Who is the lead character in 'One Piece'?", "Zoro", "Luffy", "Sanji", "Nami", "B", "Anime", "Easy"));
            addQuestion(new Question(0, "In 'Attack on Titan', what is Eren's last name?", "Ackerman", "Arlert", "Yeager", "Smith", "C", "Anime", "Easy"));
            addQuestion(new Question(0, "Which anime features the 'Elric' brothers?", "Fullmetal Alchemist", "Bleach", "Gintama", "Fairy Tail", "A", "Anime", "Medium"));
            addQuestion(new Question(0, "What is the highest rank of a ninja in Naruto's village?", "Jonin", "Chunin", "Genin", "Hokage", "D", "Anime", "Easy"));
        }

        // Movies
        if (getQuestionCountByCategory("Movies") < 10) {
            addQuestion(new Question(0, "Which movie features the character 'Jack Sparrow'?", "Peter Pan", "Pirates of the Caribbean", "Titanic", "Inception", "B", "Movies", "Easy"));
            addQuestion(new Question(0, "Who directed 'Inception'?", "Steven Spielberg", "Christopher Nolan", "Quentin Tarantino", "Martin Scorsese", "B", "Movies", "Medium"));
            addQuestion(new Question(0, "What is the name of the kingdom in 'Frozen'?", "Arendelle", "Genovia", "Wakanda", "Narnia", "A", "Movies", "Easy"));
            addQuestion(new Question(0, "Which movie won the Oscar for Best Picture in 2020?", "1917", "Joker", "Parasite", "The Irishman", "C", "Movies", "Hard"));
            addQuestion(new Question(0, "Who played Iron Man in the MCU?", "Chris Evans", "Robert Downey Jr.", "Chris Hemsworth", "Mark Ruffalo", "B", "Movies", "Easy"));
            addQuestion(new Question(0, "In 'The Matrix', what color pill does Neo take?", "Blue", "Red", "Green", "Yellow", "B", "Movies", "Medium"));
            addQuestion(new Question(0, "What is the name of the hobbit played by Elijah Wood?", "Pip", "Sam", "Frodo", "Merry", "C", "Movies", "Easy"));
            addQuestion(new Question(0, "Which 1994 film features 'The Lion King'?", "Disney", "Pixar", "Dreamworks", "Warner", "A", "Movies", "Easy"));
            addQuestion(new Question(0, "Who is the primary antagonist in 'The Dark Knight'?", "Bane", "Scarecrow", "The Joker", "Two-Face", "C", "Movies", "Medium"));
            addQuestion(new Question(0, "Titanic sank in which ocean?", "Atlantic", "Pacific", "Indian", "Arctic", "A", "Movies", "Easy"));
        }

        // Maths
        if (getQuestionCountByCategory("Maths") < 10) {
            addQuestion(new Question(0, "What is 12 x 12?", "124", "144", "164", "184", "B", "Maths", "Easy"));
            addQuestion(new Question(0, "What is the square root of 81?", "7", "8", "9", "10", "C", "Maths", "Easy"));
            addQuestion(new Question(0, "Solve: 5 + (2 x 3)?", "11", "21", "15", "10", "A", "Maths", "Easy"));
            addQuestion(new Question(0, "How many degrees are in a triangle?", "90", "180", "270", "360", "B", "Maths", "Medium"));
            addQuestion(new Question(0, "What is 7% of 100?", "0.7", "7", "70", "700", "B", "Maths", "Easy"));
            addQuestion(new Question(0, "What is the value of Pi?", "3.12", "3.14", "3.16", "3.18", "B", "Maths", "Easy"));
            addQuestion(new Question(0, "How many sides does a heptagon have?", "6", "7", "8", "9", "B", "Maths", "Medium"));
            addQuestion(new Question(0, "Solve for x: 2x = 10", "2", "5", "10", "20", "B", "Maths", "Easy"));
            addQuestion(new Question(0, "What is 0.5 as a fraction?", "1/4", "1/2", "3/4", "1/5", "B", "Maths", "Easy"));
            addQuestion(new Question(0, "What follows 1, 1, 2, 3, 5, ...?", "7", "8", "9", "10", "B", "Maths", "Medium"));
        }

        // CS
        if (getQuestionCountByCategory("CS") < 10) {
            addQuestion(new Question(0, "What does CPU stand for?", "Central Power Unit", "Central Processing Unit", "Computer Program Unit", "None", "B", "CS", "Easy"));
            addQuestion(new Question(0, "Which language is used for Web development?", "C", "HTML", "Assembly", "Fortran", "B", "CS", "Easy"));
            addQuestion(new Question(0, "Who is the father of computers?", "Steve Jobs", "Bill Gates", "Charles Babbage", "Alan Turing", "C", "CS", "Medium"));
            addQuestion(new Question(0, "What does RAM stand for?", "Read Access Memory", "Random Access Memory", "Run Access Memory", "None", "B", "CS", "Easy"));
            addQuestion(new Question(0, "What is the main language for Android?", "Java", "Kotlin", "C++", "Swift", "B", "CS", "Medium"));
            addQuestion(new Question(0, "What does SQL stand for?", "Simple Query Language", "Structured Query Language", "Shared Query Language", "Standard Query language", "B", "CS", "Medium"));
            addQuestion(new Question(0, "Which color is the GitHub logo?", "Blue", "Black", "Red", "Green", "B", "CS", "Easy"));
            addQuestion(new Question(0, "What is 1 gigabyte in MB?", "500", "1000", "1024", "2048", "C", "CS", "Easy"));
            addQuestion(new Question(0, "Which company developed Java?", "Microsoft", "Google", "Sun Microsystems", "Apple", "C", "CS", "Medium"));
            addQuestion(new Question(0, "What is the brain of the computer?", "RAM", "CPU", "HDD", "Monitor", "B", "CS", "Easy"));
        }

        // Science
        if (getQuestionCountByCategory("Science") < 10) {
            addQuestion(new Question(0, "What planet is known as the red planet?", "Venus", "Mars", "Jupiter", "Saturn", "B", "Science", "Easy"));
            addQuestion(new Question(0, "What is the chemical symbol for water?", "CO2", "H2O", "O2", "N2", "B", "Science", "Easy"));
            addQuestion(new Question(0, "Which gas do humans inhale to live?", "Oxygen", "Nitrogen", "Carbon Dioxide", "Helium", "A", "Science", "Easy"));
            addQuestion(new Question(0, "What is the hardest natural substance?", "Gold", "Iron", "Diamond", "Platinum", "C", "Science", "Medium"));
            addQuestion(new Question(0, "How many bones are in the adult body?", "106", "206", "306", "406", "B", "Science", "Medium"));
            addQuestion(new Question(0, "Which planet is closest to the Sun?", "Earth", "Venus", "Mercury", "Mars", "C", "Science", "Easy"));
            addQuestion(new Question(0, "What is the largest organ of the body?", "Heart", "Liver", "Skin", "Lungs", "C", "Science", "Medium"));
            addQuestion(new Question(0, "Boiling point of water in Celsius?", "90", "100", "110", "120", "B", "Science", "Easy"));
            addQuestion(new Question(0, "What is the speed of light?", "300,000 km/s", "150,000 km/s", "500,000 km/s", "1,000,000 km/s", "A", "Science", "Hard"));
            addQuestion(new Question(0, "Which gas is most abundant in Earth's air?", "Oxygen", "Carbon Dioxide", "Nitrogen", "Argon", "C", "Science", "Medium"));
        }

        // Fun
        if (getQuestionCountByCategory("Fun") < 10) {
            addQuestion(new Question(0, "How many legs does a spider have?", "6", "8", "10", "12", "B", "Fun", "Easy"));
            addQuestion(new Question(0, "What is the color of a school bus?", "Red", "Blue", "Yellow", "Green", "C", "Fun", "Easy"));
            addQuestion(new Question(0, "Which fruit is yellow and curved?", "Apple", "Banana", "Grape", "Orange", "B", "Fun", "Easy"));
            addQuestion(new Question(0, "How many days are in a week?", "5", "6", "7", "8", "C", "Fun", "Easy"));
            addQuestion(new Question(0, "Which animal is the King of the Jungle?", "Tiger", "Cheetah", "Elephant", "Lion", "D", "Fun", "Easy"));
            addQuestion(new Question(0, "What is the capital of France?", "London", "Berlin", "Paris", "Rome", "C", "Fun", "Easy"));
            addQuestion(new Question(0, "How many colors are in a rainbow?", "5", "6", "7", "8", "C", "Fun", "Easy"));
            addQuestion(new Question(0, "Which bird can't fly?", "Sparrow", "Ostrich", "Eagle", "Parrot", "B", "Fun", "Easy"));
            addQuestion(new Question(0, "What is the frozen form of water?", "Mist", "Steam", "Ice", "Liquid", "C", "Fun", "Easy"));
            addQuestion(new Question(0, "Who lives in a pineapple under the sea?", "Patrick", "SpongeBob", "Squidward", "Sandy", "B", "Fun", "Easy"));
        }

        // K-Drama
        if (getQuestionCountByCategory("K-Drama") < 10) {
            addQuestion(new Question(0, "Which K-drama features the 'Squid Game'?", "All of Us Are Dead", "Squid Game", "Kingdom", "Signal", "B", "K-Drama", "Easy"));
            addQuestion(new Question(0, "Who is the lead actor in 'Crash Landing on You'?", "Hyun Bin", "Lee Min Ho", "Gong Yoo", "Park Seo Joon", "A", "K-Drama", "Medium"));
            addQuestion(new Question(0, "In 'Goblin', what is the main character's weapon?", "Sword", "Bow", "Dagger", "Axe", "A", "K-Drama", "Medium"));
            addQuestion(new Question(0, "Which drama is set in the 1980s?", "Reply 1988", "Healer", "W", "Pinocchio", "A", "K-Drama", "Easy"));
            addQuestion(new Question(0, "What is the name of the AI in 'Start-Up'?", "Jang-young", "Yeong-sil", "Siri", "Alexa", "B", "K-Drama", "Medium"));
            addQuestion(new Question(0, "Who is the 'Grim Reaper' in 'Goblin'?", "Lee Dong-wook", "Song Joong-ki", "Kim Soo-hyun", "Ji Chang-wook", "A", "K-Drama", "Medium"));
            addQuestion(new Question(0, "In 'Vincenzo', what is Vincenzo's profession?", "Lawyer/Consigliere", "Doctor", "Chef", "Pilot", "A", "K-Drama", "Easy"));
            addQuestion(new Question(0, "Which drama features a hotel for ghosts?", "Hotel Del Luna", "The Master's Sun", "Mystic Pop-up Bar", "Oh My Ghost", "A", "K-Drama", "Easy"));
            addQuestion(new Question(0, "Who is the lead actress in 'My Love from the Star'?", "Jun Ji-hyun", "Song Hye-kyo", "IU", "Park Shin-hye", "A", "K-Drama", "Medium"));
            addQuestion(new Question(0, "What is the game played in the first round of 'Squid Game'?", "Dalgona", "Red Light, Green Light", "Tug of War", "Marbles", "B", "K-Drama", "Easy"));
        }
    }
}
