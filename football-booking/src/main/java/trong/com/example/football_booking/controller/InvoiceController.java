package trong.com.example.football_booking.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import trong.com.example.football_booking.dto.reponse.InvoiceResponseDTO;
import trong.com.example.football_booking.dto.reponse.ResponseData;
import trong.com.example.football_booking.dto.request.InvoiceRequestDTO;
import trong.com.example.football_booking.entity.Invoice;
import trong.com.example.football_booking.entity.User;
import trong.com.example.football_booking.service.InvoiceService;

@Slf4j
@RestController
@RequestMapping("/api/invoice")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/create")
    public ResponseData<InvoiceResponseDTO> createInvoice(
            @RequestBody InvoiceRequestDTO request) {
        log.info("Creating invoice for bookingId: {}", request.getBookingId());
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("User is not authenticated");
                return new ResponseData<>(HttpStatus.UNAUTHORIZED.value(), "User is not authenticated", null);
            }
            Long userId = (Long) authentication.getCredentials();
            if (userId == null) {
                log.warn("User ID not found in token");
                return new ResponseData<>(HttpStatus.UNAUTHORIZED.value(), "User ID not found in token", null);
            }
            InvoiceResponseDTO response = invoiceService.createInvoice(request, userId);
            return new ResponseData<>(HttpStatus.OK.value(), "Invoice created successfully", response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request: {}", e.getMessage());
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error creating invoice: {}", e.getMessage(), e);
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error creating invoice: " + e.getMessage(), null);
        }
    }


}
