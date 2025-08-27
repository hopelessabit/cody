package cody.ecommerce.cody_app.service;

import cody.ecommerce.cody_app.search.ProductSearchContext;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import vn.pipeline.Annotation;
import vn.pipeline.Sentence;
import vn.pipeline.VnCoreNLP;
import vn.pipeline.Word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
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
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return new ProductSearchContext();
        }

        ProductSearchContext context = new ProductSearchContext();
        context.setOriginalQuery(userMessage);
        context.setNormalizedQuery(normalizeText(userMessage));

        try {
            // Extract keywords and POS tags
            Annotation annotation = new Annotation(userMessage);
            vnCoreNLP.annotate(annotation);

            List<String> nouns = new ArrayList<>();
            List<String> adjectives = new ArrayList<>();
            List<String> allKeywords = new ArrayList<>();

            for (Sentence sentence : annotation.getSentences()) {
                for (Word word : sentence.getWords()) {
                    String wordForm = word.getForm().toLowerCase(); // Fixed: use getForm()
                    String posTag = word.getPosTag();

                    if (wordForm.length() >= 2 && !isStopWord(wordForm)) {
                        allKeywords.add(wordForm);

                        // Categorize by POS tags
                        if (posTag.startsWith("N")) { // Nouns
                            nouns.add(wordForm);
                        } else if (posTag.startsWith("A")) { // Adjectives
                            adjectives.add(wordForm);
                        }
                    }
                }
            }

            context.setKeywords(allKeywords);
            context.setNouns(nouns);
            context.setAdjectives(adjectives);

            // Analyze query intent and extract product attributes
            analyzeProductAttributes(context);
            analyzeQueryIntent(context);

        } catch (Exception e) {
            // Fallback analysis
            context.setKeywords(Arrays.asList(userMessage.toLowerCase().split("\\s+")));
        }

        return context;
    }


    private void analyzeProductAttributes(ProductSearchContext context) {
        String query = context.getNormalizedQuery();

        // Food categories
        if (containsAny(query, "banh", "bánh", "cake")) {
            context.addCategory("bánh");

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
        }

        if (containsAny(query, "burger", "hamburger")) {
            context.addCategory("burger");
        }

        if (containsAny(query, "ga", "gà", "chicken")) {
            context.addCategory("gà");
        }

        // Price attributes
        if (containsAny(query, "re", "rẻ", "phai chang", "phải chăng", "gia thap", "giá thấp")) {
            context.setPriceRange("cheap");
        } else if (containsAny(query, "dat", "đắt", "cao cap", "cao cấp", "premium")) {
            context.setPriceRange("expensive");
        } else if (containsAny(query, "trung binh", "trung bình", "vua phai", "vừa phải")) {
            context.setPriceRange("medium");
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
    }

    private void analyzeQueryIntent(ProductSearchContext context) {
        String query = context.getNormalizedQuery();

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
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public String normalizeText(String text) {
        if (text == null) return "";
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
    }

    private boolean isStopWord(String word) {
        Set<String> stopWords = Set.of("và", "của", "có", "là", "với", "trong", "cho", "các", "một", "này", "được", "để", "tôi", "bạn");
        return stopWords.contains(word);
    }
}