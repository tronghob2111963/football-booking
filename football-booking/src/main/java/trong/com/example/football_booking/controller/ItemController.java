package trong.com.example.football_booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import trong.com.example.football_booking.dto.reponse.ItemResponseDTO;
import trong.com.example.football_booking.dto.reponse.PageResponse;
import trong.com.example.football_booking.dto.reponse.ResponseData;
import trong.com.example.football_booking.dto.request.ItemRequestDTO;
import trong.com.example.football_booking.entity.Item;
import trong.com.example.football_booking.repository.ItemRepository;
import trong.com.example.football_booking.service.ItemService;

@Slf4j
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    private final ItemRepository itemRepository;

    @PostMapping("/create-item")
    public ResponseData<?> createItem(@RequestBody Item request) {
        log.info("Creating item: {}", request);
        try {
            Item savedItem = itemRepository.save(request);
            return new ResponseData<>(HttpStatus.OK.value(), "Item created successfully", savedItem);
        } catch (Exception e) {
            log.error("Error creating item: {}", e.getMessage());
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error creating item", null);
        }
    }

    @PutMapping("/update-item/{id}")
    public ResponseData<ItemResponseDTO> updateItem(@PathVariable Long id, @RequestBody ItemRequestDTO request) {
        log.info("Updating item with id: {}", id);
        try {
            ItemResponseDTO response = itemService.updateItem(id, request);
            return new ResponseData<>(HttpStatus.OK.value(), "Item updated successfully", response);
        } catch (Exception e) {
            log.error("Error updating item: {}", e.getMessage());
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error updating item", null);
        }
    }

    @DeleteMapping("/delete-item/{id}")
    public ResponseData<Void> deleteItem(@PathVariable Long id) {
        log.info("Deleting item with id: {}", id);
        try {
            itemService.deleteItem(id);
            return new ResponseData<>(HttpStatus.OK.value(), "Item deleted successfully", null);
        } catch (Exception e) {
            log.error("Error deleting item: {}", e.getMessage());
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error deleting item", null);
        }
    }

    @GetMapping("/list-items")
    public ResponseData<?> getAllItems(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id:asc") String sortBy) {
        log.info("Fetching items with pageNo: {}, pageSize: {}, sortBy: {}", pageNo, pageSize, sortBy);
        try {
            log.info("Fetching items with pageNo: {}, pageSize: {}, sortBy: {}", pageNo, pageSize, sortBy);
            return new ResponseData<>(HttpStatus.OK.value(), "Items retrieved successfully", itemService.getAllItems(pageNo, pageSize, sortBy));
        } catch (Exception e) {
            log.error("Error fetching items: {}", e.getMessage());
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error fetching items", null);
        }
    }
}