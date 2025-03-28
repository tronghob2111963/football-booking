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

import java.util.List;

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
    @PutMapping("/update/{invoiceId}")
    public ResponseData<InvoiceResponseDTO> updateInvoice(@PathVariable Long invoiceId, @RequestBody InvoiceRequestDTO request) {
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
            InvoiceResponseDTO response = invoiceService.updateInvoice(invoiceId, request, userId);
            return new ResponseData<>(HttpStatus.OK.value(), "Invoice updated successfully", response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request: {}", e.getMessage());
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error updating invoice: {}", e.getMessage(), e);
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error updating invoice: " + e.getMessage(), null);
        }
    }


    @GetMapping("/{invoiceId}")
    public ResponseData<InvoiceResponseDTO> viewInvoiceDetails(@PathVariable Long invoiceId) {
        log.info("Fetching invoice details for ID: {}", invoiceId);
        try {
            InvoiceResponseDTO response = invoiceService.viewInvoiceDetails(invoiceId);
            return new ResponseData<>(HttpStatus.OK.value(), "Invoice details fetched successfully", response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request: {}", e.getMessage());
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error fetching invoice details: {}", e.getMessage(), e);
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error fetching invoice details: " + e.getMessage(), null);
        }
    }
    @DeleteMapping("/delete/{invoiceId}")
    public ResponseData<Void> deleteInvoice(
            @PathVariable Long invoiceId) {
        log.info("Deleting invoice with ID: {}", invoiceId);
        try {

            invoiceService.deleteInvoice(invoiceId); // Truyền null cho userId vì không cần kiểm tra
            return new ResponseData<>(HttpStatus.OK.value(), "Invoice deleted successfully", null);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request: {}", e.getMessage());
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error deleting invoice: {}", e.getMessage(), e);
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error deleting invoice: " + e.getMessage(), null);
        }
    }


}
