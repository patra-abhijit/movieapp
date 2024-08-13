package com.moviebookingapp.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moviebookingapp.Model.Movie;
import com.moviebookingapp.Model.Seat;
import com.moviebookingapp.Repository.SeatRepository;

@Service
public class SeatService {
	
	@Autowired
	private SeatRepository seatRepo;
	
	public void addSeat(Seat seat) {
		seatRepo.save(seat);
	}
	
	public Seat getSeatById(String id) {
		Optional<Seat> seat = seatRepo.findById(id);
        return seat.orElse(null);
	}

}
