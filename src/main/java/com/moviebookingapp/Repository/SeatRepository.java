package com.moviebookingapp.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.moviebookingapp.Model.Seat;

@Repository
public interface SeatRepository extends MongoRepository<Seat, String>{

}
