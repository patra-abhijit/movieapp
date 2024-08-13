package com.moviebookingapp.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection="movie")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
	
	@Id
	private String id;
	private String movieName;
	private String theatreName;
	private int noOfTicket;
	private String status;
	

}
