package trong.com.example.football_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trong.com.example.football_booking.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
