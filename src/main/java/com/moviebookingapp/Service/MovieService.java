package com.moviebookingapp.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moviebookingapp.Model.Movie;
import com.moviebookingapp.Repository.MovieRepository;

@Service
public class MovieService {
    
	@Autowired
	private MovieRepository movieRepo;
	
	public List<Movie> getmovies() {
		return movieRepo.findAll();
	}
	        
	public void addUser(Movie movie) {
		movieRepo.save(movie);
	}
	
	public List<Movie> getAllByMovieName(String moviename){
		return movieRepo.findByMovieName(moviename);
	}
	
	 public Movie getMovieById(String id) {
	        Optional<Movie> movie = movieRepo.findById(id);
	        return movie.orElse(null);
	 }
}
