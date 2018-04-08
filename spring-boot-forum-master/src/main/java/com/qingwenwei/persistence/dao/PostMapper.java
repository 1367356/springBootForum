package com.qingwenwei.persistence.dao;

import com.qingwenwei.persistence.model.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {

    int save(Post post);
    
    int delete(Long postId);
	
    int update(Post post);
    
    Post findById(Long postId);
    
	List<Post> findAll();
	
	List<Post> findPostsByCategory(String categoryName);
	
	List<Post> findPostsByUserId(Long userId);
	
	List<Post> findPostsBetweenRange(@Param("startDateStr")String startDateStr, @Param("endDateStr")String endDateStr);
	
	Long countNumOfPostsByCategoryId(Long categoryId);
	
	Long countNumOfPostsByMonth(int month);
}