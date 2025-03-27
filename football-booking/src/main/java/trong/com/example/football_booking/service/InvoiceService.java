package trong.com.example.football_booking.service;

import trong.com.example.football_booking.dto.reponse.InvoiceResponseDTO;
import trong.com.example.football_booking.dto.request.InvoiceRequestDTO;

import java.util.List;

public interface InvoiceService {
    InvoiceResponseDTO createInvoice(InvoiceRequestDTO request, Long userId); // Thêm invoice
    InvoiceResponseDTO updateInvoice(Long invoiceId, InvoiceRequestDTO request, Long userId); // Sửa invoice
    void deleteInvoice(Long invoiceId, Long userId); // Xóa invoice
    List<InvoiceResponseDTO> getInvoicesByBookingId(Long bookingId, Long userId);
}
