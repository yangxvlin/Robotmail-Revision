package automail;

import exceptions.InvalidAddItemException;
import exceptions.InvalidDeliverException;
import exceptions.InvalidDispatchException;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Xulin Yang, 904904
 *
 * @create 2019-04-29 20:00
 * description:
 **/

public class RobotTeam implements IRobot {
    private ArrayList<IRobot> robots;
    private ArrayList<MailItem> unloadedMailItems;

    public RobotTeam() {
        robots = new ArrayList<>();
        unloadedMailItems = new ArrayList<>();
    }

    /**
     * List the MailItems added to this IRobot.
     * @return ArrayList of MailItems contained by this IRobot.
     */
    @Override
    public ArrayList<MailItem> listMailItems() {
        ArrayList<MailItem> allMailItems = new ArrayList<>();

        robots.forEach(robot -> allMailItems.addAll(robot.listMailItems()));

        return allMailItems;
    }

    /**
     * List the robots contained in this IRobot.
     * @return ArrayList of robots contained by this IRobot (1 if it's a robot)
     */
    @Override
    public ArrayList<Robot> listRobots() {
        return robots.stream()
                        .filter(obj -> obj instanceof Robot)
                        .map(obj -> (Robot) obj)
                        .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Checks if given IRobot can be dispatched to send the mail
     * @return True if can be dispatched else False
     */
    public boolean canDispatch() {
        return hasUnloadedMailItem() && hasEnoughTeamMember();
    }

    /**
     * Dispatch the IRobot to send the MailItems.
     * @throws InvalidDispatchException Indicates the Robot can not yet be dispatched.
     */
    @Override
    public void dispatch() throws InvalidDispatchException {
        assert hasUnloadedMailItem();
        /* TODO load unloaded MailItems to robots now */

        /* dispatch all robots */
        for (IRobot robot : robots) {
            robot.dispatch();
        }
    }

    /**
     * Add the given MailItem to the IRobot. Will raise Error if item not able to be added
     * @param mailItem The MailItem to be added
     * @throws InvalidAddItemException Indicates the Item can not be added to the IRobot.
     */
    @Override
    public void addMailItem(MailItem mailItem) throws InvalidAddItemException {
        if (!canAddMailItem(mailItem)) {
            throw new InvalidAddItemException();
        } else {
            unloadedMailItems.add(mailItem);
        }
    }

    /**
     * Checks if given mail item can be added to this IRobot. Takes in account of heavy items.
     * @param mailItem The MailItem to be checked
     * @return True if can add (Enough space to add the item) else False (Not enough space)
     */
    @Override
    public boolean canAddMailItem(MailItem mailItem) {
        boolean hasHeavyItem = hasHeavyItem(),
                isHeavyItem = mailItem.getWeight() > IRobot.INDIVIDUAL_MAX_WEIGHT;

        /* no item return true;
         * ensure heavy item is only in 1st loading order */
        if (!hasUnloadedMailItem()) {
            return true;
        /* can has 2 light mail items for single robot with no heavy mail item */
        } else if (!hasHeavyItem && !isHeavyItem && (this.getUnloadingMailItemSize()<2)) {
            return true;
        /* has 1 heavy item && has 1 space for light item */
        } else if (hasHeavyItem && !isHeavyItem  && (this.getUnloadingMailItemSize()<this.getNRequiredRobot())) {
            return true;
        }

        return false;
    }

    /**
     * IRobot delivers the item.
     */
    public void deliver() throws InvalidDeliverException {
        // TODO
    }

    public int getTeamSize() {return robots.size();}

    public int getUnloadingMailItemSize() {return unloadedMailItems.size();}

    private boolean hasUnloadedMailItem() {return unloadedMailItems.size()>0;}

    public boolean hasEnoughTeamMember() {
        assert hasUnloadedMailItem();
        /* has enough team member for max weight of heaviest items */
        return getTeamSize() > 0 &&
                getTeamSize() == this.getNRequiredRobot();

    }

    public void addRobot(Robot robot) {
        assert !hasEnoughTeamMember();

        robots.add(robot);
    }

    private boolean hasHeavyItem() {
        for (MailItem mailItem: unloadedMailItems) {
            if (mailItem.getWeight() > IRobot.INDIVIDUAL_MAX_WEIGHT) {
                return true;
            }
        }
        return false;
    }

    private MailItem getHeavyMailItem() {
        assert hasHeavyItem();

        for (MailItem mailItem: unloadedMailItems) {
            if (mailItem.getWeight() > IRobot.INDIVIDUAL_MAX_WEIGHT) {
                return mailItem;
            }
        }

        /* for completeness */
        return null;
    }

    private MailItem getHeaviestMailItem() {
        assert hasHeavyItem();
        int curMailItemMaxWeight = 0;
        MailItem heaviestMailItem = null;

        for (MailItem mailItem: unloadedMailItems) {
            if (mailItem.getWeight() > curMailItemMaxWeight) {
                heaviestMailItem = mailItem;
            }
        }

        return heaviestMailItem;
    }

    private int getNRequiredRobot() {
        return getHeaviestMailItem().getNRequiredRobot();
    }
}