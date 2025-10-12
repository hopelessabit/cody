package cody.ecommerce.cody_app.dto.request.chat_bot;

import lombok.Data;

@Data
public class ChatbotQueryRequest {
    private String entityType;
    private String productName;
    private String orderId;
    private String infoType;
}

