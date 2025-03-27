package trong.com.example.football_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trong.com.example.football_booking.entity.Invoice;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByBookingId(Long bookingId);
}