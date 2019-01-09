import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import simbad.sim.*;

class MyEnv extends EnvironmentDescription {

    MyEnv(int column, int row, int[][] map, int startX, int startY, int endX, int endY) {
  	    
    	long startTime=System.currentTimeMillis();//获取开始时间
        AStarAlgorithm aStar=new AStarAlgorithm(map, row, column);
        int flag=aStar.search(startX, startY, endX, endY);
        if(flag==-1){
            System.out.println("传输数据有误！");
        }else if(flag==0){
            System.out.println("没找到！");
        }
        for(int i=0;i<row;i++){
            for(int j=0;j<column;j++){
                    System.out.print(map[i][j]);   
            }
            System.out.println();
        }
    	long endTime=System.currentTimeMillis(); //获取结束时间
    	System.out.println("程序运行时间： "+(endTime-startTime)+"ms");	
    	
	       light1IsOn = true;
    	Wall w1 = new Wall(new Vector3d((column >> 1) +1, 0, 0), column+1, 1, this);
        w1.rotate90(1);
        add(w1);
        Wall w2 = new Wall(new Vector3d((-column >> 1) -1, 0, 0), column+1, 1, this);
        w2.rotate90(1);
        add(w2);
        Wall w3 = new Wall(new Vector3d(0, 0, (row >> 1) +1), row+1, 1, this);
        add(w3);
        Wall w4 = new Wall(new Vector3d(0, 0, (-row >> 1) -1), row+1, 1, this);
        add(w4);
        Box b;
        //添加障碍物
        for(int i=0;i<row;i++)
        	for(int j=0;j<column;j++){
        		if(map[i][j]==0){
                    b = new Box(new Vector3d(j- (column >> 1), 0, i- (row >> 1)), new Vector3f(1, 1, 1),
                            this);
                    add(b);
        		}
        	}

        add(new ObstacleRobot(new Vector3d(-3.5, 0, 0), "MyMovRobot")); //移动障碍机器人
        add(new Robot(new Vector3d(startY- (row >> 1), 0, startX- (column >> 1)), "robot 1",column,row,map,endX,endY));
    }
}
