package proPets.lostFound.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import proPets.lostFound.model.Post;

public interface LostFoundMongoRepository extends MongoRepository<Post, String> {
	
	List<Post> findAll();
	
	Page<Post> findAll(Pageable pageReq);
	
	//Page<Post> findAll(Query query, Pageable pageReq);

	Page<Post> findByTypeLikeAndFlagLike(String type, String flag, Pageable pageReq);
	
	Page<Post> findByBreedLikeAndFlagLike(String breed, String flag, Pageable pageReq);
	
	Page<Post> findByAuthorDataAuthorId(String authorId, Pageable pageReq);
}
