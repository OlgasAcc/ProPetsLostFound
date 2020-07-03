package proPets.lostFound.service;

import java.net.URISyntaxException;

import org.springframework.web.servlet.ModelAndView;

import proPets.lostFound.dto.NewPostDto;
import proPets.lostFound.dto.PostEditDto;

public interface LostFoundService {

	ModelAndView addPost(String currentUserId, NewPostDto newPostDto, String flag) throws URISyntaxException;

	ModelAndView removePost(String currentUserId, String postId, String flag) throws Throwable;

	ModelAndView editPost(String currentUserId, PostEditDto postEditDto, String postId, String flag) throws Throwable;

	ModelAndView getPostsFeed(int page, String flag);

	ModelAndView getPostsFeedMatchingByType(int page, String type, String flag);

	ModelAndView getPostsFeedMatchingByBreed(int page, String breed, String flag);

	ModelAndView getPostsFeedMatchingByLocation(int page, String address, String flag);

	ModelAndView getPostsFeedMatchingByFeatures(int page, String postId, String flag);

}
