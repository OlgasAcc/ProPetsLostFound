package proPets.lostFound.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder

public class PostToConvertDto { 
	
	String id; //post id
	String email; //authorId
	String flag;
	String type;
	String address;
	String distinctiveFeatures;
	String[] picturesURLs;
}
