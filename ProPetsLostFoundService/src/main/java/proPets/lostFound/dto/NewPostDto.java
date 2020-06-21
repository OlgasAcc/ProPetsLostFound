package proPets.lostFound.dto;

import lombok.Getter;

@Getter
public class NewPostDto {
	String type;
	String breed;
	String sex;
	String color;
	String height;
	String description;
	String distFeatures;
	String [] pictures; //pictures's urls
	String address;

	String authorAvatar; //должно храниться в бд, чтобы отрисовывать в ленте посты других авторов
	String authorName;  //должно храниться в бд, чтобы отображать в ленте посты других авторов
	String phone;
	String fb_link;
	String email;

}
