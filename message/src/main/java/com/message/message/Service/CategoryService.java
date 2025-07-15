package com.message.message.Service;

import com.message.message.DTO.CategoryRequest;
import com.message.message.Entity.Category;
import com.message.message.Mapper.CategoryMapper;
import com.message.message.Repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final KafkaService kafkaService;

    @Value("${kafka.bootstrap}")
    private String BOOTSTRAP;

    private static final String TOPIC = "category";

    public Category createCategory(CategoryRequest request){
        Category category = categoryRepository.save(categoryMapper.toCategory(request));
        kafkaService.sendMessage(TOPIC, "create_category", category, BOOTSTRAP);
        return category;
    }
}
