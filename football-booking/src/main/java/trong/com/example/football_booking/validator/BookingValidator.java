package trong.com.example.football_booking.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import trong.com.example.football_booking.dto.request.BookingRequestDTO;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class BookingValidator {

    public void validateBookingRequest(BookingRequestDTO request) throws IllegalArgumentException {
        log.info("Validating booking request: {}", request);

        if (request == null) {
            throw new IllegalArgumentException("Booking request cannot be null");
        }

        String bookingDate = request.getBookingDate();
        String startTime = request.getStart_time();
        String endTime = request.getEnd_time();

        if (bookingDate == null || bookingDate.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking date cannot be null or empty");
        }
        if (startTime == null || startTime.trim().isEmpty()) {
            throw new IllegalArgumentException("Start time cannot be null or empty");
        }
        if (endTime == null || endTime.trim().isEmpty()) {
            throw new IllegalArgumentException("End time cannot be null or empty");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String startDateTimeStr = bookingDate + " " + startTime;
        String endDateTimeStr = bookingDate + " " + endTime;
        log.info("Parsing start datetime: {}, end datetime: {}", startDateTimeStr, endDateTimeStr);

        LocalDateTime start = LocalDateTime.parse(startDateTimeStr, formatter);
        LocalDateTime end = LocalDateTime.parse(endDateTimeStr, formatter);

        if (start.isAfter(end) || start.isEqual(end)) {
            throw new IllegalArgumentException("Start time must be before end time.");
        }

        Duration duration = Duration.between(start, end);
        if (duration.toMinutes() <= 60) {
            throw new IllegalArgumentException("Booking duration must be greater than 1 hour.");
        }
    }

    public LocalDateTime getStartTime(BookingRequestDTO request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String startDateTimeStr = request.getBookingDate() + " " + request.getStart_time();
        log.info("Getting start time from: {}", startDateTimeStr);
        return LocalDateTime.parse(startDateTimeStr, formatter);
    }

    public LocalDateTime getEndTime(BookingRequestDTO request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String endDateTimeStr = request.getBookingDate() + " " + request.getEnd_time();
        log.info("Getting end time from: {}", endDateTimeStr);
        return LocalDateTime.parse(endDateTimeStr, formatter);
    }
}
//package trong.com.example.football_booking.validator;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import trong.com.example.football_booking.dto.request.BookingRequestDTO;
//
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//@Slf4j
//@Component
//public class BookingValidator {
//    public void validateBookingRequest(BookingRequestDTO request) throws IllegalArgumentException {
//        log.info("Validating booking request: {}" ,request);
//        if(request == null){
//            throw new IllegalArgumentException("Booking request cannot be null");
//        }
//
//        DateTimeFormatter   dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime startTime = LocalDateTime.parse(request.getStartTime(), dtf);
//        LocalDateTime endTime = LocalDateTime.parse(request.getEndTime(), dtf);
//
//        if(startTime.isAfter(endTime) || startTime.isEqual(endTime)){
//            throw new IllegalArgumentException("Start time must be before end time");
//        }
//        Duration duration = Duration.between(startTime, endTime);
//        if(duration.toMinutes() < 60){
//            throw new IllegalArgumentException("Duration must be at least 30 minutes");
//        }
//    }
//    public LocalDateTime getStartTime(BookingRequestDTO request) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//        return LocalDateTime.parse(request.getBookingDate() + " " + request.getStartTime(), formatter);
//    }
//
//    public LocalDateTime getEndTime(BookingRequestDTO request) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//        return LocalDateTime.parse(request.getBookingDate() + " " + request.getEndTime(), formatter);
//    }
//
//}
