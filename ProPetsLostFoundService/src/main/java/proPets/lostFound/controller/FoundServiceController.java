package proPets.lostFound.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
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

import proPets.lostFound.configuration.BeanConfiguration;
import proPets.lostFound.configuration.LostFoundConfiguration;
import proPets.lostFound.dto.NewPostDto;
import proPets.lostFound.dto.PostDto;
import proPets.lostFound.dto.PostEditDto;
import proPets.lostFound.dto.UserRemoveDto;
import proPets.lostFound.service.LostFoundService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/lostFound/found/v1")

public class FoundServiceController {
	final String flag = "found";
	
	@Autowired
	LostFoundService lostFoundService;

	@Autowired
	LostFoundConfiguration lostFoundConfiguration;

	@RefreshScope
	@GetMapping("/config")
	public  BeanConfiguration getRefreshedData() {
		return new BeanConfiguration(lostFoundConfiguration.getQuantity(), lostFoundConfiguration.getBaseJWTUrl(),lostFoundConfiguration.getBaseSearchUrl());
	}
	
	@PostMapping("/post")
	public List<PostDto> addPost(@RequestHeader(value = "Authorization") String authorization,
			Principal principal, @RequestBody NewPostDto newPostDto) throws Exception {
		return lostFoundService.addPost(principal.getName(), newPostDto, flag);
	}

	@DeleteMapping("/post/{postId}")
	public List<PostDto> removePost(@RequestHeader(value = "Authorization") String authorization,
			Principal principal, @PathVariable String postId) throws Throwable {
		return lostFoundService.removePost(principal.getName(), postId, flag);
	}

	@PutMapping("/post/{postId}")
	public List<PostDto> editPost(@RequestHeader(value = "Authorization") String authorization,
			Principal principal, @RequestBody PostEditDto postEditDto, @PathVariable String postId, String flag)
			throws Throwable {
		return lostFoundService.editPost(principal.getName(), postEditDto, postId, flag);
	}

	@GetMapping("/post/feed")
	public List<PostDto> getUserPostsFeed(@RequestHeader(value = "Authorization") String authorization,
			@RequestParam("page") int page) {
		return lostFoundService.getPostsFeed(page, flag);
	}

	@GetMapping("/post/feed/type")
	public List<PostDto> getUserPostsFeedMatchingByType(
			@RequestHeader(value = "Authorization") String authorization, @RequestParam("page") int page,
			@RequestParam("type") String type) {
		return lostFoundService.getPostsFeedMatchingByType(page, type, flag);
	}

	@GetMapping("/post/feed/breed")
	public List<PostDto> getUserPostsFeedMatchingByBreed(
			@RequestHeader(value = "Authorization") String authorization, @RequestParam("page") int page,
			@RequestParam("breed") String breed) {
		return lostFoundService.getPostsFeedMatchingByBreed(page, breed, flag);
	}

	@GetMapping("/post/feed/location")
	public List<PostDto> getUserPostsFeedMatchingByLocation(
			@RequestHeader(value = "Authorization") String authorization, @RequestParam("page") int page,
			@RequestParam("address") String address) {
		return lostFoundService.getPostsFeedMatchingByLocation(page, address, flag);
	}

	@GetMapping("/post/feed/features")
	public List<PostDto> getUserPostFeedMatchingByFeatures(
			@RequestHeader(value = "Authorization") String authorization, @RequestParam("page") int page,
			@RequestParam("address") String address) {
		return lostFoundService.getPostsFeedMatchingByFeatures(page, address, flag);
	}

	// TODO!!! убрать из фильтра валидации этот эндпоинт
	// добавить одноразовый код (?) вторым параметром, базу в ЛФ и фильтр
	// для отрисовки совпавших постов для автора нового поста (переход по ссылке из
	// письма на фронт, оттуда - запрос сюда)

	@GetMapping("/all_matched")
	public List<PostDto> getFeedOfMatchingPosts(@RequestParam("page") int page,
			@RequestParam("postId") String postId) throws Throwable {
		return lostFoundService.getFeedOfMatchingPosts(page, postId);
	}

	// TODO!!! убрать из фильтра валидации этот эндпоинт
	// добавить одноразовый код (?) вторым параметром, базу в ЛФ и фильтр
	// для отрисовки 1 нового поста для всех авторов, с постом которых он совпал
	// (переход по ссылке на фронт - оттуда - запрос сюда)

	@GetMapping("/new_matched")
	public PostDto getNewMatchedPost(@RequestParam("postId") String postId) throws Throwable {
		return lostFoundService.getNewMatchedPost(postId);
	}

	// for front: this request is working with "remove user" in Accounting service:
	// it is cleaning the "tail of removed user" AFTER removing the user from

	@DeleteMapping("/post/cleaner")
	public String cleanPostsOfRemovedUser(@RequestBody UserRemoveDto userRemoveDto) {
		return lostFoundService.cleanPostsOfRemovedUser(userRemoveDto);
	}
	
	@PostMapping("/accessCode")
	public void saveAccessCode(@RequestParam ("accessCode") String accessCode) throws Exception {
		lostFoundService.saveAccessCode(accessCode);
	}

}
