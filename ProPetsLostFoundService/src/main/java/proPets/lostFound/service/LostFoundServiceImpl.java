package proPets.lostFound.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.DataFormatException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.AccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import proPets.lostFound.configuration.LostFoundConfiguration;
import proPets.lostFound.dao.LostFoundRepository;
import proPets.lostFound.dto.NewPostDto;
import proPets.lostFound.dto.PostDto;
import proPets.lostFound.dto.PostEditDto;
import proPets.lostFound.exceptions.PostNotFoundException;
import proPets.lostFound.model.AuthorData;
import proPets.lostFound.model.Photo;
import proPets.lostFound.model.Post;

@Service
public class LostFoundServiceImpl implements LostFoundService {

	@Autowired
	LostFoundRepository lostFoundRepository;

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
				.flag(flag)
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

			lostFoundRepository.save(post);
			return convertPostToPostDto(post);
	}
		
	private Set<String> createSetOfDistinctiveFeatures(String newFeaturesStr) { // gets String, splites with komas,
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

	@Override
	public PostDto removePost(String currentUserId, String postId, String flag) throws Throwable {
		try {
			Post post = lostFoundRepository.findById(postId).get();

			if (currentUserId.equalsIgnoreCase(post.getAuthorData().getAuthorId())) {
				lostFoundRepository.delete(post);
				return convertPostToPostDto(post);
			} else
				throw new AccessException("Access denied: you'r not author!");
		} catch (Exception e) {
			throw new PostNotFoundException();
		}
	}

	private PostDto convertPostToPostDto(Post post) {
		return PostDto.builder()
				.id(post.getId())
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

	@Override
	public PostDto editPost(String currentUserId, PostEditDto postEditDto, String postId, String flag)
			throws Throwable {
		try {
			Post post = lostFoundRepository.findById(postId).get();
			if (currentUserId.equalsIgnoreCase(post.getAuthorData().getAuthorId())) {
				post.setBreed(postEditDto.getBreed());
				post.setSex(postEditDto.getSex());
				post.setColor(postEditDto.getColor());
				post.setHeight(postEditDto.getHeight());
				post.setDescription(postEditDto.getDescription());
				post.setDistinctiveFeatures(createSetOfDistinctiveFeatures(postEditDto.getDistFeatures()));
				post.setLocation(postEditDto.getLocation());
				post.setPictures(createSetOfPictures(postEditDto.getPictureUrls()));
				post.getAuthorData().setEmail(postEditDto.getEmail());
				post.getAuthorData().setPhone(postEditDto.getPhone());
				post.getAuthorData().setFb_link(postEditDto.getFb_link());
				post.setDateOfPublish(LocalDateTime.now());
				lostFoundRepository.save(post);
				return convertPostToPostDto(post);
			} else
				throw new AccessException("Access denied: you'r not author!");
		} catch (Exception e) {
			throw new DataFormatException();
		}
	}
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
