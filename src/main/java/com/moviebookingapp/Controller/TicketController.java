package com.moviebookingapp.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moviebookingapp.Model.Movie;
import com.moviebookingapp.Model.Seat;
import com.moviebookingapp.Model.Ticket;
import com.moviebookingapp.Repository.MovieRepository;
import com.moviebookingapp.Repository.SeatRepository;
import com.moviebookingapp.Service.KafkaProducer;
import com.moviebookingapp.Service.MovieService;
import com.moviebookingapp.Service.SeatService;
import com.moviebookingapp.Service.TicketService;
import com.moviebookingapp.Util.JwtUtil;

@RestController
@RequestMapping("/api/v1.0/moviebooking")
public class TicketController {
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private MovieService movieSer;
	
	@Autowired
	private TicketService ticketSer;
	
	@Autowired
	private SeatService seatSer;
	
	@Autowired
	private SeatRepository seatRepo;
	
	@Autowired
	private KafkaProducer kafkaProducer;
	
	@PostMapping("/moviename/add")
	public ResponseEntity<?> bookTicket(@RequestHeader("Authorization") String token,@RequestBody Ticket ticket){
		String id=ticket.getMovieName()+"-"+ticket.getTheatreName();
		Seat seat=seatSer.getSeatById(id);
		String newtoken = token.substring(7);
    	String user= jwtUtil.getUsernameFromToken(newtoken);
    	Movie movie=movieSer.getMovieById(id);
		if(movie==null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The movie is not available in this Theatre");
		}
		else if(movie.getNoOfTicket()==0) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("sold out");
		}
		else if(movie.getNoOfTicket()<ticket.getNoOfSeat()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Seats are not available");
		}
		else if(ticket.getNoOfSeat()!=ticket.getSeatNo().size()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please enter exact seat details");
		}
		if(seat!=null) {
		for(int i=0;i<ticket.getSeatNo().size();i++) {
			if(seat.getSeats().indexOf(ticket.getSeatNo().get(i))!=-1) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please enter exact seat details");
			}
		}
		Seat newseat=new Seat();
		newseat.setId(id);
		seat.getSeats().addAll(ticket.getSeatNo());
		newseat.setSeats(seat.getSeats());
		seatSer.addSeat(newseat);
		//Ticket newTicket=new Ticket();
//		ticket.setMovieName(ticket.getMovieName());
//		ticket.setTheatreName(ticket.getTheatreName());
		ticket.setMovieId(id);
		ticket.setUser(user);
//		ticket.setNoOfSeat(ticket.getNoOfSeat());
//		ticket.setSeatNo(ticket.getSeatNo());
		ticketSer.addTicket(ticket);
		Movie newmovie=new Movie();
		newmovie.setId(id);
		newmovie.setMovieName(ticket.getMovieName());
		newmovie.setTheatreName(ticket.getTheatreName());
		newmovie.setNoOfTicket(movie.getNoOfTicket()-ticket.getNoOfSeat());
		newmovie.setStatus(movie.getStatus());
		movieSer.addUser(newmovie);
		String availableSeats="Available Seats "+String.valueOf(newmovie.getNoOfTicket());
		String bookedSeats=String.valueOf(newseat.getSeats().size())+" seats are booked";
		kafkaProducer.sendMessage(availableSeats);
		kafkaProducer.sendMessage(bookedSeats);
		return ResponseEntity.status(HttpStatus.OK).body("Seat booking successful");
		}
		Seat newseat=new Seat();
		newseat.setId(id);
		newseat.setSeats(ticket.getSeatNo());
		seatSer.addSeat(newseat);
		//Ticket newTicket=new Ticket();
//		ticket.setMovieName(ticket.getMovieName());
//		ticket.setTheatreName(ticket.getTheatreName());
		ticket.setMovieId(id);
		ticket.setUser(user);
//		ticket.setNoOfSeat(ticket.getNoOfSeat());
//		ticket.setSeatNo(ticket.getSeatNo());
		ticketSer.addTicket(ticket);
		Movie newmovie=new Movie();
		newmovie.setId(id);
		newmovie.setMovieName(ticket.getMovieName());
		newmovie.setTheatreName(ticket.getTheatreName());
		newmovie.setNoOfTicket(movie.getNoOfTicket()-ticket.getNoOfSeat());
		movieSer.addUser(newmovie);
		String availableSeats="Available Seats "+String.valueOf(newmovie.getNoOfTicket());
		String bookedSeats=String.valueOf(newseat.getSeats().size())+" seats are booked";
		kafkaProducer.sendMessage(availableSeats);
		kafkaProducer.sendMessage(bookedSeats);
		return ResponseEntity.status(HttpStatus.OK).body("Seat booking successful");
		
	}
	
	
	@GetMapping("/viewBookedTickets")
	public ResponseEntity<?> viewBookedTicket(@RequestHeader("Authorization") String token){
		String newtoken = token.substring(7);
    	String user= jwtUtil.getUsernameFromToken(newtoken);
    	if(user.equals("admin@gmail.com")) {
		List<Ticket> tickets=ticketSer.allTickets();
		return ResponseEntity.ok(tickets);
		
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are unauthorized for this featre");
	}
	@GetMapping("/viewBookedTickets/{moviename}")
	public ResponseEntity<?> viewBookedTicketByMovieName(@RequestHeader("Authorization") String token,@PathVariable String moviename){
		String newtoken = token.substring(7);
    	String user= jwtUtil.getUsernameFromToken(newtoken);
    	if(user.equals("admin@gmail.com")) {
			List<Ticket> tickets=ticketSer.getByMoviename(moviename);
			return ResponseEntity.ok(tickets);
			
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are unauthorized for this featre");
	}
	
}
