package com.moviebookingapp.Model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection="tickets")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
	
	@Id
	private String id;
	private String movieName;
	private String theatreName;
	private String movieId;
	private int noOfSeat;
	private String user;
	private List<Integer> seatNo;

}
