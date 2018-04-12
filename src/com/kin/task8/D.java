package com.kin.task8;

import java.io.Serializable;

public class D implements Serializable {

	private static final long serialVersionUID = 1L;
	private int num;
	private String sen1;
	private String sen2;
	private String e1;
	private String e2;
	private int rel;
	private int ord;

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getSen1() {
		return sen1;
	}

	public void setSen1(String sen1) {
		this.sen1 = sen1;
	}

	public String getSen2() {
		return sen2;
	}

	public void setSen2(String sen2) {
		this.sen2 = sen2;
	}

	public String getE1() {
		return e1;
	}

	public void setE1(String e1) {
		this.e1 = e1;
	}

	public String getE2() {
		return e2;
	}

	public void setE2(String e2) {
		this.e2 = e2;
	}

	public int getRel() {
		return rel;
	}

	public void setRel(int rel) {
		this.rel = rel;
	}

	public int getOrd() {
		return ord;
	}

	public void setOrd(int ord) {
		this.ord = ord;
	}

	@Override
	public String toString() {
		return "D [num=" + num + ", sen1=" + sen1 + ", sen2=" + sen2 + ", e1=" + e1 + ", e2=" + e2 + ", rel=" + rel + ", ord=" + ord + "]";
	}

}
