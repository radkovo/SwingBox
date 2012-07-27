/**
 * GeneralEventListener.java
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

import java.util.EventListener;

/**
 * This is the "general listener" interface, used for gaining various
 * interesting (but not vital (?)) informations.
 * 
 * @author Peter Bielik
 * @version 1.0
 * @since 1.0 - 14.4.2011
 */
public interface GeneralEventListener extends EventListener
{

    /**
     * General event update. Occures, when somthing "interesting" happens.
     * 
     * @param e the instance of event with data
     */
    public void generalEventUpdate(GeneralEvent e);
}
