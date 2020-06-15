package proPets.lostFound.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder

public class PostEditDto {

	String breed;
	String sex;
	String color;
	String height;
	String description;
	String distFeatures;
	String[] pictureUrls;
	String location;
	
	String phone;
	String fb_link;
	String email;

}
