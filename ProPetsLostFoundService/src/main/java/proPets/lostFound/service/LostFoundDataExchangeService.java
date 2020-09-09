package proPets.lostFound.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import lombok.extern.slf4j.Slf4j;
import proPets.lostFound.configuration.LostFoundConfiguration;
import proPets.lostFound.dto.PostMQDto;

@Service
@Slf4j
public class LostFoundDataExchangeService {

	@Autowired
	LostFoundConfiguration lostFoundConfiguration;
	
	@Autowired
	DataExchange dataExchange;

	public CompletableFuture<Void> sendPostData(String postId) {
		PostMQDto postMQDto = new PostMQDto(postId);
		log.info("sending...", dataExchange);
        MessageChannel messageChannel = dataExchange.outboundPost();
        return CompletableFuture.runAsync(() -> {
	    	messageChannel.send(MessageBuilder
	                .withPayload(postMQDto)
	                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
	                .build());
	    });
	}
}
