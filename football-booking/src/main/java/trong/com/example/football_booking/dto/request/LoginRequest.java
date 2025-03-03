package trong.com.example.football_booking.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
