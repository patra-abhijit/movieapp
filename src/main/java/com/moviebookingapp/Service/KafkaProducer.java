package com.moviebookingapp.Service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.moviebookingapp.Config.CommonConstant;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class KafkaProducer {
	
    private KafkaTemplate<String,String> kafkaTemplate;
	
	public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}
	
	
	public void sendMessage(String message) {
		kafkaTemplate.send(CommonConstant.SEAT_TOPIC, message);
		//log.info("message sent");
	
	}
}
