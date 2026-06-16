package com.example.userservice.dao;

import com.example.userservice.entity.user.UserImage;
import org.springframework.data.repository.CrudRepository;

public interface ImageRepository extends CrudRepository<UserImage, Long> {

}
