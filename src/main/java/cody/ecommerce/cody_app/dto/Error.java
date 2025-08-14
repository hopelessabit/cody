package cody.ecommerce.cody_app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class Error<T>{
    private String detail;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<T> idsNotFound;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> errors;


    public Error(String detail) {
        this.detail = detail;
    }

    public Error(String detail, Map<String, String> errors) {
        this.detail = detail;
        this.errors = errors;
    }

    public Error(String detail, List<T> idsNotFound) {
        this.detail = detail;
        this.idsNotFound = idsNotFound;
    }

    public Error(String detail, List<T> idsNotFound, Map<String, String> errors) {
        this.detail = detail;
        this.idsNotFound = idsNotFound;
        this.errors = errors;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public List<T> getIdsNotFound() {
        return idsNotFound;
    }

    public void setIdsNotFound(List<T> idsNotFound) {
        this.idsNotFound = idsNotFound;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

    public static <T> Error<T> build(String detail){
        return new Error<>(detail);
    }

    public static <T> Error<T> build(String detail, List<T> idsNotFound) {
        return new Error<>(detail, idsNotFound);
    }

    public static <T> Error<T> build(String detail, Map<String, String> errors) {
        return new Error<>(detail, errors);
    }

    public static <T> Error<T> build(String detail, List<T> idsNotFound, Map<String, String> errors) {
        return new Error<>(detail, idsNotFound, errors);
    }

    public boolean hasErrors() {
        return this.getDetail() != null ||
               (this.getErrors() != null && !this.getErrors().isEmpty()) ||
               (this.getIdsNotFound() != null && !this.getIdsNotFound().isEmpty());
    }
}
