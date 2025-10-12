package cody.ecommerce.cody_app.service;

import cody.ecommerce.cody_app.dto.request.chat_bot.ChatbotQueryRequest;
import cody.ecommerce.cody_app.dto.response.chat_bot.ChatbotApiResponse;

public interface ChatbotService {
    ChatbotApiResponse query(ChatbotQueryRequest request);
}

