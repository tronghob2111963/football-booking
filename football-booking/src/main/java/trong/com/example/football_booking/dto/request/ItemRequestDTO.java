package trong.com.example.football_booking.dto.request;

import lombok.Data;

@Data
public class ItemRequestDTO {
    private String name;
    private Double price;
    private Integer stock;
    private String imageUrl;
}