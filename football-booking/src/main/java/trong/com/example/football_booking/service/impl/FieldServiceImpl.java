package trong.com.example.football_booking.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import trong.com.example.football_booking.dto.reponse.FieldReponse;
import trong.com.example.football_booking.dto.reponse.PageResponse;
import trong.com.example.football_booking.entity.Field;
import trong.com.example.football_booking.repository.FieldRepository;
import trong.com.example.football_booking.service.FieldService;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@AllArgsConstructor
public class FieldServiceImpl implements FieldService {

    private FieldRepository fieldRepository;

    @Override
    public PageResponse<?> getAllFields(int pageNo, int pageSizem ,String sortBy) {
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
        Pageable pageable = PageRequest.of(p, pageSizem, Sort.by(sorts));
        Page<Field> fields = fieldRepository.findAll(pageable);
        List<FieldReponse> fieldReponses = fields.stream().map(field -> FieldReponse.builder()
                .id(field.getId())
                .name(field.getName())
                .address(field.getAddress())
                .image(field.getImageUrl())
                .price_per_hour(field.getPricePerHour().toString())
                .status(field.getStatus().name())
                .build()).toList();
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSizem)
                .totalElements(fields.getTotalElements())
                .totalPages(fields.getTotalPages())
                .items(fieldReponses)
                .build();
    }

    @Override
    public Field getFieldById(Long id) {
        return fieldRepository.findById(id).orElseThrow(() -> new RuntimeException("Field not found with id: " + id));
    }


}
