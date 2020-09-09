package proPets.lostFound.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder

public class AuthorData {
	String authorId;
	String phone;
	String fb_link;
	String email;

	String authorAvatar; 
	String authorName;
}
