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

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
        // Decode product name from URL encoding if present
        String decodedProductName = request.getProductName() != null ? URLDecoder.decode(request.getProductName(), StandardCharsets.UTF_8) : null;
        response.setProductName(decodedProductName);
        response.setOrderId(request.getOrderId());
        response.setInfoType(request.getInfoType());

        String entityType = request.getEntityType();
        String infoType = request.getInfoType();
        String productName = decodedProductName;
        String orderId = request.getOrderId();

        if ("product".equalsIgnoreCase(entityType)) {
            if (productName == null || productName.isBlank()) {
                response.setMessage("Xin lỗi, tôi không tìm thấy sản phẩm này trong hệ thống.");
                response.setResponseType("text");
                return response;
            }
            List<Product> products = productRepository.findAllByNameContainsIgnoreCase(productName);
            if (products == null || products.isEmpty()) {
                response.setMessage("Xin lỗi, tôi không tìm thấy sản phẩm nào phù hợp.");
                response.setResponseType("text");
                return response;
            }
            // If only one product, show details as before
            if (products.size() == 1) {
                Product product = products.get(0);
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
                        List<String> ingredients = product.getProductIngredients().stream()
                            .map(pi -> pi.getIngredient().getName())
                            .filter(name -> name != null && !name.isBlank())
                            .toList();
                        data.put("ingredients", ingredients);
                        if (ingredients.isEmpty()) {
                            response.setMessage("Sản phẩm " + product.getName() + " không có thông tin về thành phần.");
                        } else {
                            String formatted = String.join(", ", ingredients);
                            response.setMessage("Thành phần của " + product.getName() + ": " + formatted);
                        }
                        response.setData(data);
                        response.setResponseType("list");
                    }
                    case "image" -> {
                        Map<String, Object> data = new HashMap<>();
                        data.put("image", product.getImages().stream().map(ProductImage::getImageUrl).toList());
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
            } else {
                // Multiple products found, group by infoType
                Map<String, Object> data = new HashMap<>();
                List<Map<String, Object>> infoList = products.stream().map(product -> {
                    Map<String, Object> summary = new HashMap<>();
                    summary.put("id", product.getId());
                    summary.put("name", product.getName());
                    summary.put("price", product.getPrice());
                    summary.put("stockQuantity", product.getStockQuantity());
                    summary.put("image", product.getImages() != null && !product.getImages().isEmpty() ? product.getImages().get(0).getImageUrl() : null);
                    switch (infoType) {
                        case "price" -> summary.put("message", "Giá của " + product.getName() + " là " + product.getPrice() + "đ");
                        case "ingredients" -> {
                            List<String> ingredients = product.getProductIngredients().stream()
                                .map(pi -> pi.getIngredient().getName())
                                .filter(name -> name != null && !name.isBlank())
                                .toList();
                            if (ingredients.isEmpty()) {
                                summary.put("message", "Sản phẩm " + product.getName() + " không có thông tin về thành phần.");
                            } else {
                                String formatted = String.join(", ", ingredients);
                                summary.put("message", "Thành phần của " + product.getName() + ": " + formatted);
                            }
                        }
                        case "image" -> summary.put("message", "Hình ảnh của " + product.getName() + ":");
                        default -> summary.put("message", "Thông tin về " + product.getName());
                    }
                    return summary;
                }).toList();
                data.put(infoType, infoList);
                response.setData(data);
                response.setMessage("Tìm thấy " + products.size() + " sản phẩm phù hợp với từ khóa: '" + productName + "'.");
                response.setResponseType("list");
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
                    data.put("status", order.getMainStatus());
                    response.setData(data);
                    response.setMessage("Trạng thái đơn hàng " + order.getId() + ": " + order.getMainStatus());
                    response.setResponseType("text");
                }
                case "total" -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("total", order.getTotalPrice());
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
