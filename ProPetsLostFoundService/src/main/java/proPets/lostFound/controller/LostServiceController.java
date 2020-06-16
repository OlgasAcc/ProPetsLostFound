package proPets.lostFound.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import proPets.lostFound.dto.NewPostDto;
import proPets.lostFound.dto.PostDto;
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
	public PostDto addPost(@RequestHeader(value = "Authorization") String authorization, Principal principal,
			@RequestBody NewPostDto newPostDto) throws Exception {		
		return lostFoundService.addPost(principal.getName(), newPostDto, flag);
	}

	@DeleteMapping("/post/{postId}")
	public PostDto removePost(@RequestHeader(value = "Authorization") String authorization, Principal principal,
			@PathVariable String postId) throws Throwable {
		return lostFoundService.removePost(principal.getName(), postId, flag);
}
	
	@PutMapping("/post/{postId}")
	public PostDto editPost(@RequestHeader(value = "Authorization") String authorization, Principal principal, @RequestBody PostEditDto postEditDto, @PathVariable String postId, String flag) throws Throwable {
		return lostFoundService.editPost(principal.getName(), postEditDto, postId, flag);
	}
	
//	@PostMapping("/post/favorites/{postId}")
//	public void makePostFavorite(@RequestHeader(value = "Authorization") String authorization, @PathVariable String postId, Principal principal) throws Throwable {
//		messagingService.makePostFavorite(principal.getName(), postId);
//	}
//	
//	@PostMapping("/post/hidden/{postId}")
//	public void makePostHidden(@RequestHeader(value = "Authorization") String authorization, @PathVariable String postId, Principal principal) throws Throwable {
//		messagingService.makePostHidden(principal.getName(), postId);
//	}
//	
//	@PostMapping("/post/unfollow/{postId}")
//	public void unfollowPostsByUser(@RequestHeader(value = "Authorization") String authorization, @PathVariable String postId, Principal principal) throws Throwable {
//		messagingService.unfollowPostsByUser(principal.getName(), postId);
//	}
//	
//	@GetMapping("/post/favorites/{userId}")
//	public Iterable<PostDto> getAllFavoritePostsByUser(@RequestHeader(value = "Authorization") String authorization, @PathVariable String userId) {
//		return messagingService.getAllFavoritePostsByUser(userId);
//	}
//	
//	@GetMapping("/post/feed")
//	public Iterable<PostDto> getUserPostFeed(@RequestHeader(value = "Authorization") String authorization, Principal principal) {
//		return messagingService.getUserPostFeed(principal.getName());
//	}
//	
//	// for front: this request is working with "remove user" in Accounting service: it is cleaning the "tail of removed user" AFTER removing the user from account db
//	@DeleteMapping("/post/cleaner")
//	public void cleanPostsAndPresenceOfRemovedUser(@RequestBody String removedUserId) {
//		messagingService.cleanPostsAndPresenceOfRemovedUser(removedUserId);
//	}

}
