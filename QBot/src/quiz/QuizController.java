package quiz;

import java.sql.SQLException;
import java.util.List;

import quizinterfaces.AnswerInterface;
import quizinterfaces.QuestionInterface;
import quizinterfaces.QuizInterface;

public class QuizController implements QuizInterface {

	@Override
	public int createGame() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addPlayer(String name, int gameID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startGame(int gameID, int rounds) {
		// TODO Auto-generated method stub

	}

	@Override
	public QuestionInterface fetchQuestion(int gameID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean enterAnswer(int gameID, String name, AnswerInterface answer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> retrieveScore(int gameID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void endGame(int gameID) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Testmethod for model Classes
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		@SuppressWarnings("unused")
		Question q=new Question(1);
	}

}
