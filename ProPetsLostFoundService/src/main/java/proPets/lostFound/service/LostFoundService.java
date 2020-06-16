package proPets.lostFound.service;

import proPets.lostFound.dto.NewPostDto;
import proPets.lostFound.dto.PostDto;
import proPets.lostFound.dto.PostEditDto;

public interface LostFoundService {

	PostDto addPost(String currentUserId, NewPostDto newPostDto, String flag);

	PostDto removePost(String currentUserId, String postId, String flag) throws Throwable;

	PostDto editPost(String currentUserId, PostEditDto postEditDto, String postId, String flag) throws Throwable;

	Iterable<PostDto> getPostFeed(String currentUserId, String flag);

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
