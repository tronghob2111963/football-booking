package trong.com.example.football_booking.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
@AllArgsConstructor
@Builder
public class FieldReponse implements Serializable {
    private Long id;
    private String name;
    private String address;
    private String image;
    private String price_per_hour;
    private String status;


}
