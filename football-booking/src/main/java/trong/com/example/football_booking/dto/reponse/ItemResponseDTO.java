package trong.com.example.football_booking.dto.reponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemResponseDTO {
    private Long id;
    private String name;
    private Double price;
    private Integer stock;
    private String imageUrl;
}