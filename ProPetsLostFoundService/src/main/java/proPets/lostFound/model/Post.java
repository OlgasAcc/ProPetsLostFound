package proPets.lostFound.model;

import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter // хотя для AuthorData он не нужен
@Builder
public class Post {

	// String lostOrFound;
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
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
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
