package proPets.lostFound.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter // хотя для AuthorData он не нужен
@EqualsAndHashCode(of = { "id" })
@ToString
@Document(collection = "all_posts_lost_found")
@Builder

public class Post {

	@Id
	String id;
	String flag;
	String type;
	String breed;
	String sex;
	String color;
	String height;
	String description;
	String address;
	String distinctiveFeatures;
	String [] picturesURLs;
	AuthorData authorData;
	@JsonFormat(pattern = "yyyy-MM-dd")
	LocalDateTime dateOfPublish;
}

