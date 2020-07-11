package proPets.lostFound.service;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.expression.AccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;

import proPets.lostFound.configuration.LostFoundConfiguration;
import proPets.lostFound.dao.LostFoundRepository;
import proPets.lostFound.dto.NewPostDto;
import proPets.lostFound.dto.PostDto;
import proPets.lostFound.dto.PostEditDto;
import proPets.lostFound.dto.UserRemoveDto;
import proPets.lostFound.exceptions.PostNotFoundException;
import proPets.lostFound.model.AuthorData;
import proPets.lostFound.model.Post;
import proPets.lostFound.service.utils.LostFoundUtil;

@Service
public class LostFoundServiceImpl implements LostFoundService {

	@Autowired
	LostFoundRepository lostFoundRepository;

	@Autowired
	LostFoundConfiguration lostFoundConfiguration;
	
	@Autowired
	LostFoundUtil lostFoundUtil;
	
	@Autowired
	LostFoundDataExchangeService dataService;
	

	@Override
	public ModelAndView addPost(String currentUserId, NewPostDto newPostDto, String flag) throws URISyntaxException, JsonProcessingException {
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
				.sex(newPostDto.getSex())
				.color(newPostDto.getColor())
				.height(newPostDto.getHeight())
				.description(newPostDto.getDescription())
				.address(newPostDto.getAddress())
				.distinctiveFeatures(newPostDto.getDistFeatures())
				.picturesURLs(newPostDto.getPicturesURLs())
				.authorData(authorData)
				.dateOfPublish(LocalDateTime.now())
				.build();

		post = lostFoundRepository.save(post);
		int quantity = lostFoundConfiguration.getQuantity();

		if(lostFoundUtil.savePostInSearchingServiceDB(post).getStatusCode()==HttpStatus.OK) {
			//Kafka
			dataService.sendPostData(post.getId());
		}

		PagedListHolder<PostDto> pagedListHolder = lostFoundUtil.createPageListHolder(0, quantity, flag);
		return lostFoundUtil.createModelAndViewObject(pagedListHolder, 0, quantity);
	}
	
	@Override
	public ModelAndView removePost(String currentUserId, String postId, String flag) throws Throwable {
		try {
			Post post = lostFoundRepository.findById(postId).orElseThrow(() -> new PostNotFoundException());

			if (currentUserId.equalsIgnoreCase(post.getAuthorData().getAuthorId())) {
				lostFoundRepository.delete(post);
				lostFoundUtil.removePostInSearchingServiceDB(postId);
				int quantity = lostFoundConfiguration.getQuantity();
				PagedListHolder<PostDto> pagedListHolder = lostFoundUtil.createPageListHolder(0, quantity, flag);
				return lostFoundUtil.createModelAndViewObject(pagedListHolder, 0, quantity);
			} else
				throw new AccessException("Access denied: you'r not author!");
		} catch (Exception e) {
			throw new PostNotFoundException();
		}
	}

	@Override
	public ModelAndView editPost(String currentUserId, PostEditDto postEditDto, String postId, String flag)
			throws URISyntaxException, JsonProcessingException, DataFormatException {
		try {
			Post post = lostFoundRepository.findById(postId).get();
			if (currentUserId.equalsIgnoreCase(post.getAuthorData().getAuthorId())) {
				post.setBreed(postEditDto.getBreed() != null ? postEditDto.getBreed() : post.getBreed());
				post.setDescription(postEditDto.getDescription() != null ? postEditDto.getDescription() : post.getDescription());
				post.setDistinctiveFeatures(postEditDto.getDistFeatures() != null ? postEditDto.getDistFeatures(): post.getDistinctiveFeatures());
				post.setAddress(postEditDto.getAddress() != null ? postEditDto.getAddress() : post.getAddress());
				post.setPicturesURLs(postEditDto.getPicturesURLs() != null ? postEditDto.getPicturesURLs() : post.getPicturesURLs());
				post.getAuthorData().setEmail(postEditDto.getEmail() != null ? postEditDto.getEmail() : post.getAuthorData().getEmail());
				post.getAuthorData().setPhone(postEditDto.getPhone() != null ? postEditDto.getPhone() : post.getAuthorData().getPhone());
				post.getAuthorData().setFb_link(postEditDto.getFb_link() != null ? postEditDto.getFb_link(): post.getAuthorData().getFb_link());
				post.setDateOfPublish(LocalDateTime.now());
				
				lostFoundRepository.save(post);
				lostFoundUtil.savePostInSearchingServiceDB (post); // отправить в Серчинг запрос на редактирование, дождаться ответа
				
				//Kafka
				dataService.sendPostData(post.getId());
				
				return getPostsFeed(0, flag);
			} else
				throw new AccessException("Access denied: you'r not author!");
		} catch (Exception e) {
			throw new DataFormatException();
		} 
	}
	
	//need to save current page number in Store (front) for updating page or repeat the last request
	@Override
	public ModelAndView getPostsFeed(int page, String flag) {	
		int quantity = lostFoundConfiguration.getQuantity();
		PagedListHolder<PostDto> pagedListHolder = lostFoundUtil.createPageListHolder(page, quantity, flag);
		return lostFoundUtil.createModelAndViewObject(pagedListHolder, page, quantity);
	}
	
	@Override
	public ModelAndView getPostsFeedMatchingByType(int page, String type, String flag) {
		List<PostDto> list = lostFoundRepository.findByType(type)
				.filter(post -> post.getFlag().equalsIgnoreCase(flag))
				.sorted((p1,p2)->p2.getDateOfPublish().compareTo(p1.getDateOfPublish()))
				.map(p -> lostFoundUtil.convertPostToPostDto(p))
				.collect(Collectors.toList());
		
		int quantity = lostFoundConfiguration.getQuantity();
		PagedListHolder<PostDto> pagedListHolder = new PagedListHolder<PostDto>(list);
		pagedListHolder.setPage(page);
		pagedListHolder.setPageSize(quantity);
		return lostFoundUtil.createModelAndViewObject(pagedListHolder, page, quantity);
	}
	
	@Override
	public ModelAndView getPostsFeedMatchingByBreed(int page, String breed, String flag) {
		List<PostDto> list = lostFoundRepository.findByBreed(breed)
				.filter(post -> post.getFlag().equalsIgnoreCase(flag))
				.sorted((p1,p2)->p2.getDateOfPublish().compareTo(p1.getDateOfPublish()))
				.map(p -> lostFoundUtil.convertPostToPostDto(p))
				.collect(Collectors.toList());
		
		int quantity = lostFoundConfiguration.getQuantity();
		PagedListHolder<PostDto> pagedListHolder = new PagedListHolder<PostDto>(list);
		pagedListHolder.setPage(page);
		pagedListHolder.setPageSize(quantity);
		return lostFoundUtil.createModelAndViewObject(pagedListHolder, page, quantity);
	}
	
	@Override
	public ModelAndView getPostsFeedMatchingByLocation(int page, String address, String flag) {

		List<String> postIds = lostFoundUtil.convertArrayToList(lostFoundUtil.getListOfPostIdByAddress(address, flag));
		
		List<PostDto> list = lostFoundRepository.findAll()
				.stream()
				.filter(p->postIds.contains(p.getId()))
				.sorted((p1,p2)->p2.getDateOfPublish().compareTo(p1.getDateOfPublish()))
				.map(p -> lostFoundUtil.convertPostToPostDto(p))
				.collect(Collectors.toList());
		
		int quantity = lostFoundConfiguration.getQuantity();
		PagedListHolder<PostDto> pagedListHolder = new PagedListHolder<PostDto>(list);
		pagedListHolder.setPage(page);
		pagedListHolder.setPageSize(quantity);
		return lostFoundUtil.createModelAndViewObject(pagedListHolder, page, quantity);
	}
	
	@Override
	public ModelAndView getPostsFeedMatchingByFeatures(int page, String postId, String flag) {

		List<String> postIds = lostFoundUtil.convertArrayToList(lostFoundUtil.getListOfPostIdByDistFeatures(postId, flag));
		
		List<PostDto> list = lostFoundRepository.findAll()
				.stream()
				.filter(p->postIds.contains(p.getId()))
				.sorted((p1,p2)->p2.getDateOfPublish().compareTo(p1.getDateOfPublish()))
				.map(p -> lostFoundUtil.convertPostToPostDto(p))
				.collect(Collectors.toList());
		
		int quantity = lostFoundConfiguration.getQuantity();
		PagedListHolder<PostDto> pagedListHolder = new PagedListHolder<PostDto>(list);
		pagedListHolder.setPage(page);
		pagedListHolder.setPageSize(quantity);
		return lostFoundUtil.createModelAndViewObject(pagedListHolder, page, quantity);
	}
	
	
	@Override
	public String cleanPostsOfRemovedUser(UserRemoveDto userRemoveDto) {
		String authorId = userRemoveDto.getUserId();
		lostFoundRepository.findAll().stream()
					.filter(p->p.getAuthorData().getAuthorId().equalsIgnoreCase(authorId))
					.forEach(post->lostFoundRepository.delete(post));	
		return authorId;
	}
	
	@Override
	public PostDto getNewMatchedPost(String postId) throws PostNotFoundException {
		Post post = lostFoundRepository.findById(postId).orElseThrow(()->new PostNotFoundException());
		return lostFoundUtil.convertPostToPostDto(post);
	}
	
	@Override
	public ModelAndView getFeedOfMatchingPosts(int page, String postId) throws Throwable {
		Post post = lostFoundRepository.findById(postId).orElseThrow(()->new PostNotFoundException());
		List<String> postIds = lostFoundUtil.convertArrayToList(lostFoundUtil.getListOfMatchedPostId(postId, post.getFlag()));
		
		List<PostDto> list = lostFoundRepository.findAll()
				.stream()
				.filter(p->postIds.contains(p.getId()))
				.sorted((p1,p2)->p2.getDateOfPublish().compareTo(p1.getDateOfPublish()))
				.map(p -> lostFoundUtil.convertPostToPostDto(p))
				.collect(Collectors.toList());
		
		int quantity = lostFoundConfiguration.getQuantity();
		PagedListHolder<PostDto> pagedListHolder = new PagedListHolder<PostDto>(list);
		pagedListHolder.setPage(page);
		pagedListHolder.setPageSize(quantity);
		return lostFoundUtil.createModelAndViewObject(pagedListHolder, page, quantity);
	}
	

}
