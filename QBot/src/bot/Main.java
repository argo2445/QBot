package bot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import quiz.QuizController;

public class Main {

	public static void main(String[] args) {

        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new DeprecatedQuizBot(new QuizController()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
