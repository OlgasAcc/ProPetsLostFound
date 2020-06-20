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

//	void makePostFavorite(String currentUserId, String postId) throws Throwable;
//
//	void makePostHidden(String currentUserId, String postId) throws Throwable;
//
//	void unfollowPostsByUser(String currentUserId, String postId) throws Throwable;
//
//	Iterable<PostDto> getAllFavoritePostsByUser(String userId);
//
//	void cleanPostsAndPresenceOfRemovedUser(String removedUserId);
//
//	Iterable<PostDto> getUserPostFeed(String currentUserId);

}
