package com.message.message.Mapper;

import com.message.message.DTO.CategoryRequest;
import com.message.message.Entity.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CategoryMapper {
    public Category toCategory(CategoryRequest request){
        if(request == null){
            log.warn("Category request is null");
            return null;
        }
        return Category.builder()
                .categoryName(request.getCategoryName())
                .description(request.getDescription())
                .build();
    }
}
