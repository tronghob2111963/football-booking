package trong.com.example.football_booking.controller;


import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import trong.com.example.football_booking.dto.reponse.ResponseData;
import trong.com.example.football_booking.entity.Field;
import trong.com.example.football_booking.repository.FieldRepository;
import trong.com.example.football_booking.service.FieldService;
import trong.com.example.football_booking.service.FileService;

@Slf4j
@RestController
@RequestMapping("/api/field")
@Validated
public class FieldController {

    @Autowired
    private FieldService fieldService;


    private FileService fileService;

    @Autowired
    private FieldRepository fieldRepository;


    @GetMapping("/list-field")
    public ResponseData<?> getallFields(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                        @Min(10) @RequestParam(defaultValue = "20", required = false) int pageSize,
                                        @RequestParam (required = false) String sortBy) {
        log.info("Getting all fields with page number: {}, page size: {}", pageNo, pageSize);
        return new ResponseData<>(HttpStatus.OK.value(), "Get all fields successfully", fieldService.getAllFields(pageNo, pageSize, sortBy));

    }

    @PostMapping("/create-field")
    public ResponseEntity<?> createField(@RequestBody Field field, @RequestParam(value = "image", required = false) MultipartFile image) throws Exception {
        log.info("Creating new field: name={}, address={}, pricePerHour={}, status={}", field.getName(), field.getAddress(), field.getPricePerHour(), field.getStatus());
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileService.uploadFile(image);
            field.setImageUrl(imageUrl);
        }
        Field savedField = fieldRepository.save(field);
        return ResponseEntity.ok(new ResponseData<>(HttpStatus.OK.value(), "Field created successfully", savedField));
    }





}
