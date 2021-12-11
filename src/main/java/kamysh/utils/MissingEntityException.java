package kamysh.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MissingEntityException extends Exception {

    private Error error;
    private ErrorCode errorCode;

    public MissingEntityException(Error error) {
        super();
        this.error = error;
    }

    public MissingEntityException(Error error, String message, Throwable cause) {
        super(message, cause);
        this.error = error;
    }

    public MissingEntityException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public MissingEntityException(Error error, String message) {
        super(message);
        this.error = error;
    }

    public MissingEntityException(Error error, Throwable cause) {
        super(cause);
        this.error = error;
    }

}
