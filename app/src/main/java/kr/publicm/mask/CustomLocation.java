package kr.publicm.mask;

public class CustomLocation {
    private double lat, lng;
    private int range;

    public CustomLocation(double lat, double lng) {
        this.setLocation(lat, lng);
    }

    public CustomLocation(double lat, double lng, int range) {
        this.setLocation(lat, lng);
        this.setRange(range);
    }

    public CustomLocation(android.location.Location location) {
        this.setLocation(location);
    }

    public CustomLocation(android.location.Location location, int range) {
        this.setLocation(location, range);
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public void setLocation(double lat, double lng) {
        this.setLat(lat);
        this.setLng(lng);
    }

    public void setLocation(double lat, double lng, int range) {
        this.setLocation(lat, lng);
        this.setRange(range);
    }

    public void setLocation(android.location.Location location) {
        this.setLocation(location.getLatitude(), location.getLongitude());
    }

    public void setLocation(android.location.Location location, int range) {
        this.setLocation(location.getLatitude(), location.getLongitude());
        this.setRange(range);
    }
};