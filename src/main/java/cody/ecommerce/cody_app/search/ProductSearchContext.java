package cody.ecommerce.cody_app.search;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProductSearchContext {
    private String originalQuery;
    private String normalizedQuery;
    private List<String> keywords = new ArrayList<>();
    private List<String> nouns = new ArrayList<>();
    private List<String> adjectives = new ArrayList<>();
    private List<String> categories = new ArrayList<>();
    private List<String> attributes = new ArrayList<>();
    private String priceRange;
    private String intent;

    public void addCategory(String category) {
        if (!categories.contains(category)) {
            categories.add(category);
        }
    }

    public void addAttribute(String attribute) {
        if (!attributes.contains(attribute)) {
            attributes.add(attribute);
        }
    }

    public void addKeyword(String keyword) {
        if (!keywords.contains(keyword)) {
            keywords.add(keyword);
        }
    }
}