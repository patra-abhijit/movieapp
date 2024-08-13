package com.moviebookingapp.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moviebookingapp.Model.Ticket;
import com.moviebookingapp.Repository.TicketRepository;

@Service
public class TicketService {
	
	@Autowired
	private TicketRepository ticketRepo;
	
	public void addTicket(Ticket ticket) {
		ticketRepo.save(ticket);
	}
	
	public List<Ticket> allTickets(){
		return ticketRepo.findAll();
	}
	
	public List<Ticket> getByMoviename(String moviename){
		return ticketRepo.findByMovieName(moviename);
	}
	
	public List<Ticket> deleteAllByMovieId(String movieId){
		return ticketRepo.deleteByMovieId(movieId);
	}

}
