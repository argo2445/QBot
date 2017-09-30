package bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class QuizBot extends TelegramLongPollingBot {
	
	@SuppressWarnings("deprecation")
	@Override
    public void onUpdateReceived(Update update) {
		if (update.hasMessage()){		
			SendMessage message = new SendMessage()
					.setChatId(update.getMessage().getChatId())
					.setText("Es tut mir Leid diese Nachricht habe ich nicht verstanden.");
			try {
				sendMessage(message);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}
    }

    @Override
    public String getBotUsername() {
        return "QuizBot";
    }

    @Override
    public String getBotToken() {
        return "479671819:AAGfKq2wfUjWwcOXjeSbTSj6Pd2C1-DucbY";
    }
    
}
