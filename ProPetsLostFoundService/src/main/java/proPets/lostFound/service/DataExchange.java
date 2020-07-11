package proPets.lostFound.service;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface DataExchange {
	
	String OUTPUT = "addEditPost";
    
    @Output(OUTPUT)
    MessageChannel outboundPost();

}
