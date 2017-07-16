package com.zsy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.zsy.master.CandidateP;
import com.zsy.master.Individual;
import com.zsy.master.KMeansClustering;

import zsy.MillerCoordinate;
import zsy.Trajectory;

/**
 * 该类获取文件夹中的文档，并进行处理
 * @author Administrator
 */
 public  class OriGetFile {
	
	 /**
	  * 该方法读取文件夹中的轨迹读入内存，并作基本的过滤
	  * 最后返回的list_trajectory_file是一个list<List<Oritrajectory>>类型的list，每个list<OriTrajectory>对应一个文件
	  * 文件中不包括重复时间的轨迹点，并且一个文件中的轨迹点数大于等于32
	  * @param path
	  * @return
	  */
	@SuppressWarnings("static-access")
	public static List readFile(String path){
		
		File folder = new File(path);
		List<File> list = new ArrayList<File>();
		File[] fileList = folder.listFiles();
		
		if(fileList==null){
			System.out.println("该路径不是个目录");
		}else{ //将所有的文件存入list中
			for(int i=0;i<fileList.length;i++){
				list.add(fileList[i]);				
			}		
		}
		
		//用迭代器逐个处理每个文件
		
		MillerCoordinate convert = new MillerCoordinate();
		double[] coordinate =new double[2];
		//List fileInfo = new ArrayList<List>();
		//List<OriTrajectory> list_trajectory = new ArrayList<OriTrajectory>();
		List<List<OriTrajectory>> list_trajectory_file = new ArrayList<List<OriTrajectory>>();//list元素是list,每个元素对应一个文件
		//List<String> list_ID = new ArrayList<String>();
		//List<Integer> list_time = new ArrayList<Integer>();
		
		Iterator it = list.iterator();
		while(it.hasNext()){
			File file = (File)it.next();
			
			//////////////////////////////////////////////
			try{
				String encoding = "GBK";							
				if(file.isFile() && file.exists()){//判断文件是否存在				
					InputStreamReader read = new InputStreamReader(	new FileInputStream(file),encoding);//考虑编码格式
					BufferedReader br = new BufferedReader(read);
					String str = null;	
					List<OriTrajectory> list_trajectory = new ArrayList<OriTrajectory>();//存放该文件中符合时间段的轨迹点，个数大于等于32
					
					OriTrajectory lasttra = new OriTrajectory();//存的是上一个轨迹点，与该点比较，来去除重复
					while((str = br.readLine()) != null){
						
						
						if(str.isEmpty()){//如果该行是空的，读下一行
							continue;
						}					
						String[] strArr = str.split(",");//以,分割字符串
						
						//如果该行的信息不全或者部分数据为0不可用的，读下一行
						if(strArr.length<4 || Double.parseDouble(strArr[2])== 0.0 || Double.parseDouble(strArr[3])== 0.0){
							continue;
						}	
						
						
						OriTrajectory trajectory = new OriTrajectory();
						trajectory.setID(Integer.parseInt(strArr[0]));
						//list_ID.add(strArr[0]);
						
						//SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				    	SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMddHHmm00");
						SimpleDateFormat df2 = new SimpleDateFormat("HHmm00");
					    Date date = df.parse(strArr[1]);				    
					    String dateStr = df1.format(date);
					    String dateStr1 = df2.format(date);
					    
					    //截取7：30-15：30时间段的数据轨迹点
					    String timestr = dateStr.substring(8,12);
					    if(timestr.compareTo("073000")<0 || timestr.compareTo("153000")>0){					    
					    	continue;
					    }					    
					    trajectory.setTime(dateStr);
					   // list_time.add(Integer.parseInt(dateStr1));
					   // Date date1 = df1.parse(dateStr);
					   // trajectory.setCheck_in_time(date1);					
					   // trajectory.setCheck_in_time(strArr[1]);
					    
					    if(Double.parseDouble(strArr[2])>180){
					    	continue;
					    }
					    
					    coordinate = convert.MillierConvertion(Double.parseDouble(strArr[2]), Double.parseDouble(strArr[3]));				    
					    trajectory.setX(coordinate[0]);
					    trajectory.setY(coordinate[1]);			
					    
					   
					   // trajectory.setX(Double.parseDouble(strArr[2]));//经度
					  //  trajectory.setY(Double.parseDouble(strArr[3]));//纬度
					    
					    if(!trajectory.getTime().equals(lasttra.getTime())){
					    	  lasttra = trajectory;
							  list_trajectory.add(trajectory);				    	
					     }
					    
					  		   
					   }//一个文件结束
					     if(list_trajectory.size()>31){
					        list_trajectory_file.add(list_trajectory);
					    }
					   // fileInfo.add(list_ID);
					   //fileInfo.add(list_trajectory);					   
					    //fileInfo.add(list_time);
					    br.close();
				        read.close();
				  }else{
					System.out.println("找不到指定的文件");
				  }
			}catch(Exception e){
				System.out.println("读取文件内容出错");
				e.printStackTrace();
			}		
			
			///////////////////////////////////
		}
		
		return list_trajectory_file;//每个元素对应一个文件，且文件中不包含重复时间的轨迹点，且轨迹点数大于32
	}
	
	/**
	 * 
	 * @Title: preTreatment
	 * @Description: 将轨迹按照每10分钟的间隔进行分割，每条轨迹32个轨迹点组成
	 * @param: @param list1 原始的轨迹数据，每个元素对应一个符合最基本条件的文件（无重复,>=32）
	 * @param: @param list_time1 2号-8号进行排序	 
	 * @throws ParseException 
	 */
	public  List preTreatment(List list1) throws ParseException{
		
		List list_trajectory_file = new ArrayList(list1);
		List<List<OriTrajectory>> finallist = new ArrayList<List<OriTrajectory>>();//存放最终实验需要的轨迹数据。每个元素list是一条包括32个节点的轨迹
		
		Iterator it = list_trajectory_file.iterator();
		while(it.hasNext()){//每次处理一个list,每个list就是一个文件
			List<OriTrajectory> list = (List<OriTrajectory>)it.next();//得到一个文件
			
			//得到该文件对应的所有日期号
			List<String> list_day = new ArrayList<String>();
			for(OriTrajectory tra :list){
				list_day.add(tra.getTime().substring(7,8));				
			}
			
			//对list按照时间进行排序
			Collections.sort(list,new Comparator<OriTrajectory>(){
				public int compare(OriTrajectory o1,OriTrajectory o2){
					return o1.getTime().compareTo(o2.getTime());
				}
			});
			
			//对list_day按照日期大小进行排序
			Collections.sort(list_day,new Comparator<String>(){
				public int compare(String o1,String o2){
					return o1.compareTo(o2);
				}				
			});
			
			//观察原始数据发现，在2-8号进行的数据收集，所以每个文件中的每一天构成一条轨迹，一天一天的处理,若
			//该天轨迹满足条件，则将其存入最终的finallist中
			for(int i=2;i<9;i++){ 				
				int start  = list_day.indexOf(Integer.toString(i));
				int end = list_day.lastIndexOf(Integer.toString(i));
				if((end-start)<32){//该文件中该日期的采样点不够32个，舍弃该文件该日期的这条轨迹
					continue;
				}
				
				List sublist = new ArrayList(list.subList(start, end+1));
				List listofday = new ArrayList();//暂时存放该天的轨迹
				OriTrajectory firstone =(OriTrajectory) sublist.get(0);
				OriTrajectory endone = (OriTrajectory)sublist.get(sublist.size()-1);
				if(endone.subTime(firstone.getTime()) <200){//规定每条轨迹32个结点，相邻轨迹点的时间间隔不能小于6分钟
					continue;
				}				
				long interval = intervaltime(firstone.getTime(),endone.getTime(),32);				
				listofday.add(firstone);//将第一条轨迹点放入该天的轨迹中			
				OriTrajectory lastTra = firstone;
				for(int p=1;p<sublist.size();p++){
					OriTrajectory curTra = (OriTrajectory)sublist.get(p);//取出当前的一个轨迹点
					if(curTra.subTime(lastTra.getTime())>=interval){
						listofday.add(curTra);
						lastTra=curTra;						
					}
					if(listofday.size()==32){//如果结点数为32，则结束循环
						finallist.add(listofday);
						break;
					}
					
				}
				
				
				
				//判断该日期的轨迹点能否形成实验数据中需要的轨迹
			/*	List sublist = new ArrayList(list.subList(start, end+1));
				OriTrajectory trajectory0 =(OriTrajectory)sublist.get(0);//第一个轨迹点
				listofday.add(trajectory0);
				String nextTime = nextTime(trajectory0.getTime());//下一个轨迹点应该满足的时间约束
				for(int p=1;p<sublist.size();p++){					
					
					OriTrajectory trajectory1 =(OriTrajectory)sublist.get(p);
					if(trajectory1.getTime().equals(nextTime) && listofday.size()<32){
						listofday.add(trajectory1);
						nextTime = nextTime(trajectory1.getTime());
						if(p==sublist.size()-1){
						    finallist.add(listofday);
						}
					}else if(trajectory1.getTime().compareTo(nextTime)<=0 && p!= (sublist.size()-1)){//该点小于下一个时刻的时间点，
						continue;						
					}else if(trajectory1.getTime().compareTo(nextTime)>0 && p!=sublist.size()-1){ //该点大于下一时刻，舍弃该天的轨迹
						break;
					}else 
						finallist.add(listofday);								
				}//end for(int p=1;p<sublist.size()+1;p++)
							*/					
			}//end for(int i=2;i<9;i++)
				
		}		
		return finallist;
	}
	
	//计算当前给定时间10分钟后的时间
	private String nextTime(String time) throws ParseException{
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		Date date_util = sdf.parse(time); //转换为util.date
		
		Calendar now=Calendar.getInstance();		
		  now.setTime(date_util);		 
		  now.add(Calendar.MINUTE,10);
		  String dateStr=sdf.format(now.getTimeInMillis());
		  return dateStr;		
	}
	
	//将一段轨迹分成nodes个节点，则时间间隔是多小（下取整）
	private long intervaltime(String date1,String date2,int nodes) throws ParseException{
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date01 = sdf.parse(date1);
		Date date02 = sdf.parse(date2);
		
		long min = (date02.getTime()-date01.getTime())/60000/(nodes-1);
		
		return min;
	}
	
	/**
	 * 直接读取经过处理的文件
	 */
	
	public List<List<OriTrajectory>> getFile(String path){		
	     File file = new File(path);	     
	     InputStreamReader read = null;
		try {
			read = new InputStreamReader(new FileInputStream(file));
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		}
	     BufferedReader br = new BufferedReader(read);
	     List<List<OriTrajectory>> finallist = new ArrayList<>();
	     List<OriTrajectory> list = new ArrayList<>();
	     String str = null;
	     try {
			while((str=br.readLine()) != null){
				 OriTrajectory tra = new OriTrajectory();
				 String[] strArr = str.split("\t");
				 tra.setID(Integer.parseInt(strArr[0]));
				 tra.setTime(strArr[1]);
			    tra.setX(Double.parseDouble(strArr[2])*1000);
			    tra.setY(Double.parseDouble(strArr[3])*1000);
			    list.add(tra);  	 
			 }
		} catch (NumberFormatException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}
	     
	     Set set = new HashSet();
	     for(OriTrajectory ori : list){	    	 
	    	 set.add(ori.getID());
	     }
	     
	     List listid = new ArrayList<>();
	     for(OriTrajectory ori :list){
	    	 listid.add(ori.getID());
	     }
	    
	     Collections.sort(list,new Comparator<OriTrajectory>(){
	    	 public int compare(OriTrajectory o1,OriTrajectory o2){
	    		 return o1.getID().compareTo(o2.getID());
	    	 }
	     });
	     
	     Collections.sort(listid,new Comparator<Integer>(){
	    	 public int compare(Integer o1,Integer o2){
	    		 return o1.compareTo(o2);
	    	 }
	     });
	     
	     Iterator<Integer> it = set.iterator();
	     while(it.hasNext()){
	    	 Integer id = it.next();
	    	 int start = listid.indexOf(id);
	    	 int end = listid.lastIndexOf(id);
	    	 List<OriTrajectory> eachid = new ArrayList<OriTrajectory>(list.subList(start, end+1));
	    	 finallist.add(eachid);    	 
	     }	 
		return finallist;
	}
}
