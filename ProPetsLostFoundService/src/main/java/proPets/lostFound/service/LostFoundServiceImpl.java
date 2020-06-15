package proPets.lostFound.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import proPets.lostFound.configuration.LostFoundConfiguration;
import proPets.lostFound.dao.FoundRepository;
import proPets.lostFound.dao.LostRepository;
import proPets.lostFound.dto.NewPostDto;
import proPets.lostFound.dto.PostDto;
import proPets.lostFound.model.AuthorData;
import proPets.lostFound.model.Photo;
import proPets.lostFound.model.Post;
import proPets.lostFound.model.PostFound;
import proPets.lostFound.model.PostLost;

@Service
public class LostFoundServiceImpl implements LostFoundService {

	@Autowired
	LostRepository lostRepository;
	
	@Autowired
	FoundRepository foundRepository;

	@Autowired
	LostFoundConfiguration lostFoundConfiguration;

	@Override
	public PostDto addPost(String currentUserId, NewPostDto newPostDto, String flag) {
		AuthorData authorData = AuthorData.builder()
							.authorId(currentUserId)
							.phone(newPostDto.getEmail())
							.fb_link(newPostDto.getFb_link())
							.email(newPostDto.getEmail())
							.authorAvatar(newPostDto.getAuthorAvatar())
							.authorName(newPostDto.getAuthorName())
							.build();
		Post post = Post.builder()
				.type(newPostDto.getType())
				.breed(newPostDto.getBreed())
				.color(newPostDto.getColor())
				.height(newPostDto.getHeight())
				.description(newPostDto.getDescription())
				.location(newPostDto.getLocation())
				.distinctiveFeatures(createSetOfDistinctiveFeatures(newPostDto.getDistFeatures()))
				.pictures(createSetOfPictures(newPostDto.getPictureUrls()))
				.authorData(authorData)
				.dateOfPublish(LocalDateTime.now())
				.build();
		if (flag.equalsIgnoreCase("lost")) {
			PostLost newPostLost = new PostLost(post);
			lostRepository.save(newPostLost);
			return convertPostToPostDto(newPostLost.getPost(), newPostLost.getId());
		}
		if (flag.equalsIgnoreCase("found")) {
			PostFound newPostFound = new PostFound(post);
			foundRepository.save(newPostFound);
			return convertPostToPostDto(newPostFound.getPost(),newPostFound.getId());
		}
		return null;
	}
		
	private Set<String> createSetOfDistinctiveFeatures(String newFeaturesStr) { // receives String, splites with komas,
		// removes beginning and ending white spaces
		Set<String> distinctiveFeatures = new HashSet<>();
		String[] features = newFeaturesStr.split(",");
		for (int i = 0; i < features.length; i++) {
			distinctiveFeatures.add(features[i].trim());
		}
		return distinctiveFeatures;
	}
	
	private Set<Photo> createSetOfPictures(String[] picturesUrls) {
		Set<Photo> set = new HashSet<>();
		for (int i = 0; i < picturesUrls.length; i++) {
			Photo newPhoto = new Photo(picturesUrls[i]);
			if (set.size() <= 4) {
				set.add(newPhoto);
			} else
				throw new MaxUploadSizeExceededException(4);
		}
		return set;
	}

//	@Override
//	public PostDto removePost(String currentUserId, String postId) throws Throwable {
//		try {
//			PostLost post = messagingRepository.findById(postId).orElseThrow(() -> new PostNotFoundException());
//			if (currentUserId.equalsIgnoreCase(post.getAuthorId())) {
//				messagingRepository.delete(post);
//				return convertPostToPostDto(post);
//			} else
//				throw new AccessException("Access denied: you'r not author!");
//		} catch (Exception e) {
//			throw new PostNotFoundException();
//			
//
//		}
//	}

	private PostDto convertPostToPostDto(Post post, String postId) {
		return PostDto.builder()
				.id(postId)
				.type(post.getType())
				.breed(post.getBreed())
				.color(post.getColor())
				.height(post.getHeight())
				.description(post.getDescription())
				.location(post.getLocation())
				.distinctiveFeatures(post.getDistinctiveFeatures())
				.pictures(post.getPictures())
				.authorData(post.getAuthorData())
				.dateOfPublish(post.getDateOfPublish())
				.build();
	}

//	@Override
//	public PostDto editPost(String currentUserId, PostEditDto postEditDto, String postId) throws Throwable {
//		try {
//			PostLost post = messagingRepository.findById(postId).orElseThrow(() -> new PostNotFoundException());
//			if (currentUserId.equalsIgnoreCase(post.getAuthorId())) {
//				if (postEditDto.getText() != null) {
//					post.setText(postEditDto.getText());
//				}
//				if (postEditDto.getPictures().size() != 0) {
//					post.setPictures(postEditDto.getPictures());
//				}
//				post.setDateOfPublish(LocalDateTime.now());
//				messagingRepository.save(post);
//				return convertPostToPostDto(post);
//			} else
//				throw new AccessException("Access denied: you'r not author!");
//		} catch (Exception e) {
//			throw new DataFormatException();
//		}
//	}
//	
//	@Override
//	public void makePostFavorite(String currentUserId, String postId) throws Throwable {
//		try {
//			PostLost post = messagingRepository.findById(postId).orElseThrow(() -> new PostNotFoundException());
//			if (post.getUsersAddedThisPostToFavorites().contains(currentUserId)) {
//				post.removeUserThatAddedThisPostToFav(currentUserId);
//				messagingRepository.save(post);
//			} else {
//				post.addUserThatAddedThisPostToFav(currentUserId);
//				messagingRepository.save(post);
//			}
//		} catch (PostNotFoundException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	@Override
//	public void makePostHidden(String currentUserId, String postId) throws Throwable {
//		try {
//			PostLost post = messagingRepository.findById(postId).orElseThrow(() -> new PostNotFoundException());
//			if (!currentUserId.equalsIgnoreCase(post.getAuthorId())) {
//				post.addUserThatHidThisPost(currentUserId);
//				messagingRepository.save(post);
//			} else
//				throw new AccessException("Access denied: you'r not author!");
//		} catch (PostNotFoundException e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public void unfollowPostsByUser(String currentUserId, String postId) throws Throwable {
//		try {
//			PostLost post = messagingRepository.findById(postId).orElseThrow(() -> new PostNotFoundException());
//			if (!currentUserId.equalsIgnoreCase(post.getAuthorId())) {
//				String userIdToUnfollow = post.getAuthorId();
//				messagingRepository.findByAuthorId(userIdToUnfollow)
//						.filter(item -> item.addUserThatUnfollowedThisPostByAuthor(currentUserId))
//						.forEach(i -> messagingRepository.save(i));
//			} else
//				throw new AccessException("Access denied: you'r not author!");
//		} catch (PostNotFoundException e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public Iterable<PostDto> getAllFavoritePostsByUser(String userId) { //проверить, есть ли такой в базе аккаунтинга, или 0 в результате хватит?
//		Iterable<PostDto> favoritesByUser = messagingRepository.findAll()
//				.stream()
//				.filter(post -> post.getUsersAddedThisPostToFavorites().contains(userId))
//				.map(post -> convertPostToPostDto(post))
//				.collect(Collectors.toList());
//		return favoritesByUser;
//	}
//
//	@Override
//	public void cleanPostsAndPresenceOfRemovedUser(String removedUserId) {
//		messagingRepository.findByAuthorId(removedUserId)
//						.forEach(i -> messagingRepository.delete(i));
//		
//		messagingRepository.findAll()
//						.stream()
//						.filter(post -> post.getUsersAddedThisPostToFavorites().contains(removedUserId))
//						.forEach(i -> i.getUsersAddedThisPostToFavorites().remove(removedUserId));
//		
//		messagingRepository.findAll()
//						.stream()
//						.filter(post -> post.getUsersHidThisPost().contains(removedUserId))
//						.forEach(i -> i.getUsersHidThisPost().remove(removedUserId));
//		
//		messagingRepository.findAll()
//						.stream()
//						.filter(post -> post.getUsersUnfollowedThisPostByAuthor().contains(removedUserId))
//						.forEach(i -> i.getUsersUnfollowedThisPostByAuthor().remove(removedUserId));
//	}
//
//	@Override
//	public Iterable<PostDto> getUserPostFeed(String currentUserId) {
//		Iterable<PostDto> list = messagingRepository.findAll()
//								.stream()
//								.filter(post->(!post.getUsersHidThisPost().contains(currentUserId))&&(!post.getUsersUnfollowedThisPostByAuthor().contains(currentUserId)))
//								.map(post -> convertPostToPostDto(post))
//								.collect(Collectors.toList());
//		return list;
//	}
}
