package automail;

import exceptions.InvalidAddItemException;
import exceptions.ItemTooHeavyException;
import strategies.IMailPool;

import java.util.*;

/**
 * Team Number: WS12-3
 * Group member: XuLin Yang(904904), Zhuoqun Huang(908525), Renjie Meng(877396)
 *
 * @create 2019-5-3 15:19:52
 * description: The individual robot delivers mail!
 **/

public class Robot implements IRobot {
    /**
     * robot message when it deliver a mail item to specific floor
     */
    private IMailDelivery delivery;

    /**
     * the mailPool that the robot is working for
     */
    private IMailPool mailPool;

    /**
     * robot's id in the system
     */
    private final String id;

    /**
     * Possible states the robot can be in
     * */
    private RobotState robotState;

    /**
     * the number of robot is working with (including self)
     */
    private TeamState teamState;

    /**
     * robot is at the floor
     */
    private int currentFloor;

    /**
     * indicator of whether robot has receive dispatch command from the system
     */
    private boolean receivedDispatch;

    /**
     * the mailItem that robot is holding in hand
     */
    private MailItem deliveryItem = null;

    /**
     * the mailItem that robot is holding in tube
     */
    private MailItem tube = null;

    /**
     * Initiates the robot's location at the start to be at the mailroom
     * also set it to be waiting for mail.
     * @param delivery governs the final delivery
     * @param mailPool is the source of mail items
     */
    public Robot(IMailDelivery delivery, IMailPool mailPool) {
    	id = "R" + hashCode();
    	robotState = RobotState.RETURNING;
    	teamState = TeamState.SINGLE;
        currentFloor = Building.MAILROOM_LOCATION;
        this.delivery = delivery;
        this.mailPool = mailPool;
        this.receivedDispatch = false;
    }

    /**
     * Send robot signal that the robot can start sending Mails
     */
    public void dispatch() {receivedDispatch = true;}

    /**
     * Signal robot to start delivery
     */
    @Override
    public void startDelivery() {receivedDispatch = false;}

    /**
     * This is called on every time step, making robot act if called.
     * @return a List of IRobot needed to be stepped in next time frame
     */
    public ArrayList<IRobot> step() {return robotState.step(this);}

    /**
     * Generic function that moves the robot towards the destination
     * @param destination the floor towards which the robot is moving
     */
    public void moveTowards(int destination) {
        if(currentFloor < destination){
            currentFloor++;
        } else {
            currentFloor--;
        }
    }

    /**
     * Get formatted RobotID as well as whether there is a item in tube of robot
     * @return String of formatted message
     */
    private String getIdTube() {return String.format("%s(%1d)", id, (tube == null ? 0 : 1));}
    
    /**
     * Prints out the change in state
     * @param nextState the state to which the robot is transitioning
     */
    public void changeState(RobotState nextState){
        /* Cannot be holding mail in tube but not mail in hand! */
    	assert(!(deliveryItem == null && tube != null));
    	if (robotState != nextState) {
            System.out.printf("T: %3d > %7s changed from %s to %s%n", Clock.Time(), getIdTube(), robotState, nextState);
    	}
    	robotState = nextState;
    	if(nextState == RobotState.DELIVERING){
            System.out.printf("T: %3d > %7s-> [%s]%n", Clock.Time(), getIdTube(), deliveryItem.toString());
    	}
    }

    /**
     * total number of robots working in the system
     * */
	static private int count = 0;

    /**
     * map of robot with its id
     */
	static private Map<Integer, Integer> hashMap = new TreeMap<>();

    /**
     * Add an MailItem to hand of robot.
     * @param mailItem The MailItem that need to be added
     * @throws ItemTooHeavyException Thrown if the Item is too heavy for the robot with current team status to carry
     */
	private void addToHand(MailItem mailItem) throws ItemTooHeavyException {
		assert(deliveryItem == null);
		deliveryItem = mailItem;
		if (teamState.validWeight() < mailItem.getWeight()) throw new ItemTooHeavyException();
	}

    /**
     * Add an MailItem to tube of robot.
     * @param mailItem The MailItem that need to be added
     * @throws ItemTooHeavyException Thrown if the Item is too heavy for the robot to put in tube
     */
	private void addToTube(MailItem mailItem) throws ItemTooHeavyException {
		assert(tube == null);
        tube = mailItem;
        /* Tube is always considered only able to carry light item */
		if (TeamState.SINGLE.validWeight() < mailItem.getWeight()) throw new ItemTooHeavyException();
	}

    /**
     * List all MailItems this robots is currently carrying
     * @return ArrayList of the items carried
     */
    @Override
    public ArrayList<MailItem> listMailItems() {
        ArrayList<MailItem> mailItems = new ArrayList<>();
        if (deliveryItem != null) {
            mailItems.add(deliveryItem);
        }
        if (tube != null) {
            mailItems.add(tube);
        }
        return mailItems;
    }

    /**
     * List the robots in current IRobot (IRobot is a more general robot, can be sworm of robots)
     * This is different from IRobot
     * @return ArrayList of itself
     */
    @Override
    public ArrayList<Robot> listRobots() {
        ArrayList<Robot> robots = new ArrayList<>();
        robots.add(this);
        return robots;
    }

    /**
     * Checks if given mail item can be given to current robot to deliver
     * @param mailItem The MailItem to be checked
     * @return True if can else false
     */
    @Override
    public boolean canAddMailItem(MailItem mailItem) {
	    // correct weight
	    if (teamState.validWeight() < mailItem.getWeight()) {
	        return false;
        }
	    // Return have space for item
        return (deliveryItem == null | tube == null);
    }

    /**
     * Add a MailItem to the current robot to carry
     * @param mailItem The MailItem to be added
     * @throws InvalidAddItemException thrown if the robot don't have enough space to add the item
     * @throws ItemTooHeavyException thrown if the item is too heavy for the robot to carry
     */
    @Override
    public void addMailItem(MailItem mailItem) throws InvalidAddItemException, ItemTooHeavyException {
	    if (deliveryItem == null) {
	        addToHand(mailItem);
        } else if (tube == null) {
	        addToTube(mailItem);
        } else {
	        throw new InvalidAddItemException();
        }
    }

    /**
     * robot deliver the mailItem
     */
    @Override
    public void deliver() {
	    delivery.deliver(deliveryItem);
	    deliveryItem = null;
    }

    /**
     * robot has delivered the item thus no item in its hand
     */
    public void clearDeliveryItem() {
	    deliveryItem = null;
    }

    @Override
    public MailItem getCurrentMailItem() {
	    return deliveryItem;
    }

    @Override
    public boolean hasNextMailItem() {
	    return tube != null;
    }

    @Override
    public void loadNextMailItem() {
	    try {
	        addToHand(tube);
	        tube = null;
        } catch (ItemTooHeavyException e) {
	        e.printStackTrace();
        }
    }

    @Override
    public int getFloor() {
	    return currentFloor;
    }

    @Override
    public void registerWaiting() {
	    mailPool.registerWaiting(this);
    }

    @Override
    public int hashCode() {
        Integer hash0 = super.hashCode();
        Integer hash = hashMap.get(hash0);
        if (hash == null) { hash = count++; hashMap.put(hash0, hash); }
        return hash;
    }

    @Override
    public String getId() {
	    return this.id;
	}

    @Override
    public ArrayList<IRobot> availableIRobots() {
	    return new ArrayList<>(Collections.singletonList(this));
    }

    @Override
    public RobotState getRobotState() {
        return robotState;
    }

    @Override
    public TeamState getTeamState() {
	    return teamState;
    }

    @Override
    public void changeTeamState(TeamState teamState) {
	    this.teamState = teamState;
    }

    @Override
    public boolean canStartDelivery() {
        return receivedDispatch;
    }
}
