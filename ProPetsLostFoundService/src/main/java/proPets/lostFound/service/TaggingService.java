package proPets.lostFound.service;

import proPets.lostFound.dto.TagResultDto;

public interface TaggingService {

	TagResultDto getPictureTags (String url);

}
