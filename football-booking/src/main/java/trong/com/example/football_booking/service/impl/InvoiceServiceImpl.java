package trong.com.example.football_booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import trong.com.example.football_booking.common.BookingStatus;
import trong.com.example.football_booking.dto.reponse.InvoiceResponseDTO;
import trong.com.example.football_booking.dto.request.InvoiceRequestDTO;
import trong.com.example.football_booking.entity.Booking;
import trong.com.example.football_booking.entity.Invoice;
import trong.com.example.football_booking.entity.Item;
import trong.com.example.football_booking.repository.BookingRepository;
import trong.com.example.football_booking.repository.InvoiceRepository;
import trong.com.example.football_booking.repository.ItemRepository;
import trong.com.example.football_booking.repository.UserRepository;
import trong.com.example.football_booking.service.InvoiceService;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    @Override
    public InvoiceResponseDTO createInvoice(InvoiceRequestDTO request, Long userId) {
        log.info("Creating invoice for bookingId: {}", request.getBookingId());

        // Tìm booking
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + request.getBookingId()));
        // Kiểm tra trạng thái booking
        if (booking.getStatus() != BookingStatus.PENDING) {
            log.error("Booking is not in PENDING status");
            throw new IllegalArgumentException("Booking must be in PENDING status to create invoice");
        }

        // Tìm item
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + request.getItemId()));

        // Tính totalPrice
        double totalPrice = item.getPrice() * request.getQuantity() + booking.getTotalCost();

        // Tạo invoice
        Invoice invoice = Invoice.builder()
                .booking(booking)
                .item(item)
                .quantity(request.getQuantity())
                .totalPrice(totalPrice)
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);

        InvoiceResponseDTO invoiceResponseDTO = InvoiceResponseDTO.builder()
                .id(savedInvoice.getId())
                .bookingId(savedInvoice.getBooking().getId())
                .itemId(savedInvoice.getItem().getId())
                .quantity(savedInvoice.getQuantity())
                .totalPrice(savedInvoice.getTotalPrice())
                .build();

        return invoiceResponseDTO;
    }

    @Override
    public InvoiceResponseDTO updateInvoice(Long invoiceId, InvoiceRequestDTO request, Long userId) {
        return null;
    }

    @Override
    public void deleteInvoice(Long invoiceId, Long userId) {

    }

    @Override
    public List<InvoiceResponseDTO> getInvoicesByBookingId(Long bookingId, Long userId) {
        return List.of();
    }
}
