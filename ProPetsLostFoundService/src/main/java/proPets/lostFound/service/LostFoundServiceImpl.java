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

import proPets.lostFound.configuration.LostFoundConfiguration;
import proPets.lostFound.dao.LostFoundRepository;
import proPets.lostFound.dto.NewPostDto;
import proPets.lostFound.dto.PostDto;
import proPets.lostFound.dto.PostEditDto;
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

			lostFoundRepository.save(post);
			int quantity = lostFoundConfiguration.getQuantity();	
			
			// тут конвертировать в ПостТуКонверт
			// тут отправить запрос в Конвертер вместе с постом из Монго (в ПостТуКонверт), дождаться ответа, что пост сохранен в Эластик
			
			// в конце - отправить асинхронный метод на поиск в противоположной базе и мэйл-уведомления в Кафку
			
			PagedListHolder<PostDto> pagedListHolder = createPageListHolder(0, quantity, flag);
			return createModelAndViewObject(pagedListHolder, 0, quantity);

	}

	@Override
	public ModelAndView removePost(String currentUserId, String postId, String flag) throws Throwable {
		try {
			Post post = lostFoundRepository.findById(postId).orElseThrow(() -> new PostNotFoundException());

			if (currentUserId.equalsIgnoreCase(post.getAuthorData().getAuthorId())) {
				lostFoundRepository.delete(post);
				
				// тут отправить в сервис Сёрчинг запрос с айди поста на удаление поста, дождаться ответа
				
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
				post.setSex(postEditDto.getSex() != null ? postEditDto.getSex() : post.getSex());
				post.setColor(postEditDto.getColor() != null ? postEditDto.getColor() : post.getColor());
				post.setHeight(postEditDto.getHeight() != null ? postEditDto.getHeight() : post.getHeight());
				post.setDescription(postEditDto.getDescription() != null ? postEditDto.getDescription() : post.getDescription());
				post.setDistinctiveFeatures(postEditDto.getDistFeatures() != null ? postEditDto.getDistFeatures(): post.getDistinctiveFeatures());
				post.setAddress(postEditDto.getAddress() != null ? postEditDto.getAddress() : post.getAddress());
				post.setPicturesURLs(postEditDto.getPicturesURLs() != null ? postEditDto.getPicturesURLs() : post.getPicturesURLs());
				post.getAuthorData().setEmail(postEditDto.getEmail() != null ? postEditDto.getEmail() : post.getAuthorData().getEmail());
				post.getAuthorData().setPhone(postEditDto.getPhone() != null ? postEditDto.getPhone() : post.getAuthorData().getPhone());
				post.getAuthorData().setFb_link(postEditDto.getFb_link() != null ? postEditDto.getFb_link(): post.getAuthorData().getFb_link());
				post.setDateOfPublish(LocalDateTime.now());
				
				lostFoundRepository.save(post);
				
				// тут конвертировать в ПостТуКонверт
				// отправить на сервер Конвертер, тот перезапишет в Эластик (Сёрчинг), вернет ОК
				
				return getPostFeed(0, flag);
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
	
	@Override
	public ModelAndView getPostFeedByBreed(int page, String breed, String flag) {
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
	
	private PostDto convertPostToPostDto(Post post) {
		return PostDto.builder()
				.id(post.getId())
				.type(post.getType())
				.breed(post.getBreed())
				.color(post.getColor())
				.height(post.getHeight())
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


}
