package bot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.telegram.telegrambots.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import quizinterfaces.AnswerInterface;
import quizinterfaces.QuestionInterface;
import quizinterfaces.QuizInterface;

public class QuizBot extends TelegramLongPollingBot {

	private QuizInterface quizInterface;

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
			}else if(update.hasCallbackQuery()) {
				answerQuestion(update);
			}
		}

	}

	private void answerQuestion(Update update) {
		quizInterface.addPlayer(update.getCallbackQuery().getFrom().getId(), update.getCallbackQuery().getMessage().getChatId());
		quizInterface.enterAnswer(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getFrom().getId(), Integer.parseInt(update.getCallbackQuery().getData()));
	}

	private void startQuiz(Update update) {
		String msg = update.getMessage().getText();
		String rounds = msg.substring(msg.indexOf(' ') + 1);
		rounds = rounds.substring(0, rounds.indexOf(' '));
		int roundsInt = 10;
		try {
			roundsInt = Integer.parseInt(rounds);
		} catch (NumberFormatException ex) {

		}
		long chatId = update.getMessage().getChatId();
		quizInterface.createGame(chatId);
		quizInterface.addPlayer(update.getMessage().getFrom().getId(), chatId, update.getMessage().getFrom().getUserName());

		showNewQuestion(chatId);
	}

	private void showNewQuestion(long chatId) {
		QuestionInterface question = quizInterface.fetchQuestion(chatId);
		if(question==null)
		{
			List<String> score= quizInterface.retrieveScore(chatId);
			
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
			AnswerInterface aIf=question.getAnswers().get(i);
			aList.add(new InlineKeyboardButton(aIf.getAnswerText()).setCallbackData(""+aIf.getDatabaseId()));
		}
		try {
			execute(questionMessage);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
		TimerTask tt=new TimerTask() {
			
			@Override
			public void run() {
				showNewQuestion(chatId);
				
			}
		};
		new Timer().schedule(tt,2000L);
	}

}
