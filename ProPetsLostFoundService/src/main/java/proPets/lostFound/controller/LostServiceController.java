package proPets.lostFound.controller;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import proPets.lostFound.configuration.BeanConfiguration;
import proPets.lostFound.configuration.LostFoundConfiguration;
import proPets.lostFound.dto.NewPostDto;
import proPets.lostFound.dto.PostDto;
import proPets.lostFound.dto.PostEditDto;
import proPets.lostFound.dto.UserRemoveDto;
import proPets.lostFound.service.LostFoundService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/lostFound/lost/v1")

public class LostServiceController {
	final String flag = "lost";

	@Autowired
	LostFoundService lostFoundService;

	@Autowired
	LostFoundConfiguration lostFoundConfiguration;

	@Autowired
	private HttpServletRequest requestContext;

	@RefreshScope
	@GetMapping("/config")
	public BeanConfiguration getRefreshedData() {
		return new BeanConfiguration(lostFoundConfiguration.getQuantity(), lostFoundConfiguration.getBaseSearchUrl());
	}

	@PostMapping("/post")
	public List<PostDto> addPost(@RequestBody NewPostDto newPostDto) throws Exception {
		String authorId = requestContext.getHeader("authorId");
		System.out.println(authorId);
		return lostFoundService.addPost(authorId, newPostDto, flag);
	}

	@DeleteMapping("/post/{postId}")
	public List<PostDto> removePost(@PathVariable String postId) throws Throwable {
		String authorId = requestContext.getHeader("authorId");
		return lostFoundService.removePost(authorId, postId, flag);
	}

	@PutMapping("/post/{postId}")
	public List<PostDto> editPost(@RequestBody PostEditDto postEditDto, @PathVariable String postId, String flag)
			throws Throwable {
		String authorId = requestContext.getHeader("authorId");
		return lostFoundService.editPost(authorId, postEditDto, postId, flag);
	}

	@GetMapping("/post/feed")
	public List<PostDto> getUserPostsFeed(@RequestParam("page") int page)
			throws InterruptedException, ExecutionException {
		return lostFoundService.getPostsFeed(page, flag).get();
	}

	@GetMapping("/post/feed/type")
	public List<PostDto> getUserPostsFeedMatchingByType(@RequestParam("page") int page,
			@RequestParam("type") String type) {
		return lostFoundService.getPostsFeedMatchingByType(page, type, flag);
	}

	@GetMapping("/post/feed/breed")
	public List<PostDto> getUserPostsFeedMatchingByBreed(@RequestParam("page") int page,
			@RequestParam("breed") String breed) {
		return lostFoundService.getPostsFeedMatchingByBreed(page, breed, flag);
	}

	@GetMapping("/post/feed/location")
	public List<PostDto> getUserPostsFeedMatchingByLocation(@RequestParam("page") int page,
			@RequestParam("address") String address) {
		return lostFoundService.getPostsFeedMatchingByLocation(page, address, flag);
	}

	@GetMapping("/post/feed/features")
	public List<PostDto> getUserPostFeedMatchingByFeatures(@RequestParam("page") int page,
			@RequestParam("features") String features) {
		return lostFoundService.getPostsFeedMatchingByFeatures(page, features, flag);
	}

	// для отрисовки совпавших постов для автора нового поста (переход по ссылке из
	// письма на фронт, оттуда - запрос сюда)

	@GetMapping("/all_matched")
	public List<PostDto> getFeedOfMatchingPosts(@RequestParam("page") int page, @RequestParam("postId") String postId,
			@RequestParam("accessCode") String accessCode) throws Throwable {
		return lostFoundService.getFeedOfMatchingPosts(page, postId);
	}

	// для отрисовки 1 нового поста для всех авторов, с постом которых он совпал
	// (переход по ссылке на фронт - оттуда - запрос сюда)

	@GetMapping("/new_matched")
	public PostDto getNewMatchedPost(@RequestParam("postId") String postId,
			@RequestParam("accessCode") String accessCode) throws Throwable {
		return lostFoundService.getNewMatchedPost(postId);
	}

	// for front: this request is working with "remove user" in Accounting service:
	// it is cleaning the "tail of removed user" AFTER removing the user from
	@DeleteMapping("/post/cleaner")
	public String cleanPostsOfRemovedUser(@RequestBody UserRemoveDto userRemoveDto) {
		return lostFoundService.cleanPostsOfRemovedUser(userRemoveDto);
	}

	@PostMapping("/accessCode")
	public String saveAccessCode(@RequestParam("accessCode") String accessCode) throws Exception {
		lostFoundService.saveAccessCode(accessCode);
		return accessCode;
	}

}
