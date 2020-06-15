package proPets.lostFound.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Getter;
import lombok.Setter;
import proPets.lostFound.configuration.LostFoundConfiguration;

//@NoArgsConstructor
@Getter
@Setter

public class Photo {

	@Autowired
	LostFoundConfiguration lostFoundConfig;

	String picturesUrl;
	List<String> tags;

	public Photo(String pictureUrl) {
		this.picturesUrl = pictureUrl;
		tags = new ArrayList<String>(); // заглушка: здесь должен отправляться запрос через TagService в Имаггу с помощью бина
										// РестТемплейт
	}
}