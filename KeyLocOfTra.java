package creation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.bean.Location;
import com.bean.Trajectory;
import com.main.KMeansMng;

public class KeyLocOfTra {
	
	//n是tf的大小
	private int m;
	private int n;
	private List<Trajectory> oriTras;
	private Map<Integer, Trajectory> IdOfTra;
	
	public KeyLocOfTra(int m,List<Trajectory> oriTras,int n,Map<Integer, Trajectory> IdOfTra){
		this.m = m;
		this.oriTras = oriTras;
		this.n = n;
		this.IdOfTra = IdOfTra;
	}
	
	/*
	 * 统计一条轨迹每个位置出现的次数,返回次数大于3的位置及对应的统计值
	 */
	public  Map<nLocation,Integer> locCountInTra(Trajectory tra){
		
		Map<nLocation,Integer> map = new HashMap<nLocation,Integer>();
		Map<nLocation,Integer> map1 = new HashMap<nLocation,Integer>();
		List<nLocation> nlocs = new ArrayList<nLocation>();
		
		for(Location loc : tra.getLocations()){
			nLocation nloc = new nLocation(loc);
			nloc.setStr((loc.getX().hashCode() + loc.getY().hashCode()));			
			nlocs.add(nloc);			
		}		
		
		for(nLocation nloc :nlocs){
			if (map.containsKey(nloc)) {
				map.put(nloc, map.get(nloc)+1);
			}else{
				map.put(nloc, 1);
			}
		}
		
		for(Entry<nLocation,Integer> ent :map.entrySet()){
			if(ent.getValue()>= n){
				map1.put(ent.getKey(),ent.getValue());
			}			
		}
		
		return map1;
	}
	
	/*
	 * 统计某个位置在多少条轨迹中出现过
	 */
	public  int locCountInAll(nLocation nloc){
		
		Location loc = nloc.getLoc();
		int count = 0;
		for(Trajectory tra : oriTras){
			List<Location> ls = tra.getLocations();
			for(Location loc1:ls){
				if(loc1.getX().equals(loc.getX()) && loc1.getY().equals(loc.getY())){
					count++;
					break;
				}
			}
						
		}		
		return count;
	}
	
	//计算某条轨迹中若干位置的tf_idf，返回tf_idf值最大的位置
	
	public Map<nLocation,Double> getLocTf_Idf(Map<nLocation,Integer> map){
		
		int U = oriTras.size();
		Map<nLocation,Double> mp = new HashMap<nLocation,Double>();
		Map<nLocation,Double> mp1 = new HashMap<nLocation,Double>();
		double max = 0;
		nLocation  key = null;
		for(Entry<nLocation,Integer> ent :map.entrySet()){
			nLocation nloc = ent.getKey();
			
			int tf = ent.getValue();
			
			int countInAll = locCountInAll(nloc);
			
			double tf_idf = tf * ( Math.log(U / (countInAll+1)) / Math.log(2) );
			
			if(max < tf_idf){
				max = tf_idf;
				key = nloc;
			}
			
			mp.put(nloc, tf_idf);			
		}		
		mp1.put(key, max);		
		return mp1;
	}
	
	/**
	 * 
	 * @return Map<20个时刻点，位置集合>
	 */
	public Map<Integer,List<nLocation>> keyLocsEachTime(){
		
        Map<nLocation,Double> map = new HashMap<nLocation,Double>();			
		for(Trajectory tra :oriTras){			
			 Map<nLocation,Integer> submap1 =locCountInTra(tra);
				 if(submap1.entrySet().size()==0){
					 continue;
				 }
			 Map<nLocation,Double>  submap2 = getLocTf_Idf(submap1);
			 map.putAll(submap2);	
		}
		
		List<nLocation> keyLocs = new ArrayList<nLocation>();
		for(Entry<nLocation,Double> ent :map.entrySet()){
			keyLocs.add(ent.getKey());
		}
		
		Map<Integer,List<nLocation>> keyMap = new HashMap<Integer,List<nLocation>>();
		for(nLocation nLoc : keyLocs){//将每一个位置对应到相应时刻
			Trajectory tra = IdOfTra.get(nLoc.getLoc().getID());//该位置点属于哪条轨迹
			if(tra != null){
				List<Location> locs = tra.getLocations();
				for(int i = 0;i<locs.size();i++){
					if(nLoc.getStr().equals((locs.get(i).getX().hashCode()+locs.get(i).getY().hashCode()))){
						if(!keyMap.containsKey(i)){							
							keyMap.put(i,new ArrayList<nLocation>());							
						}
						keyMap.get(i).add(nLoc);					
					}
				}
			}
		}
		
		return keyMap;
	}
	
	/*/////////////////////////////////////////////////////////////
	 * 此处可以再次修改
	 *////////////////////////////*/
	
	public Map<Integer,Integer> modikeyLocCount(){	
		
		Map<Integer,Integer> map = new HashMap<Integer,Integer>();
		Map<Integer,List<nLocation>> mp = keyLocsEachTime();
		int count=0;
		for(Entry<Integer,List<nLocation>> ent : mp.entrySet()){
			count += ent.getValue().size();
			//System.out.println("第"+ent.getKey()+"时刻关键位置数量: "+ent.getValue().size());
		}
	//	System.out.println("+++++"+count+"+++++");	
		
		for(Entry<Integer,List<nLocation>> ent : mp.entrySet()){
			
			String str=String.valueOf(ent.getValue().size());
			int j = str.length();//判断是几位数
			if(j == 1){//是一位数
			   // System.out.println(ent.getValue().size()+"--"+ 0);
			    map.put(ent.getKey(),0);
			}
			if(j == 2){
				int shiwei = Integer.parseInt(""+str.charAt(0));
				
				if(shiwei >= 5){
					//System.out.println(ent.getValue().size()+"--"+100);
					map.put(ent.getKey(),100);
				}else{
					map.put(ent.getKey(), 0);
					//System.out.println(ent.getValue().size()+"--"+0);
				}
			}
			if(j==3){
				int shiwei = Integer.parseInt(""+str.charAt(1));
				if(shiwei >= 5){
					int baiwei = Integer.parseInt(""+str.charAt(0)) + 1; 
					//System.out.println(ent.getValue().size()+"--" + baiwei*100);
					map.put(ent.getKey(),baiwei*100);
				}
				else{
					int baiwei = Integer.parseInt(""+str.charAt(0));
					//System.out.println(ent.getValue().size()+"--" + baiwei*100);
					map.put(ent.getKey(),baiwei*100);
				}				
			}			
			//System.out.println();			
		}
		
		for(Entry<Integer,Integer> ent : map.entrySet()){
			//System.out.println("时刻"+ent.getKey()+"  :"+ ent.getValue() + "——" + Math.round(m * (1 - ent.getValue()*1.0 / count)));
	
			Long newValue = Math.round(m * (1 - ent.getValue()*1.0 / count));
			map.put(ent.getKey(), Integer.valueOf(newValue.toString()) );
		}
		
		for(Entry<Integer,Integer> ent : map.entrySet()){
			System.out.println("时刻"+ent.getKey()+"  :"+ ent.getValue());
		}
		return map;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		KMeansMng kMeansMng=new KMeansMng("in/inputpre.txt");       
		List<Trajectory> oriTras =kMeansMng.getOrgTraList();
				
		KeyLocOfTra key = new KeyLocOfTra(100,oriTras,10,kMeansMng.getIdOfTra());			
		Map<Integer,Integer> map = key.modikeyLocCount();
		
      }

}
