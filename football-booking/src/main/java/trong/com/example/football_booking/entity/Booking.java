package trong.com.example.football_booking.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import trong.com.example.football_booking.common.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "field_id")
    private Field field;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
