package com.moviebookingapp.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.moviebookingapp.Model.User;
import com.moviebookingapp.Repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepo;
	
//	@Autowired
//    private BCryptPasswordEncoder passwordEncoder; // Ensure BCrypt is configured in your security configuration

    public boolean updatePassword(String username, String newPassword) {
        User user=userRepo.findByEmail(username);
        //userRepo.delete(user);
        if(user!=null) {
                user.setPassword(newPassword);
                userRepo.save(user);
                return true;
        }
            return false;
    }
	
	public User addUser(User user) {
		return userRepo.save(user);
	}
	
	public User findByEmail(String email) {
		return userRepo.findByEmail(email);
	}
	
	public User findByLoginId(String id) {
		return userRepo.findByLoginId(id);
	}

}
