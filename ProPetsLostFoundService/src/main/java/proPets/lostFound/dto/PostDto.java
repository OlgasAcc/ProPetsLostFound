package proPets.lostFound.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder

public class PostDto {
	
	String id;
	String type;
	String breed;
	String sex;
	String color;
	String height;
	String description;
	String address;
	String distinctiveFeatures;
	String[] picturesURLs;
	AuthorData authorData;
	@JsonFormat(pattern = "yyyy-MM-dd")
	LocalDateTime dateOfPublish;
}
