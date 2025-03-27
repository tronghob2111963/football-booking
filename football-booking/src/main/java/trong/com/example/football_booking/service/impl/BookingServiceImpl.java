package trong.com.example.football_booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.misc.Unsafe;
import trong.com.example.football_booking.common.BookingStatus;
import trong.com.example.football_booking.dto.reponse.BookingResponse;
import trong.com.example.football_booking.dto.reponse.PageResponse;
import trong.com.example.football_booking.dto.request.BookingRequestDTO;
import trong.com.example.football_booking.entity.Booking;
import trong.com.example.football_booking.entity.Field;
import trong.com.example.football_booking.entity.User;
import trong.com.example.football_booking.repository.BookingRepository;
import trong.com.example.football_booking.repository.FieldRepository;
import trong.com.example.football_booking.repository.UserRepository;
import trong.com.example.football_booking.service.BookingService;
import trong.com.example.football_booking.validator.BookingValidator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final FieldRepository fieldRepository;
    private final UserRepository userRepository;
    private final BookingValidator bookingValidator;

    @Override
    public PageResponse<?> getAllBookings(int pageNo, int pageSize, String sortBy) {
        log.info("Fetching bookings with pageNo: {}, pageSize: {}, sortBy: {}", pageNo, pageSize, sortBy);

        int p = Math.max(pageNo - 1, 0); // Đảm bảo page không âm
        List<Sort.Order> sorts = new ArrayList<>();
        if (StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    sorts.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                } else {
                    sorts.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
            }
        }

        Pageable pageable = PageRequest.of(p, pageSize, Sort.by(sorts));
        Page<Booking> bookings = bookingRepository.findAll(pageable);
        List<Booking> bookingList = bookings.stream()
                .map(booking -> Booking.builder()
                        .id(booking.getId())
                        .field_id(booking.getField_id())
                        .user(booking.getUser())
                        .start_time(booking.getStart_time())
                        .end_time(booking.getEnd_time())
                        .status(BookingStatus.valueOf(booking.getStatus().name()))
                        .build())
                .toList();

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalElements(bookings.getTotalElements())
                .totalPages(bookings.getTotalPages())
                .items(bookingList)
                .build();
    }

    @Override
    public BookingResponse createBooking(BookingRequestDTO request, Long userId) throws IllegalArgumentException {
        log.info("Creating booking for userId: {}, fieldId: {}, request: {}", userId, request.getField_id(), request);

        // Kiểm tra userId
        if (userId == null) {
            log.error("User ID is null");
            throw new IllegalArgumentException("User ID cannot be null");
        }

        // Kiểm tra fieldId
        Long fieldId = request.getField_id();
        if (fieldId == null) {
            log.error("Field ID is null in request: {}", request);
            throw new IllegalArgumentException("Field ID cannot be null");
        }

        // Validate request
        bookingValidator.validateBookingRequest(request);

        // Tìm user và field
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new IllegalArgumentException("Field not found with ID: " + fieldId));

        // Lấy thời gian từ validator
        LocalDateTime startTime = bookingValidator.getStartTime(request);
        LocalDateTime endTime = bookingValidator.getEndTime(request);

        // Kiểm tra xung đột lịch
        if (isFieldBooked(fieldId, startTime, endTime)) {
            throw new IllegalArgumentException("Field is already booked for the selected time slot");
        }

        double hours = Duration.between(startTime, endTime).toMinutes() / 60.0;
        double totalPrice = hours * field.getPrice_per_hour();


        log.info("Booking cost for field {} between {} and {} is: {} VND", fieldId, startTime, endTime, totalPrice);

        // Tạo booking
        Booking booking = Booking.builder()
                .field_id(field)
                .user(user)
                .start_time(startTime)
                .end_time(endTime)
                .totalCost(totalPrice)
                .status(BookingStatus.PENDING)
                .build();

        // Lưu vào database
        Booking savedBooking = bookingRepository.save(booking);
        // Trả về thông tin booking
        BookingResponse response = new BookingResponse();
        response.setId(savedBooking.getId());
        response.setField_id(savedBooking.getField_id().getId());
        response.setUserId(savedBooking.getUser().getId());
        response.setStartTime(savedBooking.getStart_time());
        response.setEndTime(savedBooking.getEnd_time());
        response.setFieldName(savedBooking.getField_id().getName());
        response.setStatus(savedBooking.getStatus().name());
        response.setFieldAddress(savedBooking.getField_id().getAddress());
        response.setTotalCost(totalPrice);
        log.info("Booking created successfully with ID: {}", savedBooking.getId());
        return response;
    }

    @Override
    public BookingResponse updateBooking(Long bookingId, BookingRequestDTO request, Long userId) throws IllegalArgumentException {
        log.info("Updating booking with ID: {}, userId: {}, request: {}", bookingId, userId, request);

        // Kiểm tra userId
        if (userId == null) {
            log.error("User ID is null");
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (bookingId == null) {
            log.error("Booking ID is null");
            throw new IllegalArgumentException("Booking ID cannot be null");
        }

        // Tìm booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

        // Kiểm tra quyền hạn
        if (booking.getUser().getId() != userId) {
            log.error("User is not authorized to update this booking");
            throw new IllegalArgumentException("User is not authorized to update this booking");
        }

        // Kiểm tra trạng thái booking
        if (booking.getStatus() != BookingStatus.PENDING) {
            log.error("Booking is not in PENDING status");
            throw new IllegalArgumentException("Booking is not in PENDING status");
        }

        // Validate request
        bookingValidator.validateBookingRequest(request);

        // Kiểm tra fieldId
        Long fieldId = request.getField_id();
        if (fieldId == null) {
            log.error("Field ID is null in request: {}", request);
            throw new IllegalArgumentException("Field ID cannot be null");
        }

        // Tìm field
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new IllegalArgumentException("Field not found with ID: " + fieldId));

        // Lấy thời gian từ validator
        LocalDateTime startTime = bookingValidator.getStartTime(request);
        LocalDateTime endTime = bookingValidator.getEndTime(request);

        // Kiểm tra thời gian mới có nằm trong vòng 3 giờ từ thời điểm hiện tại không
        if (startTime.isBefore(LocalDateTime.now().plusHours(3))) {
            log.error("Booking cannot be updated with a start time within 3 hours from now");
            throw new IllegalArgumentException("Booking cannot be updated with a start time within 3 hours from now");
        }

        // Kiểm tra xung đột lịch (loại trừ chính booking hiện tại)
        boolean isBooked = bookingRepository.isFieldBookedExcludingBooking(fieldId, startTime, endTime, bookingId);
        if (isBooked) {
            log.error("Field is already booked for the selected time slot");
            throw new IllegalArgumentException("Field is already booked for the selected time slot");
        }

        // Tính lại số tiền
        double hours = Duration.between(startTime, endTime).toMinutes() / 60.0;
        double totalPrice = hours * field.getPrice_per_hour();
        log.info("Updated booking cost for field {} between {} and {} is: {} VND", fieldId, startTime, endTime, totalPrice);

        // Cập nhật booking
        booking.setField_id(field);
        booking.setStart_time(startTime);
        booking.setEnd_time(endTime);
        booking.setTotalCost(totalPrice);
        if (request.getStatus() != null) {
            booking.setStatus(BookingStatus.valueOf(request.getStatus()));
        }

        // Lưu vào database
        Booking updatedBooking = bookingRepository.save(booking);

        // Trả về response
        BookingResponse response = new BookingResponse();
        response.setId(updatedBooking.getId());
        response.setField_id(updatedBooking.getField_id().getId());
        response.setUserId(updatedBooking.getUser().getId());
        response.setStartTime(updatedBooking.getStart_time());
        response.setEndTime(updatedBooking.getEnd_time());
        response.setStatus(String.valueOf(updatedBooking.getStatus()));
        response.setFieldName(updatedBooking.getField_id().getName());
        response.setFieldAddress(updatedBooking.getField_id().getAddress());
        response.setTotalCost(totalPrice);
        log.info("Booking updated successfully with ID: {}", updatedBooking.getId());
        return response;
    }

    @Override
    public void cancelBooking(Long bookingId, Long userId) throws IllegalArgumentException {
        log.info("Canceling booking with ID: {}, userId: {}", bookingId, userId);
        if(userId == null) {
            log.error("User ID is null");
            throw new IllegalArgumentException("User ID cannot be null");
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

        // Kiểm tra quyền hạn
        if(booking.getUser().getId() != userId) {
            log.error("User is not authorized to cancel this booking");
            throw new IllegalArgumentException("User is not authorized to cancel this booking");
        }
        //kiem tra trang thai booking
        if(booking.getStatus() != BookingStatus.PENDING) {
            log.error("Booking is not in PENDING status");
            throw new IllegalArgumentException("Booking is not in PENDING status");
        }
        //kiem tra booking co the huy hay khong
        if(booking.getStart_time().isBefore(LocalDateTime.now().plusHours(3))) {
            log.error("Booking cannot be canceled within 1 hour of start time");
            throw new IllegalArgumentException("Booking cannot be canceled within 1 hour of start time");
        }
        //Huy booking
        booking.setStatus(BookingStatus.CANCELED);
        bookingRepository.save(booking);
        log.info("Booking canceled successfully");

    }

    @Override
    public PageResponse<?> getMyBookings(Long userId, int pageNo, int pageSize, String sortBy) {
        log.info("Fetching bookings for userId: {} with pageNo: {}, pageSize: {}, sortBy: {}", userId, pageNo, pageSize, sortBy);

        if (userId == null) {
            log.error("User ID is null");
            throw new IllegalArgumentException("User ID cannot be null");
        }

        int p = Math.max(pageNo - 1, 0);
        List<Sort.Order> sorts = new ArrayList<>();
        if (StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    sorts.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                } else {
                    sorts.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
            }
        }
        Pageable   pageable = PageRequest.of(p, pageSize, Sort.by(sorts));
        Page<Booking> bookings = bookingRepository.findAllByUser_id(userId, pageable);
        List<Booking> bookingList = bookings.stream()
                .map(booking -> Booking.builder()
                        .id(booking.getId())
                        .field_id(booking.getField_id())
                        .start_time(booking.getStart_time())
                        .end_time(booking.getEnd_time())
                        .totalCost(booking.getTotalCost())
                        .status(BookingStatus.valueOf(booking.getStatus().name()))
                        .build())
                .toList();
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalElements(bookings.getTotalElements())
                .totalPages(bookings.getTotalPages())
                .items(bookingList)
                .build();
    }



    private Boolean isFieldBooked(Long field_id, LocalDateTime start_time, LocalDateTime end_time) {
        log.info("Checking if field {} is booked between {} and {}", field_id, start_time, end_time);
        boolean isBooked = bookingRepository.isFieldBooked(field_id, start_time, end_time);
        log.info("Field booked: {}", isBooked);
        return isBooked;
    }


}
//package trong.com.example.football_booking.service.impl;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//import trong.com.example.football_booking.common.BookingStatus;
//import trong.com.example.football_booking.dto.reponse.BookingResponse;
//import trong.com.example.football_booking.dto.reponse.PageResponse;
//import trong.com.example.football_booking.dto.request.BookingRequestDTO;
//import trong.com.example.football_booking.entity.Booking;
//import trong.com.example.football_booking.entity.Field;
//import trong.com.example.football_booking.entity.User;
//import trong.com.example.football_booking.repository.BookingRepository;
//import trong.com.example.football_booking.repository.FieldRepository;
//import trong.com.example.football_booking.repository.UserRepository;
//import trong.com.example.football_booking.service.BookingService;
//import trong.com.example.football_booking.validator.BookingValidator;
//
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class BookingServiceImpl implements BookingService {
//
//    private final BookingRepository bookingRepository;
//    private final FieldRepository fieldRepository;
//    private final UserRepository userRepository;
//    private final BookingValidator bookingValidator;
//
//    @Override
//    public PageResponse<?> getAllBookings(int pageNo, int pageSize, String sortBy) {
//        log.info("Fetching bookings with pageNo: {}, pageSize: {}, sortBy: {}", pageNo, pageSize, sortBy);
//
//        int p = Math.max(pageNo - 1, 0); // Đảm bảo page không âm
//        List<Sort.Order> sorts = new ArrayList<>();
//        if (StringUtils.hasLength(sortBy)) {
//            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
//            Matcher matcher = pattern.matcher(sortBy);
//            if (matcher.find()) {
//                if (matcher.group(3).equalsIgnoreCase("asc")) {
//                    sorts.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
//                } else {
//                    sorts.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
//                }
//            }
//        }
//
//        Pageable pageable = PageRequest.of(p, pageSize, Sort.by(sorts));
//        Page<Booking> bookings = bookingRepository.findAll(pageable);
//        List<Booking> bookingList = bookings.stream()
//                .map(booking -> Booking.builder()
//                        .id(booking.getId())
//                        .field_id(booking.getField_id())
//                        .user(booking.getUser())
//                        .start_time(booking.getStart_time())
//                        .end_time(booking.getEnd_time())
//                        .status(BookingStatus.valueOf(booking.getStatus().name()))
//                        .build())
//                .toList();
//
//        return PageResponse.builder()
//                .pageNo(pageNo)
//                .pageSize(pageSize)
//                .totalElements(bookings.getTotalElements())
//                .totalPages(bookings.getTotalPages())
//                .items(bookingList)
//                .build();
//    }
//
////    @Override
////    public Long createBooking(BookingRequestDTO request, Long userId) throws IllegalArgumentException {
////        log.info("Creating booking for userId: {}, fieldId: {}, request: {}", userId, request.getField_id(), request);
////
////        // Kiểm tra userId
////        if (userId == null) {
////            log.error("User ID is null");
////            throw new IllegalArgumentException("User ID cannot be null");
////        }
////
////        // Kiểm tra fieldId
////        Long fieldId = request.getField_id();
////        if (fieldId == null) {
////            log.error("Field ID is null in request: {}", request);
////            throw new IllegalArgumentException("Field ID cannot be null");
////        }
////
////        // Validate request
////        bookingValidator.validateBookingRequest(request);
////
////        // Tìm user và field
////        User user = userRepository.findById(userId)
////                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
////        Field field = fieldRepository.findById(fieldId)
////                .orElseThrow(() -> new IllegalArgumentException("Field not found with ID: " + fieldId));
////
////        // Lấy thời gian từ validator
////        LocalDateTime startTime = bookingValidator.getStartTime(request);
////        LocalDateTime endTime = bookingValidator.getEndTime(request);
////
////        // Kiểm tra xung đột lịch
////        if (isFieldBooked(fieldId, startTime, endTime)) {
////            throw new IllegalArgumentException("Field is already booked for the selected time slot");
////        }
////
////        // Tạo booking
////        Booking booking = Booking.builder()
////                .field_id(field)
////                .user(user)
////                .start_time(startTime)
////                .end_time(endTime)
////                .status(BookingStatus.PENDING)
////                .build();
////
////        // Lưu vào database
////        Booking savedBooking = bookingRepository.save(booking);
////        log.info("Booking created successfully with ID: {}", savedBooking.getId());
////        return savedBooking.getId();
////    }
//@Override
//public BookingResponse createBooking(BookingRequestDTO request, Long userId) throws IllegalArgumentException {
//    log.info("Creating booking for userId: {}, fieldId: {}, request: {}", userId, request.getField_id(), request);
//
//    // Kiểm tra userId
//    if (userId == null) {
//        log.error("User ID is null");
//        throw new IllegalArgumentException("User ID cannot be null");
//    }
//
//    // Kiểm tra fieldId
//    Long fieldId = request.getField_id();
//    if (fieldId == null) {
//        log.error("Field ID is null in request: {}", request);
//        throw new IllegalArgumentException("Field ID cannot be null");
//    }
//
//    // Validate request
//    bookingValidator.validateBookingRequest(request);
//
//    // Tìm user và field
//    User user = userRepository.findById(userId)
//            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
//    Field field = fieldRepository.findById(fieldId)
//            .orElseThrow(() -> new IllegalArgumentException("Field not found with ID: " + fieldId));
//
//    // Lấy thời gian từ validator
//    LocalDateTime startTime = bookingValidator.getStartTime(request);
//    LocalDateTime endTime = bookingValidator.getEndTime(request);
//
//    // Kiểm tra xung đột lịch
//    if (isFieldBooked(fieldId, startTime, endTime)) {
//        throw new IllegalArgumentException("Field is already booked for the selected time slot");
//    }
//
//    // Tính số tiền
//    double hours = Duration.between(startTime, endTime).toMinutes() / 60.0;
//    double totalPrice = hours * field.getPrice_per_hour();
//
//    log.info("Booking cost for field {} between {} and {} is: {} VND", fieldId, startTime, endTime, totalPrice);
//
//    // Tạo booking
//    Booking booking = Booking.builder()
//            .field_id(field)
//            .user(user)
//            .start_time(startTime)
//            .end_time(endTime)
//            .status(BookingStatus.PENDING)
//            .build();
//
//    // Lưu vào database
//    Booking savedBooking = bookingRepository.save(booking);
//
//    // Trả về thông tin booking
//    BookingResponse response = new BookingResponse();
//    response.setId(savedBooking.getId());
//    response.setField_id(savedBooking.getField_id().getId());
//    response.setUserId(savedBooking.getUser().getId());
//    response.setStartTime(String.valueOf(savedBooking.getStart_time()));
//    response.setEndTime(String.valueOf(savedBooking.getEnd_time()));
//    response.setStatus(String.valueOf(savedBooking.getStatus()));
//
//    response.setTotalCost(totalPrice);
//    log.info("Booking created successfully with ID: {}", savedBooking.getId());
//    return response;
//}
//
//    @Override
//    public BookingResponse updateBooking(Long id, BookingRequestDTO request, Long userId) throws IllegalArgumentException {
//        return null;
//    }
//
//    @Override
//    public void cancelBooking(Long bookingId, Long userId) throws IllegalArgumentException {
//        log.info("Canceling booking with ID: {}, userId: {}", bookingId, userId);
//        if(userId == null) {
//            log.error("User ID is null");
//            throw new IllegalArgumentException("User ID cannot be null");
//        }
//        Booking booking = bookingRepository.findById(bookingId)
//                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));
//
//        // Kiểm tra quyền hạn
//        if(booking.getUser().getId() != userId) {
//            log.error("User is not authorized to cancel this booking");
//            throw new IllegalArgumentException("User is not authorized to cancel this booking");
//        }
//        //kiem tra trang thai booking
//        if(booking.getStatus() != BookingStatus.PENDING) {
//            log.error("Booking is not in PENDING status");
//            throw new IllegalArgumentException("Booking is not in PENDING status");
//        }
//        //kiem tra booking co the huy hay khong
//        if(booking.getStart_time().isBefore(LocalDateTime.now().plusHours(3))) {
//            log.error("Booking cannot be canceled within 1 hour of start time");
//            throw new IllegalArgumentException("Booking cannot be canceled within 1 hour of start time");
//        }
//        //Huy booking
//        booking.setStatus(BookingStatus.CANCELED);
//        bookingRepository.save(booking);
//        log.info("Booking canceled successfully");
//
//    }
//
//    private Boolean isFieldBooked(Long field_id, LocalDateTime start_time, LocalDateTime end_time) {
//        log.info("Checking if field {} is booked between {} and {}", field_id, start_time, end_time);
//        boolean isBooked = bookingRepository.isFieldBooked(field_id, start_time, end_time);
//        log.info("Field booked: {}", isBooked);
//        return isBooked;
//    }
//
//
//}