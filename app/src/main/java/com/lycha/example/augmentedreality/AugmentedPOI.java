package com.lycha.example.augmentedreality;

public class AugmentedPOI {
	private int poiId;
	private String poiName;
	private String poiDescription;
	private double poiLatitude;
	private double poiLongitude;
	
	public AugmentedPOI(String newName, String newDescription,
						double newLatitude, double newLongitude) {
		this.poiName = newName;
        this.poiDescription = newDescription;
        this.poiLatitude = newLatitude;
        this.poiLongitude = newLongitude;
	}
	
	public int getPoiId() {
		return poiId;
	}
	public void setPoiId(int poiId) {
		this.poiId = poiId;
	}
	public String getPoiName() {
		return poiName;
	}
	public void setPoiName(String poiName) {
		this.poiName = poiName;
	}
	public String getPoiDescription() {
		return poiDescription;
	}
	public void setPoiDescription(String poiDescription) {
		this.poiDescription = poiDescription;
	}
	public double getPoiLatitude() {
		return poiLatitude;
	}
	public void setPoiLatitude(double poiLatitude) {
		this.poiLatitude = poiLatitude;
	}
	public double getPoiLongitude() {
		return poiLongitude;
	}
	public void setPoiLongitude(double poiLongitude) {
		this.poiLongitude = poiLongitude;
	}
}
