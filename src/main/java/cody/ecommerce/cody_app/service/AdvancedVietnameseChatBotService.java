package cody.ecommerce.cody_app.service;

import cody.ecommerce.cody_app.dto.response.chat_bot.ChatbotResponse;
import cody.ecommerce.cody_app.entity.Product;
import cody.ecommerce.cody_app.repository.ProductRepository;
import cody.ecommerce.cody_app.search.ProductSearchContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdvancedVietnameseChatBotService {

    private final ProductRepository productRepository;
    private final AdvancedVietnameseNLPService nlpService;

    public ChatbotResponse processMessage(String userMessage) {
        log.info("Processing chatbot message: {}", userMessage);

        if (userMessage == null || userMessage.trim().isEmpty()) {
            log.warn("Empty or null user message received");
            return new ChatbotResponse("Xin chào! Tôi có thể giúp bạn tìm sản phẩm. Hãy mô tả chi tiết sản phẩm bạn muốn!");
        }

        try {
            // Analyze the full context of the query
            log.debug("Analyzing query with NLP service");
            ProductSearchContext context = nlpService.analyzeQuery(userMessage);
            log.info("Query analysis complete. Categories: {}, Keywords: {}",
                    context.getCategories(), context.getKeywords());

            Set<Product> results = new HashSet<>();

            // Search by categories (highest priority)
            for (String category : context.getCategories()) {
                log.debug("Searching by category: {}", category);
                try {
                    List<Product> categoryProducts = productRepository.findByCategory(category);
                    log.debug("Found {} products for category: {}", categoryProducts.size(), category);
                    results.addAll(categoryProducts);
                } catch (Exception e) {
                    log.error("Error searching by category {}: {}", category, e.getMessage(), e);
                }
            }

            // Search by all keywords
            for (String keyword : context.getKeywords()) {
                if (keyword.length() >= 2) {
                    log.debug("Searching by keyword: {}", keyword);
                    try {
                        List<Product> keywordProducts = productRepository.searchByKeyword(keyword);
                        log.debug("Found {} products for keyword: {}", keywordProducts.size(), keyword);
                        results.addAll(keywordProducts);
                    } catch (Exception e) {
                        log.error("Error searching by keyword {}: {}", keyword, e.getMessage(), e);
                    }
                }
            }

            // Search by nouns (product names)
            for (String noun : context.getNouns()) {
                if (noun.length() >= 2) {
                    log.debug("Searching by noun: {}", noun);
                    try {
                        List<Product> nounProducts = productRepository.searchByKeyword(noun);
                        log.debug("Found {} products for noun: {}", nounProducts.size(), noun);
                        results.addAll(nounProducts);
                    } catch (Exception e) {
                        log.error("Error searching by noun {}: {}", noun, e.getMessage(), e);
                    }
                }
            }

            log.info("Total products found before filtering: {}", results.size());

            // Apply price filtering
            try {
                results = filterByPriceRange(results, context.getPriceRange());
                log.debug("Products after price filtering: {}", results.size());
            } catch (Exception e) {
                log.error("Error in price filtering: {}", e.getMessage(), e);
            }

            // Apply attribute filtering
            try {
                results = filterByAttributes(results, context.getAttributes());
                log.debug("Products after attribute filtering: {}", results.size());
            } catch (Exception e) {
                log.error("Error in attribute filtering: {}", e.getMessage(), e);
            }

            List<Product> finalResults = new ArrayList<>(results);
            log.info("Final results count: {}", finalResults.size());

            ChatbotResponse response = new ChatbotResponse(generateContextualResponse(finalResults, context));
            log.info("Generated response successfully");
            return response;

        } catch (Exception e) {
            log.error("Error processing chatbot message: {}", e.getMessage(), e);
            return new ChatbotResponse("Xin lỗi, đã có lỗi xảy ra. Vui lòng thử lại!");
        }
    }

    private Set<Product> filterByPriceRange(Set<Product> products, String priceRange) {
        log.debug("Filtering {} products by price range: {}", products.size(), priceRange);

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

            try {
                return products.stream()
                        .filter(p -> {
                            if (p.getPrice() == null) {
                                log.warn("Product with null price found: {}", p.getName());
                                return false;
                            }
                            return p.getPrice().compareTo(min) >= 0 && p.getPrice().compareTo(max) <= 0;
                        })
                        .collect(Collectors.toSet());
            } catch (Exception e) {
                log.error("Error filtering by price range: {}", e.getMessage(), e);
                return products;
            }
        }

        return products;
    }

    private Set<Product> filterByAttributes(Set<Product> products, List<String> attributes) {
        log.debug("Filtering {} products by attributes: {}", products.size(), attributes);

        if (attributes.isEmpty()) {
            return products;
        }

        try {
            return products.stream()
                    .filter(product -> {
                        try {
                            String productText = (product.getName() + " " +
                                    (product.getDescription() != null ? product.getDescription() : "")).toLowerCase();

                            return attributes.stream().anyMatch(attr ->
                                    productText.contains(nlpService.normalizeText(attr)));
                        } catch (Exception e) {
                            log.error("Error processing product {} during attribute filtering: {}",
                                    product.getId(), e.getMessage(), e);
                            return false;
                        }
                    })
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("Error in attribute filtering: {}", e.getMessage(), e);
            return products;
        }
    }

    // Keep other methods unchanged but add logging where needed...
    private String generateContextualResponse(List<Product> products, ProductSearchContext context) {
        log.debug("Generating response for {} products", products.size());

        if (products.isEmpty()) {
            return generateNoResultsResponse(context);
        }

        try {
            // Sort by relevance
            products.sort((p1, p2) -> calculateRelevance(p2, context) - calculateRelevance(p1, context));

            String intent = context.getIntent();
            log.debug("Generating response for intent: {}", intent);

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
        } catch (Exception e) {
            log.error("Error generating contextual response: {}", e.getMessage(), e);
            return "Xin lỗi, đã có lỗi xảy ra khi tạo phản hồi.";
        }
    }

    // Keep all other methods unchanged...
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