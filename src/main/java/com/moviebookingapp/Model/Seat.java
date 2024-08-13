package com.moviebookingapp.Model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection="Seat")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Seat {
	
	private String id;
	private List<Integer> seats;

}
