package strategies;

import automail.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Xulin Yang, 904904
 *
 * @create 2019-05-01 19:42
 * description:
 **/

public class SelectMailItemToDeliverPlan implements ISelectMailItemToDeliverPlan {

    @Override
    public ArrayList<MailItem> generateDeliverMailItemPlan(List<MailItem> unloadedMailItems) {
        ArrayList<MailItem> plan = new ArrayList<>();

        for (MailItem mailItem: unloadedMailItems) {
            if (canAddMailItem(plan, mailItem)) {
                plan.add(mailItem);
            }
        }

        return plan;
    }

    @Override
    public boolean hasEnoughRobot(int nAvailableRobot, List<MailItem> plan) {
        return nAvailableRobot>=getPlanRequiredRobot(plan);
    }

    private boolean canAddMailItem(ArrayList<MailItem> plan, MailItem mailItemToAdd) {
        boolean hasHeavyItem = plan.stream().anyMatch(x -> x.getWeight()> ITeamState.SINGLE_MAX_WEIGHT),
                isHeavyItem = mailItemToAdd.getWeight() > TeamState.SINGLE.validWeight();

        /* no item return true;
         * ensure heavy item is only in 1st loading order */
        if (plan.isEmpty()) {
            return true;
        /* can has 2 light mail items for single robot with no heavy mail item */
        } else if (!hasHeavyItem && !isHeavyItem && (plan.size()<2)) {
            return true;
        /* has 1 heavy item && has 1 space for light item */
        } else  {
            // -1 for exclude heavy mail item from plan
            return (hasHeavyItem && !isHeavyItem  && (plan.size()-1<getPlanRequiredRobot(plan)));
        }
    }


    /**
     * get unloaded mail item with max weight
     * @return mail item in unloaded List with max weight
     * */
    private static MailItem getHeaviestMailItem(List<MailItem> mailItems) {
        assert mailItems.size()>0;
        int curMailItemMaxWeight = 0;
        MailItem heaviestMailItem = null;

        for (MailItem mailItem: mailItems) {
            if (mailItem.getWeight() > curMailItemMaxWeight) {
                heaviestMailItem = mailItem;
                curMailItemMaxWeight = heaviestMailItem.getWeight();
            }
        }

        return heaviestMailItem;
    }

    public int getPlanRequiredRobot(List<MailItem> plan) {
        return ITeamState.getNRequiredRobot(getHeaviestMailItem(plan));
    }
}
