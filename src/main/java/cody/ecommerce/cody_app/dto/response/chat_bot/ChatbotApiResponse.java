package cody.ecommerce.cody_app.dto.response.chat_bot;

import lombok.Data;

@Data
public class ChatbotApiResponse {
    private String entityType;
    private String productName;
    private String orderId;
    private String infoType;
    private Object data;
    private String message;
    private String responseType;
}

