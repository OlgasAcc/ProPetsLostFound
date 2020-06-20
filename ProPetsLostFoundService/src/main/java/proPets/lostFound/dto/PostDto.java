package proPets.lostFound.dto;

import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import proPets.lostFound.model.AuthorData;

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
	String location;	
	Set<String> distinctiveFeatures;
	String[] picturesURLs;
	AuthorData authorData;
	@JsonFormat(pattern = "yyyy-MM-dd")
	LocalDateTime dateOfPublish;
}
