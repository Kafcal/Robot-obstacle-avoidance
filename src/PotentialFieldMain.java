import javax.vecmath.*;

import simbad.sim.*;
import simbad.demo.*;
import simbad.gui.Simbad;


public class PotentialFieldMain extends Demo
{
    // 终点坐标
    private Vector2d goal = new Vector2d(8,8 );
    private Vector3d goal3d = new Vector3d(8, 0,8);

    private static boolean hasArrived = false;  //是否到达目标

    private static final double repelConstant = 210.0;// 斥力系数
    private static final double attractConstant = 30.0;// 引力系数

    public class Robot extends Agent
    {

        RangeSensorBelt sonars,bumpers;  //声呐、保险杠
        LampActuator lamp;               //指示灯

        public void initBehavior() {}  //机器人初始化行为

        Robot(Vector3d position, String name)
        {
            super(position, name);
            bumpers = RobotFactory.addBumperBeltSensor(this);
            sonars = RobotFactory.addSonarBeltSensor(this);
            lamp = RobotFactory.addLamp(this);
        }

        Vector3d getVelocity()
        {
            return this.linearVelocity; //线速度
        }

        private int getQuadrant(Vector2d vector) //计算向量的象限
        {
            double x = vector.x;
            double y = vector.y;
            if (x > 0 && y > 0)// 第一象限
            {
                return 1;
            } else if (x < 0 && y > 0)// 第二象限
            {
                return 2;
            } else if (x <0 && y < 0)// 第三象限
            {
                return 3;
            } else if (x > 0 && y <0)// 第四象限
            {
                return 4;
            } else if (x > 0 && y == 0)// x正半轴
            {
                return -1;
            } else if (x == 0 && y > 0)// y正半轴
            {
                return -2;
            } else if (x <0 && y == 0)// x负半轴
            {
                return -3;
            } else if (x == 0 && y <0)// y负半轴
            {
                return -4;
            } else
            {
                return 0;
            }
        }

        private double getAngle(Vector2d v1, Vector2d v2) //计算两个向量之间的弧度角（v1->v2）
        {
            double k = v1.y / v1.x;
            double y = k * v2.x;
            switch (getQuadrant(v1))
            {
                case 1:                           //v1在第一象限
                case 4:                           //第四象限
                case -1:                          //x正半轴
                    if (v2.y < y)
                        return 2 * Math.PI - v1.angle(v2);//两个向量之间的夹角弧度
                    else
                        return v1.angle(v2);
                case 2:
                case 3:
                case -3:
                    if (v2.y > y)
                    {
                        return 2 * Math.PI - v1.angle(v2);
                    }
                    return v1.angle(v2);
                case -2:
                    int i = getQuadrant(v2);
                    if (i == -1 || i == 1 || i == 4)
                        return 2 * Math.PI - v1.angle(v2);
                    else
                        return v1.angle(v2);
                case -4:
                    int j = getQuadrant(v2);
                    if (j == -3 || j == 2 || j == 3)
                        return 2 * Math.PI - v1.angle(v2);
                    else
                        return v1.angle(v2);
                default:
                    return -1;
            }

        }

        private Vector2d decomposition(Vector2d v, Vector2d point)  //把力分解到x轴和z轴
        {
            Vector2d global = new Vector2d(1, 0); //向量（1,0）
            double alfa = getAngle(global, v); //向量V与X夹角
            double beta = getAngle(point,v ); //

            double k1 = Math.cos(alfa + beta) / Math.cos(beta);
            double k2 = Math.sin(alfa + beta) / Math.sin(beta);

            double x = point.x * k1;
            double y = point.y * k2;

            return new Vector2d(x, y);
        }

        private double repelForce(double distance, double range) //计算斥力大小
        {
            double force = 0;
            Point3d p = new Point3d();
            getCoords(p); //获取当前坐标
            if (distance <= range) //距离大于range则没有斥力
            {
                force = (1/distance - 1/range)*(1/distance - 1/range)*repelConstant;//计算斥力
            }

            return force;
        }

        private double attractForce(double distance) //计算吸引力
        {
            return attractConstant * distance;
        }

        private boolean checkGoal() //检查是否到达目的地
        {
            Point3d currentPos = new Point3d();
            getCoords(currentPos); //当前坐标
            Point3d goalPos = new Point3d(goal3d.x, goal3d.y, goal3d.z);
            // 如果当前距离目标点小于0.5那么即认为是到达
            return currentPos.distance(goalPos) <= 0.5;
        }


        public void performBehavior()
        {
            // 为了防止智能体剧烈晃动，每10帧计算一次受力
            if (getCounter() % 10 == 0)
            {
                Vector3d velocity = getVelocity(); //获取速度
                Vector2d direct = new Vector2d(velocity.z, velocity.x); //前进的方向向量

                Point3d p = new Point3d();
                getCoords(p);
                Vector2d pos = new Vector2d(p.z, p.x);

                double d0 = sonars.getMeasurement(0);// front声纳，正前方
                double d1 = sonars.getMeasurement(1);// frontleft声纳，左前方
                double d2 = sonars.getMeasurement(8);// frontright声纳，右前方

                double rf0 = repelForce(d0,5.0); //三个方向的斥力
                double rf1 = repelForce(d1, 4.0);
                double rf2 = repelForce(d2, 4.0);

                // 计算斥力的合力
                double k1 = Math.cos(2 * Math.PI / 9);
                double k2 = Math.sin(2 * Math.PI / 9);
                Vector2d vf0 = new Vector2d(0 - rf0, 0);
                Vector2d vf1 = new Vector2d((0 - rf1 * k1), (0 - rf1 * k2));
                Vector2d vf2 = new Vector2d((rf2 * k1), (rf2 * k2));
                Vector2d composition = new Vector2d();

                composition.setX(vf0.x + vf1.x + vf2.x);
                composition.setY(vf0.y + vf1.y + vf2.y);

                Vector2d repelForceVector = decomposition(direct, composition);

                Vector2d toGoal = new Vector2d((goal.x - pos.x),
                        (goal.y - pos.y));
                double disGoal = toGoal.length();
                double goalForce = attractForce(disGoal);

                Vector2d goalForceVector = new Vector2d(
                        (goalForce * toGoal.x / disGoal),
                        (goalForce * toGoal.y / disGoal));

                double x = repelForceVector.x + goalForceVector.x;
                double y = repelForceVector.y + goalForceVector.y;

                Vector2d allForces = new Vector2d(x, y);

                double angle = getAngle(direct, allForces);

                // 判断转动方向
                if (angle < Math.PI)
                {
                    setRotationalVelocity(angle);
                } else if (angle > Math.PI)
                {
                    setRotationalVelocity((angle - 2 * Math.PI));
                }

                if (checkGoal())
                {
                    // 到达目标点，停止运动
                    setTranslationalVelocity(0);
                    setRotationalVelocity(0);
                    lamp.setOn(true);
                    if (!hasArrived){
                        System.out.println("势场法Robot成功到达目的地了！");
                        System.out.println("用时:"+getLifeTime()+"s");
                        System.out.println("运动距离:"+getOdometer()+"m");
                        hasArrived = true;
                    }
                    return;
                } else
                {
                    lamp.setOn(false);
                    setTranslationalVelocity(0.5);
                }

                // 检测是否碰撞
                if (bumpers.oneHasHit())
                {
                    lamp.setBlink(true);

                    double left = sonars.getFrontLeftQuadrantMeasurement();
                    double right = sonars.getFrontRightQuadrantMeasurement();
                    double front = sonars.getFrontQuadrantMeasurement();

                    if ((front < 0.7) || (left < 0.7) || (right < 0.7))
                    {
                        if (left < right)
                        {
                            setRotationalVelocity(-1 - (0.1 * Math.random()));// 随机向右转
                        } else
                        {
                            setRotationalVelocity(1 - (0.1 * Math.random()));// 随机向左转
                        }
                        setTranslationalVelocity(0);
                    }
                }
                else lamp.setBlink(false);
            }
        }
    }

    private PotentialFieldMain()
    {
        int column = 20, row = 20;
        int[][] map = Map.map;

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

        add(new ObstacleRobot(new Vector3d(-3.5, 0, 0), "MyMovRobot"));
        add(new Robot(new Vector3d(-10, 0, -10), "My Robot"));
    }

    public static void main(String[] args)
    {
        System.setProperty("j3d.implicitAntialiasing", "true");
        new Simbad(new PotentialFieldMain(), false);
    }
}
