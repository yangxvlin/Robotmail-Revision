package strategies;

import java.util.*;
import automail.*;
import exceptions.NotEnoughRobotException;
import exceptions.UnSupportedTooMuchRobotException;

/**
 * Xulin Yang, 904904
 * 
 * @create 2019-04-25 15:50
 * description:
 */
public class Task {
	private List<Robot> supportingRobots;
	private Robot leadingRobot;
	private int destinationFloor;

	public Task(ArrayList<Robot> robots, int destinationFloor) {
		assert robots.size() > 0;
		this.leadingRobot = null;
		this.supportingRobots = new ArrayList<>();

		/* choose first robot in task to ve leading robot to lead the team
		 * TODO probably this is some kind of task forming strategy and should be factored out
		 * by XuLin
		 * */
		for (int i = 0; i < robots.size(); i++) {
			/* TODO magic number
			 * by XuLin
			 * */
			if (i == 0) {
				leadingRobot = robots.get(i);
			} else {
				supportingRobots.add(robots.get(i));
			}
		}

		this.destinationFloor = destinationFloor;
	}

	public Task(Robot robot, int destinationFloor) {
        this.leadingRobot = robot;
        this.supportingRobots = new ArrayList<>();
        this.destinationFloor = destinationFloor;
    }

	public boolean isRobotLeading(Robot robot) {
		return robot.getId().equals(this.leadingRobot.getId());
	}

	public int getDestinationFloor() {return this.destinationFloor;}

	public Task getNextTask(Robot robot, int destinationFloor) {
        return new Task(robot, destinationFloor);
    }

    public Task getReturnTask(Robot robot) {
        return new Task(robot, Building.MAILROOM_LOCATION);
    }

    public int getNumRobot() {
	    return this.supportingRobots.size() + 1;
    }


    public static int getTeamWeight(int nRobots)
            throws NotEnoughRobotException, UnSupportedTooMuchRobotException {

	    if (nRobots <= 0) {
            throw new NotEnoughRobotException();
        }

	    switch (nRobots) {
            case 1:
                return Robot.INDIVIDUAL_MAX_WEIGHT;
            case 2:
                return Robot.PAIR_MAX_WEIGHT;
            case 3:
                return Robot.TRIPLE_MAX_WEIGHT;
            default:
                throw new UnSupportedTooMuchRobotException();
        }

    }
}