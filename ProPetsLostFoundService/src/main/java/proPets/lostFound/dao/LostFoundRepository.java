package proPets.lostFound.dao;

import java.util.stream.Stream;

import org.springframework.data.mongodb.repository.MongoRepository;

import proPets.lostFound.model.Post;

public interface LostFoundRepository extends MongoRepository<Post, String> {

	Stream<Post> findByType(String type);
}
