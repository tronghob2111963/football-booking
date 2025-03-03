package trong.com.example.football_booking.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import trong.com.example.football_booking.entity.User;
import trong.com.example.football_booking.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tìm người dùng dựa trên tên người dùng
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Người dùng không tồn tại"));

        // Kiểm tra vai trò của người dùng
        if (user.getRole() == null) {
            throw new UsernameNotFoundException("Vai trò của người dùng không hợp lệ");
        }

        // Tạo đối tượng UserDetails
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_" + user.getRole().name());
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                true, true, true, true,
                authorities
        );
    }


}
