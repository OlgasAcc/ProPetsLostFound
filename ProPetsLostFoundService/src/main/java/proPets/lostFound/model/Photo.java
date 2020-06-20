package proPets.lostFound.model;

import java.io.Serializable;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import proPets.lostFound.dto.TagDto;

@NoArgsConstructor
@Getter
@Setter

public class Photo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String picture;
	Set<TagDto> tags;

	public Photo(String picture, Set<TagDto> tags) {
		this.picture = picture;
		this.tags = tags;
	}
}