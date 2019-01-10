import simbad.gui.Simbad;

public class AStarMain {

    public static void main(String[] args) {
    	int[][] map = Map.map;
        System.setProperty("j3d.implicitAntialiasing", "true"); //抗锯齿图形保真
		new Simbad(new MyEnv(20,20,map,0,0,19,19), false);
    }
}
