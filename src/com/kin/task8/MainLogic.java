package com.kin.task8;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;

public class MainLogic {

	public static final String F_TRAIN = "data/TRAIN_FILE.TXT";// 8000条32000行
	public static final String F_TEST = "data/TEST_FILE.TXT";// 2717条10868行
	public static final String D_TRAIN_1 = "data/TRAIN_D_1.txt";// 处理后的包装类8000个
	public static final String D_TRAIN_2 = "data/TRAIN_D_2.txt";// 去除,.()并把e1和e2多词合并
	
	
	public static final String D_TEST_1 = "data/TEST_D_1.txt";// 处理后的包装类2717个
	public static final String D_TEST_2 = "data/TEST_D_2.txt";// 去除,.()并把e1和e2多词合并
	
	
	public static final String W_1 = "data/W_1.txt";// W1Dicts
	public static final String W_2 = "data/W_2.txt";// W2Dicts
	public static final String W_2_1 = "data/W_2_1.txt";// W2Dicts
	
	
	
	public void DataToD1() throws Exception {
		List<D> trains = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(new File(F_TRAIN)));
		for (int i = 0; i < 8000; i++) {
			D d = new D();
			String line = br.readLine();
			d.setNum(Integer.parseInt(line.split("\t")[0]));
			d.setSen1(line.split("\t")[1].replace("\"", "").replace("<e1>", "").replace("</e1>", "").replace("<e2>", "").replace("</e2>", ""));
			d.setE1(findE1(line.split("\t")[1]));
			d.setE2(findE2(line.split("\t")[1]));
			d.setSen2(findSen2(line.split("\t")[1].replace("\"", "")));

			line = br.readLine();
			d.setRel(findRel(line));
			d.setOrd(findOrd(line));
			line = br.readLine();
			line = br.readLine();
			trains.add(d);
		}
		br.close();
		ObjectOutputStream trainOut = new ObjectOutputStream(new FileOutputStream(new File(D_TRAIN_1)));
		trainOut.writeObject(trains);
		trainOut.close();
	}
	
	
	public void DataTestToD1() throws Exception {
		List<D> trains = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(new File(F_TEST)));
		for (int i = 0; i < 2717; i++) {
			D d = new D();
			String line = br.readLine();
			d.setNum(Integer.parseInt(line.split("\t")[0]));
			d.setSen1(line.split("\t")[1].replace("\"", "").replace("<e1>", "").replace("</e1>", "").replace("<e2>", "").replace("</e2>", ""));
			d.setE1(findE1(line.split("\t")[1]));
			d.setE2(findE2(line.split("\t")[1]));
			d.setSen2(findSen2(line.split("\t")[1].replace("\"", "")));

			line = br.readLine();
			d.setRel(findRel(line));
			d.setOrd(findOrd(line));
			line = br.readLine();
			line = br.readLine();
			trains.add(d);
		}
		br.close();
		ObjectOutputStream trainOut = new ObjectOutputStream(new FileOutputStream(new File(D_TEST_1)));
		trainOut.writeObject(trains);
		trainOut.close();
	}
	
	

	public void D1ToD2() throws Exception {
		List<D> trains = (ArrayList<D>) getObjectFromFile(D_TRAIN_1);
		int count = 0;
		for (int i = 0; i < trains.size(); i++) {
			boolean flag = false;
			String s1 = trains.get(i).getSen1();
			String s11 = trains.get(i).getSen1().replaceAll("\\,", " ").replaceAll("\\.", " ").replaceAll("\\:", " ").replaceAll("\\(", " ").replaceAll("\\)", " ").replaceAll("[' ']+", " ");
			String s22 = trains.get(i).getSen2().replaceAll("\\,", " ").replaceAll("\\.", " ").replaceAll("\\:", " ").replaceAll("\\(", " ").replaceAll("\\)", " ").replaceAll("[' ']+", " ");
			trains.get(i).setSen1(s11);
			trains.get(i).setSen2(s22.trim());

			String[] e1 = trains.get(i).getE1().split(" ");
			String[] e2 = trains.get(i).getE2().split(" ");
			if (e1.length != 1) {
				flag = true;

				String e11 = e1[0];
				for (int j = 1; j < e1.length; j++) {
					e11 = e11 + "_" + e1[j];
				}
				trains.get(i).setE1(e11);
				trains.get(i).setSen1(s11.replaceAll(e11.replaceAll("_", " "), e11));
			}
			if (e2.length != 1) {
				flag = true;

				String e22 = e2[0];
				for (int j = 1; j < e2.length; j++) {
					e22 = e22 + "_" + e2[j];
				}
				trains.get(i).setE2(e22);
				trains.get(i).setSen1(trains.get(i).getSen1().replaceAll(e22.replaceAll("_", " "), e22));
			}

			if (trains.get(i).getSen2().replaceAll("[' ']+", "").equals("")) {
				trains.get(i).setSen2(null);
			}

			if (flag) {
				count++;
				// System.out.println(s1);
				// System.out.println(s11);
				// System.out.println(trains.get(i).getSen1());
				// System.out.println(trains.get(i).getSen2());
				// System.out.println("e1: " + Arrays.toString(e1));
				// System.out.println("e2: " + Arrays.toString(e2));
				// System.out.println("e1: " + trains.get(i).getE1());
				// System.out.println("e2: " + trains.get(i).getE2());
				// System.out.println();
			}

		}
		// System.out.println(count);

		ObjectOutputStream trainOut = new ObjectOutputStream(new FileOutputStream(new File(D_TRAIN_2)));
		trainOut.writeObject(trains);
		trainOut.close();

		 for (D d : trains) {
		 System.out.println(d.getSen1());
		 }

	}

	
	
	public void D1ToD2Test() throws Exception {
		List<D> trains = (ArrayList<D>) getObjectFromFile(D_TEST_1);
		int count = 0;
		for (int i = 0; i < trains.size(); i++) {
			boolean flag = false;
			String s1 = trains.get(i).getSen1();
			String s11 = trains.get(i).getSen1().replaceAll("\\,", " ").replaceAll("\\.", " ").replaceAll("\\:", " ").replaceAll("\\(", " ").replaceAll("\\)", " ").replaceAll("[' ']+", " ");
			String s22 = trains.get(i).getSen2().replaceAll("\\,", " ").replaceAll("\\.", " ").replaceAll("\\:", " ").replaceAll("\\(", " ").replaceAll("\\)", " ").replaceAll("[' ']+", " ");
			trains.get(i).setSen1(s11);
			trains.get(i).setSen2(s22.trim());

			String[] e1 = trains.get(i).getE1().split(" ");
			String[] e2 = trains.get(i).getE2().split(" ");
			if (e1.length != 1) {
				flag = true;

				String e11 = e1[0];
				for (int j = 1; j < e1.length; j++) {
					e11 = e11 + "_" + e1[j];
				}
				trains.get(i).setE1(e11);
				trains.get(i).setSen1(s11.replaceAll(e11.replaceAll("_", " "), e11));
			}
			if (e2.length != 1) {
				flag = true;

				String e22 = e2[0];
				for (int j = 1; j < e2.length; j++) {
					e22 = e22 + "_" + e2[j];
				}
				trains.get(i).setE2(e22);
				trains.get(i).setSen1(trains.get(i).getSen1().replaceAll(e22.replaceAll("_", " "), e22));
			}

			if (trains.get(i).getSen2().replaceAll("[' ']+", "").equals("")) {
				trains.get(i).setSen2(null);
			}

			if (flag) {
				count++;
				// System.out.println(s1);
				// System.out.println(s11);
				// System.out.println(trains.get(i).getSen1());
				// System.out.println(trains.get(i).getSen2());
				// System.out.println("e1: " + Arrays.toString(e1));
				// System.out.println("e2: " + Arrays.toString(e2));
				// System.out.println("e1: " + trains.get(i).getE1());
				// System.out.println("e2: " + trains.get(i).getE2());
				// System.out.println();
			}

		}
		// System.out.println(count);

		ObjectOutputStream trainOut = new ObjectOutputStream(new FileOutputStream(new File(D_TEST_2)));
		trainOut.writeObject(trains);
		trainOut.close();

//		 for (D d : trains) {
//			 System.out.println(d.getSen1());
//		 }

	}
	
	
	
	
	public void makeTest(int flag,int size) throws Exception {
		
//		for (int qqq = 0; qqq < 10; qqq++) {
		
		String qqq = "0";
		
		
		//word vector文件
		String dictFileName = "data/task8_ALL_"+size+".txt";
//		String dictFileName = "/Users/kinten/w/vecotrs/task8_ALL_"+size+".txt";
//		String dictFileName = "/Users/kinten/w/arff/F_ica/task8_ALL_500_"+size+"_"+size+".txt";
//		String dictFileName = "/Users/kinten/w/arff/F_ica/task8_ALL_500_500_"+size+".txt";
//		String dictFileName = "/Users/kinten/w/vecotrs/back/task8_ALL_"+size+"_"+qqq+".txt";
//		String dictFileName = "/Users/kinten/w/arff1/F_ica/task8_ALL_"+size+".txt";
		
		//要生成的文件
		String fileName = "data/"+size+"_2717.arff";
//		String fileName = "/Users/kinten/w/arff/F/"+size+"_2717_"+flag+".arff";
//		String fileName = "/Users/kinten/w/arff/F_non/"+size+"_2717_"+flag+".arff";
//		String fileName = "/Users/kinten/w/arff/F_ica/500_"+size+"_"+size+"_2717.arff";
//		String fileName = "/Users/kinten/w/arff/F_ica/500_500_"+size+"_2717.arff";
//		String fileName = "/Users/kinten/w/arff1/F/"+size+"_"+qqq+"_2717_"+flag+".arff";
//		String fileName = "/Users/kinten/w/arff1/F/"+size+"_2717_"+flag+".arff";
//		String fileName = "/Users/kinten/w/arff1/F_ica/"+size+"_2717.arff";
		
		List<D> datas = (ArrayList<D>) getObjectFromFile(D_TEST_2);
		Map<String, float[]> dicts = loadDicts(dictFileName, size);

		PrintWriter pw = new PrintWriter(new FileWriter(fileName));

		pw.println("@relation " + dictFileName);
		for (int i = 1; i <= size; i++) {
			pw.println("@attribute 'feature_" + i + "' numeric");
		}
		pw.println("@attribute 'order' numeric");
//		pw.println("@attribute 'Label' { 1, 2, 3, 4, 5, 6, 7, 8, 9}");
		pw.println("@attribute 'Label' { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}");
		
		pw.println();
		pw.println("@data");

		if (flag == 1) {
			// e1和e1之间单词的和的平均值
			for (int i = 0; i < 2717; i++) {
//				if (datas.get(i).getRel() != 10 && datas.get(i).getSen2() != null) {
				if (datas.get(i).getSen2() != null) {	
					String[] subString = datas.get(i).getSen2().split(" ");
					float[] a = new float[subString.length];
					float[] w = new float[subString.length];
					float ww = 0;
					for (int j = 0; j < w.length; j++) {
						w[j] = 1;
						ww = w[j] + ww;
					}
					for (int j = 0; j < a.length; j++) {
						a[j] = w[j] / ww;
					}
					float[] v = new float[size];
					
					for (int j = 0; j < size; j++) {
						for (int k = 0; k < subString.length; k++) {
							v[j] = v[j] + dicts.get(subString[k])[j] * a[k];
						}
					}

					String vv = Arrays.toString(v).replace("[", "").replace("]", "").replaceAll(" ", "");
					pw.println(vv + "," + datas.get(i).getOrd() + ",'" + datas.get(i).getRel() + "'");
	
				}
			}
		} else if (flag == 2) {
			// 在1的基础上添加上e1和e2
			for (int i = 0; i < 8000; i++) {
				if (datas.get(i).getRel() != 10 && datas.get(i).getSen2() != null) {
					String[] s = datas.get(i).getSen2().split(" ");
					String[] subString = new String[s.length + 2];

					subString[0] = datas.get(i).getE1();
					subString[1] = datas.get(i).getE2();
					for (int j = 2; j < subString.length; j++) {
						subString[j] = s[j - 2];
					}

					float[] a = new float[subString.length];
					float[] w = new float[subString.length];
					float ww = 0;
					for (int j = 0; j < w.length; j++) {
						w[j] = 1;
						ww = w[j] + ww;
					}
					for (int j = 0; j < a.length; j++) {
						a[j] = w[j] / ww;
					}
					float[] v = new float[size];
					for (int j = 0; j < size; j++) {
						for (int k = 0; k < subString.length; k++) {
							v[j] = v[j] + dicts.get(subString[k])[j] * a[k];
						}
					}
					String vv = Arrays.toString(v).replace("[", "").replace("]", "").replaceAll(" ", "");
					pw.println(vv + "," + datas.get(i).getOrd() + ",'" + datas.get(i).getRel() + "'");
				}
			}

		} else if (flag == 3) {
			// w=m1在中间出现的次数/m1出现的总次数
			HashMap<String, W> w2Dicts = (HashMap<String, W>)getObjectFromFile(W_2_1);
			
			for (int i = 0; i < 2717; i++) {
//				if (datas.get(i).getRel() != 10 && datas.get(i).getSen2() != null) {
				if (datas.get(i).getSen2() != null) {
					String[] subString = datas.get(i).getSen2().split(" ");
					double[] a = new double[subString.length];
					double[] w = new double[subString.length];
					double ww = 0;
					for (int j = 0; j < w.length; j++) {
						w[j] = w2Dicts.get(subString[j]).getWeigh();
						
						
						
						
						/*
						int siz = w.length;
						if (siz%2!=0) {
							int max = siz/2+1;
							int sum = 0;
							for (int jj = 1; jj <= max; jj++) {
								sum = sum +jj*2;
							}
							sum = sum -1;
							if (j<=siz/2) {
//								System.out.println((siz/2+1-j)+"/"+sum);	
								w[j] = (siz/(double)2+1-j)/(double)sum;
							}
							else {
//								System.out.println((j-siz/2+j)+"/"+sum);
								w[j] = (j-siz/(double)2+j)/(double)sum;
							}
						}
						else {
							int max = siz/2;
							int sum = 0;
							for (int jj = 1; jj <= max; jj++) {
								sum = sum +jj*2;
							}
							if (j<siz/2-1) {
//								System.out.println((siz/2-j)+"/"+sum);	
								w[j] = (siz/(double)2-j)/(double)sum;
							}
							else if (j==siz/2-1 || j==siz/2) {
//								System.out.println((1)+"/"+sum);
								w[j] = 1.0/(double)sum;
							}
							else {
//								System.out.println((j-siz/2+1)+"/"+sum);
								w[j] = (j-siz/(double)2+1)/(double)sum;
							}
						}
						*/
						
						
						
						
						
						
						
						
						ww = w[j] + ww;
//						ww = w[j]*w[j] + ww;
//						ww = Math.sqrt(w[j]) + ww;
					}
					for (int j = 0; j < a.length; j++) {
						a[j] = w[j] / ww;
//						a[j] = (w[j]*w[j]) / ww;
//						a[j] = Math.sqrt(w[j]) / ww;
					}
					double[] v = new double[size];
					for (int j = 0; j < size; j++) {
						for (int k = 0; k < subString.length; k++) {
							v[j] = v[j] + dicts.get(subString[k])[j] * a[k];
						}
					}
					String vv = Arrays.toString(v).replace("[", "").replace("]", "").replaceAll(" ", "");
					pw.println(vv + "," + datas.get(i).getOrd() + ",'" + datas.get(i).getRel() + "'");
				}
			}

		} else if (flag == 4) {
			// w=log(8000/m1在别的sen1出现的总次数)
			
			for (int i = 0; i < 8000; i++) {
				
				if (i%1000 == 0) {
					System.out.println(i);
				}
				
				if (datas.get(i).getRel() != 10 && datas.get(i).getSen2() != null) {
					String[] subString = datas.get(i).getSen2().split(" ");
					float[] a = new float[subString.length];
					float[] w = new float[subString.length];
					float ww = 0;
					for (int j = 0; j < w.length; j++) {
						w[j] = getW2(i,datas,subString[j]);
						ww = w[j] + ww;
					}
					for (int j = 0; j < a.length; j++) {
						a[j] = w[j] / ww;
					}
					float[] v = new float[size];
					for (int j = 0; j < size; j++) {
						for (int k = 0; k < subString.length; k++) {
							v[j] = v[j] + dicts.get(subString[k])[j] * a[k];
						}
					}
					String vv = Arrays.toString(v).replace("[", "").replace("]", "").replaceAll(" ", "");
					pw.println(vv + "," + datas.get(i).getOrd() + ",'" + datas.get(i).getRel() + "'");
				}
			}

		}else if (flag == 5) {
			// w=log(8000/m1在别的sen2出现的总次数)
			
			for (int i = 0; i < 8000; i++) {
				
				if (i%1000 == 0) {
					System.out.println(i);
				}
				
				if (datas.get(i).getRel() != 10 && datas.get(i).getSen2() != null) {
					String[] subString = datas.get(i).getSen2().split(" ");
					float[] a = new float[subString.length];
					float[] w = new float[subString.length];
					float ww = 0;
					for (int j = 0; j < w.length; j++) {
						w[j] = getW3(i,datas,subString[j]);
						ww = w[j] + ww;
					}
					for (int j = 0; j < a.length; j++) {
						a[j] = w[j] / ww;
					}
					float[] v = new float[size];
					for (int j = 0; j < size; j++) {
						for (int k = 0; k < subString.length; k++) {
							v[j] = v[j] + dicts.get(subString[k])[j] * a[k];
						}
					}
					String vv = Arrays.toString(v).replace("[", "").replace("]", "").replaceAll(" ", "");
					pw.println(vv + "," + datas.get(i).getOrd() + ",'" + datas.get(i).getRel() + "'");
				}
			}

		}else if (flag == 6) {
			// w=m1在本类中间出现的次数/m1出现的总次数
			
			for (int i = 0; i < 8000; i++) {
				
				if (i%1000 == 0) {
					System.out.println(i);
				}
				
				if (datas.get(i).getRel() != 10 && datas.get(i).getSen2() != null) {
					String[] subString = datas.get(i).getSen2().split(" ");
					float[] a = new float[subString.length];
					float[] w = new float[subString.length];
					float ww = 0;
					for (int j = 0; j < w.length; j++) {
						w[j] = getW3(i,datas,subString[j]);
						ww = w[j] + ww;
					}
					for (int j = 0; j < a.length; j++) {
						a[j] = w[j] / ww;
					}
					float[] v = new float[size];
					for (int j = 0; j < size; j++) {
						for (int k = 0; k < subString.length; k++) {
							v[j] = v[j] + dicts.get(subString[k])[j] * a[k];
						}
					}
					String vv = Arrays.toString(v).replace("[", "").replace("]", "").replaceAll(" ", "");
					pw.println(vv + "," + datas.get(i).getOrd() + ",'" + datas.get(i).getRel() + "'");
				}
			}

		}
		
		
		
		
		
		
		else {

		}

		pw.flush();
		pw.close();
		System.out.println(qqq+" test OK!");
//		}
	}
	
	
	
	
	
	public void make(int flag,int size) throws Exception {
		
//		for (int qqq = 0; qqq < 10; qqq++) {
		String qqq = "0";
		
		//word vectors文件
		String dictFileName = "data/task8_ALL_"+size+".txt";
//		String dictFileName = "/Users/kinten/w/vecotrs/task8_ALL_"+size+".txt";
//		String dictFileName = "/Users/kinten/w/arff/F_ica/task8_ALL_500_"+size+"_"+size+".txt";
//		String dictFileName = "/Users/kinten/w/arff/F_ica/task8_ALL_500_500_"+size+".txt";
//		String dictFileName = "/Users/kinten/w/vecotrs/back/task8_ALL_"+size+"_"+qqq+".txt";
//		String dictFileName = "/Users/kinten/w/arff1/F_ica/task8_ALL_"+size+".txt";
		
		
		//要生成的文件
		String fileName = "data/"+size+"_8000.arff";
//		String fileName = "/Users/kinten/w/arff/F/"+size+"_8000_"+flag+".arff";
//		String fileName = "/Users/kinten/w/arff/F_non/"+size+"_8000_"+flag+".arff";
//		String fileName = "/Users/kinten/w/arff/F_ica/500_"+size+"_"+size+"_8000.arff";
//		String fileName = "/Users/kinten/w/arff/F_ica/500_500_"+size+"_8000.arff";
//		String fileName = "/Users/kinten/w/arff1/F/"+size+"_"+qqq+"_8000_"+flag+".arff";
//		String fileName = "/Users/kinten/w/arff1/F/"+size+"_8000_"+flag+".arff";
//		String fileName = "/Users/kinten/w/arff1/F_ica/"+size+"_8000.arff";
		
		
		
		List<D> datas = (ArrayList<D>) getObjectFromFile(D_TRAIN_2);
		Map<String, float[]> dicts = loadDicts(dictFileName, size);

		PrintWriter pw = new PrintWriter(new FileWriter(fileName));

		pw.println("@relation " + dictFileName);
		for (int i = 1; i <= size; i++) {
			pw.println("@attribute 'feature_" + i + "' numeric");
		}
		pw.println("@attribute 'order' numeric");

//		pw.println("@attribute 'Label' { 1, 2, 3, 4, 5, 6, 7, 8, 9}");
		pw.println("@attribute 'Label' { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}");		
		
		
		pw.println();
		pw.println("@data");

		if (flag == 1) {
			// e1和e1之间单词的和的平均值
			for (int i = 0; i < 8000; i++) {
//				if (datas.get(i).getRel() != 10 && datas.get(i).getSen2() != null) {
				if (datas.get(i).getSen2() != null) {
					String[] subString = datas.get(i).getSen2().split(" ");
					float[] a = new float[subString.length];
					float[] w = new float[subString.length];
					float ww = 0;
					for (int j = 0; j < w.length; j++) {
						w[j] = 1;
						ww = w[j] + ww;
					}
					for (int j = 0; j < a.length; j++) {
						a[j] = w[j] / ww;
					}
					float[] v = new float[size];
					for (int j = 0; j < size; j++) {
						for (int k = 0; k < subString.length; k++) {
							v[j] = v[j] + dicts.get(subString[k])[j] * a[k];
						}
					}
					String vv = Arrays.toString(v).replace("[", "").replace("]", "").replaceAll(" ", "");
					pw.println(vv + "," + datas.get(i).getOrd() + ",'" + datas.get(i).getRel() + "'");
					
					
				}
			}
		} else if (flag == 2) {
			// 在1的基础上添加上e1和e2
			for (int i = 0; i < 8000; i++) {
				if (datas.get(i).getRel() != 10 && datas.get(i).getSen2() != null) {
					String[] s = datas.get(i).getSen2().split(" ");
					String[] subString = new String[s.length + 2];

					subString[0] = datas.get(i).getE1();
					subString[1] = datas.get(i).getE2();
					for (int j = 2; j < subString.length; j++) {
						subString[j] = s[j - 2];
					}

					float[] a = new float[subString.length];
					float[] w = new float[subString.length];
					float ww = 0;
					for (int j = 0; j < w.length; j++) {
						w[j] = 1;
						ww = w[j] + ww;
					}
					for (int j = 0; j < a.length; j++) {
						a[j] = w[j] / ww;
					}
					float[] v = new float[size];
					for (int j = 0; j < size; j++) {
						for (int k = 0; k < subString.length; k++) {
							v[j] = v[j] + dicts.get(subString[k])[j] * a[k];
						}
					}
					String vv = Arrays.toString(v).replace("[", "").replace("]", "").replaceAll(" ", "");
					pw.println(vv + "," + datas.get(i).getOrd() + ",'" + datas.get(i).getRel() + "'");
				}
			}

		} else if (flag == 3) {
			// w=m1在中间出现的次数/m1出现的总次数
			HashMap<String, W> w2Dicts = (HashMap<String, W>)getObjectFromFile(W_2_1);
			
			
			for (int i = 0; i < 8000; i++) {
//				if (datas.get(i).getRel() != 10 && datas.get(i).getSen2() != null) {
				if (datas.get(i).getSen2() != null) {
					String[] subString = datas.get(i).getSen2().split(" ");
					double[] a = new double[subString.length];
					double[] w = new double[subString.length];
					double ww = 0;
					for (int j = 0; j < w.length; j++) {
						w[j] = w2Dicts.get(subString[j]).getWeigh();
						
						/*
						int siz = w.length;
						if (siz%2!=0) {
							int max = siz/2+1;
							int sum = 0;
							for (int jj = 1; jj <= max; jj++) {
								sum = sum +jj*2;
							}
							sum = sum -1;
							if (j<=siz/2) {
//								System.out.println((siz/2+1-j)+"/"+sum);	
								w[j] = (siz/(double)2+1-j)/(double)sum;
							}
							else {
//								System.out.println((j-siz/2+j)+"/"+sum);
								w[j] = (j-siz/(double)2+j)/(double)sum;
							}
						}
						else {
							int max = siz/2;
							int sum = 0;
							for (int jj = 1; jj <= max; jj++) {
								sum = sum +jj*2;
							}
							if (j<siz/2-1) {
//								System.out.println((siz/2-j)+"/"+sum);	
								w[j] = (siz/(double)2-j)/(double)sum;
							}
							else if (j==siz/2-1 || j==siz/2) {
//								System.out.println((1)+"/"+sum);
								w[j] = 1.0/(double)sum;
							}
							else {
//								System.out.println((j-siz/2+1)+"/"+sum);
								w[j] = (j-siz/(double)2+1)/(double)sum;
							}
						}
						*/
						
						
						ww = w[j] + ww;
//						ww = w[j]*w[j] + ww;
//						ww = Math.sqrt(w[j]) + ww;
					}
					for (int j = 0; j < a.length; j++) {
						a[j] = w[j] / ww;
//						a[j] = (w[j]*w[j]) / ww;
//						a[j] = Math.sqrt(w[j]) / ww;
					}
					double[] v = new double[size];
					for (int j = 0; j < size; j++) {
						for (int k = 0; k < subString.length; k++) {
							v[j] = v[j] + dicts.get(subString[k])[j] * a[k];
						}
					}
					String vv = Arrays.toString(v).replace("[", "").replace("]", "").replaceAll(" ", "");
					pw.println(vv + "," + datas.get(i).getOrd() + ",'" + datas.get(i).getRel() + "'");
				}
			}

		} else if (flag == 4) {
			// w=log(8000/m1在别的sen1出现的总次数)
			
			for (int i = 0; i < 8000; i++) {
				
				if (i%1000 == 0) {
					System.out.println(i);
				}
				
				if (datas.get(i).getRel() != 10 && datas.get(i).getSen2() != null) {
					String[] subString = datas.get(i).getSen2().split(" ");
					float[] a = new float[subString.length];
					float[] w = new float[subString.length];
					float ww = 0;
					for (int j = 0; j < w.length; j++) {
						w[j] = getW2(i,datas,subString[j]);
						ww = w[j] + ww;
					}
					for (int j = 0; j < a.length; j++) {
						a[j] = w[j] / ww;
					}
					float[] v = new float[size];
					for (int j = 0; j < size; j++) {
						for (int k = 0; k < subString.length; k++) {
							v[j] = v[j] + dicts.get(subString[k])[j] * a[k];
						}
					}
					String vv = Arrays.toString(v).replace("[", "").replace("]", "").replaceAll(" ", "");
					pw.println(vv + "," + datas.get(i).getOrd() + ",'" + datas.get(i).getRel() + "'");
				}
			}

		}else if (flag == 5) {
			// w=log(8000/m1在别的sen2出现的总次数)
			
			for (int i = 0; i < 8000; i++) {
				
				if (i%1000 == 0) {
					System.out.println(i);
				}
				
				if (datas.get(i).getRel() != 10 && datas.get(i).getSen2() != null) {
					String[] subString = datas.get(i).getSen2().split(" ");
					float[] a = new float[subString.length];
					float[] w = new float[subString.length];
					float ww = 0;
					for (int j = 0; j < w.length; j++) {
						w[j] = getW3(i,datas,subString[j]);
						ww = w[j] + ww;
					}
					for (int j = 0; j < a.length; j++) {
						a[j] = w[j] / ww;
					}
					float[] v = new float[size];
					for (int j = 0; j < size; j++) {
						for (int k = 0; k < subString.length; k++) {
							v[j] = v[j] + dicts.get(subString[k])[j] * a[k];
						}
					}
					String vv = Arrays.toString(v).replace("[", "").replace("]", "").replaceAll(" ", "");
					pw.println(vv + "," + datas.get(i).getOrd() + ",'" + datas.get(i).getRel() + "'");
				}
			}

		}else if (flag == 6) {
			// w=m1在本类中间出现的次数/m1出现的总次数
			
			for (int i = 0; i < 8000; i++) {
				
				if (i%1000 == 0) {
					System.out.println(i);
				}
				
				if (datas.get(i).getRel() != 10 && datas.get(i).getSen2() != null) {
					String[] subString = datas.get(i).getSen2().split(" ");
					float[] a = new float[subString.length];
					float[] w = new float[subString.length];
					float ww = 0;
					for (int j = 0; j < w.length; j++) {
						w[j] = getW3(i,datas,subString[j]);
						ww = w[j] + ww;
					}
					for (int j = 0; j < a.length; j++) {
						a[j] = w[j] / ww;
					}
					float[] v = new float[size];
					for (int j = 0; j < size; j++) {
						for (int k = 0; k < subString.length; k++) {
							v[j] = v[j] + dicts.get(subString[k])[j] * a[k];
						}
					}
					String vv = Arrays.toString(v).replace("[", "").replace("]", "").replaceAll(" ", "");
					pw.println(vv + "," + datas.get(i).getOrd() + ",'" + datas.get(i).getRel() + "'");
				}
			}

		}
		
		
		
		
		
		
		else {

		}

		pw.flush();
		pw.close();
		System.out.println(qqq+" train OK!");
//		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private String findSen2(String sen1) {
		String m = "";
		Pattern pattern;
		Matcher matcher;
		String e1 = "</e1>";
		String e2 = "<e2>";
		String a = e1 + ".*?" + e2;

		pattern = Pattern.compile(a);
		matcher = pattern.matcher(sen1);
		while (matcher.find()) {
			m = matcher.group().replace(e1, "").replace(e2, "");
		}
		// System.out.println(m);
		return m.trim();
	}

	private String findE1(String sent) {
		String e1 = "";
		Pattern pattern;
		Matcher matcher;
		pattern = Pattern.compile("<e1>.*?</e1>");
		matcher = pattern.matcher(sent);
		while (matcher.find()) {
			e1 = matcher.group().replace("<e1>", "").replace("</e1>", "");
		}
		return e1;
	}

	private String findE2(String sent) {
		String e2 = "";
		Pattern pattern;
		Matcher matcher;
		pattern = Pattern.compile("<e2>.*?</e2>");
		matcher = pattern.matcher(sent);
		while (matcher.find()) {
			e2 = matcher.group().replace("<e2>", "").replace("</e2>", "");
		}
		return e2;
	}

	private int findRel(String line) {
		int num = 0;
		if (line.indexOf("Cause-Effect") != -1) {
			num = 1;
		} else if (line.indexOf("Instrument-Agency") != -1) {
			num = 2;
		} else if (line.indexOf("Product-Producer") != -1) {
			num = 3;
		} else if (line.indexOf("Content-Container") != -1) {
			num = 4;
		} else if (line.indexOf("Entity-Origin") != -1) {
			num = 5;
		} else if (line.indexOf("Entity-Destination") != -1) {
			num = 6;
		} else if (line.indexOf("Component-Whole") != -1) {
			num = 7;
		} else if (line.indexOf("Member-Collection") != -1) {
			num = 8;
		} else if (line.indexOf("Message-Topic") != -1) {
			num = 9;
		} else if (line.indexOf("Other") != -1) {
			num = 10;
		} else {
			num = -1;
		}
		return num;
	}

	private int findOrd(String line) {
		int num = 0;
		if (line.indexOf("e1,e2") != -1) {
			num = 1;
		} else if (line.indexOf("e2,e1") != -1) {
			num = 2;
		} else {
			num = 1;
		}
		return num;
	}

	private Object getObjectFromFile(String file) {
		Object object = null;
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			object = in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}

	private Map<String, float[]> loadDicts(String fileName, int size) throws IOException {
		Map<String, float[]> dicts = new HashMap<String, float[]>();
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line = br.readLine();
		line = br.readLine();
		while ((line = br.readLine()) != null) {
			String temp[] = line.split(" ");
			float[] v = new float[size];
			for (int i = 0; i < v.length; i++) {
				v[i] = Float.parseFloat(temp[i + 1]);
			}
			dicts.put(temp[0], v);
		}
		return dicts;
	}

	public void makeW1Dicts() throws IOException {
		String dictFileName = "task8_v_40.txt";
		int size = 40;

		List<D> datas = (ArrayList<D>) getObjectFromFile(D_TRAIN_2);
		Map<String, float[]> dicts = loadDicts(dictFileName, size);

		Map<String, Float> w1Dicts = new HashMap<String, Float>();

		Iterator iter = dicts.entrySet().iterator();
		int count = 1;
		while (iter.hasNext()) {
			count++;
			if (count % 1000 == 0) {
				System.out.println(count);
			}

			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			int sum = 0;
			int mSum = 0;
			for (D d : datas) {

				String[] s1 = d.getSen1().split(" ");
				for (String string : s1) {
					if (string.equals(key)) {
						sum++;
					}
				}

				if (d.getSen2() != null) {
					String[] s2 = d.getSen2().split(" ");
					for (String string : s2) {
						if (string.equals(key)) {
							mSum++;
						}
					}
				}
			}
			w1Dicts.put(key, (float) mSum / (float) sum);

		}

		ObjectOutputStream trainOut = new ObjectOutputStream(new FileOutputStream(new File(W_1)));
		trainOut.writeObject(w1Dicts);
		trainOut.close();
	}
	
	
	public void makeW2Dicts() throws IOException {
		String fileName = "L/task8_ALL_40_0.txt";
		
		List<D> datas1 = (ArrayList<D>) getObjectFromFile(D_TRAIN_2);
		List<D> datas2 = (ArrayList<D>) getObjectFromFile(D_TEST_2);
		
		List<D> datas = new ArrayList<D>();
		datas.addAll(datas1);
		datas.addAll(datas2);
//		System.out.println(datas.get(0));
		
//		Map<String, float[]> dicts = loadDicts(dictFileName, size);
		
		List<W> words = new ArrayList<W>();
		
		BufferedReader br = new BufferedReader(new FileReader("data/" + fileName));
		String line = br.readLine();
		line = br.readLine();
		while ((line = br.readLine()) != null) {
			String temp[] = line.split(" ");
			W w = new W();
			w.setWord(temp[0]);
			words.add(w);
		}
		System.out.println(words.size());
//		System.out.println(words.get(0));
		
		
		for (int i = 0; i < words.size(); i++) {
			if (i % 1000 == 0) {
				System.out.println(i);
			}
			int count  = 0;
			int sum	 = 0;
			for (D d : datas) {
				String[] s1 = d.getSen1().split(" ");
				for (String string : s1) {
					if (string.equals(words.get(i).getWord())) {
						sum++;
					}
				}
				if (d.getSen2() != null) {
					String[] s2 = d.getSen2().split(" ");
					for (String string : s2) {
						if (string.equals(words.get(i).getWord())) {
							count++;
						}
					}
				}
			}
			words.get(i).setCount(count);
			words.get(i).setSum(sum);
			words.get(i).setWeigh((double) count / (double) sum);
//			System.out.println(words.get(i));
		}
		ObjectOutputStream trainOut = new ObjectOutputStream(new FileOutputStream(new File(W_2)));
		trainOut.writeObject(words);
		trainOut.close();
	}
	public void makeW2Dicts_1() throws IOException {
		ArrayList<W> words = (ArrayList<W>)getObjectFromFile(W_2);
		Map<String, W> w2Dicts = new HashMap<String, W>();
		System.out.println(words.size());
		for (int i = 0; i < words.size(); i++) {
			if (i % 1000 == 0) {
				System.out.println(i);
			}
//			words.get(i).setWeigh((double)wn(words.get(i).getWord()));
//			System.out.println(words.get(i));
			w2Dicts.put(words.get(i).getWord(), words.get(i));
		}
		
		
		
		
		ObjectOutputStream trainOut = new ObjectOutputStream(new FileOutputStream(new File(W_2_1)));
		trainOut.writeObject(w2Dicts);
		trainOut.close();
		
	}
	
	
	public int wn(String word) throws IOException{
		
		int flag = 1;
		
		String path = "/usr/local/WordNet-3.0/dict";
		URL url = new URL("file", null, path);
		IDictionary dict = new Dictionary(url);
		dict.open();

		
//		IIndexWord n = dict.getIndexWord(word, POS.NOUN);
//		if (n != null) {
////			flag = flag+n.getWordIDs().size();
//			flag = flag+1;
//		}
//		IIndexWord v = dict.getIndexWord(word, POS.VERB);
//		if (v != null) {
////			flag = flag+v.getWordIDs().size();
//			flag = flag+1;
//		}
//		IIndexWord adj = dict.getIndexWord(word, POS.ADJECTIVE);
//		if (adj != null) {
////			flag = flag+adj.getWordIDs().size();
//			flag = flag+1;
//		}
		IIndexWord adv = dict.getIndexWord(word, POS.ADVERB);
		if (adv != null) {
			flag = flag+adv.getWordIDs().size();
			flag = flag-1;
		}
		
		dict.close();
		return flag;
	}
	
	
	
	
	public float getW2(int index,List<D> datas,String word) throws IOException {
		float result = 0;
		
		int sum = 0;
		
		for (int i = 0; i < 8000; i++) {
			if (i == index) {
				continue;
			}
			else {
				boolean flag = false;
				String[] temp = datas.get(i).getSen1().split(" ");	
				for (String string : temp) {
					if (string.equals(word)) {
						flag = true;
					}
				}
				if (flag) {
					sum ++;
				}
			}
			
		}
		if (sum == 0) {
			result = (float)4.0;
		}
		else {
			result = (float)(Math.log10(8000)- Math.log10(sum));	
		}
		return result;
	}
	public float getW3(int index,List<D> datas,String word) throws IOException {
		float result = 0;
		
		int sum = 0;
		
		for (int i = 0; i < 8000; i++) {
			if (i == index) {
				continue;
			} else if (datas.get(i).getSen2() == null) {
				continue;
			}
			else {
				boolean flag = false;
				String[] temp = datas.get(i).getSen2().split(" ");	
				for (String string : temp) {
					if (string.equals(word)) {
						flag = true;
					}
				}
				if (flag) {
					sum ++;
				}
			}
			
		}
		if (sum == 0) {
			result = (float)4.0;
		}
		else {
			result = (float)(Math.log10(8000)- Math.log10(sum));	
		}
		return result;
	}	
	
	
	
	public void makeW6Dicts() throws IOException {
		String dictFileName = "task8_v_40.txt";
		int size = 40;

		List<D> datas = (ArrayList<D>) getObjectFromFile(D_TRAIN_2);
		Map<String, float[]> dicts = loadDicts(dictFileName, size);

		Map<String, float[]> w6Dicts = new HashMap<String, float[]>();

		Iterator iter = dicts.entrySet().iterator();
		int count = 1;
		while (iter.hasNext()) {
			count++;
			if (count % 1000 == 0) {
				System.out.println(count);
			}

			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			int sum = 0;
			int mSum = 0;
			for (D d : datas) {

				String[] s1 = d.getSen1().split(" ");
				for (String string : s1) {
					if (string.equals(key)) {
						sum++;
					}
				}

				if (d.getSen2() != null) {
					String[] s2 = d.getSen2().split(" ");
					for (String string : s2) {
						if (string.equals(key)) {
							mSum++;
						}
					}
				}
			}
			
			
//			w1Dicts.put(key, (float) mSum / (float) sum);

		}

//		ObjectOutputStream trainOut = new ObjectOutputStream(new FileOutputStream(new File(W_6)));
//		trainOut.writeObject(w1Dicts);
//		trainOut.close();
	}
	
	
	
	
	
	public void test1() throws Exception {
		
	
		List<D> datas1 = (ArrayList<D>) getObjectFromFile(D_TRAIN_2);
		for (D d : datas1) {
			System.out.println(d.getSen2());
		}	
	
	
		/*
		int size = 40;
		
		File file1 = new File("data/ICA/"+size+"_0.arff");
		File file2 = new File("data/ICA/a_"+size+".txt");
        BufferedReader reader = new BufferedReader(new FileReader(file1));
        PrintWriter pw = new PrintWriter(new FileWriter(file2));
        
        String tempString = null;
        int line = 1;
        boolean flag = false;
        while ((tempString = reader.readLine()) != null) {
        	if (line == (size + 6)) {
        		flag = true;
			}
        	if (flag) {
//        		System.out.println("line " + line + ": " + tempString);	
        		String  temp [] = tempString.split(",");
        		System.out.println(temp.length);
        		String p = temp[0];
        		for (int i = 1; i < size; i++) {
					p = p +" " + temp[i];
				}
//        		System.out.println(p);
        		pw.println(p);
			}
            line++;
        }
        
        pw.flush();
		pw.close();
        reader.close();
        */
	}
	
	public void test2() throws Exception {
		
		int size = 100;
		int size2 = 40;
		
		File file1 = new File("data/ICA/a_"+size+"_to_"+size2+".csv");
		File file2 = new File("data/ICA/"+size+"_0.arff");
		File file3 = new File("data/ICA/"+size+"_to_"+size2+"_ica.arff");

		BufferedReader reader1 = new BufferedReader(new FileReader(file1));
		BufferedReader reader2 = new BufferedReader(new FileReader(file2));
		PrintWriter pw = new PrintWriter(new FileWriter(file3));
		
		List<String[]> data = new ArrayList<String[]>();
		String tempString1 = null;
		 while ((tempString1 = reader1.readLine()) != null) {
			 String  temp [] = tempString1.split(",");
			 data.add(temp);
		 }
		 System.out.println(data.size());		 
		 
		  String tempString2 = null;
		  int line1 = 0;
		  int line2 = 1;
	      boolean flag2 = false;
	      while ((tempString2 = reader2.readLine()) != null) {
	    		if (line2 < (size+6)) {
	    			pw.println(tempString2);	
				}
	    		else {
	    			String tempdata [] = data.get(line1);
	    			String temp[] = tempString2.split(",");
	    			String p = tempdata[0];
	    			for (int i = 1; i < size2; i++) {
						p = p +"," + tempdata[i];
					}
	    			p = p +"," + temp[size];
	    			p = p +"," + temp[size+1];
//	    			System.out.println(p);
	    			pw.println(p);
	    			line1++;
				}
	    		line2++;
	      }
	      pw.flush();
	  	  pw.close();
	      reader1.close();
	      reader2.close();
		
	}
	// ----------------------------------------------------------------

	
	public void testWN() throws Exception {
		List<D> datas = (ArrayList<D>) getObjectFromFile(D_TRAIN_2);
		
		
//		System.out.println(datas.get(1).getE1());
//		System.out.println(datas.get(1).getE2());
		
		
		String path = "/usr/local/WordNet-3.0/dict";
		URL url = new URL("file", null, path);
		IDictionary dict = new Dictionary(url);
		dict.open();
		
//		getSynonyms(dict,datas.get(1).getE1());
		getRelated(dict,"car");
	
	/*	int ce1 = 0;
		int ce2 = 0;
		int c1and2 = 0;
		int c1or2 = 0;
		
		System.out.println(datas.size());
		
		for (int i = 0; i < datas.size(); i++) {
//			if (i%1000 == 0) {
//				System.out.println(i);
//			}
			
			boolean a = getRelated(dict,datas.get(i).getE1());
			boolean b = getRelated(dict,datas.get(i).getE2());
			
			if (a) {
				ce1++;
			}
//			System.out.println(datas.get(i).getE1());
			if (b) {
				ce2++;
			}
			if (a && b) {
				c1and2++;
			}
			if (a || b) {
				c1or2++;
			}
		}
		System.out.println("e1:		"+ce1);
		System.out.println("e2:		"+ce2);
		System.out.println("e1 and e2:	"+c1and2);
		System.out.println("e1 or e2:	"+c1or2);
		
		
		*/
		
		
	}
	
	
	public void getSynonyms(IDictionary dict,String word) {
		// look up first sense of the word "dog "
		IIndexWord idxWord = dict.getIndexWord(word, POS.NOUN);
		IWordID wordID = idxWord.getWordIDs().get(0); // 1st meaning
		IWord iWord = dict.getWord(wordID);
		ISynset synset = iWord.getSynset();
		// iterate over words associated with the synset
		for (IWord w : synset.getWords())
			System.out.println(w.getLemma());
	}
	
	public boolean getRelated(IDictionary dict,String word) {
		int count = 0;
		
		// get the synset
		IIndexWord idxWord = dict.getIndexWord(word, POS.NOUN);
		if (idxWord == null) {
//			System.out.println(word+":null");
		}
		else {
			IWordID wordID = idxWord.getWordIDs().get(0); // 1st meaning
			IWord iWord = dict.getWord(wordID);
			ISynset synset = iWord.getSynset();

			// get the hypernyms
//			List<ISynsetID> hypernyms = synset.getRelatedSynsets(Pointer.HYPERNYM);
			List<ISynsetID> hypernyms = synset.getRelatedSynsets(Pointer.HYPONYM);
//			List<ISynsetID> hypernyms = synset.getRelatedSynsets(Pointer.MERONYM_PART);
//			List<ISynsetID> hypernyms = synset.getRelatedSynsets(Pointer.TOPIC_MEMBER);
			
//			List<ISynsetID> hypernyms = synset.getRelatedSynsets(Pointer.VERB_GROUP);
			
			// print out each hypernyms id and synonyms
			List<IWord> words;
			for (ISynsetID sid : hypernyms) {
				words = dict.getSynset(sid).getWords();
				for (Iterator<IWord> i = words.iterator(); i.hasNext();) {
					System.out.println(i.next().getLemma());
					count ++;
					break;
				}
			}
		}

		if (count>0) {
			return true;
		}
		else {
			return false;
		}
		
		

	}
	
	
	
	
	
	
	
	
	
	
	
	public void test3() throws Exception {
		List<D> datas = (ArrayList<D>) getObjectFromFile(D_TRAIN_2);
		
		
		System.out.println(datas.get(1).getE1());
		System.out.println(datas.get(1).getE2());
		
		
		String path = "/usr/local/WordNet-3.0/dict";
		URL url = new URL("file", null, path);
		IDictionary dict = new Dictionary(url);
		dict.open();
		
		for (int i = 0; i < datas.size(); i++) {
			if (i%100 == 0) {
//				System.out.println(i);
			}
			
			getVec(dict, datas.get(i).getE1(), datas.get(i).getE2(),1);
			getVec(dict, datas.get(i).getE2(), datas.get(i).getE1(),1);
			getVec(dict, datas.get(i).getE1(), datas.get(i).getE2(),2);
			getVec(dict, datas.get(i).getE2(), datas.get(i).getE1(),2);
			getVec(dict, datas.get(i).getE1(), datas.get(i).getE2(),3);
			getVec(dict, datas.get(i).getE2(), datas.get(i).getE1(),3);
//			System.out.println(result);
			
			
		}
		
	}
	public boolean  getVec(IDictionary dict,String e1,String e2,int flag){
		
		IIndexWord e1Word = dict.getIndexWord(e1, POS.NOUN);
		if (e1Word == null) {
//			System.out.println(word+":null");
			return false;
		}
		else {
			IWord e1w = dict.getWord(e1Word.getWordIDs().get(0));
			ISynset synset = e1w.getSynset();
			List<ISynsetID> hypernyms = null;
			if (flag == 1) {
				hypernyms = synset.getRelatedSynsets(Pointer.MERONYM_PART);	
			}
			else if (flag == 2) {
				hypernyms = synset.getRelatedSynsets(Pointer.HYPONYM);
			} else if(flag == 3){
				hypernyms = synset.getRelatedSynsets(Pointer.HYPERNYM);
			}
			
			
			
			List<IWord> words;
			for (ISynsetID sid : hypernyms) {
				words = dict.getSynset(sid).getWords();
				for (Iterator<IWord> i = words.iterator(); i.hasNext();) {
//					System.out.println(i.next().getLemma());
					if (i.next().getLemma().equals(e2)) {
						System.out.println("!!!!" + e1+"  "+e2);
						return true;
					}
				}
			}
		}
		
		
		return false;
		
	}
	
	
public void test11() throws Exception {
		
		int size = 500;
		
		File file1 = new File("/Users/kinten/w/vecotrs/task8_ALL_"+size+".txt");
		File file2 = new File("/Users/kinten/w/arff1/F_ica/a_"+size+".txt");
        BufferedReader reader = new BufferedReader(new FileReader(file1));
        PrintWriter pw = new PrintWriter(new FileWriter(file2));
        
        String tempString = reader.readLine();
        tempString = reader.readLine();
        
        while ((tempString = reader.readLine()) != null) {
//        		System.out.println(tempString);	
        		String  temp [] = tempString.split(" ");
//        		System.out.println(temp.length);
        		String p = "";
        		for (int i = 1; i < temp.length; i++) {
					p = p +" " + temp[i];
				}
//        		System.out.println(p);
        		pw.println(p);
        }
        
        pw.flush();
		pw.close();
        reader.close();
	}

	public void test22() throws Exception {

		int size = 500;
		int size2 = 100;
		int size3 = 100;
		
		File file1 = new File("/Users/kinten/w/arff/F_ica/a_"+size+"_"+size2+"_"+size3+".csv");
		File file2 = new File("/Users/kinten/w/vecotrs/task8_ALL_"+size+".txt");
		File file3 = new File("/Users/kinten/w/arff/F_ica/task8_ALL_"+size+"_"+size2+"_"+size3+".txt");

		BufferedReader reader1 = new BufferedReader(new FileReader(file1));
		BufferedReader reader2 = new BufferedReader(new FileReader(file2));
		PrintWriter pw = new PrintWriter(new FileWriter(file3));
		
		List<String[]> data = new ArrayList<String[]>();
		String tempString1 = null;
		 while ((tempString1 = reader1.readLine()) != null) {
			 String  temp [] = tempString1.split(",");
			 data.add(temp);
		 }
//		 System.out.println(data.size());	
		 
		  String tempString2 = null;
		  int line1 = 0;
		  int line2 = 1;
	      while ((tempString2 = reader2.readLine()) != null) {
	    		if (line2 == 1) {
	    			pw.println(tempString2);	
				}
	    		else if (line2 == 2) {
	    			pw.println(tempString2);	
				}
	    		else {
	    			String tempdata [] = data.get(line1);
	    			String temp[] = tempString2.split(" ");
	    			String p = temp[0];
	    			for (int i = 0; i < tempdata.length; i++) {
						p = p +" " + tempdata[i];
					}
//	    			System.out.println(p);
	    			pw.println(p);
	    			line1++;
				}
	    		line2++;
	      }
	      pw.flush();
	  	  pw.close();
	      reader1.close();
	      reader2.close();
}
	
	public void test33() throws Exception {

		int size = 500;
		int size2 = 500;
		
		File file1 = new File("/Users/kinten/w/arff1/F_ica/b_"+size2+".csv");
		File file2 = new File("/Users/kinten/w/vecotrs/task8_ALL_"+size+".txt");
		File file3 = new File("/Users/kinten/w/arff1/F_ica/task8_ALL_"+size2+"_.txt");

		BufferedReader reader1 = new BufferedReader(new FileReader(file1));
		BufferedReader reader2 = new BufferedReader(new FileReader(file2));
		PrintWriter pw = new PrintWriter(new FileWriter(file3));
		
		List<String[]> data = new ArrayList<String[]>();
		String tempString1 = null;
		 while ((tempString1 = reader1.readLine()) != null) {
			 String  temp [] = tempString1.split(",");
			 data.add(temp);
		 }
//		 System.out.println(data.size());	
		 
		  String tempString2 = null;
		  int line1 = 0;
		  int line2 = 1;
	      while ((tempString2 = reader2.readLine()) != null) {
	    		if (line2 == 1) {
	    			pw.println(tempString2);	
				}
	    		else if (line2 == 2) {
	    			pw.println(tempString2);	
				}
	    		else {
	    			String tempdata [] = data.get(line1);
	    			String temp[] = tempString2.split(" ");
	    			String p = temp[0];
	    			for (int i = 0; i < tempdata.length; i++) {
						p = p +" " + tempdata[i];
					}
//	    			System.out.println(p);
	    			pw.println(p);
	    			line1++;
				}
	    		line2++;
	      }
	      pw.flush();
	  	  pw.close();
	      reader1.close();
	      reader2.close();
}	
	
	

}
