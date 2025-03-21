package trong.com.example.football_booking.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class BookingResponse {
//    id BIGINT AUTO_INCREMENT PRIMARY KEY,
//    field_id BIGINT,
//    user_id BIGINT,
//    start_time DATETIME NOT NULL,
//    end_time DATETIME NOT NULL,
//    status ENUM('PENDING', 'CONFIRMED', 'CANCELED') DEFAULT 'PENDING',
//    FOREIGN KEY (field_id) REFERENCES fields(id),
//    FOREIGN KEY (user_id) REFERENCES users(id)
    private Long id;
    private Long field_id;
    private Long userId;
    private String startTime;
    private String endTime;
    private String status;
}
