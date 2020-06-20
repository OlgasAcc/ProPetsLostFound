package proPets.lostFound.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import proPets.lostFound.dto.NewPostDto;
import proPets.lostFound.dto.PostEditDto;
import proPets.lostFound.service.LostFoundService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/lost/v1")

public class LostServiceController {
	final String flag = "lost";

	@Autowired
	LostFoundService lostFoundService;

	@PostMapping("/post")
	public Map<String, Object> addPost(@RequestHeader(value = "Authorization") String authorization, Principal principal,
			@RequestBody NewPostDto newPostDto) throws Exception {		
		return lostFoundService.addPost(principal.getName(), newPostDto, flag).getModel();
	}

	@DeleteMapping("/post/{postId}")
	public Map<String, Object> removePost(@RequestHeader(value = "Authorization") String authorization, Principal principal,
			@PathVariable String postId) throws Throwable {
		return lostFoundService.removePost(principal.getName(), postId, flag).getModel();
}
	
	@PutMapping("/post/{postId}")
	public Map<String, Object> editPost(@RequestHeader(value = "Authorization") String authorization, Principal principal, @RequestBody PostEditDto postEditDto, @PathVariable String postId, String flag) throws Throwable {
		return lostFoundService.editPost(principal.getName(), postEditDto, postId, flag).getModel();
	}
		
	@GetMapping("/post/feed")
	public Map<String, Object> getUserPostFeed(@RequestHeader(value = "Authorization") String authorization, @RequestParam("page") int page) {
		return lostFoundService.getPostFeed(page, flag).getModel();
	}
	
	@GetMapping("/post/feed/type")
	public Map<String, Object> getUserPostFeedByType(@RequestHeader(value = "Authorization") String authorization, @RequestParam("page") int page, @RequestParam("type") String type) {
		return lostFoundService.getPostFeedByType(page, type, flag).getModel();
	}
	
	@GetMapping("/post/feed/breed")
	public Map<String, Object> getUserPostFeedByBreed(@RequestHeader(value = "Authorization") String authorization, @RequestParam("page") int page, @RequestParam("breed") String breed) {
		return lostFoundService.getPostFeedByBreed(page, breed, flag).getModel();
	}
	
//	// for front: this request is working with "remove user" in Accounting service: it is cleaning the "tail of removed user" AFTER removing the user from account db
//	@DeleteMapping("/post/cleaner")
//	public void cleanPostsAndPresenceOfRemovedUser(@RequestBody String removedUserId) {
//		lostFoundService.cleanPostsAndPresenceOfRemovedUser(removedUserId);
//	}

}
