package com.kin.task8;

import java.io.Serializable;

public class W implements Serializable {

	private static final long serialVersionUID = 1L;
	private String word;
	private int sum;
	private int count;
	private double weigh;
	private int wn;

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getSum() {
		return sum;
	}

	public void setSum(int sum) {
		this.sum = sum;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public double getWeigh() {
		return weigh;
	}

	public void setWeigh(double weigh) {
		this.weigh = weigh;
	}

	public int getWn() {
		return wn;
	}

	public void setWn(int wn) {
		this.wn = wn;
	}

	@Override
	public String toString() {
		return "W [word=" + word + ", sum=" + sum + ", count=" + count + ", weigh=" + weigh + ", wn=" + wn + "]";
	}

}
