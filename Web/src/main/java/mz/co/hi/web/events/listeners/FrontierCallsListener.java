package mz.co.hi.web.events.listeners;

import mz.co.hi.web.events.args.FrontierRequestInterception;

/**
 * @author Mário Júnior
 */
public interface FrontierCallsListener {

    public void preFrontier(FrontierRequestInterception interception);
    public void postFrontier(FrontierRequestInterception interception);

}