package trong.com.example.football_booking.dto.request;


import lombok.Data;
import trong.com.example.football_booking.common.FieldsStatus;

@Data
public class FieldRequestDTO {
    private String name;
    private String address;
    private Double price_per_house;
    private String imageUrl;
    private FieldsStatus status;
}
