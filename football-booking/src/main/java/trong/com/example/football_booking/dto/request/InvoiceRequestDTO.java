package trong.com.example.football_booking.dto.request;


import lombok.Data;

@Data
public class InvoiceRequestDTO {
    private Long bookingId;
    private Long itemId;
    private Integer quantity;
}
