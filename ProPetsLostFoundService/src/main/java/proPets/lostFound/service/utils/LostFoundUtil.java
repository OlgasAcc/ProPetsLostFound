package proPets.lostFound.service.utils;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.BodyBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import proPets.lostFound.configuration.LostFoundConfiguration;
import proPets.lostFound.dao.LostFoundRepository;
import proPets.lostFound.dto.PostDto;
import proPets.lostFound.dto.PostToConvertDto;
import proPets.lostFound.dto.SearchResponseDto;
import proPets.lostFound.model.Post;

@Component
public class LostFoundUtil implements Serializable {

	@Autowired
	LostFoundRepository lostFoundRepository;
	
	@Autowired
	LostFoundConfiguration lostFoundConfiguration;

	private static final long serialVersionUID = -2550185165626007488L;
	
	public PostToConvertDto convertPostToPostToConvertDto (Post post) {
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
		
	@Async("processExecutor")
	public CompletableFuture<String> removePostInSearchingServiceDB(String postId) {
		RestTemplate restTemplate = lostFoundConfiguration.restTemplate();
		// String url = "https://propets-.../search/v1/post";
		String url = "http://localhost:8085/search/v1/post"; // to Searching service
		try {
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).queryParam("postId", postId);
			RequestEntity<PostToConvertDto> request = new RequestEntity<>(HttpMethod.DELETE, builder.build().toUri());
			ResponseEntity<String> newResponse = restTemplate.exchange(request, String.class);
			return CompletableFuture.completedFuture(newResponse.getBody());
		} catch (HttpClientErrorException e) {
			throw new RuntimeException("Removing post is failed");
		}
	}
	
	public ResponseEntity<String> savePostInSearchingServiceDB (Post post) {
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
	
	
	public List<String> convertArrayToList(String[] array){
		 List<String> collection = new ArrayList<>();
		for (int i = 0; i < array.length; i++) {
			collection.add(array[i]);
		}
		 return collection;
	}
	
	public PostDto convertPostToPostDto(Post post) {
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
	
	public PagedListHolder<PostDto> createPageListHolder(int pageNumber, int quantity, String flag) {	
		List<PostDto> list = getUpdatedFilteredPostFeed(flag);
		PagedListHolder<PostDto> pagedListHolder = new PagedListHolder<>(list);
		pagedListHolder.setPage(pageNumber);
		pagedListHolder.setPageSize(quantity);
		return pagedListHolder;
	}
	
	public ModelAndView createModelAndViewObject (PagedListHolder<PostDto> pagedListHolder, int page, int pageSize) {
		ModelAndView mav = new ModelAndView("list of posts", HttpStatus.OK);
		mav.addObject("pagedList", pagedListHolder.getPageList());
		mav.addObject("page", 0);
		mav.addObject("maxPage", pagedListHolder.getPageCount());
		return mav;
	}

	public List<PostDto> getUpdatedFilteredPostFeed(String flag){
		List<PostDto> list = lostFoundRepository.findAll().stream()
				.filter(post -> post.getFlag().equalsIgnoreCase(flag))
				.sorted((p1,p2)->p2.getDateOfPublish().compareTo(p1.getDateOfPublish()))
				.map(p -> convertPostToPostDto(p))
				.collect(Collectors.toList());
		return list;
	}	

	
	public String[] getListOfPostIdByAddress(String address, String flag) {
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
	
	public String[] getListOfPostIdByDistFeatures(String postId, String flag) {
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

	public String[] getListOfMatchedPostId(String postId, String flag) {
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
