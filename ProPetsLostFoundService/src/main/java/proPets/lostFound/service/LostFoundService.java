package proPets.lostFound.service;

import java.net.URISyntaxException;
import java.util.List;
import java.util.zip.DataFormatException;

import com.fasterxml.jackson.core.JsonProcessingException;

import proPets.lostFound.dto.NewPostDto;
import proPets.lostFound.dto.PostDto;
import proPets.lostFound.dto.PostEditDto;
import proPets.lostFound.dto.UserRemoveDto;

public interface LostFoundService {

	List<PostDto> addPost(String currentUserId, NewPostDto newPostDto, String flag) throws URISyntaxException, JsonProcessingException;

	List<PostDto> removePost(String currentUserId, String postId, String flag) throws Throwable;

	List<PostDto> editPost(String currentUserId, PostEditDto postEditDto, String postId, String flag) throws URISyntaxException, JsonProcessingException, DataFormatException;

	List<PostDto> getPostsFeed(int page, String flag);

	List<PostDto> getPostsFeedMatchingByType(int page, String type, String flag);

	List<PostDto> getPostsFeedMatchingByBreed(int page, String breed, String flag);

	List<PostDto> getPostsFeedMatchingByLocation(int page, String address, String flag);

	List<PostDto> getPostsFeedMatchingByFeatures(int page, String postId, String flag);

	String cleanPostsOfRemovedUser(UserRemoveDto userRemoveDto);

	PostDto getNewMatchedPost(String postId) throws Throwable;

	List<PostDto> getFeedOfMatchingPosts(int page, String postId) throws Throwable;

	void saveAccessCode(String accessCode);

}
