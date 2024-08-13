package com.moviebookingapp.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.moviebookingapp.Model.Movie;

@Repository
public interface MovieRepository extends MongoRepository<Movie, String> {

	public List<Movie> findByMovieName(String moviename);
}
