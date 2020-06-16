package proPets.lostFound.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.AccessException;
import org.springframework.stereotype.Service;

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
		Set<Photo> pictures = createSetOfPictures(newPostDto.getPictures());

		Post post = Post.builder()
				.flag(flag)
				.type(newPostDto.getType())
				.breed(newPostDto.getBreed())
				.color(newPostDto.getColor())
				.height(newPostDto.getHeight())
				.description(newPostDto.getDescription())
				.location(newPostDto.getLocation())
				.distinctiveFeatures(createSetOfDistinctiveFeatures(newPostDto.getDistFeatures()))
				.pictures(pictures)
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
	
	private Set<Photo> createSetOfPictures(String [] urls) {
		Set<Photo> pictures = new HashSet<>();
		for (int i = 0; i < urls.length; i++) {
			Photo newPhoto = new Photo(urls[i]);
			if (pictures.size() <= 4) {
				pictures.add(newPhoto);
			}
		}
		return pictures;
	}

	@Override
	public PostDto removePost(String currentUserId, String postId, String flag) throws Throwable {
		try {
			Post post = lostFoundRepository.findById(postId).orElseThrow(() -> new PostNotFoundException());

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
			Set<Photo> pictures = createSetOfPictures(postEditDto.getPictures());
			if (currentUserId.equalsIgnoreCase(post.getAuthorData().getAuthorId())) {
				post.setBreed(postEditDto.getBreed());
				post.setSex(postEditDto.getSex());
				post.setColor(postEditDto.getColor());
				post.setHeight(postEditDto.getHeight());
				post.setDescription(postEditDto.getDescription());
				post.setDistinctiveFeatures(createSetOfDistinctiveFeatures(postEditDto.getDistFeatures()));
				post.setLocation(postEditDto.getLocation());
				post.setPictures(pictures);
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
	
	@Override
	public Iterable<PostDto> getPostFeed(String currentUserId, String flag) {
		Iterable<PostDto> list = lostFoundRepository.findAll().stream()
				.filter(post -> post.getFlag().equalsIgnoreCase(flag))
				.sorted((p1,p2)->p2.getDateOfPublish().compareTo(p1.getDateOfPublish()))
				.map(post -> convertPostToPostDto(post))
				.collect(Collectors.toList());
		return list;
	}


}
