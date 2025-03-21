package trong.com.example.football_booking.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import trong.com.example.football_booking.dto.reponse.ResponseData;

import java.nio.file.AccessDeniedException;
import java.util.Date;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class GloBalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleValidationException(Exception ex, WebRequest request) {
        System.out.println("========>Handle Validation Exception");
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(BAD_REQUEST.value());
        errorResponse.setTimestamp(new Date());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        /// getReasonPhrase --> BAD_REQUEST


        String message = ex.getMessage();
        if(ex instanceof MethodArgumentNotValidException) {
            int start = message.lastIndexOf("[");
            int end = message.lastIndexOf("]");
            message = message.substring(start +1,end-1);
            errorResponse.setError("Pay load invalid");
        }else if(ex instanceof ConstraintViolationException) {
            message.substring(message.indexOf(" ")+1);
            errorResponse.setError("Path Variable invalid");
        }
        errorResponse.setMessage(message);
        return errorResponse;
    }
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalErrrorException(Exception ex, WebRequest request) {
        System.out.println("========>Handle Internal Errror Exception");
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(INTERNAL_SERVER_ERROR.value());
        errorResponse.setTimestamp(new Date());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setError(INTERNAL_SERVER_ERROR.getReasonPhrase());
        if(ex instanceof MethodArgumentTypeMismatchException) {
            errorResponse.setError("Failled to conver to type");
        }
        return errorResponse;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseData<?> handleAccessDeniedException(AccessDeniedException ex) {
        return new ResponseData<>(HttpStatus.FORBIDDEN.value(), "Bạn không có quyền thực hiện hành động này!", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseData<?> handleGeneralException(Exception ex) {
        return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Đã có lỗi xảy ra: " + ex.getMessage(), null);
    }


}
