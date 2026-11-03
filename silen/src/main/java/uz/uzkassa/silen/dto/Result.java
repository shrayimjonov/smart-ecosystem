package uz.uzkassa.silen.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@JsonRootName("result")
public final class Result<T> implements Serializable {
    @JsonProperty
    private final int errorCode;

    @JsonProperty
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String code;

    @JsonProperty
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String message;

    @JsonProperty
    private T data;

    private Result(String code, String message, int errorCode, T data) {
        this.code = code;
        this.message = message;
        this.errorCode = errorCode;
        this.data = data;
    }

    @JsonCreator // for work un-serialization
    public static <T> Result<T> success() {
        return Result.success(null, null);
    }

    public static <T> Result<T> success(T data) {
        return Result.success(null, data);
    }

    public static <T> Result<T> success(String code, T data) {
        return new Result<>(code, null, 0, data);
    }

    public static <T> Result<T> error(String message, String code, int errorCode, T data) {
        return new Result<>(code, message, errorCode, data);
    }
}
