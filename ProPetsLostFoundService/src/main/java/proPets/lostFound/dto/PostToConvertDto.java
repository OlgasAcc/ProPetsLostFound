package proPets.lostFound.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder

public class PostToConvertDto implements Serializable{ 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String id; //post id
	String email; //authorId
	String flag;
	String type;
	String address;
	String distinctiveFeatures;
	String[] picturesURLs;
}
