
package org.fit.cssbox.swingbox.util;

import java.util.HashMap;
import java.util.Map;

/**
 * The Class Anchor. Provides info about hyperlinks.
 * 
 * @author Peter Bielik
 * @version 1.0
 * @since 1.0 - 4.5.2011
 */
public class Anchor
{
    // a helper object for hyperlinks
    private boolean active;
    private Map<String, String> properties;

    /**
     * Instantiates a new anchor.
     * 
     * @param activity
     *            is it really a link ?
     * @param props
     *            the properties
     */
    public Anchor(boolean activity, Map<String, String> props)
    {
        this.active = activity;
        properties = new HashMap<String, String>(props);
    }

    /**
     * Instantiates a new anchor.
     */
    public Anchor()
    {
        properties = new HashMap<String, String>();
        active = false;
    }

    /**
     * Checks if is active.
     * 
     * @return true, if is a real link
     */
    public boolean isActive()
    {
        return active;
    }

    /**
     * Sets the activity.
     * 
     * @param active
     *            if true, is a link
     */
    public void setActive(boolean active)
    {
        this.active = active;
    }

    /**
     * Gets the properties.
     * 
     * @return the properties
     */
    public Map<String, String> getProperties()
    {
        return properties;
    }

    /**
     * Equal properties.
     * 
     * @param other
     *            the other
     * @return true, if successful
     */
    public boolean equalProperties(Map<String, String> other)
    {
        return properties.equals(other);
    }

    @Override
    public String toString()
    {
        return "Anchor(@" + Integer.toHexString(hashCode()) + ")[Active: "
                + active + ", Properties: " + properties.toString() + "]";
    }
}
