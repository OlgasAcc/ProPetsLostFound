package proPets.lostFound.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import proPets.lostFound.dto.NewPostDto;
import proPets.lostFound.dto.PostDto;
import proPets.lostFound.service.LostFoundService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/found/v1")

public class FoundServiceController {
	final String flag = "found";
	
	@Autowired
	LostFoundService lostFoundService;

	@PostMapping("/post")
	public PostDto addPost(@RequestHeader(value = "Authorization") String authorization, Principal principal,
			@RequestBody NewPostDto newPostDto) throws Exception {		
		return lostFoundService.addPost(principal.getName(), newPostDto, flag);
	}


}
