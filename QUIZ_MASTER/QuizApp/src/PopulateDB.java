import com.quizapp.server.DatabaseManager;
import com.quizapp.shared.Question;

public class PopulateDB {
    public static void main(String[] args) {
        DatabaseManager db = DatabaseManager.getInstance();
        
        System.out.println("Adding questions...");
        
        Question q1 = new Question(0, "In the anime 'Attack on Titan', what is the name of the protagonist?", "Eren Yeager", "Levi Ackerman", "Armin Arlert", "Mikasa Ackerman", "A");
        Question q2 = new Question(0, "Which movie features the quote 'May the Force be with you'?", "Star Trek", "Star Wars", "The Matrix", "Interstellar", "B");
        Question q3 = new Question(0, "In 'Death Note', what is the real name of the detective known as 'L'?", "Light Yagami", "L Lawliet", "Ryuk", "Near", "B");
        Question q4 = new Question(0, "Who directed the movie 'Inception'?", "Steven Spielberg", "Quentin Tarantino", "Christopher Nolan", "Martin Scorsese", "C");
        Question q5 = new Question(0, "In the anime 'Naruto', what is the name of the Nine-Tails fox?", "Shukaku", "Matatabi", "Kurama", "Gyuki", "C");
        Question q6 = new Question(0, "Which anime features a notebook that can kill people?", "Bleach", "One Piece", "Death Note", "Fairy Tail", "C");
        Question q7 = new Question(0, "What is the highest-grossing anime film of all time (as of 2023)?", "Spirited Away", "Demon Slayer: Mugen Train", "Your Name", "Howl's Moving Castle", "B");
        Question q8 = new Question(0, "In 'The Matrix', which pill does Neo take?", "Blue Pill", "Red Pill", "Green Pill", "Yellow Pill", "B");

        db.addQuestion(q1);
        db.addQuestion(q2);
        db.addQuestion(q3);
        db.addQuestion(q4);
        db.addQuestion(q5);
        db.addQuestion(q6);
        db.addQuestion(q7);
        db.addQuestion(q8);
        
        System.out.println("Questions added successfully! Total questions: " + db.getAllQuestions().size());
    }
}
