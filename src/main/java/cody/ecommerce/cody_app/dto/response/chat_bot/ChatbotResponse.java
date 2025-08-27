package cody.ecommerce.cody_app.dto.response.chat_bot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ChatbotResponse {
    private String response;
    private LocalDateTime timestamp;

    public ChatbotResponse(String response) {
        this.response = response;
        this.timestamp = LocalDateTime.now();
    }
}
