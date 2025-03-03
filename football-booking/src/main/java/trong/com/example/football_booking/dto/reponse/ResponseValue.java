package trong.com.example.football_booking.dto.reponse;

import org.springframework.http.HttpStatusCode;

public class ResponseValue extends  ResponseScuccess{

    public ResponseValue(HttpStatusCode status, String message) {
        super(status, message);
    }
}
