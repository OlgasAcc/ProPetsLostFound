package proPets.lostFound.dto;

import java.util.Map;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class TagDto {
	double confidence;
	Map<String, String> tag;
}
