package trong.com.example.football_booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import trong.com.example.football_booking.common.BookingStatus;
import trong.com.example.football_booking.dto.reponse.InvoiceResponseDTO;
import trong.com.example.football_booking.dto.request.InvoiceRequestDTO;
import trong.com.example.football_booking.entity.*;
import trong.com.example.football_booking.repository.*;
import trong.com.example.football_booking.service.InvoiceService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        // Kiểm tra số lượng tồn kho
        if (item.getStock() < request.getQuantity()) {
            log.error("Not enough stock for item ID: {}. Requested: {}, Available: {}",
                    request.getItemId(), request.getQuantity(), item.getStock());
            throw new IllegalArgumentException("Not enough stock for item: " + item.getName() +
                    ". Requested: " + request.getQuantity() + ", Available: " + item.getStock());
        }

        // Giảm số lượng tồn kho
        item.setStock(item.getStock() - request.getQuantity());
        itemRepository.save(item);

        // Tạo InvoiceItem
        InvoiceItem invoiceItem = InvoiceItem.builder()
                .item(item)
                .quantity(request.getQuantity())
                .build();

        // Tính totalPrice
        double totalPrice = item.getPrice() * request.getQuantity() + booking.getTotalCost();

        // Tạo invoice
        Invoice invoice = Invoice.builder()
                .booking(booking)
                .items(new ArrayList<>())
                .totalPrice(totalPrice)
                .build();

        // Thêm InvoiceItem vào danh sách items
        invoiceItem.setInvoice(invoice);
        invoice.getItems().add(invoiceItem);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Tạo InvoiceResponseDTO
        return mapToInvoiceResponseDTO(savedInvoice);
    }

    @Override
    public InvoiceResponseDTO updateInvoice(Long invoiceId, InvoiceRequestDTO request, Long userId) {
        // Kiểm tra userId
        if (userId == null) {
            log.error("User ID is null");
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (invoiceId == null) {
            log.error("Invoice ID is null");
            throw new IllegalArgumentException("Invoice ID cannot be null");
        }

        // Tìm invoice
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found with ID: " + invoiceId));

        // Kiểm tra trạng thái booking
        if (invoice.getBooking().getStatus() != BookingStatus.PENDING) {
            log.error("Booking is not in PENDING status");
            throw new IllegalArgumentException("Booking must be in PENDING status to update invoice");
        }

        // Tìm item mới
        Item newItem = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + request.getItemId()));

        // Kiểm tra xem item đã tồn tại trong invoice chưa
        Optional<InvoiceItem> existingInvoiceItem = invoice.getItems().stream()
                .filter(invoiceItem -> invoiceItem.getItem().getId().equals(request.getItemId()))
                .findFirst();

        if (existingInvoiceItem.isPresent()) {
            // Nếu item đã tồn tại, cộng dồn số lượng
            InvoiceItem invoiceItem = existingInvoiceItem.get();
            int oldQuantity = invoiceItem.getQuantity();
            int newQuantity = oldQuantity + request.getQuantity();

            // Kiểm tra tồn kho
            if (newItem.getStock() < request.getQuantity()) {
                log.error("Not enough stock for item ID: {}. Requested: {}, Available: {}",
                        request.getItemId(), request.getQuantity(), newItem.getStock());
                throw new IllegalArgumentException("Not enough stock for item: " + newItem.getName() +
                        ". Requested: " + request.getQuantity() + ", Available: " + newItem.getStock());
            }

            // Cập nhật số lượng và tồn kho
            invoiceItem.setQuantity(newQuantity);
            newItem.setStock(newItem.getStock() - request.getQuantity());
            itemRepository.save(newItem);
        } else {
            // Nếu item chưa tồn tại, thêm mới vào danh sách items
            if (newItem.getStock() < request.getQuantity()) {
                log.error("Not enough stock for item ID: {}. Requested: {}, Available: {}",
                        request.getItemId(), request.getQuantity(), newItem.getStock());
                throw new IllegalArgumentException("Not enough stock for item: " + newItem.getName() +
                        ". Requested: " + request.getQuantity() + ", Available: " + newItem.getStock());
            }

            InvoiceItem newInvoiceItem = InvoiceItem.builder()
                    .item(newItem)
                    .quantity(request.getQuantity())
                    .invoice(invoice)
                    .build();
            invoice.getItems().add(newInvoiceItem);

            // Giảm tồn kho
            newItem.setStock(newItem.getStock() - request.getQuantity());
            itemRepository.save(newItem);
        }

        // Tính lại totalPrice
        double totalPrice = invoice.getItems().stream()
                .mapToDouble(invoiceItem -> invoiceItem.getItem().getPrice() * invoiceItem.getQuantity())
                .sum() + invoice.getBooking().getTotalCost();
        invoice.setTotalPrice(totalPrice);

        // Lưu invoice
        Invoice updatedInvoice = invoiceRepository.save(invoice);

        return mapToInvoiceResponseDTO(updatedInvoice);
    }




    @Override
    public InvoiceResponseDTO viewInvoiceDetails(Long invoiceId) {
        log.info("Fetching invoice details for ID: {}", invoiceId);
        // Tìm invoice
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found with ID: " + invoiceId));

        return mapToInvoiceResponseDTO(invoice);
    }

    @Override
    public void deleteInvoice(Long invoiceId) {
        log.info("Deleting invoice with ID: {}", invoiceId);

        // Kiểm tra invoiceId
        if (invoiceId == null) {
            log.error("Invoice ID is null");
            throw new IllegalArgumentException("Invoice ID cannot be null");
        }


        // Tìm invoice
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found with ID: " + invoiceId));

        // Xóa invoice (các InvoiceItem sẽ tự động bị xóa nhờ cascade)
        invoiceRepository.delete(invoice);
        log.info("Invoice deleted successfully with ID: {}", invoiceId);
    }


    private InvoiceResponseDTO mapToInvoiceResponseDTO(Invoice invoice) {
        List<InvoiceResponseDTO.ItemDetail> itemDetails = invoice.getItems().stream()
                .map(invoiceItem -> InvoiceResponseDTO.ItemDetail.builder()
                        .itemId(invoiceItem.getItem().getId())
                        .itemName(invoiceItem.getItem().getName())
                        .itemPrice(invoiceItem.getItem().getPrice())
                        .quantity(invoiceItem.getQuantity())
                        .itemStock(invoiceItem.getItem().getStock())
                        .build())
                .collect(Collectors.toList());

        return InvoiceResponseDTO.builder()
                .id(invoice.getId())
                .bookingId(invoice.getBooking().getId())
                .field(invoice.getBooking().getField_id().getId())
                .items(itemDetails)
                .totalPrice(invoice.getTotalPrice())
                .start_time(invoice.getBooking().getStart_time().toString())
                .end_time(invoice.getBooking().getEnd_time().toString())
                .total_cost(invoice.getBooking().getTotalCost().toString())
                .build();
    }
}
