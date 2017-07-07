package com.cjp.util;

/**
 * <p>文件名称：LapNoise.java</p>
 * <p>文件描述：生成拉普拉斯分布</p>
 */
public class LapNoise {

	/**
	 * @param args
	 */
	public static double getLap(double u,double b) {
		double x=Math.random();
		if (x<0.5) {
			return b*Math.log(2*x)+u;
		}else{
			return u-b*Math.log(2-2*x);
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(getLap(0, 1));
	}

}
