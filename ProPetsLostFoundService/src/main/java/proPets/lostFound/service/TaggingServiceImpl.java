package proPets.lostFound.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import proPets.lostFound.configuration.LostFoundConfiguration;
import proPets.lostFound.dto.TagResponseDto;
import proPets.lostFound.dto.TagResultDto;

@Service
public class TaggingServiceImpl implements TaggingService {

	@Autowired
	LostFoundConfiguration lostFoundConfiguration;

	@Override
	public TagResultDto getPictureTags(String url) {
		RestTemplate restTemplate = lostFoundConfiguration.restTemplate();
	
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic YWNjXzc5MDBkNDQ3YmUzYTM1Njo2Y2ZkYzFmMmRkY2Y5ZTIxODY3YzFjNGY2ZmVmOGNjYg==");
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(lostFoundConfiguration.getUrl())
				.queryParam("image_url",url)
				.queryParam("language", "en")
				.queryParam("limit", 10);
		RequestEntity<String> request = new RequestEntity<String>(headers, HttpMethod.GET, builder.build().toUri());

		ResponseEntity<TagResponseDto> response = restTemplate.exchange(request, TagResponseDto.class);
		return response.getBody().getResult();
	}

	
}
