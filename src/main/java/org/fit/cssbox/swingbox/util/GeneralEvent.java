/**
 * GeneralEvent.java
 * (c) Peter Bielik and Radek Burget, 2011-2012
 *
 * SwingBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * SwingBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with SwingBox. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.fit.cssbox.swingbox.util;

import java.util.EventObject;

/**
 * The Class GeneralEvent. This class represents a general events and provides
 * some data.
 * 
 * @author Peter Bielik
 */
public class GeneralEvent extends EventObject
{
    private static final long serialVersionUID = -3946243806649687837L;
    public Object primary_value = null;
    public Object secondary_value = null;
    public EventType event_type = null;

    /**
     * Currently supported types of event.
     */
    public enum EventType { page_loading_begin, page_loading_end, page_loading_error }

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
    public GeneralEvent(Object source, EventType et, Object primary_value, Object secondary_value)
    {
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
    public Object getPrimaryValue()
    {
        return this.primary_value;
    }

    /**
     * Gets the secondary value.
     * 
     * @return the secondary value
     */
    public Object getSecondaryValue()
    {
        return this.secondary_value;
    }

    /**
     * Gets the type of event.
     * 
     * @return the event type
     */
    public EventType getEventType()
    {
        return this.event_type;
    }

}
