

import java.util.Comparator;


class NodeFComparator implements Comparator<Node>{  // 比较节点F值大小

  @Override
  public int compare(Node o1, Node o2) {
      if( o1.getF()-o2.getF()>0){
    	  return 1;
      }
      else if(o1.getF()-o2.getF()<0){
    	  return -1;
      }
      else {
		return 0;
	}
  }
  
}