package proPets.lostFound.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter

public class Photo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String picture;
	Set<String> tags;

	public Photo(String picture) {
		this.picture = picture;
		tags = new HashSet<String>(); // заглушка: здесь должен отправляться запрос через TagService в Имаггу с помощью бина										// РестТемплейт
	}
}