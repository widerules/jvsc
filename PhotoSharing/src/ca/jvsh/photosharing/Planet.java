package ca.jvsh.photosharing;

public class Planet {

	private String name;
	private Integer distance;
	private String descr;
	private int idImg;
	private boolean checked;



	public Planet(String name, Integer distance) {
		this.name = name;
		this.distance = distance;
	}

	public Planet(String name, String descr) {
		this.name = name;
		this.descr = descr;
	}

	public Planet(String name, Integer distance, String descr, int idImg) {
		this.name = name;
		this.distance = distance;
		this.descr = descr;
		this.idImg = idImg;
	}
	


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isChecked()
	{
		return checked;
	}
	
	public void setChecked(boolean checked)
	{
		this.checked = checked;
	}
	
	public Integer getDistance() {
		return distance;
	}
	public void setDistance(Integer distance) {
		this.distance = distance;
	}
	public int getIdImg() {
		return idImg;
	}
	public void setIdImg(int idImg) {
		this.idImg = idImg;
	}


}