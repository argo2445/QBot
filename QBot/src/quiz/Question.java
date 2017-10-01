package quiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import quizinterfaces.AnswerInterface;
import quizinterfaces.QuestionInterface;

public class Question implements QuestionInterface {

	private int dataBaseId;
	private String questionText;
	private boolean isTrue;
	private List<Answer> answers;
	private Answer rightAnswer;
	private Category category;
	private int answered;
	private int rightAnswered;

	private static List<Category> categories;

	/**
	 * @param questionDatabaseID
	 *            QuestionId used in Database
	 * @throws SQLException
	 */
	public Question(int questionDatabaseID) {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + QuizController.DB_PATH);

			if (categories == null) {
				readCategories(connection);
			}
			// categories exist
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(10);
			ResultSet resultSet = statement.executeQuery("SELECT "
					+ "questionid, questiontext, istrue, askedquestions, rightansweredquestions, categoryid, rightanswerid "
					+ "FROM question WHERE questionid=" + questionDatabaseID);

			if (resultSet.next()) {
				this.questionText = resultSet.getString("questiontext");
				this.isTrue = resultSet.getBoolean("istrue");
				this.rightAnswered = resultSet.getInt("rightansweredquestions");
				this.answered = resultSet.getInt("askedquestions");
				this.dataBaseId = resultSet.getInt("questionid");
				this.answers = readAnswers(dataBaseId, connection);
				if (this.answers.size() > 0) {
					int rightAnswerId = resultSet.getInt("rightanswerid");
					if (rightAnswerId == 0) {
						// Entscheidungsfrage
						if (isTrue) {
							rightAnswer = answers.get(0);
						} else {
							rightAnswer = answers.get(1);
						}
					} else {
						// keine Entscheidungsfrage
						Optional<Answer> optRightAnswer = answers.stream()
								.filter(aw -> aw.getDatabaseId() == rightAnswerId).findFirst();
						if (optRightAnswer.isPresent())
							this.rightAnswer = optRightAnswer.get();
						else {
							throw new IllegalArgumentException(
									"Die korrekte Antwort für " + this.dataBaseId + " konnte nicht gefunden werden.");
						}
					}
				}
				int categoryKey = resultSet.getInt("categoryid");
				Optional<Category> categ = categories.stream().filter(cat -> cat.getDatabaseID() == categoryKey)
						.findFirst();
				if (categ.isPresent()) {
					this.category = categ.get();
				} else {
					throw new IllegalArgumentException(
							"Die Kategorie mit dem Schlüssel " + categoryKey + " existiert nicht.");
				}

			}
			connection.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	private List<Answer> readAnswers(int questionDataBaseId, Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(10);
		List<Answer> answers = new ArrayList<Answer>();

		ResultSet answerSet = statement
				.executeQuery("SELECT answerid, answertext  " + "FROM answer WHERE questionid=" + questionDataBaseId);
		while (answerSet.next()) {
			int awId = answerSet.getInt("answerid");
			String awText = answerSet.getString("answertext");
			answers.add(new Answer(this,awId, awText));

		}
		if (answers.size() == 0) {
			answers.add(new Answer(this,-1, "Richtig"));
			answers.add(new Answer(this,-2, "Falsch"));
		}
		return answers;
	}

	/**
	 * @return the dataBaseId
	 */
	public int getDataBaseId() {
		return dataBaseId;
	}

	/**
	 * @return the answers
	 */
	public List<Answer> getAnswers() {
		return answers;
	}

	/**
	 * @return the category
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * @return the answered
	 */
	public int getAnswered() {
		return answered;
	}

	/**
	 * @return the rightAnswered
	 */
	public int getRightAnswered() {
		return rightAnswered;
	}

	/**
	 * @return the categories
	 */
	public static List<Category> getCategories() {
		return categories;
	}

	/**
	 * @param connection
	 * @throws SQLException
	 */
	private void readCategories(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(10);
		categories = new ArrayList<>();
		ResultSet rs = statement.executeQuery("SELECT categoryid, description FROM category");
		while (rs.next()) {
			int categorieId = rs.getInt("categoryid");
			String description = rs.getString("description");
			// Check if category already exists
			if (categories.stream().filter(elem -> elem.getDatabaseID() == categorieId).count() == 0) {
				categories.add(new Category(categorieId, description));
			}
		}
	}

	@Override
	public String getQuestionText() {
		return questionText;
	}

	@Override
	public boolean isTrue() {
		return isTrue;
	}

	@Override
	public AnswerInterface getRightAnswer() {
		return rightAnswer;
	}

	public boolean IsFact() {
		return answers == null || answers.size() == 0;
	}

}
