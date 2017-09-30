package quiz;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.fasterxml.jackson.databind.ser.std.StdArraySerializers.LongArraySerializer;

import javafx.print.PageLayout;
import quizinterfaces.AnswerInterface;
import quizinterfaces.QuestionInterface;
import quizinterfaces.QuizInterface;

public class QuizController implements QuizInterface {

	public static final String DB_PATH = "res/qbot.sqlite";

	/*
	 * (non-Javadoc)
	 * 
	 * @see quizinterfaces.QuizInterface#createGame(long)
	 */
	@Override
	public void createGame(long chatID) {
		Connection connection;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + QuizController.DB_PATH);
			Statement statement = connection.createStatement();
			statement.executeQuery("INSERT INTO game numberaskedquestions, chatid VALUES 0," + chatID);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see quizinterfaces.QuizInterface#addPlayer(int, long)
	 */
	@Override
	public void addPlayer(int playerId, long chatID) {
		Connection connection;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + QuizController.DB_PATH);
			Statement statement = connection.createStatement();
			ResultSet gameSet = statement
					.executeQuery("SELECT gameid, numberaskedquestions FROM game WHERE chatid=" + chatID);
			if (gameSet.next() && gameSet.getInt("numberaskedquestions") == 0)// noch keine Frage gestellt und das Spiel
																				// existiert
			{
				int gameId = gameSet.getInt("gameid");
				// Finde heraus, ob es schon einen Spieler mit PlayerId gibt.
				ResultSet playerSet = statement.executeQuery("SELECT playerid FROM player WHERE playerid=" + playerId);
				if (!playerSet.next()) {
					// Spieler existiert noch nicht->Spieler erzeugen
					statement.executeQuery("INSERT INTO palyer name VALUES " + playerId);
					playerSet = statement.executeQuery("SELECT playerid FROM player WHERE playerid=" + playerId);
					if (!playerSet.next())
						throw new RuntimeException("Eben erzeugt und doch nicht da?");
				}
				int dbPlayerId = playerSet.getInt("playerid");
				// prüfe ob Spieler schon im Spiel vorhanden
				ResultSet playerGameSet = statement.executeQuery(
						"SELECT * FROM playergame WHERE playerid=" + dbPlayerId + " AND gameid=" + gameId);
				if (!playerGameSet.next()) {
					// Füge Spieler zum Spiel hinzu
					statement.execute("INSERT INTO playergame playerid, gameid, score VALUES " + dbPlayerId + ", "
							+ gameId + ", 0");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see quizinterfaces.QuizInterface#startGame(long, int)
	 */
	@Override
	public void startGame(long chatID, int rounds) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see quizinterfaces.QuizInterface#fetchQuestion(long)
	 */
	@Override
	public QuestionInterface fetchQuestion(long chatID) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see quizinterfaces.QuizInterface#enterAnswer(long, int,
	 * quizinterfaces.AnswerInterface)
	 */
	@Override
	public boolean enterAnswer(long chatID, int playerId, AnswerInterface answer) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see quizinterfaces.QuizInterface#retrieveScore(long)
	 */
	@Override
	public List<String> retrieveScore(long chatID) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see quizinterfaces.QuizInterface#endGame(long)
	 */
	@Override
	public void endGame(long chatID) {
		// TODO Auto-generated method stub

	}

	/**
	 * Testmethod for model Classes
	 * 
	 * @param args
	 * @throws SQLException
	 */
	public static void main(String[] args) throws SQLException {
		@SuppressWarnings("unused")
		Question q = new Question(1);
	}

}
