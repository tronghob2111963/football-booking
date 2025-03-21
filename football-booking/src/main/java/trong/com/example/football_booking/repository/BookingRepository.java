package trong.com.example.football_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import trong.com.example.football_booking.entity.Booking;

import java.time.LocalDateTime;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.field_id.id = :field_id " +
            "AND ((b.start_time <= :end_time AND b.end_time >= :start_time) " +
            "OR (b.start_time >= :start_time AND b.end_time <= :end_time))")
    boolean isFieldBooked(Long field_id, LocalDateTime start_time, LocalDateTime end_time);
}
