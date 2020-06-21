package proPets.lostFound.service;

import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.model.GeocodingResult;

import proPets.lostFound.configuration.LostFoundConfiguration;
import proPets.lostFound.model.GeoPoint;

@Service
public class GeoLocationServiceImpl implements GeoLocationService {

	@Autowired
	LostFoundConfiguration lostFoundConfiguration;

	@Override
	public Double[] getLocationByAddress(String address) throws URISyntaxException {

		GeoApiContext context = new GeoApiContext().setApiKey(lostFoundConfiguration.getGeoKey());
		GeocodingApiRequest request = GeocodingApi.newRequest(context).address(formateAddressForGeoRequest(address))
				.language("en");
		try {
			GeocodingResult[] results = request.await();
			Double[] res = new Double[2];
			res[0] = results[0].geometry.location.lat;
			res[1] = results[0].geometry.location.lng;
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String formateAddressForGeoRequest(String address) {
		return address.replace(" ", "+");
	}

	@Override
	public GeoPoint getGeoPointByAddress(String address) throws URISyntaxException {
		GeoPoint location = new GeoPoint();
		Double[] coordinates = getLocationByAddress(address);
		location.setAddress(address);
		location.setLatitude(coordinates[0]);
		location.setLongitude(coordinates[1]);
		return location;
	}

}
