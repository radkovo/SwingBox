package org.fit.cssbox.swingbox.util;

import java.util.EventObject;

/**
 * The Class GeneralEvent. This class represents a general events and provides
 * some data.
 *
 * @author Peter Bielik
 * @version 1.0
 * @since 1.0 - 14.4.2011
 */
public class GeneralEvent extends EventObject{
    private static final long serialVersionUID = -3946243806649687837L;
    public Object primary_value = null;
    public Object secondary_value = null;
    public EventType event_type = null;

    /**
     *Currently supported types of event.
     */
    public enum EventType {
	page_loading_begin,
	page_loading_end,
	page_loading_error
    }


    /**
     * Instantiates a new general event.
     *
     * @param source
     *            where event happened
     * @param et
     *            the type of event
     * @param primary_value
     *            some data
     * @param secondary_value
     *            some data
     */
    public GeneralEvent(Object source, EventType et, Object primary_value, Object secondary_value) {
	super(source);
	this.event_type = et;
	this.primary_value = primary_value;
	this.secondary_value = secondary_value;
    }

    /**
     * Gets the primary value.
     *
     * @return the primary value
     */
    public Object getPrimaryValue() {
	return this.primary_value;
    }

    /**
     * Gets the secondary value.
     *
     * @return the secondary value
     */
    public Object getSecondaryValue() {
	return this.secondary_value;
    }

    /**
     * Gets the type of event.
     *
     * @return the event type
     */
    public EventType getEventType() {
	return this.event_type;
    }

}
