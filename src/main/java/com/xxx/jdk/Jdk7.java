package com.xxx.jdk;

import java.util.ArrayList;
import java.util.List;

public class Jdk7 {

	public static void collections() {
		// List<String> list=["item"]; //向List集合中添加元素
		// String item=list[0]; //从List集合中获取元素
		//
		// Set<String> set={"item"}; //向Set集合对象中添加元素
		//
		// Map<String,Integer> map={"key":1}; //向Map集合中添加对象
		// int value=map["key"]; //从Map集合中获取对象

		List<String> tempList = new ArrayList<>(); // 即泛型实例化类型自动推断
	}

	public static void switchStr() {
		String s = "test";
		switch (s) {
		case "test":
			System.out.println("test");
		case "test1":
			System.out.println("test1");
			break;
		default:
			System.out.println("break");
			break;
		}
	}

	public static void number_() {
		int one_million = 1_000_000;
		System.out.println(one_million);
		int binary = 0b1001_1001;
		System.out.println(binary);
	}

	public static void newReadEvm() {
		// System.getJavaIoTempDir() // IO临时文件夹
		// System.getJavaHomeDir() // JRE的安装目录
		// System.getUserHomeDir() // 当前用户目录
		// System.getUserDir() // 启动java进程时所在的目录5
	}

	public static void safeCount(){
//		Math.safeToInt(1L);
//		Math.safeNegate(int value)
//		long Math.safeSubtract(long value1, int value2)
//		long Math.safeSubtract(long value1, long value2)
//		int Math.safeMultiply(int value1, int value2)
//		long Math.safeMultiply(long value1, int value2)
//		long Math.safeMultiply(long value1, long value2)
//		long Math.safeNegate(long value)
//		int Math.safeAdd(int value1, int value2)
//		long Math.safeAdd(long value1, int value2)
//		long Math.safeAdd(long value1, long value2)
//		int Math.safeSubtract(int value1, int value2)
	}

	public static void main(String[] args) {
		collections();
		switchStr();
		number_();
		
//		Character.equalsIgnoreCase('a', 'a');
	}

}
