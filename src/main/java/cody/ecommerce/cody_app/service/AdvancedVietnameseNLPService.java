package cody.ecommerce.cody_app.service;

import cody.ecommerce.cody_app.search.ProductSearchContext;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.pipeline.Annotation;
import vn.pipeline.Sentence;
import vn.pipeline.VnCoreNLP;
import vn.pipeline.Word;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class AdvancedVietnameseNLPService {

    private VnCoreNLP vnCoreNLP;

    @PostConstruct
    public void init() {
        try {
            String[] annotators = {"wseg", "pos"};
            this.vnCoreNLP = new VnCoreNLP(annotators);
        } catch (Exception e) {
            System.err.println("Failed to initialize VnCoreNLP: " + e.getMessage());
        }
    }


    public ProductSearchContext analyzeQuery(String userMessage) {
        log.debug("Analyzing query: {}", userMessage);

        if (userMessage == null || userMessage.trim().isEmpty()) {
            log.warn("Empty or null user message in NLP analysis");
            return new ProductSearchContext();
        }

        ProductSearchContext context = new ProductSearchContext();
        context.setOriginalQuery(userMessage);

        try {
            String normalizedQuery = normalizeText(userMessage);
            context.setNormalizedQuery(normalizedQuery);
            log.debug("Normalized query: {}", normalizedQuery);
        } catch (Exception e) {
            log.error("Error normalizing text: {}", e.getMessage(), e);
            context.setNormalizedQuery(userMessage.toLowerCase());
        }

        try {
            // Extract keywords and POS tags
            log.debug("Creating VnCoreNLP annotation");
            Annotation annotation = new Annotation(userMessage);
            vnCoreNLP.annotate(annotation);
            log.debug("VnCoreNLP annotation completed");

            List<String> nouns = new ArrayList<>();
            List<String> adjectives = new ArrayList<>();
            List<String> allKeywords = new ArrayList<>();

            for (Sentence sentence : annotation.getSentences()) {
                log.debug("Processing sentence with {} words", sentence.getWords().size());

                for (Word word : sentence.getWords()) {
                    try {
                        String wordForm = word.getForm().toLowerCase();
                        String posTag = word.getPosTag();

                        log.trace("Processing word: {} (POS: {})", wordForm, posTag);

                        if (wordForm.length() >= 2 && !isStopWord(wordForm)) {
                            allKeywords.add(wordForm);

                            // Categorize by POS tags
                            if (posTag.startsWith("N")) { // Nouns
                                nouns.add(wordForm);
                                log.trace("Added noun: {}", wordForm);
                            } else if (posTag.startsWith("A")) { // Adjectives
                                adjectives.add(wordForm);
                                log.trace("Added adjective: {}", wordForm);
                            }
                        }
                    } catch (Exception e) {
                        log.error("Error processing word in sentence: {}", e.getMessage(), e);
                    }
                }
            }

            context.setKeywords(allKeywords);
            context.setNouns(nouns);
            context.setAdjectives(adjectives);

            log.debug("Extracted {} keywords, {} nouns, {} adjectives",
                    allKeywords.size(), nouns.size(), adjectives.size());

            // Analyze query intent and extract product attributes
            analyzeProductAttributes(context);
            analyzeQueryIntent(context);

            log.info("Query analysis completed. Intent: {}, Categories: {}",
                    context.getIntent(), context.getCategories());

        } catch (Exception e) {
            log.error("Error in VnCoreNLP analysis, falling back to simple analysis: {}", e.getMessage(), e);
            // Fallback analysis
            try {
                context.setKeywords(Arrays.asList(userMessage.toLowerCase().split("\\s+")));
                analyzeProductAttributes(context);
                analyzeQueryIntent(context);
            } catch (Exception fallbackError) {
                log.error("Error in fallback analysis: {}", fallbackError.getMessage(), fallbackError);
            }
        }

        return context;
    }

    private void analyzeProductAttributes(ProductSearchContext context) {
        log.debug("Analyzing product attributes");

        try {
            String query = context.getNormalizedQuery();
            if (query == null) {
                log.warn("Normalized query is null in analyzeProductAttributes");
                return;
            }

            // Food categories
            if (containsAny(query, "banh", "bánh", "cake")) {
                context.addCategory("bánh");
                log.debug("Added category: bánh");

                if (containsAny(query, "ngot", "ngọt", "sweet", "dessert")) {
                    context.addCategory("bánh ngọt");
                }
                if (containsAny(query, "mi", "mì", "bread", "sandwich")) {
                    context.addCategory("bánh mì");
                }
                if (containsAny(query, "kem", "cream", "birthday", "sinh nhat")) {
                    context.addCategory("bánh kem");
                }
                if (containsAny(query, "quy", "cookie", "biscuit")) {
                    context.addCategory("bánh quy");
                }
            }

            if (containsAny(query, "do uong", "đồ uống", "nuoc", "nước", "drink", "beverage")) {
                context.addCategory("đồ uống");
                log.debug("Added category: đồ uống");

                if (containsAny(query, "ca phe", "cà phê", "coffee")) {
                    context.addKeyword("cà phê");
                }
                if (containsAny(query, "tra", "trà", "tea")) {
                    context.addKeyword("trà");
                }
                if (containsAny(query, "coca", "pepsi", "nuoc ngot")) {
                    context.addKeyword("nước ngọt");
                }
            }

            if (containsAny(query, "pizza", "bánh pizza")) {
                context.addCategory("pizza");
                log.debug("Added category: pizza");
            }

            if (containsAny(query, "burger", "hamburger")) {
                context.addCategory("burger");
                log.debug("Added category: burger");
            }

            if (containsAny(query, "ga", "gà", "chicken")) {
                context.addCategory("gà");
                log.debug("Added category: gà");
            }

            // Price attributes
            if (containsAny(query, "re", "rẻ", "phai chang", "phải chăng", "gia thap", "giá thấp")) {
                context.setPriceRange("cheap");
                log.debug("Set price range: cheap");
            } else if (containsAny(query, "dat", "đắt", "cao cap", "cao cấp", "premium")) {
                context.setPriceRange("expensive");
                log.debug("Set price range: expensive");
            } else if (containsAny(query, "trung binh", "trung bình", "vua phai", "vừa phải")) {
                context.setPriceRange("medium");
                log.debug("Set price range: medium");
            }

            // Taste attributes
            if (containsAny(query, "ngot", "ngọt", "sweet")) {
                context.addAttribute("ngọt");
            }
            if (containsAny(query, "cay", "cay", "spicy", "hot")) {
                context.addAttribute("cay");
            }
            if (containsAny(query, "chua", "chua", "sour")) {
                context.addAttribute("chua");
            }

            // Size attributes
            if (containsAny(query, "lon", "lớn", "big", "large")) {
                context.addAttribute("lớn");
            }
            if (containsAny(query, "nho", "nhỏ", "small")) {
                context.addAttribute("nhỏ");
            }

        } catch (Exception e) {
            log.error("Error analyzing product attributes: {}", e.getMessage(), e);
        }
    }

    private void analyzeQueryIntent(ProductSearchContext context) {
        log.debug("Analyzing query intent");

        try {
            String query = context.getNormalizedQuery();
            if (query == null) {
                log.warn("Normalized query is null in analyzeQueryIntent");
                context.setIntent("GENERAL");
                return;
            }

            if (containsAny(query, "tim", "tìm", "co", "có", "search", "look for")) {
                context.setIntent("SEARCH");
            } else if (containsAny(query, "mua", "order", "dat", "đặt")) {
                context.setIntent("BUY");
            } else if (containsAny(query, "gia", "giá", "bao nhieu", "bao nhiêu", "price", "cost")) {
                context.setIntent("PRICE_INQUIRY");
            } else if (containsAny(query, "goi y", "gợi ý", "recommend", "suggest")) {
                context.setIntent("RECOMMENDATION");
            } else {
                context.setIntent("GENERAL");
            }

            log.debug("Determined intent: {}", context.getIntent());

        } catch (Exception e) {
            log.error("Error analyzing query intent: {}", e.getMessage(), e);
            context.setIntent("GENERAL");
        }
    }

    private boolean containsAny(String text, String... keywords) {
        if (text == null) return false;

        for (String keyword : keywords) {
            if (text.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public String normalizeText(String text) {
        if (text == null) {
            log.warn("Null text passed to normalizeText");
            return "";
        }

        try {
            return text.toLowerCase()
                    .replace("á", "a").replace("à", "a").replace("ả", "a").replace("ã", "a").replace("ạ", "a")
                    .replace("ă", "a").replace("ắ", "a").replace("ằ", "a").replace("ẳ", "a").replace("ẵ", "a").replace("ặ", "a")
                    .replace("â", "a").replace("ấ", "a").replace("ầ", "a").replace("ẩ", "a").replace("ẫ", "a").replace("ậ", "a")
                    .replace("é", "e").replace("è", "e").replace("ẻ", "e").replace("ẽ", "e").replace("ẹ", "e")
                    .replace("ê", "e").replace("ế", "e").replace("ề", "e").replace("ể", "e").replace("ễ", "e").replace("ệ", "e")
                    .replace("í", "i").replace("ì", "i").replace("ỉ", "i").replace("ĩ", "i").replace("ị", "i")
                    .replace("ó", "o").replace("ò", "o").replace("ỏ", "o").replace("õ", "o").replace("ọ", "o")
                    .replace("ô", "o").replace("ố", "o").replace("ồ", "o").replace("ổ", "o").replace("ỗ", "o").replace("ộ", "o")
                    .replace("ơ", "o").replace("ớ", "o").replace("ờ", "o").replace("ở", "o").replace("ỡ", "o").replace("ợ", "o")
                    .replace("ú", "u").replace("ù", "u").replace("ủ", "u").replace("ũ", "u").replace("ụ", "u")
                    .replace("ư", "u").replace("ứ", "u").replace("ừ", "u").replace("ử", "u").replace("ữ", "u").replace("ự", "u")
                    .replace("ý", "y").replace("ỳ", "y").replace("ỷ", "y").replace("ỹ", "y").replace("ỵ", "y")
                    .replace("đ", "d").trim();
        } catch (Exception e) {
            log.error("Error normalizing text '{}': {}", text, e.getMessage(), e);
            return text.toLowerCase();
        }
    }

    private boolean isStopWord(String word) {
        try {
            Set<String> stopWords = Set.of("và", "của", "có", "là", "với", "trong", "cho", "các", "một", "này", "được", "để", "tôi", "bạn");
            return stopWords.contains(word);
        } catch (Exception e) {
            log.error("Error checking stop word '{}': {}", word, e.getMessage(), e);
            return false;
        }
    }
}