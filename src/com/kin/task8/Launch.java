package com.kin.task8;

public class Launch implements Runnable {

	private MainLogic mainLogic;

	public Launch() {
		mainLogic = new MainLogic();
	}

	@Override
	public void run() {
		try {
//			mainLogic.DataToD1();
//			mainLogic.DataTestToD1();
			
//			mainLogic.D1ToD2();
//			mainLogic.D1ToD2Test();
			
			
			
//			mainLogic.makeW1Dicts();
			
//			mainLogic.makeW2Dicts();
//			mainLogic.makeW2Dicts_1();
			
			
			
			//1=不加权重,3=加权重
			mainLogic.make(3,100);
			mainLogic.makeTest(3,100);
			
//			mainLogic.test1();			
//			mainLogic.test2();
			
//			mainLogic.testWN();
//			mainLogic.test3();
			
			
			
			
			
//			mainLogic.test11();
//			mainLogic.test22();
//			mainLogic.test33();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
