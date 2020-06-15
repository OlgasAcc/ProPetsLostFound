package proPets.lostFound.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import proPets.lostFound.service.LostFoundService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/found/v1")
public class FoundServiceController {

	@Autowired
	LostFoundService lostFoundService;

//	@PostMapping("/post")
//	public PostDto addPost(@RequestHeader(value = "Authorization") String authorization, Principal principal,
//			@RequestBody NewPostDto newPostDto) throws Exception {		
//		return messagingService.addPost(principal.getName(), newPostDto);
//	}


}
