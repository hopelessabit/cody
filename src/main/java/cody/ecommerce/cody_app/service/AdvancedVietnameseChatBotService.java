package cody.ecommerce.cody_app.service;

import cody.ecommerce.cody_app.dto.response.chat_bot.ChatbotResponse;
import cody.ecommerce.cody_app.entity.Product;
import cody.ecommerce.cody_app.repository.ProductRepository;
import cody.ecommerce.cody_app.search.ProductSearchContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvancedVietnameseChatBotService {

    private final ProductRepository productRepository;
    private final AdvancedVietnameseNLPService nlpService;

    public ChatbotResponse processMessage(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return new ChatbotResponse("Xin chào! Tôi có thể giúp bạn tìm sản phẩm. Hãy mô tả chi tiết sản phẩm bạn muốn!");
        }

        // Analyze the full context of the query
        ProductSearchContext context = nlpService.analyzeQuery(userMessage);

        Set<Product> results = new HashSet<>();

        // Search by categories (highest priority)
        for (String category : context.getCategories()) {
            results.addAll(productRepository.findByCategory(category));
        }

        // Search by all keywords
        for (String keyword : context.getKeywords()) {
            if (keyword.length() >= 2) {
                results.addAll(productRepository.searchByKeyword(keyword));
            }
        }

        // Search by nouns (product names)
        for (String noun : context.getNouns()) {
            if (noun.length() >= 2) {
                results.addAll(productRepository.searchByKeyword(noun));
            }
        }

        // Apply price filtering
        results = filterByPriceRange(results, context.getPriceRange());

        // Apply attribute filtering
        results = filterByAttributes(results, context.getAttributes());

        List<Product> finalResults = new ArrayList<>(results);
        return new ChatbotResponse(generateContextualResponse(finalResults, context));
    }

    private Set<Product> filterByPriceRange(Set<Product> products, String priceRange) {
        if (priceRange == null || products.isEmpty()) {
            return products;
        }

        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;

        switch (priceRange) {
            case "cheap":
                maxPrice = new BigDecimal("50000");
                break;
            case "medium":
                minPrice = new BigDecimal("50000");
                maxPrice = new BigDecimal("200000");
                break;
            case "expensive":
                minPrice = new BigDecimal("200000");
                break;
        }

        if (minPrice != null || maxPrice != null) {
            BigDecimal min = minPrice != null ? minPrice : BigDecimal.ZERO;
            BigDecimal max = maxPrice != null ? maxPrice : new BigDecimal("999999999");

            return products.stream()
                    .filter(p -> p.getPrice().compareTo(min) >= 0 && p.getPrice().compareTo(max) <= 0)
                    .collect(Collectors.toSet());
        }

        return products;
    }

    private Set<Product> filterByAttributes(Set<Product> products, List<String> attributes) {
        if (attributes.isEmpty()) {
            return products;
        }

        return products.stream()
                .filter(product -> {
                    String productText = (product.getName() + " " +
                            (product.getDescription() != null ? product.getDescription() : "")).toLowerCase();

                    return attributes.stream().anyMatch(attr ->
                            productText.contains(nlpService.normalizeText(attr)));
                })
                .collect(Collectors.toSet());
    }

    private String generateContextualResponse(List<Product> products, ProductSearchContext context) {
        if (products.isEmpty()) {
            return generateNoResultsResponse(context);
        }

        // Sort by relevance
        products.sort((p1, p2) -> calculateRelevance(p2, context) - calculateRelevance(p1, context));

        String intent = context.getIntent();

        switch (intent) {
            case "PRICE_INQUIRY":
                return generatePriceResponse(products, context);
            case "RECOMMENDATION":
                return generateRecommendationResponse(products, context);
            case "BUY":
                return generateBuyResponse(products, context);
            default:
                return generateSearchResponse(products, context);
        }
    }

    private String generateNoResultsResponse(ProductSearchContext context) {
        StringBuilder response = new StringBuilder("Xin lỗi, không tìm thấy sản phẩm phù hợp với yêu cầu: ");

        if (!context.getCategories().isEmpty()) {
            response.append("\n🏷️ Loại: ").append(String.join(", ", context.getCategories()));
        }
        if (!context.getAttributes().isEmpty()) {
            response.append("\n✨ Đặc điểm: ").append(String.join(", ", context.getAttributes()));
        }
        if (context.getPriceRange() != null) {
            response.append("\n💰 Mức giá: ").append(getPriceRangeText(context.getPriceRange()));
        }

        response.append("\n\nThử tìm: bánh ngọt, đồ uống, pizza, hoặc mô tả chi tiết hơn!");
        return response.toString();
    }

    private String generateSearchResponse(List<Product> products, ProductSearchContext context) {
        if (products.size() == 1) {
            Product p = products.get(0);
            StringBuilder response = new StringBuilder();
            response.append("🍰 **").append(p.getName()).append("**\n");
            response.append("💰 Giá: **").append(String.format("%,.0f", p.getPrice())).append(" VNĐ**\n");
            response.append("📦 Còn lại: ").append(p.getStockQuantity()).append(" sản phẩm\n");
            if (p.getDescription() != null) {
                response.append("\n📝 ").append(p.getDescription());
            }
            return response.toString();
        }

        StringBuilder response = new StringBuilder("🔍 Tìm thấy **" + products.size() + " sản phẩm** phù hợp");

        if (!context.getCategories().isEmpty()) {
            response.append(" cho ").append(String.join(", ", context.getCategories()));
        }

        response.append(":\n\n");

        for (int i = 0; i < Math.min(6, products.size()); i++) {
            Product p = products.get(i);
            response.append(String.format("%d. **%s** - %,.0f VNĐ\n",
                    i + 1, p.getName(), p.getPrice()));
        }

        if (products.size() > 6) {
            response.append("\n*...và ").append(products.size() - 6).append(" sản phẩm khác*");
        }

        return response.toString();
    }

    private String generatePriceResponse(List<Product> products, ProductSearchContext context) {
        if (products.isEmpty()) return generateNoResultsResponse(context);

        Product cheapest = products.stream().min(Comparator.comparing(Product::getPrice)).get();
        Product mostExpensive = products.stream().max(Comparator.comparing(Product::getPrice)).get();

        StringBuilder response = new StringBuilder("💰 **Thông tin giá:**\n\n");
        response.append("📉 Rẻ nhất: **").append(cheapest.getName())
                .append("** - ").append(String.format("%,.0f", cheapest.getPrice())).append(" VNĐ\n");
        response.append("📈 Đắt nhất: **").append(mostExpensive.getName())
                .append("** - ").append(String.format("%,.0f", mostExpensive.getPrice())).append(" VNĐ");

        return response.toString();
    }

    private String generateRecommendationResponse(List<Product> products, ProductSearchContext context) {
        products = products.stream().limit(3).collect(Collectors.toList());

        StringBuilder response = new StringBuilder("✨ **Gợi ý cho bạn:**\n\n");

        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            response.append("🌟 **").append(p.getName()).append("**\n");
            response.append("   💰 ").append(String.format("%,.0f", p.getPrice())).append(" VNĐ\n");
            if (p.getDescription() != null && p.getDescription().length() > 0) {
                response.append("   📝 ").append(p.getDescription().substring(0,
                        Math.min(50, p.getDescription().length()))).append("...\n");
            }
            response.append("\n");
        }

        return response.toString();
    }

    private String generateBuyResponse(List<Product> products, ProductSearchContext context) {
        if (products.isEmpty()) return generateNoResultsResponse(context);

        Product topResult = products.get(0);
        return String.format("🛒 **Sản phẩm đề xuất để mua:**\n\n" +
                        "🍰 **%s**\n" +
                        "💰 Giá: **%,.0f VNĐ**\n" +
                        "📦 Còn: %d sản phẩm\n\n" +
                        "Bạn có muốn đặt hàng không?",
                topResult.getName(), topResult.getPrice(), topResult.getStockQuantity());
    }

    private int calculateRelevance(Product product, ProductSearchContext context) {
        int score = 0;
        String productText = (product.getName() + " " +
                (product.getDescription() != null ? product.getDescription() : "")).toLowerCase();

        // Category match bonus
        for (String category : context.getCategories()) {
            if (product.getCategories().stream()
                    .anyMatch(cat -> cat.getName().toLowerCase().contains(category))) {
                score += 100;
            }
        }

        // Keyword match in name
        for (String keyword : context.getKeywords()) {
            if (product.getName().toLowerCase().contains(keyword)) {
                score += 50;
            }
        }

        // Attribute match
        for (String attribute : context.getAttributes()) {
            if (productText.contains(nlpService.normalizeText(attribute))) {
                score += 30;
            }
        }

        return score;
    }

    private String getPriceRangeText(String priceRange) {
        switch (priceRange) {
            case "cheap": return "Giá rẻ (dưới 50k)";
            case "medium": return "Trung bình (50k-200k)";
            case "expensive": return "Cao cấp (trên 200k)";
            default: return "Không xác định";
        }
    }
}