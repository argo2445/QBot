package quiz;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
	public void addPlayer(int playerId, long chatID, String UserName) {
		Connection connection;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + QuizController.DB_PATH);
			Statement statement = connection.createStatement();
			ResultSet gameSet = statement.executeQuery("SELECT gameid, numberaskedquestions FROM game "
					+ "WHERE chatid=" + chatID + " AND gameid=(SELECT MAX(gameid) FROM game)");
			if (gameSet.next())// das Spiel
								// existiert
			{
				int gameId = gameSet.getInt("gameid");
				// Finde heraus, ob es schon einen Spieler mit PlayerId gibt.
				ResultSet playerSet = statement.executeQuery("SELECT playerid FROM player WHERE playerid=" + playerId);
				if (!playerSet.next()) {
					// Spieler existiert noch nicht->Spieler erzeugen
					statement.executeQuery("INSERT INTO player name VALUES " + playerId);
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
		// Rufe die letzte dbGameId zu chatID ab
		// Wähle bis zu rounds zufällige Zeilen aus questions
		// füge questiongame (questionid,gameid) für alle Fragen hinzus
		Connection connection;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + QuizController.DB_PATH);
			Statement statement = connection.createStatement();
			int dbGameId;
			ResultSet gameSet = statement.executeQuery("SELECT gameid, numberaskedquestions FROM game "
					+ "WHERE chatid=" + chatID + " AND gameid=(SELECT MAX(gameid) FROM game)");
			if (gameSet.next()) {
				dbGameId = gameSet.getInt("gameid");
				if (gameSet.getInt("numberaskedquestions") != 0) {
					throw new RuntimeException("Das Spiel " + dbGameId + " läuft bereits.");
				}
			} else {
				throw new RuntimeException("Zur ChatId " + chatID + " existiert kein Spiel.");
			}
			if (rounds <= 0)
				return;
			// select ROUNDS random Questions
			ResultSet questionSet = statement.executeQuery(
					"SELECT questionid FROM question WHERE questionid IN (SELECT questionid FROM question ORDER BY RANDOM() LIMIT "
							+ rounds + ")");
			while (questionSet.next()) {
				int qId = questionSet.getInt("questionid");
				// Add Question Game Pair
				statement.execute("INSERT INTO questiongame questionid, gameid VALUES " + qId + ", " + dbGameId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see quizinterfaces.QuizInterface#fetchQuestion(long)
	 */
	@Override
	public QuestionInterface fetchQuestion(long chatID) {
		Connection connection;
		try {
			// lade numberaskedquestions und gameid von dem letzten Spiel mit der chatId
			// lade questiongame von dem Spiel
			// gehe bis zu dem aktuellen numberaskedquestions-Wert und lade die
			// entsprechende Frage
			// inkrementiere numberaskedquestions
			// gib die Frage zurück
			connection = DriverManager.getConnection("jdbc:sqlite:" + QuizController.DB_PATH);
			Statement statement = connection.createStatement();
			int dbGameId, askedQuestions;
			ResultSet gameSet = statement.executeQuery("SELECT gameid, numberaskedquestions FROM game "
					+ "WHERE chatid=" + chatID + " AND gameid=(SELECT MAX(gameid) FROM game)");
			if (gameSet.next()) {
				dbGameId = gameSet.getInt("gameid");
				askedQuestions = gameSet.getInt("numberaskedquestions");

			} else {
				throw new RuntimeException("Es existiert kein Spiel mit der chatId: " + chatID);
			}

			ResultSet questionGameSet = statement
					.executeQuery("SELECT questionid,gameid FROM questiongame WHERE gameid=" + dbGameId);
			if (!questionGameSet.absolute(askedQuestions + 1))
				return null;
			int questionId = questionGameSet.getInt("questionid");
			Question question = new Question(questionId);
			statement.execute(
					"UPDATE game SET numberaskedquestions=" + (askedQuestions + 1) + " WHERE gameid=" + dbGameId);
			return question;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see quizinterfaces.QuizInterface#enterAnswer(long, int,
	 * quizinterfaces.AnswerInterface)
	 */
	@Override
	public boolean enterAnswer(long chatID, int playerId, int answerDbId) {
		// suche den Spieler mit dem Namen playerId und rufe dessen dbPlayerId ab
		// Wenn gewusst, addiere richtige frage
		// Addiere eine Frage zum Spieler
		// wenn gewusst: lade das letzte Spiel mit der ChatId
		// inkrementiere den Score
		boolean correct = false;
		// Answer castedAnswer = (Answer) answer;
		// if
		// (castedAnswer.getQuestion().getRightAnswer().getAnswerText().equals(answer.getAnswerText()))
		// {
		// correct = true;
		// }
		Connection connection;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + QuizController.DB_PATH);
			Statement statement = connection.createStatement();

			// Check if answerDbId is ID of correct Answer
			ResultSet rightAnswerSet = statement.executeQuery(
					"SELECT rightanswerid FROM question INNER JOIN answer ON question.questionid=answer.questionid");
			if (rightAnswerSet.next()) {
				int rightanswerId = rightAnswerSet.getInt("rightanswerid");
				if (rightanswerId == answerDbId)
					correct = true;
			}
			ResultSet playerSet = statement.executeQuery("SELECT playerId, answeredquestions, rightansweredquestions "
					+ "FROM player WHERE name=" + playerId);
			int dbPlayerId, rightanswered, answered, dbGameId;
			if (playerSet.next()) {
				dbPlayerId = playerSet.getInt("playerid");
				rightanswered = playerSet.getInt("rightansweredquestions");
				answered = playerSet.getInt("answeredquestions");
			} else {
				throw new RuntimeException("Spieler mit dem Namen " + playerId + " existiert nicht.");
			}
			if (correct) {
				// update Player
				statement.execute("UPDATE player SET answeredquestions=" + (answered + 1) + ",rightansweredquestions="
						+ (rightanswered + 1) + "  WHERE playerid=" + dbPlayerId);

				// Update Score
				ResultSet gameSet = statement.executeQuery("SELECT gameid, numberaskedquestions FROM game "
						+ "WHERE chatid=" + chatID + " AND gameid=(SELECT MAX(gameid) FROM game)");
				if (gameSet.next()) {
					dbGameId = gameSet.getInt("gameid");
					statement.execute("UPDATE playergame SET score=(score+1) WHERE gameid= " + dbGameId
							+ " AND playerid= " + dbPlayerId);
				} else {
					throw new RuntimeException(
							"Das gesuchte Spiel mit der chatId " + chatID + "konnte nicht gefunden werden.");
				}

			} else {
				// update Player
				statement.execute(
						"UPDATE player SET answeredquestions=" + (answered + 1) + "  WHERE playerid=" + dbPlayerId);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return correct;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see quizinterfaces.QuizInterface#retrieveScore(long)
	 */
	@Override
	public List<String> retrieveScore(long chatID) {
		List<String> scores = new ArrayList<>();
		Connection connection;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + QuizController.DB_PATH);
			Statement statement = connection.createStatement();
			int dbGameId = 0;
			ResultSet gameSet = statement.executeQuery("SELECT gameid, numberaskedquestions FROM game "
					+ "WHERE chatid=" + chatID + " AND gameid=(SELECT MAX(gameid) FROM game)");
			if (gameSet.next()) {
				dbGameId = gameSet.getInt("gameid");
			}
			ResultSet scoreSet = statement
					.executeQuery("SELECT score , player.playerid, name " + "FROM playergame INNER JOIN player "
							+ "ON player.playerid=playergame.playerid " + "WHERE playergame.gameid=" + dbGameId);
			while (scoreSet.next()) {
				String playerName = scoreSet.getString("name");
				int playerScore = scoreSet.getInt("score");
				scores.add(playerName + ": " + playerScore);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return scores;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see quizinterfaces.QuizInterface#endGame(long)
	 */
	@Override
	public void endGame(long chatID) {
		Connection connection;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + QuizController.DB_PATH);
			Statement statement = connection.createStatement();
			int dbGameId = 0;
			ResultSet gameSet = statement.executeQuery("SELECT gameid, numberaskedquestions FROM game "
					+ "WHERE chatid=" + chatID + " AND gameid=(SELECT MAX(gameid) FROM game)");
			if (gameSet.next()) {
				dbGameId = gameSet.getInt("gameid");
				statement.execute("UPDATE game SET numberaskedquestions=" + (Integer.MAX_VALUE - 1) + " WHERE gameid="
						+ dbGameId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// /**
	// * Testmethod for model Classes
	// *
	// * @param args
	// * @throws SQLException
	// */
	// public static void main(String[] args) throws SQLException {
	// @SuppressWarnings("unused")
	// Question q = new Question(1);
	// }

}
