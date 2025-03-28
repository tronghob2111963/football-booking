package trong.com.example.football_booking.dto.reponse;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponseDTO {
    private Long id;
    private Long bookingId;
    private Long field;
    private List<ItemDetail> items; // Danh sách các item trong hóa đơn
    private Double totalPrice;
    private String start_time;
    private String end_time;
    private String total_cost;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDetail {
        private Long itemId;
        private String itemName;
        private Double itemPrice;
        private Integer quantity;
        private Integer itemStock; // Số lượng tồn kho còn lại
    }
}