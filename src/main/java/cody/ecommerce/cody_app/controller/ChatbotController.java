package cody.ecommerce.cody_app.controller;

import cody.ecommerce.cody_app.dto.ResponseData;
import cody.ecommerce.cody_app.dto.request.chat_bot.ChatRequest;
import cody.ecommerce.cody_app.dto.response.chat_bot.ChatbotResponse;
import cody.ecommerce.cody_app.service.AdvancedVietnameseChatBotService;
import cody.ecommerce.cody_app.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/chatbot")
public class ChatbotController {
    private final AdvancedVietnameseChatBotService advancedVietnameseChatBotService;

    @PostMapping("/")
    public ResponseEntity<ResponseData<ChatbotResponse>> response(@RequestBody(required = false) ChatRequest request){
        return ResponseUtil.getResponse(() -> advancedVietnameseChatBotService.processMessage(request.getMessage()), "Lấy phản hồi từ chatbot thành công");
    }
}
