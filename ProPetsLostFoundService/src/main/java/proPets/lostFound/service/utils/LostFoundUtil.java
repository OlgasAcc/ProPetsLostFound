package proPets.lostFound.service.utils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.BodyBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import proPets.lostFound.configuration.LostFoundConfiguration;
import proPets.lostFound.dao.LostFoundMongoRepository;
import proPets.lostFound.dto.PostDto;
import proPets.lostFound.dto.PostToConvertDto;
import proPets.lostFound.dto.SearchResponseDto;
import proPets.lostFound.model.Post;

@Component
public class LostFoundUtil {

	@Autowired
	LostFoundMongoRepository lostFoundRepository;
	
	@Autowired
	LostFoundConfiguration lostFoundConfiguration;
	
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
	public CompletableFuture<List<PostDto>> getListAndConvertToListOfPostDto (PageRequest pageReq){
		Page<Post> posts = lostFoundRepository.findAll(pageReq);
		return CompletableFuture.supplyAsync(() -> {
	    	return posts.getContent().stream()
					.map(this::convertPostToPostDto)
					.collect(Collectors.toList());
	    });
	}
		
	@Async("processExecutor")
	public CompletableFuture<String> removePostInSearchingServiceDB(String postId) {
		RestTemplate restTemplate = lostFoundConfiguration.restTemplate();

		String url = lostFoundConfiguration.getBaseSearchUrl() + "search/v1/post"; // to Searching service
		try {
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).queryParam("postId", postId);
			RequestEntity<PostToConvertDto> request = new RequestEntity<>(HttpMethod.DELETE, builder.build().toUri());
			ResponseEntity<String> newResponse = restTemplate.exchange(request, String.class);
			return CompletableFuture.completedFuture(newResponse.getBody());
		} catch (HttpClientErrorException e) {
			throw new RuntimeException("Removing post is failed");
		}
	}
	
	@Async("processExecutor")
	public CompletableFuture<Post> savePostInDatabase(Post post) {
	    return CompletableFuture.supplyAsync(() -> {
	    	return lostFoundRepository.save(post);
	    });
	}
	
	@Async("processExecutor")
	public CompletableFuture<Void> deletePostFromDatabase(Post post) {
	    return CompletableFuture.runAsync(() -> {
	    	lostFoundRepository.delete(post);
	    });
	}
	
	@Async("processExecutor")
	public CompletableFuture<String> savePostInSearchingServiceDB (Post post) {
		PostToConvertDto body = convertPostToPostToConvertDto (post);
System.out.println("im saving post");
		RestTemplate restTemplate = lostFoundConfiguration.restTemplate();

		String url = lostFoundConfiguration.getBaseSearchUrl() + "search/v1/post"; //to Searching service
		try {
			HttpHeaders newHeaders = new HttpHeaders();
			newHeaders.add("Content-Type", "application/json");
			BodyBuilder requestBodyBuilder = RequestEntity.method(HttpMethod.POST, URI.create(url)).headers(newHeaders);
			RequestEntity<PostToConvertDto> request = requestBodyBuilder.body(body);
			ResponseEntity<String> newResponse = restTemplate.exchange(request, String.class);
			return CompletableFuture.completedFuture(newResponse.getBody());
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
	
	public List<PostDto> getListOfPostDtoByListOfPostIds (List<String>postIds, int page){
		int quantity = lostFoundConfiguration.getQuantity();
		//Query query = new Query();
		//query.addCriteria(Criteria.where("id").in(postIds));
		PageRequest pageReq = PageRequest.of(page, quantity, Sort.Direction.DESC, "dateOfPublish");
		
		List<Post> list = lostFoundRepository.findAll().stream()
				.filter(p->postIds.contains(p.getId()))
				.collect(Collectors.toList());
		System.out.println(list.toArray());
		Page<Post> posts = new PageImpl<Post>(list, pageReq, quantity);
		System.out.println(posts.getContent().toArray());
		return posts.getContent()
				.stream()
				.map(p -> convertPostToPostDto(p))
				.collect(Collectors.toList());
	}
	
	public String[] getListOfPostIdByAddress(String address, String flag) {
		RestTemplate restTemplate = lostFoundConfiguration.restTemplate();
		try {
			HttpHeaders newHeaders = new HttpHeaders();
			newHeaders.add("Content-Type", "application/json");

			String url = lostFoundConfiguration.getBaseSearchUrl() + "search/v1/location"; //to Searching service
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
					.queryParam("address", address)
					.queryParam("flag", flag);
			RequestEntity<String> request = new RequestEntity<String>(HttpMethod.GET, builder.build().toUri());
			ResponseEntity<SearchResponseDto> newResponse = restTemplate.exchange(request, SearchResponseDto.class);
			return newResponse.getBody().getPostIds();
		} catch (HttpClientErrorException e) {
			throw new RuntimeException("Searching post is failed");
		}
	}
	
	public String[] getListOfPostIdByDistFeatures(String features, String flag) {
		RestTemplate restTemplate = lostFoundConfiguration.restTemplate();
		try {
			HttpHeaders newHeaders = new HttpHeaders();
			newHeaders.add("Content-Type", "application/json");

			String url = lostFoundConfiguration.getBaseSearchUrl() + "search/v1/features"; //to Searching service
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
					.queryParam("features", features)
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

			String url = lostFoundConfiguration.getBaseSearchUrl() + "search/v1/all_matched"; //to Searching service
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


