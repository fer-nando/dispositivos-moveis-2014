
public class Municipio {
	
	private int id;
	//private String cd_geocodigo;
	//private String tipo;
	//private String cd_geocodba;
	//private String nm_bairro;
	//private String cd_geocodsd;
	//private String nm_subdistrito;
	//private String cd_geocodds;
	//private String nm_distrito;
	private int cd_geocodmu;
	private String nm_municipio;
	//private String nm_micro;
	//private String nm_meso;
	private String nm_uf;
	//private String cd_nivel;
	//private String cd_categoria;
	//private String nm_categoria;
	//private String nm_localidade;
	private double longitude;
	private double latitude;
	private double altitude;
	
	public Municipio() {
		clear();
	}
	public void clear() {
		id = -1;
		cd_geocodmu = -1;
		nm_municipio = "";
		nm_uf = "";
		longitude = -1;
		latitude = -1;
		altitude = -1;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCd_geocodmu() {
		return cd_geocodmu;
	}
	public void setCd_geocodmu(int cd_geocodmu) {
		this.cd_geocodmu = cd_geocodmu;
	}
	public String getNm_municipio() {
		return nm_municipio;
	}
	public void setNm_municipio(String nm_municipio) {
		this.nm_municipio = nm_municipio;
	}
	public String getNm_uf() {
		return nm_uf;
	}
	public void setNm_uf(String nm_uf) {
		this.nm_uf = nm_uf;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getAltitude() {
		return altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	@Override
	public String toString() {
		return "" + id 
				+ "; " + cd_geocodmu 
				+ "; " + nm_municipio 
				+ "; " + nm_uf 
				+ "; " + longitude 
				+ "; " + latitude 
				+ "; " + altitude;
	}
	
}
