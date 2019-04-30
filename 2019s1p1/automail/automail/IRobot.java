package automail;

import exceptions.InvalidDeliverException;
import exceptions.InvalidAddItemException;
import exceptions.InvalidDispatchException;

import java.util.ArrayList;

public interface IRobot {

    int INDIVIDUAL_MAX_WEIGHT = 2000;
    int PAIR_MAX_WEIGHT = 2600;
    int TRIPLE_MAX_WEIGHT = 3000;

    /* ------------------------------------------------------------------------------------------------ */
    /*                                     Pre dispatch                                                 */
    /* ------------------------------------------------------------------------------------------------ */

    /**
     * List the MailItems added to this IRobot.
     * @return ArrayList of MailItems contained by this IRobot.
     */
    ArrayList<MailItem> listMailItems();

    /**
     * List the robots contained in this IRobot.
     * @return ArrayList of robots contained by this IRobot (1 if it's a robot)
     */
    ArrayList<Robot> listRobots();

    /**
     * Checks if given mail item can be added to this IRobot. Takes in account of heavy items.
     * @param mailItem The MailItem to be checked
     * @return True if can add (Enough space to add the item) else False (Not enough space)
     */
    boolean canAddMailItem(MailItem mailItem);

    /**
     * Add the given MailItem to the IRobot. Will raise Error if item not able to be added
     * @param mailItem The MailItem to be added
     * @throws InvalidAddItemException Indicates the Item can not be added to the IRobot.
     */
    void addMailItem(MailItem mailItem) throws InvalidAddItemException;

    /**
     * Checks if given IRobot can be dispatched to send the mail
     * @return True if can be dispatched else False
     */
    boolean canDispatch();

    /**
     * Dispatch the IRobot to send the MailItems.
     * @throws InvalidDispatchException Indicates the Robot can not yet be dispatched.
     */
    void dispatch() throws InvalidDispatchException;

    /* ------------------------------------------------------------------------------------------------ */
    /*                                     Post dispatch                                                */
    /* ------------------------------------------------------------------------------------------------ */

    /**
     * IRobot delivers the item.
     */
    void deliver();
    // TODO Commenting out the exception because not too sure if throwing exceptions is good here.
    // void deliver() throws InvalidDeliverException;

    /**
     * Move this robot toward a location
     * @param destination The destination to move to
     */
    void moveTowards(int destination);

    // TODO Redefine change state for Team robots
    /**
     * Change the state of this robot
     * @param robotState The state to change to
     */
    void changeState(RobotState robotState);


    MailItem getMailItem();

    /**
     * Checks if the IRobot has more item(s) to deliver
     * @return True if there are more, otherwise False
     */
    boolean hasNextMailItem();

    /**
     * Load the next MailItem of the IRobot (I.e. put it as an active task for delivery)
     * TODO Maybe put a Exception here, but we are having too many Exceptions already which is not a good sign
     */
    void loadNextMailItem();

    /**
     * Checks if IRobot is at some certain floor.
     * It's implemented this way because in the future there might be cases where IRobot could be on different floors
     * the same time (Maybe conducting some cross-floor operations). And this abstracts away the number of floors IRobot
     * can have.
     * @param floor The floor to check
     * @return True is IRobot is at certain floor, else False
     */
    boolean atFloor(int floor);

    /**
     * Tells the IRobot to maintain active and wait for more orders from control system.
     */
    void maintainActive();
}