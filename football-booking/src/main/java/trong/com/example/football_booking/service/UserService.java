package trong.com.example.football_booking.service;

import trong.com.example.football_booking.dto.request.UserRequestDTO;
import trong.com.example.football_booking.entity.User;

public interface UserService {
    User getUserById(Long id);
    long saveUser(UserRequestDTO userDTO); // Dùng cho đăng ký
    User findByUsername(String username); // Dùng cho đăng nhập
}
