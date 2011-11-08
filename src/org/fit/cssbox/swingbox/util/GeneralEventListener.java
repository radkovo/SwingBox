/**
 *
 */
package org.fit.cssbox.swingbox.util;

import java.util.EventListener;

/**
 * This is the "general listener" interface, used for gaining various
 * interesting (but not vital (?)) informations.
 *
 * @author Peter Bielik
 * @version 1.0
 * @since 1.0 - 14.4.2011
 */
public interface GeneralEventListener extends EventListener{

    /**
     * General event update. Occures, when somthing "interesting" happens.
     *
     * @param e
     *            the instance of event with data
     */
    public void generalEventUpdate (GeneralEvent e);
}
