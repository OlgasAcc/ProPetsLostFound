package proPets.lostFound.service;

import lombok.Getter;

@Getter
public class GeocodeResponse {

    public String status;
    public results[] results;

}

class results {
    public String formatted_address;
    public geometry geometry;
    public String[] types;
    public address_component[] address_components;
}

class geometry {
    public bounds bounds;
    public String location_type;
    public location location;
    public bounds viewport;
}

class bounds {

    public location northeast;
    public location southwest;
}

class location {
    public String lat;
    public String lng;
}

class address_component {
    public String long_name;
    public String short_name;
    public String[] types;
}
