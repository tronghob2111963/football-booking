package trong.com.example.football_booking.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import trong.com.example.football_booking.common.FieldsStatus;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "fields")
@Entity
public class Field {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(name = "price_per_hour", nullable = false)
    private Double price_per_hour;

    @Enumerated(EnumType.STRING)
    private FieldsStatus status;

    @Column(name = "image_url")  // Thêm trường image_url
    private String imageUrl;


}
