package proPets.lostFound.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class GoogleGeoCodeResponse {
	public String status;
	public Results[] results;

	@Getter
	public class Results {
		public String formatted_address;
		public Geometry geometry;
		public String[] types;
		public Address_component[] address_components;
	}

	@Getter
	public class Geometry {
		public Bounds bounds;
		public String location_type;
		public Location location;
		public Bounds viewport;
	}

	@Getter
	public class Location {
		public double lat;
		public double lng;
	}

	@Getter
	public class Bounds {
		public Location northeast;
		public Location southwest;
	}

	@Getter
	public class Address_component {
		public String long_name;
		public String short_name;
		public String[] types;
	}
}