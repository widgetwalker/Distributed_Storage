-- Quiz Application Database Schema
-- SQLite SQL Script

CREATE TABLE IF NOT EXISTS questions (
    id INTEGER PRIMARY KEY,
    question_text TEXT NOT NULL,
    option_a TEXT NOT NULL,
    option_b TEXT NOT NULL,
    option_c TEXT NOT NULL,
    option_d TEXT NOT NULL,
    correct_option TEXT NOT NULL,
    difficulty TEXT,
    category TEXT
);

CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    email TEXT,
    total_score INTEGER DEFAULT 0,
    quizzes_attempted INTEGER DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS results (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    username TEXT NOT NULL,
    score INTEGER NOT NULL,
    total_questions INTEGER NOT NULL,
    time_taken INTEGER NOT NULL,
    attempted_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Sample Questions
INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, difficulty)
VALUES 
('Which of these is the Capital of UAE?', 'Dubai', 'Abu Dhabi', 'Fujera', 'Ras-al-Khaimah', 'B', 'Easy'),
('Who created this Application?', 'Bill Gates', 'Maroor Chethan Pai', 'Elon Musk', 'Anupam Mittal', 'B', 'Medium'),
('What would be the heart rate of a person if the cardiac output is 5 L?', '100 beats per minute', '70 beats per minute', '92 beats per minute', 'The person has died :(', 'A', 'Hard'),
('Which is The host country of G20 summit this year?', 'Brazil', 'Indonesia', 'India', 'China', 'C', 'Easy'),
('What is the Parent company of ChatGPT?', 'OpenAI', 'Microsoft', 'Meta', 'NeuroLink', 'A', 'Easy'),
('How to read entire file in one line using java 8?', 'Files.readAllLines()', 'Files.read()', 'Files.readFile()', 'Files.lines()', 'A', 'Medium'),
('Hydrolysis of Sucrose is catalysed by', 'H+', 'enzymes', 'Mineral acids', 'all of the above', 'D', 'Hard'),
('Which of the following people are not a part of Mahabharata?', 'Ghatotkatcha', 'Hanuman', 'Khumbhakaran', 'Pandu', 'C', 'Medium'),
('Which of the following was formed in S.Miller''s experiment?', 'amino acids', 'lipids', 'UV radiations', 'Nucleic acid', 'A', 'Hard'),
('Which of the following is not a Environmental Burden?', 'Oligotrophication', 'Eutrophication', 'dystrophy', 'None of the above', 'A', 'Medium');
