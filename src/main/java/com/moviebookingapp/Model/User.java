package com.moviebookingapp.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

	@Id
	private String id;
	private String loginId;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String confirmPassword;
	private String contactNo;
    
    
}
