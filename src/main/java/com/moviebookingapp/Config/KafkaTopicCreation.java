package com.moviebookingapp.Config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
@Configuration
public class KafkaTopicCreation {
	
	public NewTopic createSeatTopic() {
		return TopicBuilder.name(CommonConstant.SEAT_TOPIC).build();
	}
	
	public NewTopic createStatusTopic() {
		return TopicBuilder.name(CommonConstant.STATUS_TOPIC).build();
	}

}
