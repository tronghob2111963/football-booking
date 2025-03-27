package trong.com.example.football_booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import trong.com.example.football_booking.dto.reponse.ItemResponseDTO;
import trong.com.example.football_booking.dto.reponse.PageResponse;
import trong.com.example.football_booking.dto.request.ItemRequestDTO;
import trong.com.example.football_booking.entity.Item;
import trong.com.example.football_booking.repository.ItemRepository;
import trong.com.example.football_booking.service.ItemService;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

//    @Override
//    public ItemResponseDTO createItem(ItemRequestDTO request) {
//        log.info("Creating item: {}", request);
//
//        // Validate request
//        if (request.getName() == null || request.getName().isEmpty()) {
//            throw new IllegalArgumentException("Item name cannot be null or empty");
//        }
//        if (request.getPrice() == null || request.getPrice() <= 0) {
//            throw new IllegalArgumentException("Price must be greater than 0");
//        }
//        if (request.getStock() == null || request.getStock() < 0) {
//            throw new IllegalArgumentException("Stock cannot be negative");
//        }
//
//        // Tạo item mới
//        Item item = new Item();
//        item.setName(request.getName());
//        item.setPrice(request.getPrice());
//        item.setStock(request.getStock());
//        item.setImageUrl(request.getImageUrl());
//
//        // Lưu vào database
//        Item savedItem = itemRepository.save(item);
//        log.info("Item created successfully with ID: {}", savedItem.getId());
//
//        // Trả về response
//        return mapToResponse(savedItem);
//    }

    @Override
    public ItemResponseDTO updateItem(Long id, ItemRequestDTO request) {
        log.info("Updating item with ID: {}, request: {}", id, request);

        // Tìm item
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + id));

        // Validate request
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be null or empty");
        }
        if (request.getPrice() == null || request.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        if (request.getStock() == null || request.getStock() < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }

        // Cập nhật item
        item.setName(request.getName());
        item.setPrice(request.getPrice());
        item.setStock(request.getStock());
        item.setImageUrl(request.getImageUrl());

        // Lưu vào database
        Item updatedItem = itemRepository.save(item);
        log.info("Item updated successfully with ID: {}", updatedItem.getId());

        // Trả về response
        return ItemResponseDTO.builder()
                .id(updatedItem.getId())
                .name(updatedItem.getName())
                .price(updatedItem.getPrice())
                .stock(updatedItem.getStock())
                .imageUrl(updatedItem.getImageUrl())
                .build();
    }

    @Override
    public void deleteItem(Long id) {
        log.info("Deleting item with ID: {}", id);

        // Kiểm tra item tồn tại
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + id));

        // Xóa item
        itemRepository.delete(item);
        log.info("Item deleted successfully with ID: {}", id);
    }

    @Override
    public PageResponse<?> getAllItems(int pageNo, int pageSize, String sortBy) {
        int p =0;
        if(pageNo > 0){
            p = pageNo - 1;
        }
        List<Sort.Order> sorts = new ArrayList<>();
        if(StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if(matcher.find()){
                if(matcher.group(3).equalsIgnoreCase("asc")){
                    sorts.add(new Sort.Order(Sort.Direction.ASC,matcher.group(1)));
                }else {
                    sorts.add(new Sort.Order(Sort.Direction.DESC,matcher.group(1)));
                }
            }
        }
        Pageable pageable = PageRequest.of(p, pageSize, Sort.by(sorts));
        Page<Item> items = itemRepository.findAll(pageable);
        List<ItemResponseDTO> itemResponse =items.stream().map(item -> ItemResponseDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .stock(item.getStock())
                .imageUrl(item.getImageUrl())
                .build()).toList();
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalElements(items.getTotalElements())
                .totalPages(items.getTotalPages())
                .items(items)
                .build();
    }

//    private ItemResponseDTO mapToResponse(Item item) {
//        ItemResponseDTO response = new ItemResponseDTO();
//        response.setId(item.getId());
//        response.setName(item.getName());
//        response.setPrice(item.getPrice());
//        response.setStock(item.getStock());
//        response.setImageUrl(item.getImageUrl());
//        return response;
//    }
}
