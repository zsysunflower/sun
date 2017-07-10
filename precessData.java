package com.cjp.bean;

import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.ArrayList;

public class precessData {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		////////////////////////////////////////////////////选取规范轨迹//////////////////////////////////////////////////////////////////////////////////
		
		 String path = "D:\\Trajectory03.txt";	
		
		 File file = new File(path);
		 try(PrintWriter w = new PrintWriter("F:/Trajectory03.txt")) {
			InputStreamReader read = new InputStreamReader(new FileInputStream(file),"GBK");
			BufferedReader br = new BufferedReader(read);
			
			List<OriTrajectory> list04 = new ArrayList<>();
			List<Double> list04jingdu = new ArrayList<>();
			String str = null;
			while((str =br.readLine()) != null){
				OriTrajectory tra = new OriTrajectory();
				String[] strArr = str.split("\t");				
				
				 // 经纬度过滤，北京地区的经纬度    北纬39”26’至41”03’，东经115”25’至 117”30’				 
				if(strArr[2].compareTo("117.30")>0 || strArr[2].compareTo("115.25")<0 || strArr[3].compareTo("39.26")<0 || strArr[3].compareTo("41.03")>0){
					continue;
				}
				
			    tra.setID(Integer.parseInt(strArr[0]));								
				list04jingdu.add(Double.parseDouble(strArr[2]));//该list中只存放用户的经度
				tra.setTime(strArr[1]);
				tra.setX(Double.valueOf(strArr[2]));
				tra.setY(Double.valueOf(strArr[3]));
				list04.add(tra);
			}
			
		    	System.out.println("所有的位置点数"+list04.size());
		    	
		    	Set<Double> set = new HashSet<>();
		    	for(Double jingdu :list04jingdu){
		    		set.add(jingdu);
		    	}
		    	System.out.println("所有不同的经度数为："+set.size());		    	
		    	
		   //将list04文件按照经度进行分割
		    	List<List<OriTrajectory>> list04Byjingdu = new ArrayList<>();
		    	Iterator<Double> it = set.iterator();
		    	while(it.hasNext()){
		    		double jingdu = it.next();	
		    		int start = list04jingdu.indexOf(jingdu);
		    		int end = list04jingdu.lastIndexOf(jingdu);
		    		List<OriTrajectory> sublist04 = new ArrayList<OriTrajectory>(list04.subList(start, end+1));
		    		list04Byjingdu.add(sublist04);
		    	}
		    	System.out.println("所有不同的经度数为:"+list04Byjingdu.size());	    	
	     		    		    	
		       Iterator<List<OriTrajectory>> it3 = list04Byjingdu.iterator();
               while(it3.hasNext()){
               	List<OriTrajectory> eachid = it3.next();               
	                	Collections.sort(eachid,new Comparator<OriTrajectory>(){
			    			public int compare(OriTrajectory o1,OriTrajectory o2){
			    				return o1.getY().compareTo(o2.getY());
			    			}
			    		});                    	
               }                 
               for(List<OriTrajectory> line : list04Byjingdu){			    	
   		    	  for(OriTrajectory org :line){   		    				    	
   	   		        w.print(org.getID()+"\t");
   	   		        w.print(org.getTime()+"\t");
   	   		        w.print(org.getX()+"\t");
   	   		        w.print(org.getY()+"\t\n"); 
            	   				    				     			   
   		         }
               }
   		  } catch (FileNotFoundException e) {				
   				e.printStackTrace();
   			}                                      
	   System.out.println("处理完毕");
	}
}


