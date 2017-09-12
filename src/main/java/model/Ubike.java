package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the ubike database table.
 * 
 */
@Entity
@NamedQuery(name="Ubike.findAll", query="SELECT u FROM Ubike u")
public class Ubike implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String sno;

	private String act;

	private String ar;

	private String aren;

	private Integer bemp;

	private double lat;

	private double lng;

	@Temporal(TemporalType.TIMESTAMP)
	private Date mday;

	private String sarea;

	private String sareaen;

	private Integer sbi;

	private String sna;

	private String snaen;

	private Integer tot;

	public Ubike() {
	}

	public String getSno() {
		return this.sno;
	}

	public void setSno(String sno) {
		this.sno = sno;
	}

	public String getAct() {
		return this.act;
	}

	public void setAct(String act) {
		this.act = act;
	}

	public String getAr() {
		return this.ar;
	}

	public void setAr(String ar) {
		this.ar = ar;
	}

	public String getAren() {
		return this.aren;
	}

	public void setAren(String aren) {
		this.aren = aren;
	}

	public Integer getBemp() {
		return this.bemp;
	}

	public void setBemp(Integer bemp) {
		this.bemp = bemp;
	}

	public double getLat() {
		return this.lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return this.lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public Date getMday() {
		return this.mday;
	}

	public void setMday(Date mday) {
		this.mday = mday;
	}

	public String getSarea() {
		return this.sarea;
	}

	public void setSarea(String sarea) {
		this.sarea = sarea;
	}

	public String getSareaen() {
		return this.sareaen;
	}

	public void setSareaen(String sareaen) {
		this.sareaen = sareaen;
	}

	public Integer getSbi() {
		return this.sbi;
	}

	public void setSbi(Integer sbi) {
		this.sbi = sbi;
	}

	public String getSna() {
		return this.sna;
	}

	public void setSna(String sna) {
		this.sna = sna;
	}

	public String getSnaen() {
		return this.snaen;
	}

	public void setSnaen(String snaen) {
		this.snaen = snaen;
	}

	public Integer getTot() {
		return this.tot;
	}

	public void setTot(Integer tot) {
		this.tot = tot;
	}

}