package proPets.lostFound.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

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

	public void sendPostData(String postId) throws JsonProcessingException {
		PostMQDto postMQDto = new PostMQDto(postId);
		log.info("Sending greetings {}", dataExchange);
        MessageChannel messageChannel = dataExchange.outboundPost();
        messageChannel.send(MessageBuilder
                .withPayload(postMQDto)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build());
	}
}
