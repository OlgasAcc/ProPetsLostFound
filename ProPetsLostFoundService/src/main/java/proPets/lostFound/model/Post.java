package proPets.lostFound.model;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter // хотя для AuthorData он не нужен
@Builder
@Document (collection="all_posts_lost_found")

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
	String location;
	Set<String> distinctiveFeatures;
	Set<Photo> pictures;
	AuthorData authorData;
	@JsonFormat(pattern = "yyyy-MM-dd")
	LocalDateTime dateOfPublish;

}

//	public boolean addPicture(String picture) {
//		Photo newPhoto = new Photo(picture);
//		if (pictures.size() <= 4) {
//			return pictures.add(newPhoto);
//		} else
//			throw new MaxUploadSizeExceededException(4);
//	}

//	public boolean removePicture(String picture) {
//		return pictures.remove(picture);
//	}
