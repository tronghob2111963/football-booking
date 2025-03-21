package trong.com.example.football_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trong.com.example.football_booking.entity.Field;

public interface FieldRepository extends JpaRepository<Field, Long> {

}
