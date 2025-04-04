package trong.com.example.football_booking.entity;


import jakarta.persistence.*;
import lombok.*;
import trong.com.example.football_booking.common.UserRole;

import java.util.ArrayList;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name="username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name="password", nullable = false, length = 255)
    private String password;

    @Column(name="email", unique = true, nullable = false, length = 50)
    private String email;

    @Column(name="phone", unique = true, nullable = false, length = 50)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;


}
