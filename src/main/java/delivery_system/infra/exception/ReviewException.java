package delivery_system.infra.exception;

import org.springframework.http.HttpStatus;

public class ReviewException extends RuntimeException {

    private final HttpStatus status;

    public ReviewException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public ReviewException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public ReviewException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public ReviewException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}