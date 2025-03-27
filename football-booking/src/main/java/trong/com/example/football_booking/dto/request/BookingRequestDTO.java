
package trong.com.example.football_booking.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
public class BookingRequestDTO {
    private Long field_id;
    private Long userId;
    private String bookingDate;
    private String start_time;
    private String end_time;
    private String status;
    private String total_cost;

}
