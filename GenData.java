package com.cjp.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class GenData {
	public static void main(String[] args) throws FileNotFoundException {
		PrintWriter pw=new PrintWriter(new File("in/data.txt"));
		int n=1000;
		int locs=4;
		pw.println(locs);
		for (int i = 0; i < n; i++) {
			int m=(int) (Math.random()*500)+1;
			pw.print(m+"\t");
			double t=Math.random()*190;
			for (int j = 0; j < m; j++) {
				double dt=Math.random()*10;
				t+=dt;
				int loc=(int) (Math.random()*locs)+1;
				pw.print("L"+loc+":"+t+"\t");
			}
			pw.println();
		}
		pw.flush();
		pw.close();
	}
}
