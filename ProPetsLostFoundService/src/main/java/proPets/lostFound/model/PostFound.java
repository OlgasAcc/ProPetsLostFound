package proPets.lostFound.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode(of = { "id" })
@Builder
@Document(collection = "posts_found")

public class PostFound {

	@Id
	String id;
	Post post;
	
	public PostFound(Post post) {
		this.post = post;
	}

}
