package proPets.lostFound.service;

import java.net.URISyntaxException;
import java.util.zip.DataFormatException;

import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;

import proPets.lostFound.dto.NewPostDto;
import proPets.lostFound.dto.PostDto;
import proPets.lostFound.dto.PostEditDto;
import proPets.lostFound.dto.UserRemoveDto;

public interface LostFoundService {

	ModelAndView addPost(String currentUserId, NewPostDto newPostDto, String flag) throws URISyntaxException, JsonProcessingException;

	ModelAndView removePost(String currentUserId, String postId, String flag) throws Throwable;

	ModelAndView editPost(String currentUserId, PostEditDto postEditDto, String postId, String flag) throws URISyntaxException, JsonProcessingException, DataFormatException;

	ModelAndView getPostsFeed(int page, String flag);

	ModelAndView getPostsFeedMatchingByType(int page, String type, String flag);

	ModelAndView getPostsFeedMatchingByBreed(int page, String breed, String flag);

	ModelAndView getPostsFeedMatchingByLocation(int page, String address, String flag);

	ModelAndView getPostsFeedMatchingByFeatures(int page, String postId, String flag);

	String cleanPostsOfRemovedUser(UserRemoveDto userRemoveDto);

	PostDto getNewMatchedPost(String postId) throws Throwable;

	ModelAndView getFeedOfMatchingPosts(int page, String postId) throws Throwable;

}
