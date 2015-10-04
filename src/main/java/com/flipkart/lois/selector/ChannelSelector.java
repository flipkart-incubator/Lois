package com.flipkart.lois.selector;

/**
 * Author: shashwat
 * Created: 01/10/15.
 */
public class ChannelSelector {

    interface Case {
        boolean tryExec() throws Exception;
    }

    private final Case[] cases;

    public ChannelSelector(Case... cases) {
        this.cases = cases;
    }

    public boolean doSelect() throws Exception {
        for (Case aCase : cases) {
            if (aCase.tryExec()) {
                return true;
            }
        }

        return false;
    }

}
