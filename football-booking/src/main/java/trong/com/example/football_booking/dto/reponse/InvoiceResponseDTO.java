package trong.com.example.football_booking.dto.reponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvoiceResponseDTO {
    private Long id;
    private Long bookingId;
    private Long itemId;
    private String itemName;
    private Double itemPrice;
    private Integer quantity;
    private Double totalPrice;
}
