package com.qingwenwei.persistence.dao;

import com.qingwenwei.persistence.model.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
	
	Category findByName(String categoryName);
	
	int save(Category category);
	
	List<Category> findAll();
	
}
