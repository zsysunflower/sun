package creation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.bean.Trajectory;
import com.diffprivate.PartitionSelectionBean;
import com.kmeans.Cluster;

public class ClustersEachtime {
	private int m;
	private List<PartitionSelectionBean> selectedPar;
	private KeyLocOfTra key;
	
   public ClustersEachtime(int m,List<PartitionSelectionBean> selectedPar,KeyLocOfTra key){
		this.m=m;
	    this.selectedPar = selectedPar;
		this.key = key;		
	}
	/**
	 * 用新簇替换旧簇
	 */
   public List<List<Cluster<Trajectory>>> newClusters(){
	List<List<Cluster<Trajectory>>> oldClusters = new ArrayList<List<Cluster<Trajectory>>>();
		
	for(PartitionSelectionBean ocluster : selectedPar){		
		oldClusters.add(ocluster.getObj());	
	}
	Map<Integer,Integer> map = key.modikeyLocCount();
	
	//Random rand = new Random();
	for(Entry<Integer,Integer> ent: map.entrySet()){// 每个时刻进行相同的处理	
		int time1 = ent.getKey();//当前合并的时刻
		List<Cluster<Trajectory>> orgCls = oldClusters.get(time1);// 当前时刻的划分
		int newCls = ent.getValue();//新划分的簇数		
		//int subCls = m - newCls;//需要合并的簇数
		int subCls = 2;
		
	    for(int i=0; i<subCls; i++){
	    	//int randNum = rand.nextInt(orgCls.size());
	    	int randNum = 1;
	    	//int neastNum = neastClu(randNum,orgCls);//合并到该时刻
	    	int neastNum = 2;
	    	addCls(randNum,neastNum,orgCls);//将两个时刻进行合并
	    }	
	}
		return oldClusters;
   }
  
   /**
    * 合并两个相近的簇
    * @param randNum
    * @param newNum
    * @param orgCls
    * @return
    */
   private void addCls(int randNum,int newNum,List<Cluster<Trajectory>> orgCls){
	   
	   List<Trajectory> randlist = new ArrayList<Trajectory>();
			            randlist = orgCls.get(randNum).getList();
	   List<Trajectory> newlist = orgCls.get(newNum).getList();
	   Trajectory tra = new Trajectory();
	   if(newlist.addAll(randlist)){		   
		   tra = tra.getNewCenter(newlist);			   
	   }
	   
	   if(tra.getLocations() == null){
		   
		   System.out.println("}}}}}}res.getLocations(){{{{{ ");
		   
	   }
	   
	   orgCls.get(newNum).setCenter(tra);
	   
	   //清空另一个list
	   
	   orgCls.get(randNum).getList().clear();
	   orgCls.remove(randNum);	  
   }
   
   
   
   
   /**
    * 求中心轨迹最近的两个簇
    * @param randNum
    * @param orgCls
    * @return
    */
   private int neastClu(int randNum,List<Cluster<Trajectory>> orgCls){
	   
	   Cluster<Trajectory> randCls = orgCls.get(randNum);
	   Trajectory randCenter = randCls.getCenter();
	   double mindis = 1e+100;
	   int newNum = 0;
	   for(int i=0;i < orgCls.size();i++){
		   if(i == randNum){
			   continue;
		   }
		  double dis = randCenter.getDistance(orgCls.get(i).getCenter());
		  System.out.println("中心轨迹的距离："+dis);
		  if(mindis>dis){
			  mindis = dis;
			  newNum = i;
		  }
	   }
	       System.out.println("最小的中心轨迹距离："+mindis);
	   return newNum;	   
   }
	

	public static void main(String[] args) {
	
	}

}
