package com.qingwenwei.service;

import com.qingwenwei.web.dto.PostDto;

import java.util.Map;

public interface DashboardService {
	
	Map<String, Object> getDashboard(String tab, String start, String end);
	
	Map<String, Object> getPostEditJson(Long postId);
	
	Map<String, Object> editPost(PostDto newPostForm);
	
	Map<String, Object> getNumOfPostsByCategoriesForPieChart();
	
	Map<String, Object> getNumOfPostsByMonthForBarChart();
	
}
