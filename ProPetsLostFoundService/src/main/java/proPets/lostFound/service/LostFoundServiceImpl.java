package proPets.lostFound.service;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.expression.AccessException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import proPets.lostFound.configuration.LostFoundConfiguration;
import proPets.lostFound.dao.LostFoundJPARepository;
import proPets.lostFound.dao.LostFoundMongoRepository;
import proPets.lostFound.dto.NewPostDto;
import proPets.lostFound.dto.PostDto;
import proPets.lostFound.dto.PostEditDto;
import proPets.lostFound.dto.UserRemoveDto;
import proPets.lostFound.exceptions.PostNotFoundException;
import proPets.lostFound.model.AccessCode;
import proPets.lostFound.model.AuthorData;
import proPets.lostFound.model.Post;
import proPets.lostFound.service.utils.LostFoundUtil;

@Service
public class LostFoundServiceImpl implements LostFoundService {

	@Autowired
	LostFoundMongoRepository lostFoundRepository;
	
	@Autowired
	LostFoundJPARepository lostFoundJPARepository;

	@Autowired
	LostFoundConfiguration lostFoundConfiguration;
	
	@Autowired
	LostFoundUtil lostFoundUtil;
	
	@Autowired
	LostFoundDataExchangeService dataService;
	
	//@PersistenceContext
	//EntityManager entityManager;
	
	@Autowired
	JpaTransactionManager jpaTransactionManager;
	
	@Override
	public List<PostDto> addPost(String currentUserId, NewPostDto newPostDto, String flag) throws URISyntaxException, JsonProcessingException, InterruptedException, ExecutionException {
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
		//post = lostFoundRepository.save(post);
		
		CompletableFuture<List<PostDto>> result = lostFoundUtil.savePostInDatabase(post)
		        .thenApply(p -> lostFoundUtil.savePostInSearchingServiceDB(post))
		        .thenCompose(p->dataService.sendPostData(post.getId()))
		        .thenComposeAsync(p->getPostsFeed(0, flag));
		
		return result.get();
		//if (lostFoundUtil.savePostInSearchingServiceDB(post).getStatusCode() == HttpStatus.OK) {
		//	// Kafka
		//	dataService.sendPostData(post.getId());
		//}
		//return getPostsFeed(0, flag);
	}
	
	@Override
	public List<PostDto> removePost(String currentUserId, String postId, String flag) throws Throwable {
		Post post = lostFoundRepository.findById(postId).orElseThrow(() -> new PostNotFoundException());
		if (currentUserId.equalsIgnoreCase(post.getAuthorData().getAuthorId())) {
			CompletableFuture<List<PostDto>> result = lostFoundUtil.deletePostFromDatabase(post)
					.thenApply(p -> lostFoundUtil.removePostInSearchingServiceDB(postId))
					.thenComposeAsync(p -> getPostsFeed(0, flag));
			return result.get();

			// lostFoundRepository.delete(post);
			// lostFoundUtil.removePostInSearchingServiceDB(postId);
			// return getPostsFeed(0, flag);
			
		} else {
			throw new AccessException("Access denied: you'r not author!");
		}			
	}
	
	@Override
	public List<PostDto> editPost(String currentUserId, PostEditDto postEditDto, String postId, String flag)
			throws URISyntaxException, JsonProcessingException, DataFormatException {
		try {
			Post post = lostFoundRepository.findById(postId).orElseThrow(()->new PostNotFoundException());
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
				return null;
				//NEED BE UPDATED!!!! 
				//return getPostsFeed(0, flag);
			} else
				throw new AccessException("Access denied: you'r not author!");
		} catch (Exception e) {
			throw new DataFormatException();
		} 
	}
	
	//need to save current page number in Store (front) for updating page or repeat the last request
	@Override
	public CompletableFuture<List<PostDto>> getPostsFeed(int page, String flag) {	
		int quantity = lostFoundConfiguration.getQuantity();
		PageRequest pageReq = PageRequest.of(page, quantity, Sort.Direction.DESC, "dateOfPublish");
		return lostFoundUtil.getListAndConvertToListOfPostDto(pageReq);
	}
	
	@Override
	public List<PostDto> getPostsFeedMatchingByType(int page, String type, String flag) {
		int quantity = lostFoundConfiguration.getQuantity();
		PageRequest pageReq = PageRequest.of(page, quantity, Sort.Direction.DESC, "dateOfPublish");
		Page<Post> posts = lostFoundRepository.findByTypeLikeAndFlagLike(type, flag, pageReq);
		return posts.getContent()
				.stream()
				.map(p -> lostFoundUtil.convertPostToPostDto(p))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<PostDto> getPostsFeedMatchingByBreed(int page, String breed, String flag) {
		int quantity = lostFoundConfiguration.getQuantity();
		PageRequest pageReq = PageRequest.of(page, quantity, Sort.Direction.DESC, "dateOfPublish");
		Page<Post> posts = lostFoundRepository.findByBreedLikeAndFlagLike(breed, flag, pageReq);
		return posts.getContent()
				.stream()
				.map(p -> lostFoundUtil.convertPostToPostDto(p))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<PostDto> getPostsFeedMatchingByLocation(int page, String address, String flag) {		
		List<String> postIds = lostFoundUtil.convertArrayToList(lostFoundUtil.getListOfPostIdByAddress(address, flag));
		return lostFoundUtil.getListOfPostDtoByListOfPostIds(postIds,page);
	}
	
	@Override
	public List<PostDto> getPostsFeedMatchingByFeatures(int page, String postId, String flag) {
		List<String> postIds = lostFoundUtil.convertArrayToList(lostFoundUtil.getListOfPostIdByDistFeatures(postId, flag));		
		return lostFoundUtil.getListOfPostDtoByListOfPostIds(postIds,page);
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
	public List<PostDto> getFeedOfMatchingPosts(int page, String postId) throws Throwable {
		Post post = lostFoundRepository.findById(postId).orElseThrow(()->new PostNotFoundException());
		List<String> postIds = lostFoundUtil.convertArrayToList(lostFoundUtil.getListOfMatchedPostId(postId, post.getFlag()));
		return lostFoundUtil.getListOfPostDtoByListOfPostIds(postIds,page);
	}

	@Override
	public void saveAccessCode(String accessCode) {
		AccessCode newCode = new AccessCode(accessCode);
		lostFoundJPARepository.save(newCode);		
	}
}
