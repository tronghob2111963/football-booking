package trong.com.example.football_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import trong.com.example.football_booking.entity.InvoiceItem;

import java.util.Optional;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
    @Query("SELECT i FROM InvoiceItem i WHERE i.invoice.id = ?1 AND i.item.id = ?2")
    Optional findByInvoiceIdAndItemId(Long invoiceId, Long itemId);
}
