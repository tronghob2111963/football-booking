package trong.com.example.football_booking.dto.request;

import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import trong.com.example.football_booking.common.UserRole;


@Getter
public class UserRequestDTO implements Serializable {

    @NotBlank(message = "Username is required")
    private String username;

    @NotNull(message = "Password is required")
    private String password;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    public UserRequestDTO(String password, String username, String email, String phone) {
        this.password = password;
        this.username = username;
        this.email = email;
        this.phone = phone;
    }
    private UserRole role;




}
