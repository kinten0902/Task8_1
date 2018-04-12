package com.kin.task8;

public class Main {

	public static void main(String[] args) {
		Launch launch = new Launch();
		Thread thread = new Thread(launch);
		thread.start();
	}

}
