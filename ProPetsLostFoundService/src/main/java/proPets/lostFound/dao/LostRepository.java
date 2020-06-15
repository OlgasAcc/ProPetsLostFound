package proPets.lostFound.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import proPets.lostFound.model.PostLost;

public interface LostRepository extends MongoRepository<PostLost, String> {

	// @Query("{categories:{$in: ?0}}")
   // Iterable<Node> findByCategories(List<String> categories);
	

	//List<PostLost> findAll();
	
	//Stream<PostLost>findByAuthorId(String authorId);
}
