package cody.ecommerce.cody_app.controller;

import cody.ecommerce.cody_app.dto.ResponseData;
import cody.ecommerce.cody_app.dto.request.chat_bot.ChatRequest;
import cody.ecommerce.cody_app.dto.request.chat_bot.ChatbotQueryRequest;
import cody.ecommerce.cody_app.dto.response.chat_bot.ChatbotApiResponse;
import cody.ecommerce.cody_app.dto.response.chat_bot.ChatbotResponse;
import cody.ecommerce.cody_app.service.AdvancedVietnameseChatBotService;
import cody.ecommerce.cody_app.service.ChatbotService;
import cody.ecommerce.cody_app.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ChatbotController {
    private final AdvancedVietnameseChatBotService advancedVietnameseChatBotService;
    private final ChatbotService chatbotService;

    @PostMapping("/v1/chatbot/v2/")
    public ResponseEntity<ResponseData<ChatbotResponse>> response(@RequestBody(required = false) ChatRequest request){
        return ResponseUtil.getResponse(() -> advancedVietnameseChatBotService.processMessage(request.getMessage()), "Lấy phản hồi từ chatbot thành công");
    }

    @GetMapping("/v1/chatbot")
    public ResponseEntity<ChatbotApiResponse> queryChatbot(
            @RequestParam(name = "entity_type") String entityType,
            @RequestParam(name = "product_name", required = false) String productName,
            @RequestParam(name = "order_id", required = false) String orderId,
            @RequestParam(name = "info_type") String infoType
    ) {
        ChatbotQueryRequest request = new ChatbotQueryRequest();
        request.setEntityType(entityType);
        request.setProductName(productName);
        request.setOrderId(orderId);
        request.setInfoType(infoType);
        ChatbotApiResponse response = chatbotService.query(request);
        return ResponseEntity.ok(response);
    }
}
