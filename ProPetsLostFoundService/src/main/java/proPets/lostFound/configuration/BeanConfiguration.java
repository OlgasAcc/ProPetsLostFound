package proPets.lostFound.configuration;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
@Getter
public class BeanConfiguration {
	private int quantity;
	private String baseJWTUrl;
	private String baseSearchUrl;

	public BeanConfiguration(int quantity, String baseJWTUrl,String baseSearchUrl) {
		this.quantity = quantity;
		this.baseJWTUrl=baseJWTUrl;
		this.baseSearchUrl=baseSearchUrl;
	}
}
