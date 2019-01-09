import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import simbad.sim.Agent;
import simbad.sim.LampActuator;
import simbad.sim.RobotFactory;

public class Robot extends Agent {
	private int[][] map;
	private int x;
	private int y;
	private int eX;
	private int eY;
	private int row;
	private int column;
	private Vector3d p;   //初始位置
    private int direction = 1;  //移动方向  ,八个方向
    private LampActuator lamp;

    private static boolean hasArrived = false;
    
    public Robot(Vector3d position, String name,int column, int row,int[][] map,int eX,int eY) {
        super(position, name);
        this.map = map;
        this.row = row;
        this.column = column;
       //得到当前的坐标
        x = (int)position.z+column/2;
        y = (int)position.x+row/2;
        p = position;
        this.eX = eX;
        this.eY = eY;
        lamp = RobotFactory.addLamp(this);
    }

    //初始化
    public void initBehavior() {
     
        x = (int)p.z+column/2;
        y = (int)p.x+row/2; 
    	direction=1;
    	//还原路径
    	
        for(int i=0;i<row;i++){
            for(int j=0;j<column;j++){                       
            	if(map[i][j]==2){
            		map[i][j]=-1;   
            	}
                System.out.print(map[i][j]);  
            }
            System.out.println();
        }
    }

    private boolean check(int x, int y){
    	Point3d p= new Point3d();
    	getCoords(p);
    	double px= p.z+10;
    	double py= p.x+10;

		return !(Math.abs(px - x) <= 0.001) || !(Math.abs(py - y) <= 0.001);
    }

    
    public void performBehavior() {
    	double angle;
        	
    	if (getCounter() % 20 == 0){
			Point3d p= new Point3d();
			getCoords(p);
			double px=p.z+10.0;
			double py=p.x+10.0;
			
			
			if(Math.abs(px-eX)<=0.00001 && Math.abs(py-eY)<=0.00001){  // 到达目的地
				lamp.setOn(true);
	    		setRotationalVelocity(0);
	    		setTranslationalVelocity(0);
	    		if (!hasArrived){
					System.out.println("A星算法Robot成功到达目的地了！");
					System.out.println("用时:"+getLifeTime()+"s");
					System.out.println("运动距离:"+getOdometer()+"m");
					hasArrived = true;
				}
				return;
			}
			lamp.setOn(false);

    		//向右移动一个单位
        	if(y<column-1 && map[x][y+1]==-1){	
        		angle = getRotationalAngle(1);
        		if(check(x, y + 1)){
	        		if(angle==0){
	        			//平移
		        		setRotationalVelocity(0);
		        		setTranslationalVelocity(1);
		        		
	        		}else{
	        			//转动指定角度
		        		setTranslationalVelocity(0);
		        		setRotationalVelocity(angle);
		        	}
	        		return;
        		}
        		else{
        			setTranslationalVelocity(0);
	        		
        			map[x][y]=2;
        			y++;
        		}
        	}
        	//向左移动一个单位
        	if(y>0 && map[x][y-1]==-1){
        		angle = getRotationalAngle(5);
        		if(check(x, y - 1)){
	        		if(angle==0){
	        			//平移
	        			
		        		
		        		setRotationalVelocity(0);
		        		setTranslationalVelocity(1);
		        		
	        		}else{
	        			//转动指定角度
		        		setTranslationalVelocity(0);
		        		setRotationalVelocity(angle);
		        	}
	        		return;
        		}
        		else{
        			setTranslationalVelocity(0);
        			map[x][y]=2;
        			y--;
        		}
        	}
        	//向上移动一个单位
        	if(x>0 && map[x-1][y]==-1){
        		angle = getRotationalAngle(3);
        		if(check(x - 1, y)){
	        		if(angle==0){
	        			//平移
		        		setRotationalVelocity(0);
		        		setTranslationalVelocity(1);
		        		
	        		}else{
	        			//转动指定角度
		        		setTranslationalVelocity(0);
		        		setRotationalVelocity(angle);
		        	}
	        		return;
        		}
        		else{
        			setTranslationalVelocity(0);
        			map[x][y]=2;
        			x--;
        		}
        	}
        	//向下移动一个单位
        	if(x<row-1 && map[x+1][y]==-1){
        		angle = getRotationalAngle(7);
        		if(check(x + 1, y)){
	        		if(angle==0){
	        			//平移
		        		setRotationalVelocity(0);
		        		setTranslationalVelocity(1);
		        		
	        		}else{
	        			//转动指定角度
		        		setTranslationalVelocity(0);
		        		setRotationalVelocity(angle);
		        	}
	        		return;
        		}
        		else{
        			setTranslationalVelocity(0);
	        		
        			map[x][y]=2;
        			x++;
        		}
        	}
        	
        	
        	//右下
        	if(y<column-1&&x<row-1 && map[x+1][y+1]==-1){
        		angle = getRotationalAngle(8);
        		if(check(x + 1, y + 1)){
	        		if(angle==0){
	        			//平移
		        		setRotationalVelocity(0);
		        		setTranslationalVelocity(Math.sqrt(2.0));
		        		
	        		}else{
	        			//转动指定角度
		        		setTranslationalVelocity(0);
		        		setRotationalVelocity(angle);
		        	}
	        		return;
        		}
        		
        		else{
        			setTranslationalVelocity(0);
        			map[x][y]=2;
        			x++;
        			y++;
        		}
        	}
        	//左下
        	if(y>0&&x<row-1 && map[x+1][y-1]==-1){
        		angle = getRotationalAngle(6);
        		if(check(x + 1, y - 1)){
	        		if(angle==0){
	        			//平移
		        		setRotationalVelocity(0);
		        		setTranslationalVelocity(Math.sqrt(2.0));
		        		
	        		}else{
	        			//转动指定角度
		        		setTranslationalVelocity(0);
		        		setRotationalVelocity(angle);
		        	}
	        		return;
        		}
        		else{
        			setTranslationalVelocity(0);
        			map[x][y]=2;
        			y--;
        			x++;
        		}
        	}
        	
          	//左上
        	if(x>0&&y>0 && map[x-1][y-1]==-1){
        		angle = getRotationalAngle(4);
        		if(check(x - 1, y - 1)){
	        		if(angle==0){
	        			//平移
		        		setRotationalVelocity(0);
		        		setTranslationalVelocity(Math.sqrt(2.0));
		        		
	        		}else{
	        			//转动指定角度
		        		setTranslationalVelocity(0);
		        		setRotationalVelocity(angle);
		        	}
	        		return;
        		}
        		else{
        			setTranslationalVelocity(0);
        			map[x][y]=2;
        			x--;
        			y--;
        		}
        	}
        	//右上
        	if(y<column-1&&x>0 && map[x-1][y+1]==-1){
        		angle = getRotationalAngle(2);
        		if(check(x - 1, y + 1)){
	        		if(angle==0){
	        			//平移
		        		setRotationalVelocity(0);
		        		setTranslationalVelocity(Math.sqrt(2.0));
		        		
	        		}else{
	        			//转动指定角度
		        		setTranslationalVelocity(0);
		        		setRotationalVelocity(angle);
		        	}
				}
        		else{
        			setTranslationalVelocity(0);
        			map[x][y]=2;
        			y++;
        			x--;
        		}
        	}
            	
        	
        }
    }
    
    private double getRotationalAngle(int d){
    	int result = direction - d;
    	direction=d;
    	//左转90度
    	if(result == -2 || result == 6){
    		return Math.PI/2;
    	}
    	//右转90度
    	if(result == 2 || result == -6){
    		return -Math.PI/2;
    	}
    	//左转45度
    	if(result == -1 || result == 7){
    		return Math.PI/4;
    	}
    	//右转45度
    	if(result == 1 || result == -7){
    		return -Math.PI/4;
    	}
    	//左转135度	
    	if(result == -3 || result == 5){
    		return 5*Math.PI/4;
    	}
    	//右转135度
    	if(result == 3 || result ==-5){
    		return 5*Math.PI/4;
    	}
    	
    	if(result == 0){
    		return 0;
    	}
    	//转180度
		return Math.PI;
    }
}
