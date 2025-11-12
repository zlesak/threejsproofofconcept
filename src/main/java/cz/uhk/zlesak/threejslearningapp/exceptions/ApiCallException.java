package cz.uhk.zlesak.threejslearningapp.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

/**
 * Custom exception to handle API call errors.
 */
@Getter
public class ApiCallException extends Exception {
    private final String chapterId;
    private final HttpStatusCode status;
    private final String responseBody;
    private final String request;

    /**
     * Constructor for ApiCallException.
     * @param message message of the exception
     * @param chapterId chapterId related to the exception
     * @param request the request that caused the exception
     * @param status HTTP status code of the response
     * @param responseBody response body of the API call
     * @param cause the cause of the exception
     */
    public ApiCallException(String message, String chapterId, String request, HttpStatusCode status, String responseBody, Throwable cause) {
        super(message, cause);
        this.chapterId = chapterId;
        this.request = request;
        this.status = status;
        this.responseBody = responseBody;
    }

    /**
     * String representation of the ApiCallException.
     * @return String representation of the ApiCallException
     */
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

