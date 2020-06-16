package proPets.lostFound.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import proPets.lostFound.model.Post;

public interface LostFoundRepository extends MongoRepository<Post, String> {

	// @Query("{categories:{$in: ?0}}")
   // Iterable<Node> findByCategories(List<String> categories);


	//List<PostLost> findAll();
	
	//Stream<PostLost>findByAuthorId(String authorId);
}
