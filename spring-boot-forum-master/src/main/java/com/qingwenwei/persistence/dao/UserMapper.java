package com.qingwenwei.persistence.dao;

import com.qingwenwei.persistence.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
	
	int save(@Param("user") User user);
	
	int update(@Param("user") User user);
	
	List<User> findAll();
	
	User findById(Long id);
	
	User findByUsername(String username);
	
	User findByEmail(String email);
	
	User findByConfirmationToken(String confirmationToken);
	
}
