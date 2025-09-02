package cz.uhk.zlesak.threejslearningapp.application.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class ApiCallException extends Exception {
    private final String chapterId;
    private final HttpStatusCode status;
    private final String responseBody;
    private final String request;

    public ApiCallException(String message, String chapterId, String request, HttpStatusCode status, String responseBody, Throwable cause) {
        super(message, cause);
        this.chapterId = chapterId;
        this.request = request;
        this.status = status;
        this.responseBody = responseBody;
    }

    @Override
    public String toString() {
        return "ApiCallException{" +
                "chapterId='" + chapterId + '\'' +
                "request='" + request + '\'' +
                ", status=" + status +
                ", responseBody='" + responseBody + '\'' +
                '}';
    }
}

