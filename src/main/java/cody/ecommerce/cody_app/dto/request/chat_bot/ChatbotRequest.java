package cody.ecommerce.cody_app.dto.request.chat_bot;

public class ChatbotRequest {
    public String productName;
    public String orderId;
    public String infoType;
    public String originalMessage;

    public ChatbotRequest(String productName, String orderId, String infoType, String originalMessage) {
        this.productName = productName;
        this.orderId = orderId;
        this.infoType = infoType;
        this.originalMessage = originalMessage;
    }
}