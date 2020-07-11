package proPets.lostFound.configuration;

import org.springframework.cloud.stream.annotation.EnableBinding;

import proPets.lostFound.service.DataExchange;

@EnableBinding(DataExchange.class)
public class PostOutboundConfig {
	
}