package com.moviebookingapp.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.moviebookingapp.Model.User;
import com.moviebookingapp.Repository.UserRepository;
import com.moviebookingapp.Service.KafkaProducer;
import com.moviebookingapp.Service.UserService;
import com.moviebookingapp.Util.JwtUtil;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/api/v1.0/moviebooking")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
	private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
	private KafkaProducer kafkaProducer;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
          //User newuser = new User();
          User previousUser=userService.findByEmail(user.getEmail());
          User preUser=userService.findByLoginId(user.getLoginId());
          String password=user.getPassword();
          String confirmPassword=user.getConfirmPassword();
          if(user.getFirstName()==null || user.getLastName()==null || user.getEmail()==null || user.getPassword()==null || user.getConfirmPassword()==null || user.getContactNo()==null || user.getLoginId()==null) {
        	  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please fill all the mandatory fields");
          }
          else if(previousUser!=null) {
        	  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already have account");
          }
          else if(preUser!=null) {
        	  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("LoginId must be unique");
          }
          else if(!password.equals(confirmPassword)) {
        	  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password and ConfirmPassword must be same");
          }
   
//          newuser.setFirstName((user.getFirstName()));
//          newuser.setPassword(user.getPassword());
//          newuser.setLastName(user.getLastName());
//          newuser.setEmail(user.getEmail());
//          newuser.setContactNo(user.getContactNo());
//          newuser.setLoginId(user.getLoginId());
          userService.addUser(user);
          String message=user.getEmail()+" successfully registered in this application";
  		  kafkaProducer.sendMessage(message);
        return ResponseEntity.ok("User registered successfully");
    }

    @GetMapping("/login")
	public ResponseEntity<?> performLogin(@RequestParam String username, @RequestParam String password){
    	Map<String, String> mapObj = new HashMap<String, String>();
    	User newuser=userService.findByEmail(username);
		final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			String jwtToken = jwtUtil.generateToken(userDetails);
			if(username==null || password==null) {
				mapObj.put("message", "Please fill all the mandatory fields");
				return new ResponseEntity<>(mapObj, HttpStatus.BAD_REQUEST);
			}
			else if (username.equals("admin@gmail.com") & password.equals("1234@")) {
				mapObj.put("message", "Admin successfully logged in");
				mapObj.put("jwtToken", jwtToken);
				String message="Admin successfully logged in";
		  		  kafkaProducer.sendMessage(message);
				return new ResponseEntity<>(mapObj, HttpStatus.OK);
			}
			else if(newuser==null || !newuser.getPassword().equals(password)) {
				mapObj.put("message", "please enter correct username & password");
				return new ResponseEntity<>(mapObj,HttpStatus.BAD_REQUEST);
			}
			else {
				mapObj.put("message", "User successfully logged in");
				mapObj.put("jwtToken", jwtToken);
				String message=newuser.getEmail()+" successfully logged in";
		  		  kafkaProducer.sendMessage(message);
				return new ResponseEntity<>(mapObj, HttpStatus.OK);
			}

	}
    
    @GetMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
    	String user=jwtUtil.getUsernameFromToken(token.substring(7));
    	String message=user+" successfully logged logout";
		  kafkaProducer.sendMessage(message);
    	return ResponseEntity.ok("Successfully logout");
    }
    
    @GetMapping("/{username}/forgot")
    public ResponseEntity<?> forgotPassword(@PathVariable String username,@RequestParam String newPassword){
    	Map<String, String> mapObj = new HashMap<String, String>();
    	if (username != null && newPassword != null) {
			boolean response = userService.updatePassword(username, newPassword);
			if (response) {
				mapObj.put("message", "password updated successfully...");
				String message=username+" password updated successfully";
		  		  kafkaProducer.sendMessage(message);
				return new ResponseEntity<>(mapObj, HttpStatus.OK);
			} else {
				mapObj.put("message", "password updation failed...");
				return new ResponseEntity<>(mapObj, HttpStatus.CONFLICT);
			}
		}
		mapObj.put("message", "please enter your nickname & new password ...");
		return new ResponseEntity<>(mapObj, HttpStatus.CONFLICT);
    }
    
    @GetMapping("/username")
    public String getUsername(@RequestHeader("Authorization") String authorization) {
    	String token = authorization.substring(7);
    	return jwtUtil.getUsernameFromToken(token);
    }
    
    @GetMapping("/get")
    public List<User> getall(){
    	return userRepository.findAll();
    }

}
