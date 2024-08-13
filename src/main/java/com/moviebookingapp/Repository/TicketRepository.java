package com.moviebookingapp.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.moviebookingapp.Model.Ticket;

@Repository
public interface TicketRepository extends MongoRepository<Ticket, String>{

	public List<Ticket> findByMovieName(String moviename);
	
	public List<Ticket> deleteByMovieId(String movieId);
}
