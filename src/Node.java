public class Node {
	private int x;//X坐标
    private int y;//Y坐标
    private Node parentNode;//父类节点
    private double g;//当前点到起点的移动距离
    private double h;//当前点到终点的移动距离，即曼哈顿距离|x1-x2|+|y1-y2|(忽略障碍物)
    private double f;//f=g+h

    Node(int x, int y, Node parentNode){
        this.x=x;
        this.y=y;
        this.parentNode=parentNode;
    }
  
    int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    Node getParentNode() {
        return parentNode;
    }
    void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }
    double getG() {
        return g;
    }
    void setG(double g) {
        this.g = g;
    }
    public double getH() {
        return h;
    }
    void setH(double h) {
        this.h = h;
    }
    double getF() {
        return f;
    }
    void setF(double f) {
        this.f = f;
    }
}
