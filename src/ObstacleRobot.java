import simbad.sim.Agent;
import simbad.sim.LampActuator;
import simbad.sim.RangeSensorBelt;
import simbad.sim.RobotFactory;

import javax.vecmath.Vector3d;

public class ObstacleRobot extends Agent {   //移动障碍机器人

    private RangeSensorBelt bumpers;
    private LampActuator lamp;
    private double speed = 0.4;

    ObstacleRobot(Vector3d position, String name) {
        super(position,name);
        bumpers = RobotFactory.addBumperBeltSensor(this);
        lamp = RobotFactory.addLamp(this);
    }

    public void initBehavior() {
        setTranslationalVelocity(speed);
    }

    public void performBehavior() {
        if (bumpers.oneHasHit()) {
            lamp.setBlink(true);
        }else
            lamp.setBlink(false);

        if(getCounter()%80 == 0){
            speed = -speed;
            setTranslationalVelocity(speed);
        }
    }
}