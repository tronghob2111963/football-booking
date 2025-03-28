package trong.com.example.football_booking.service;

import trong.com.example.football_booking.dto.reponse.InvoiceResponseDTO;
import trong.com.example.football_booking.dto.request.InvoiceRequestDTO;

public interface InvoiceService {
    InvoiceResponseDTO createInvoice(InvoiceRequestDTO request, Long userId); // Thêm invoice
    InvoiceResponseDTO updateInvoice(Long invoiceId, InvoiceRequestDTO request, Long userId); // Sửa invoice// Xóa invoice
   // Xóa invoice theo bookingId
    InvoiceResponseDTO viewInvoiceDetails(Long invoiceId);
    public void deleteInvoice(Long invoiceId); // Xóa invoice
 // Xóa invoice theo bookingId
}
