package proPets.lostFound.dao.mongoDB;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import proPets.lostFound.model.post.Post;

@Repository
public interface LostFoundMongoRepository extends MongoRepository<Post, String> {
		
	//@Query("{ \"_id\" : { \"$in\" : [\"?0\"]}}")
	Page<Post> findByIdIn(List<String> postIds, Pageable pageReq);
	
	Stream<Post>findByAuthorDataAuthorId(String authorId);

	Page<Post> findByTypeLikeAndFlagLike(String type, String flag, Pageable pageReq);
	
	Page<Post> findByBreedLikeAndFlagLike(String breed, String flag, Pageable pageReq);
	
	Page<Post> findByAuthorDataAuthorId(String authorId, Pageable pageReq);
}
