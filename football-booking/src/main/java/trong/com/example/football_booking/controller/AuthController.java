package trong.com.example.football_booking.controller;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trong.com.example.football_booking.config.JwtTokenProvider;
import trong.com.example.football_booking.dto.reponse.LoginResponse;
import trong.com.example.football_booking.dto.reponse.ResponseData;
import trong.com.example.football_booking.dto.request.LoginRequest;
import trong.com.example.football_booking.dto.request.UserRequestDTO;
import trong.com.example.football_booking.entity.User;
import trong.com.example.football_booking.service.UserService;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;


    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;


    private final  AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseData<Long> registerUser(@Valid @RequestBody UserRequestDTO user) {
        log.info("Registering user: {}", user);
        try {
            long userId = userService.saveUser(user);

            return new ResponseData<>(HttpStatus.CREATED.value(), "User registered successfully", userId);
        } catch (Exception e) {
            log.error("Error registering user: {}", user, e);
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error registering user", null);
        }

    }
    @PostMapping("/login")
    public ResponseData<?> login(@RequestBody LoginRequest loginRequest) {
        // Kiểm tra dữ liệu người dùng
        if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), "Dữ liệu người dùng không hợp lệ");
        }

        // Xác thực người dùng
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            // Tạo token
            String token = jwtTokenProvider.generateToken(authentication);
            String role = authentication.getAuthorities().stream()
                    .map(authority -> authority.getAuthority())
                    .findFirst()
                    .orElse("ROLE_USER");
            return new ResponseData<>(HttpStatus.OK.value(), "Login thành công", new LoginResponse(token, role));
        } else {
            return new ResponseData<>(HttpStatus.UNAUTHORIZED.value(), "Xác thực thất bại");
        }
    }
//    @PostMapping("/login")
//    public ResponseData<?> login(@RequestBody LoginRequest loginRequest) {
//        log.info("User login with username: {}", loginRequest.getUsername());
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
//        String token = jwtTokenProvider.generateToken(authentication);
//        String role = authentication.getAuthorities().stream()
//                .map(authority -> authority.getAuthority())
//                .findFirst()
//                .orElse("ROLE_USER");
//        return new ResponseData<>(HttpStatus.OK.value(), "Login successful", new LoginResponse(token, role));
//    }
}





