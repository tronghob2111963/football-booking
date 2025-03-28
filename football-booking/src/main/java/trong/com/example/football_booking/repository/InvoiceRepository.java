package trong.com.example.football_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import trong.com.example.football_booking.entity.Invoice;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("SELECT i FROM Invoice i JOIN i.items it WHERE i.booking.id = :bookingId AND it.item.id = :itemId")
    Optional<Invoice> findByBookingIdAndItemId(@Param("bookingId") Long bookingId, @Param("itemId") Long itemId);

    List<Invoice> findByBookingId(Long bookingId);
}
