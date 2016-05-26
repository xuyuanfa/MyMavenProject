package com.xxx;

public class TestInteger {
	public static void main(String[] args) {
		int i = 1000;
		Integer i1 = 1000;
		Integer i2 = new Integer(1000);
		System.out.println(i == i1);
		System.out.println(i == i2);
		System.out.println(i1 == i2);
		System.out.println(i1.equals(i));
		System.out.println(i1.equals(null));
		
		System.out.println();
		// -128~127可以用==比较，因为Integer内部实现缓存，其他的是new了对象，需用equals
		int j = 127;
		Integer j1 = 127;
		Integer j2 = new Integer(127);
		System.out.println(j == j1);
		System.out.println(j == j2);
		System.out.println(j1 == j2);
		System.out.println(j1.equals(j));
		System.out.println(j1.equals(null));
	}
}
