package proPets.lostFound.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.expression.AccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

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
	public ModelAndView addPost(String currentUserId, NewPostDto newPostDto, String flag) {
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
						
			int quantity = lostFoundConfiguration.getQuantity();
			PagedListHolder<PostDto> pagedListHolder = createPageListHolder(0, quantity, flag);
			return createModelAndViewObject(pagedListHolder, 0, quantity);
	}

	@Override
	public ModelAndView removePost(String currentUserId, String postId, String flag) throws Throwable {
		try {
			Post post = lostFoundRepository.findById(postId).orElseThrow(() -> new PostNotFoundException());

			if (currentUserId.equalsIgnoreCase(post.getAuthorData().getAuthorId())) {
				lostFoundRepository.delete(post);
				int quantity = lostFoundConfiguration.getQuantity();
				PagedListHolder<PostDto> pagedListHolder = createPageListHolder(0, quantity, flag);
				return createModelAndViewObject(pagedListHolder, 0, quantity);
			} else
				throw new AccessException("Access denied: you'r not author!");
		} catch (Exception e) {
			throw new PostNotFoundException();
		}
	}

	@Override
	public ModelAndView editPost(String currentUserId, PostEditDto postEditDto, String postId, String flag)
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
				
				int quantity = lostFoundConfiguration.getQuantity();
				PagedListHolder<PostDto> pagedListHolder = createPageListHolder(0, quantity, flag);
				return createModelAndViewObject(pagedListHolder, 0, quantity);
			} else
				throw new AccessException("Access denied: you'r not author!");
		} catch (Exception e) {
			throw new DataFormatException();
		}
	}
	
	//need to save current page number in Store (front) for updating page or repeat the last request
	@Override
	public ModelAndView getPostFeed(int page, String flag) {		
		int quantity = lostFoundConfiguration.getQuantity();
		PagedListHolder<PostDto> pagedListHolder = createPageListHolder(page, quantity, flag);
		return createModelAndViewObject(pagedListHolder, page, quantity);
	}
	
	@Override
	public ModelAndView getPostFeedByType(int page, String type, String flag) {
		List<PostDto> list = lostFoundRepository.findByType(type)
				.filter(post -> post.getFlag().equalsIgnoreCase(flag))
				.sorted((p1,p2)->p2.getDateOfPublish().compareTo(p1.getDateOfPublish()))
				.map(p -> convertPostToPostDto(p))
				.collect(Collectors.toList());
		
		int quantity = lostFoundConfiguration.getQuantity();
		PagedListHolder<PostDto> pagedListHolder = new PagedListHolder<PostDto>(list);
		pagedListHolder.setPage(page);
		pagedListHolder.setPageSize(quantity);
		return createModelAndViewObject(pagedListHolder, page, quantity);
	}
	
	private Set<String> createSetOfDistinctiveFeatures(String newFeaturesStr) { // gets String, splits with komas,
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
	
	private PagedListHolder<PostDto> createPageListHolder(int pageNumber, int quantity, String flag) {	
		List<PostDto> list = getUpdatedFilteredPostFeed(flag);
		PagedListHolder<PostDto> pagedListHolder = new PagedListHolder<>(list);
		pagedListHolder.setPage(pageNumber);
		pagedListHolder.setPageSize(quantity);
		return pagedListHolder;
	}
	
	private ModelAndView createModelAndViewObject (PagedListHolder<PostDto> pagedListHolder, int page, int pageSize) {
		ModelAndView mav = new ModelAndView("list of posts", HttpStatus.OK);
		mav.addObject("pagedList", pagedListHolder.getPageList());
		mav.addObject("page", 0);
		mav.addObject("maxPage", pagedListHolder.getPageCount());
		return mav;
	}

	private List<PostDto> getUpdatedFilteredPostFeed(String flag){
		List<PostDto> list = lostFoundRepository.findAll().stream()
				.filter(post -> post.getFlag().equalsIgnoreCase(flag))
				.sorted((p1,p2)->p2.getDateOfPublish().compareTo(p1.getDateOfPublish()))
				.map(p -> convertPostToPostDto(p))
				.collect(Collectors.toList());
		return list;
	}

}
