package proPets.lostFound.service;

import java.util.Set;

import proPets.lostFound.dto.TagResultDto;

public interface TaggingService {

	TagResultDto getPictureTags (String url);
	
	Set<String> getDistinctiveFeaturesTags (String newFeatures);

}
