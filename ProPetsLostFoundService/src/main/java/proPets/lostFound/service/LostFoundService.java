package proPets.lostFound.service;

import org.springframework.web.servlet.ModelAndView;

import proPets.lostFound.dto.NewPostDto;
import proPets.lostFound.dto.PostEditDto;

public interface LostFoundService {

	ModelAndView addPost(String currentUserId, NewPostDto newPostDto, String flag);

	ModelAndView removePost(String currentUserId, String postId, String flag) throws Throwable;

	ModelAndView editPost(String currentUserId, PostEditDto postEditDto, String postId, String flag) throws Throwable;

	ModelAndView getPostFeed(int page, String flag);

	ModelAndView getPostFeedByType(int page, String type, String flag);

	ModelAndView getPostFeedByBreed(int page, String breed, String flag);

}
