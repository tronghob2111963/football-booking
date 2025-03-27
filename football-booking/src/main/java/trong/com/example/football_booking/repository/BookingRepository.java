package trong.com.example.football_booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import trong.com.example.football_booking.entity.Booking;

import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.field_id.id = :fieldId " +
            "AND b.status != 'CANCELED' " +
            "AND ((b.start_time <= :endTime AND b.end_time >= :startTime))")
    boolean isFieldBooked(@Param("fieldId") Long fieldId,
                          @Param("startTime") LocalDateTime startTime,
                          @Param("endTime") LocalDateTime endTime);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.field_id.id = :fieldId " +
            "AND b.id != :bookingId " +
            "AND b.status != 'CANCELED' " +
            "AND ((b.start_time <= :endTime AND b.end_time >= :startTime))")
    boolean isFieldBookedExcludingBooking(@Param("fieldId") Long fieldId,
                                          @Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime,
                                          @Param("bookingId") Long bookingId);

    Page<Booking> findAllByUser_id(Long userId, Pageable pageable);
}
