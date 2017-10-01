package bot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import quiz.QuizController;
import quizinterfaces.AnswerInterface;
import quizinterfaces.QuestionInterface;
import quizinterfaces.QuizInterface;

public class QuizBot extends TelegramLongPollingBot {

	private QuizInterface quizInterface;

	public QuizBot(QuizController quizController) {
		quizInterface = quizController;
	}

	@Override
	public String getBotUsername() {
		return "QuizBot";
	}

	@Override
	public String getBotToken() {
		return "479671819:AAGfKq2wfUjWwcOXjeSbTSj6Pd2C1-DucbY";
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage()) {
			if (update.getMessage().getText().startsWith("/start")) {
				startQuiz(update);
			} else if (update.getMessage().getText().startsWith("/stop")) {
				quizInterface.endGame(update.getMessage().getChatId());
			}
		} else if (update.hasCallbackQuery()) {
			answerQuestion(update);
		}

	}

	HashMap<Integer, Integer> userAnswers;

	private void answerQuestion(Update update) {
		quizInterface.addPlayer(update.getCallbackQuery().getFrom().getId(),
				update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getFrom().getUserName());
		int userId = update.getCallbackQuery().getFrom().getId();
		String query = update.getCallbackQuery().getData();
		String questionIdS = query.substring(0, query.indexOf('_'));
		String answerIdS = query.substring(query.indexOf('_') + 1);
		int questionId = Integer.parseInt(questionIdS);
		int answerId = Integer.parseInt(answerIdS);
		if (userAnswers == null) {
			userAnswers = new HashMap<>();
		}
		if (!userAnswers.containsKey(userId) || userAnswers.get(userId) != questionId) {
			quizInterface.enterAnswer(update.getCallbackQuery().getMessage().getChatId(),
					update.getCallbackQuery().getFrom().getId(), answerId);
			userAnswers.put(userId, questionId);
		}

	}

	private void startQuiz(Update update) {
		String msg = update.getMessage().getText();
		String rounds = "";
		try {
			rounds = msg.substring(msg.indexOf(' ') + 1);
			if (rounds.contains(" "))
				;
			rounds = rounds.substring(0, rounds.indexOf(' '));
		} catch (Exception e) {

		}
		int roundsInt = 10;
		try {
			roundsInt = Integer.parseInt(rounds);
		} catch (NumberFormatException ex) {

		}
		long chatId = update.getMessage().getChatId();
		quizInterface.createGame(chatId);
		quizInterface.addPlayer(update.getMessage().getFrom().getId(), chatId,
				update.getMessage().getFrom().getUserName());
		quizInterface.startGame(chatId, roundsInt);
		showNewQuestion(chatId);
	}

	private void showNewQuestion(long chatId) {
		QuestionInterface question = quizInterface.fetchQuestion(chatId);
		if (question == null) {
			List<String> score = quizInterface.retrieveScore(chatId);
			String output = "Die Runde wurde beendet. Spielstand: \n";
			for (String string : score) {
				output += string + "\n";
			}
			SendMessage msg = new SendMessage(chatId, output);
			try {
				execute(msg);
			} catch (TelegramApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		SendMessage questionMessage = new SendMessage(chatId, question.getQuestionText());
		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		questionMessage.setReplyMarkup(inlineKeyboardMarkup);
		List<List<InlineKeyboardButton>> bList = new ArrayList<>();
		inlineKeyboardMarkup.setKeyboard(bList);
		List<InlineKeyboardButton> aList = null;
		for (int i = 0; i < question.getAnswers().size(); i++) {
			if (i % 2 == 0) {
				aList = new ArrayList<>();
				bList.add(aList);
			}
			AnswerInterface aIf = question.getAnswers().get(i);
			if (aIf.getDatabaseId() > 0)
				aList.add(new InlineKeyboardButton(aIf.getAnswerText())
						.setCallbackData("" + question.getDataBaseId() + "_" + aIf.getDatabaseId()));
			else {
				if (question.isTrue() && i == 0) {
					aList.add(new InlineKeyboardButton(aIf.getAnswerText())
							.setCallbackData("" + question.getDataBaseId() + "_" + -1));
				} else if (question.isTrue() && i == 1) {
					aList.add(new InlineKeyboardButton(aIf.getAnswerText())
							.setCallbackData("" + question.getDataBaseId() + "_" + 0));
				} else if (!question.isTrue() && i == 0) {
					aList.add(new InlineKeyboardButton(aIf.getAnswerText())
							.setCallbackData("" + question.getDataBaseId() + "_" + 0));
				} else {
					aList.add(new InlineKeyboardButton(aIf.getAnswerText())
							.setCallbackData("" + question.getDataBaseId() + "_" + -1));
				}
			}
		}
		int bs = 0; // ab hier hässlich
		try {
			bs = execute(questionMessage).getMessageId();
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}

		EditMessageText new_message = new EditMessageText().setChatId(chatId).setMessageId(bs)
				.setText(question.getQuestionText());

		TimerTask tt = new TimerTask() {

			@Override
			public void run() {
				try {
					execute(new_message);
				} catch (TelegramApiException e) {
					e.printStackTrace();
				} // ende hässlich
				showNewQuestion(chatId);

			}
		};
		new Timer().schedule(tt, 10000L);
	}

}
