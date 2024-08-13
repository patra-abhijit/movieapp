package com.moviebookingapp.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.moviebookingapp.Model.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
	
    public User findByEmail(String email);
    public User findByLoginId(String loginId);
}
