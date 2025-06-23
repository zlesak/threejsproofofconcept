package cz.uhk.zlesak.threejslearningapp.data;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class ApiCallException extends Exception {
    private final String chapterId;
    private final HttpStatusCode status;
    private final String responseBody;

    public ApiCallException(String message, String chapterId, HttpStatusCode status, String responseBody, Throwable cause) {
        super(message, cause);
        this.chapterId = chapterId;
        this.status = status;
        this.responseBody = responseBody;
    }

    @Override
    public String toString() {
        return "ApiCallException{" +
                "chapterId='" + chapterId + '\'' +
                ", status=" + status +
                ", responseBody='" + responseBody + '\'' +
                '}';
    }
}

