package cody.ecommerce.cody_app.service.impl;

import cody.ecommerce.cody_app.dto.request.chat_bot.ChatbotQueryRequest;
import cody.ecommerce.cody_app.dto.response.chat_bot.ChatbotApiResponse;
import cody.ecommerce.cody_app.entity.sub_entity.ProductImage;
import cody.ecommerce.cody_app.entity.sub_entity.ProductIngredient;
import cody.ecommerce.cody_app.service.ChatbotService;
import cody.ecommerce.cody_app.entity.Product;
import cody.ecommerce.cody_app.entity.Order;
import cody.ecommerce.cody_app.repository.ProductRepository;
import cody.ecommerce.cody_app.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ChatbotServiceImpl implements ChatbotService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    public ChatbotApiResponse query(ChatbotQueryRequest request) {
        ChatbotApiResponse response = new ChatbotApiResponse();
        response.setEntityType(request.getEntityType());
        response.setProductName(request.getProductName());
        response.setOrderId(request.getOrderId());
        response.setInfoType(request.getInfoType());

        String entityType = request.getEntityType();
        String infoType = request.getInfoType();
        String productName = request.getProductName();
        String orderId = request.getOrderId();

        if ("product".equalsIgnoreCase(entityType)) {
            if (productName == null || productName.isBlank()) {
                response.setMessage("Xin lỗi, tôi không tìm thấy sản phẩm này trong hệ thống.");
                response.setResponseType("text");
                return response;
            }
            Product product = productRepository.findByNameContainsIgnoreCase(productName).orElse(null);
            if (product == null) {
                response.setMessage("Xin lỗi, tôi không tìm thấy sản phẩm này trong hệ thống.");
                response.setResponseType("text");
                return response;
            }
            switch (infoType) {
                case "price" -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("price", product.getPrice());
                    response.setData(data);
                    response.setMessage("Giá của " + product.getName() + " là " + product.getPrice() + "đ");
                    response.setResponseType("text");
                }
                case "ingredients" -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("ingredients", product.getProductIngredients().stream().map(ProductIngredient::getIngredient).toList()); // Assuming getIngredients() returns a List<String>
                    response.setData(data);
                    response.setMessage("Thành phần của " + product.getName() + ":");
                    response.setResponseType("list");
                }
                case "image" -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("image", product.getImages().stream().map(ProductImage::getImageUrl).toList()); // Assuming getImageUrl() returns a String
                    data.put("name", product.getName());
                    response.setData(data);
                    response.setMessage("Hình ảnh của " + product.getName() + ":");
                    response.setResponseType("image");
                }
                default -> {
                    response.setMessage("Xin lỗi, tôi chưa hỗ trợ loại thông tin này cho sản phẩm.");
                    response.setResponseType("text");
                }
            }
        } else if ("order".equalsIgnoreCase(entityType)) {
            if (orderId == null || orderId.isBlank()) {
                response.setMessage("Xin lỗi, tôi không tìm thấy đơn hàng này trong hệ thống.");
                response.setResponseType("text");
                return response;
            }
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                response.setMessage("Xin lỗi, tôi không tìm thấy đơn hàng này trong hệ thống.");
                response.setResponseType("text");
                return response;
            }
            switch (infoType) {
                case "status" -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("status", order.getMainStatus()); // Assuming getStatus() returns a String or Enum
                    response.setData(data);
                    response.setMessage("Trạng thái đơn hàng " + order.getId() + ": " + order.getMainStatus());
                    response.setResponseType("text");
                }
                case "total" -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("total", order.getTotalPrice()); // Assuming getTotal() returns a number
                    response.setData(data);
                    response.setMessage("Tổng tiền đơn hàng " + order.getId() + ": " + order.getTotalPrice() + "đ");
                    response.setResponseType("text");
                }
                default -> {
                    response.setMessage("Xin lỗi, tôi chưa hỗ trợ loại thông tin này cho đơn hàng.");
                    response.setResponseType("text");
                }
            }
        } else {
            response.setMessage("Xin lỗi, tôi chưa hiểu câu hỏi của bạn. Bạn có thể hỏi về sản phẩm (giá, thành phần, tồn kho) hoặc đơn hàng (trạng thái, tổng tiền, ngày giao hàng).");
            response.setResponseType("text");
        }
        return response;
    }
}
