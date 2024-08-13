package com.moviebookingapp.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moviebookingapp.Exception.MovieNotFoundException;
import com.moviebookingapp.Model.Movie;
import com.moviebookingapp.Model.Seat;
import com.moviebookingapp.Model.Ticket;
import com.moviebookingapp.Repository.MovieRepository;
import com.moviebookingapp.Repository.SeatRepository;
import com.moviebookingapp.Repository.TicketRepository;
import com.moviebookingapp.Service.KafkaProducer;
import com.moviebookingapp.Service.MovieService;
import com.moviebookingapp.Service.TicketService;
import com.moviebookingapp.Util.JwtUtil;

@RestController
@RequestMapping("/api/v1.0/moviebooking")
public class MovieController {
	
	@Autowired
    private JwtUtil jwtUtil;

	@Autowired
	private MovieService movieService;
	
	@Autowired
	private MovieRepository movieRepo;
	
	@Autowired
	private SeatRepository seatRepo;
	
	@Autowired
	private TicketRepository ticketRepo;
	
	@Autowired
	private KafkaProducer kafkaProducer;
	
	@Autowired
	private TicketService ticketSer;
	
	
	@GetMapping("/all")
	public List<Movie> getallMovie(@RequestHeader("Authorization") String token) throws MovieNotFoundException{
		String user= jwtUtil.getUsernameFromToken(token.substring(7));
		String message=user+" check all the movie details";
		kafkaProducer.sendMessage(message);
		return movieService.getmovies();
	}
	
	@PostMapping("/add")
	public ResponseEntity<?> addNewMovie(@RequestHeader("Authorization") String token,@RequestBody Movie movie){
		String newtoken = token.substring(7);
    	String user= jwtUtil.getUsernameFromToken(newtoken);
		if(!user.equals("admin@gmail.com")) {
			String message=user+" tried a unauthorized feature";
			kafkaProducer.sendMessage(message);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized for this feature");
		}
		//Movie newmovie=new Movie();
		movie.setId(movie.getMovieName()+"-"+movie.getTheatreName());
//		movie.setMovieName(movie.getMovieName());
//		movie.setTheatreName(movie.getTheatreName());
//		movie.setNoOfTicket(movie.getNoOfTicket());
		movieService.addUser(movie);
		String message="Admin added new movie";
		kafkaProducer.sendMessage(message);
		return ResponseEntity.ok("Movie updated successfully");	
		}
	
	@PutMapping("/{moviename}/update/{theatrename}")
	public ResponseEntity<?> updateStatus(@RequestHeader("Authorization") String token,@PathVariable String moviename,@PathVariable String theatrename) throws MovieNotFoundException{
		String id=moviename+"-"+theatrename;
		String newtoken = token.substring(7);
    	String user= jwtUtil.getUsernameFromToken(newtoken);
		if(user.equals("admin@gmail.com")) {
			Movie movie=movieService.getMovieById(id);
			if(movie.getNoOfTicket()==0) {
				movie.setStatus("SOLD OUT");
			}
			else {
				movie.setStatus("BOOK ASAP");
			}
			movieService.addUser(movie);
			String message="Status is updated by Admin as "+movie.getStatus()+" for "+movie.getMovieName()+" at "+movie.getTheatreName();
			kafkaProducer.sendMessage(message);
			return ResponseEntity.ok("Status updated");
		}
		String message=user+" tried a unauthorized feature";
		kafkaProducer.sendMessage(message);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized");
	}
	
	@GetMapping("/{moviename}/check/{theatrename}")
	public ResponseEntity<?> checkStatus(@RequestHeader("Authorization") String token,@PathVariable String moviename,@PathVariable String theatrename) throws MovieNotFoundException{
		String id=moviename+"-"+theatrename;
		Movie movie=movieService.getMovieById(id);
		String status=movie.getStatus();
		String user= jwtUtil.getUsernameFromToken(token.substring(7));
		String message=user+" checked "+moviename+" details at " +theatrename;
		kafkaProducer.sendMessage(message);
		return ResponseEntity.ok().body(status);
	}
	
	@GetMapping("/movies/search/{moviename}")
	public List<Movie> getByMovieName(@RequestHeader("Authorization") String token,@PathVariable String moviename) throws MovieNotFoundException{
		String user= jwtUtil.getUsernameFromToken(token.substring(7));
		String message=user+" checked "+moviename+" details";
		kafkaProducer.sendMessage(message);
		return movieService.getAllByMovieName(moviename);
		
	}
	
	@DeleteMapping("/{moviename}/delete/{theatrename}")
	public ResponseEntity<?> deleteMovie(@RequestHeader("Authorization") String token,@PathVariable String moviename,@PathVariable String theatrename) throws MovieNotFoundException{
		String id=moviename+"-"+theatrename;
		String newtoken = token.substring(7);
    	String user= jwtUtil.getUsernameFromToken(newtoken);
		if(user.equals("admin@gmail.com")) {
		movieRepo.deleteById(id);
		seatRepo.deleteById(id);
		ticketSer.deleteAllByMovieId(id);
		return ResponseEntity.ok("movie deleted");
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized for this feature");
	}
	
}
