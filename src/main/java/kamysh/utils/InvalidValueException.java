package kamysh.utils;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvalidValueException extends Exception {

    private Error error;
    private ErrorCode errorCode;

    public InvalidValueException(Error error) {
        super();
        this.error = error;
    }

    public InvalidValueException(Error error, String message, Throwable cause) {
        super(message, cause);
        this.error = error;
    }

    public InvalidValueException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public InvalidValueException(Error error, String message) {
        super(message);
        this.error = error;
    }

    public InvalidValueException(Error error, Throwable cause) {
        super(cause);
        this.error = error;
    }

}
