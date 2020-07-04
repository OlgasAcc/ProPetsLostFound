package proPets.lostFound.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class PostEditDto {

	String breed;
	String description;
	String distFeatures;
	String[] picturesURLs;
	String address;
	
	String phone;
	String fb_link;
	String email;

}
