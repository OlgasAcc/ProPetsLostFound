package proPets.lostFound.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.expression.AccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.BodyBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import proPets.lostFound.configuration.LostFoundConfiguration;
import proPets.lostFound.dao.LostFoundRepository;
import proPets.lostFound.dto.NewPostDto;
import proPets.lostFound.dto.PostDto;
import proPets.lostFound.dto.PostEditDto;
import proPets.lostFound.dto.PostToConvertDto;
import proPets.lostFound.dto.SearchResponseDto;
import proPets.lostFound.exceptions.PostNotFoundException;
import proPets.lostFound.model.AuthorData;
import proPets.lostFound.model.Post;


@Service
public class LostFoundServiceImpl implements LostFoundService {

	@Autowired
	LostFoundRepository lostFoundRepository;

	@Autowired
	LostFoundConfiguration lostFoundConfiguration;

	@Override
	public ModelAndView addPost(String currentUserId, NewPostDto newPostDto, String flag) throws URISyntaxException {
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

		savePostInSearchingServiceDB(post);
		// дожидается ответа: пост сохранен в Эластиксёрч

		// отправить в Кафку асинхр запрос на поиск совпадений и рассылку

		PagedListHolder<PostDto> pagedListHolder = createPageListHolder(0, quantity, flag);
		return createModelAndViewObject(pagedListHolder, 0, quantity);
	}
	
	@Override
	public ModelAndView removePost(String currentUserId, String postId, String flag) throws Throwable {
		try {
			Post post = lostFoundRepository.findById(postId).orElseThrow(() -> new PostNotFoundException());

			if (currentUserId.equalsIgnoreCase(post.getAuthorData().getAuthorId())) {
				lostFoundRepository.delete(post);
				
				ResponseEntity<String> response=removePostInSearchingServiceDB(postId);
				if(response.getStatusCodeValue()!=200) {
					throw new HttpServerErrorException(HttpStatus.REQUEST_TIMEOUT, "Removing was failed in Searching service database");
				}
				
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
				savePostInSearchingServiceDB (post); // отправить в Серчинг запрос на редактирование, дождаться ответа
				
				// отправить в Кафку асинхр запрос на поиск совпадений и рассылку
				
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
		PagedListHolder<PostDto> pagedListHolder = createPageListHolder(page, quantity, flag);
		return createModelAndViewObject(pagedListHolder, page, quantity);
	}
	
	@Override
	public ModelAndView getPostsFeedMatchingByType(int page, String type, String flag) {
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
	
	@Override
	public ModelAndView getPostsFeedMatchingByBreed(int page, String breed, String flag) {
		List<PostDto> list = lostFoundRepository.findByBreed(breed)
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
	
	@Override
	public ModelAndView getPostsFeedMatchingByLocation(int page, String address, String flag) {

		List<String> postIds = convertArrayToList(getListOfPostIdByAddress(address, flag));
		
		List<PostDto> list = lostFoundRepository.findAll()
				.stream()
				.filter(p->postIds.contains(p.getId()))
				.sorted((p1,p2)->p2.getDateOfPublish().compareTo(p1.getDateOfPublish()))
				.map(p -> convertPostToPostDto(p))
				.collect(Collectors.toList());
		
		int quantity = lostFoundConfiguration.getQuantity();
		PagedListHolder<PostDto> pagedListHolder = new PagedListHolder<PostDto>(list);
		pagedListHolder.setPage(page);
		pagedListHolder.setPageSize(quantity);
		return createModelAndViewObject(pagedListHolder, page, quantity);
	}
	
	@Override
	public ModelAndView getPostsFeedMatchingByFeatures(int page, String postId, String flag) {

		List<String> postIds = convertArrayToList(getListOfPostIdByDistFeatures(postId, flag));
		
		List<PostDto> list = lostFoundRepository.findAll()
				.stream()
				.filter(p->postIds.contains(p.getId()))
				.sorted((p1,p2)->p2.getDateOfPublish().compareTo(p1.getDateOfPublish()))
				.map(p -> convertPostToPostDto(p))
				.collect(Collectors.toList());
		
		int quantity = lostFoundConfiguration.getQuantity();
		PagedListHolder<PostDto> pagedListHolder = new PagedListHolder<PostDto>(list);
		pagedListHolder.setPage(page);
		pagedListHolder.setPageSize(quantity);
		return createModelAndViewObject(pagedListHolder, page, quantity);
	}
	
	
	@Override
	public void cleanPostsOfRemovedUser(String authorId) {
		lostFoundRepository.findAll().stream()
					.filter(p->p.getAuthorData().getAuthorId().equalsIgnoreCase(authorId))
					.forEach(post->lostFoundRepository.delete(post));	
		ResponseEntity<String> response=removePostsOfAuthorInSearchingService (authorId);
		if(response.getStatusCodeValue()!=200) {
			throw new HttpServerErrorException(HttpStatus.REQUEST_TIMEOUT, "Removing was failed in Searching service database");
		}
	}
	
	@Override
	public PostDto getNewMatchedPost(String postId) throws PostNotFoundException {
		Post post = lostFoundRepository.findById(postId).orElseThrow(()->new PostNotFoundException());
		return convertPostToPostDto(post);
	}
	
	@Override
	public ModelAndView getFeedOfMatchingPosts(int page, String postId) throws Throwable {
		Post post = lostFoundRepository.findById(postId).orElseThrow(()->new PostNotFoundException());
		List<String> postIds = convertArrayToList(getListOfMatchedPostId(postId, post.getFlag()));
		
		List<PostDto> list = lostFoundRepository.findAll()
				.stream()
				.filter(p->postIds.contains(p.getId()))
				.sorted((p1,p2)->p2.getDateOfPublish().compareTo(p1.getDateOfPublish()))
				.map(p -> convertPostToPostDto(p))
				.collect(Collectors.toList());
		
		int quantity = lostFoundConfiguration.getQuantity();
		PagedListHolder<PostDto> pagedListHolder = new PagedListHolder<PostDto>(list);
		pagedListHolder.setPage(page);
		pagedListHolder.setPageSize(quantity);
		return createModelAndViewObject(pagedListHolder, page, quantity);
	}
	
	
	
	
	
	

// UTILS!
//___________________________________________________________
	

	private PostToConvertDto convertPostToPostToConvertDto (Post post) {
		return PostToConvertDto.builder()
				.id(post.getId())
				.flag(post.getFlag())
				.email(post.getAuthorData().getAuthorId())
				.type(post.getType())
				.address(post.getAddress())
				.distinctiveFeatures(post.getDistinctiveFeatures())
				.picturesURLs(post.getPicturesURLs())
				.build();
	}
	

	private ResponseEntity<String> removePostInSearchingServiceDB(String postId) {
		RestTemplate restTemplate = lostFoundConfiguration.restTemplate();
		// String url = "https://propets-.../security/v1/post";
		String url = "http://localhost:8085/search/v1/post"; // to Searching service
		try {
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).queryParam("postId", postId);
			RequestEntity<PostToConvertDto> request = new RequestEntity<>(HttpMethod.DELETE, builder.build().toUri());
			ResponseEntity<String> newResponse = restTemplate.exchange(request, String.class);
			return newResponse;
		} catch (HttpClientErrorException e) {
			throw new RuntimeException("Removing post is failed");
		}
	}
	
	private ResponseEntity<String> removePostsOfAuthorInSearchingService(String authorId) {
		RestTemplate restTemplate = lostFoundConfiguration.restTemplate();
		// String url = "https://propets-.../security/v1/post";
		String url = "http://localhost:8085/search/v1/cleaner"; // to Searching service
		try {
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).queryParam("authorId", authorId);
			RequestEntity<PostToConvertDto> request = new RequestEntity<>(HttpMethod.DELETE, builder.build().toUri());
			ResponseEntity<String> newResponse = restTemplate.exchange(request, String.class);
			return newResponse;
		} catch (HttpClientErrorException e) {
			throw new RuntimeException("Removing post is failed");
		}
		
	}
	
	private ResponseEntity<String> savePostInSearchingServiceDB (Post post) {
		PostToConvertDto body = convertPostToPostToConvertDto (post);

		RestTemplate restTemplate =lostFoundConfiguration.restTemplate();

		//String url = "https://propets-.../security/v1/post";
		String url = "http://localhost:8085/search/v1/post"; //to Searching service
		try {
			HttpHeaders newHeaders = new HttpHeaders();
			newHeaders.add("Content-Type", "application/json");
			BodyBuilder requestBodyBuilder = RequestEntity.method(HttpMethod.POST, URI.create(url)).headers(newHeaders);
			RequestEntity<PostToConvertDto> request = requestBodyBuilder.body(body);
			ResponseEntity<String> newResponse = restTemplate.exchange(request, String.class);
			return newResponse;
		} catch (HttpClientErrorException e) {
			throw new RuntimeException("Saving post is failed");
		}
	}
	
	
	private List<String> convertArrayToList(String[] array){
		 List<String> collection = new ArrayList<>();
		for (int i = 0; i < array.length; i++) {
			collection.add(array[i]);
		}
		 return collection;
	}
	
	private PostDto convertPostToPostDto(Post post) {
		return PostDto.builder()
				.id(post.getId())
				.type(post.getType())
				.breed(post.getBreed())
				.color(post.getColor())
				.height(post.getHeight())
				.sex(post.getSex())
				.description(post.getDescription())
				.address(post.getAddress())
				.distinctiveFeatures(post.getDistinctiveFeatures())
				.picturesURLs(post.getPicturesURLs())
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

	
	private String[] getListOfPostIdByAddress(String address, String flag) {
		RestTemplate restTemplate =lostFoundConfiguration.restTemplate();
		try {
			HttpHeaders newHeaders = new HttpHeaders();
			newHeaders.add("Content-Type", "application/json");
			//String url = "https://propets-.../search/v1/location";
			String url = "http://localhost:8085/search/v1/location"; //to Searching service
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
					.queryParam("address",address)
					.queryParam("flag", flag);
			RequestEntity<String> request = new RequestEntity<String>(HttpMethod.GET, builder.build().toUri());
			ResponseEntity<SearchResponseDto> newResponse = restTemplate.exchange(request, SearchResponseDto.class);
			return newResponse.getBody().getPostIds();
		} catch (HttpClientErrorException e) {
			throw new RuntimeException("Searching post is failed");
		}
	}
	
	private String[] getListOfPostIdByDistFeatures(String postId, String flag) {
		RestTemplate restTemplate =lostFoundConfiguration.restTemplate();
		try {
			HttpHeaders newHeaders = new HttpHeaders();
			newHeaders.add("Content-Type", "application/json");
			//String url = "https://propets-.../search/v1/features";
			String url = "http://localhost:8085/search/v1/features"; //to Searching service
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
					.queryParam("postId",postId)
					.queryParam("flag", flag);
			RequestEntity<String> request = new RequestEntity<String>(HttpMethod.GET, builder.build().toUri());
			ResponseEntity<SearchResponseDto> newResponse = restTemplate.exchange(request, SearchResponseDto.class);
			return newResponse.getBody().getPostIds();
		} catch (HttpClientErrorException e) {
			throw new RuntimeException("Searching post is failed");
		}
	}

	private String[] getListOfMatchedPostId(String postId, String flag) {
		RestTemplate restTemplate =lostFoundConfiguration.restTemplate();
		try {
			HttpHeaders newHeaders = new HttpHeaders();
			newHeaders.add("Content-Type", "application/json");
			//String url = "https://propets-.../search/v1/features";
			String url = "http://localhost:8085/search/v1/all_matched"; //to Searching service
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
					.queryParam("postId",postId)
					.queryParam("flag", flag);
			RequestEntity<String> request = new RequestEntity<String>(HttpMethod.GET, builder.build().toUri());
			ResponseEntity<SearchResponseDto> newResponse = restTemplate.exchange(request, SearchResponseDto.class);
			return newResponse.getBody().getPostIds();
		} catch (HttpClientErrorException e) {
			throw new RuntimeException("Searching posts is failed");
		}
	}

	

}
