/**
 * DefaultHyperlinkHandler.java
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

import java.awt.Cursor;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * Customizable implementation of HyperlinListener. This default implementation only changes
 * the cursor shape when a link is entered or exited.
 * 
 * @author Peter Bielik
 */
public class DefaultHyperlinkHandler implements HyperlinkListener
{
    private static final Cursor HandCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    private static final Cursor DefaultCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

    @Override
    public void hyperlinkUpdate(HyperlinkEvent evt)
    {
        JEditorPane pane = (JEditorPane) evt.getSource();
        if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
        {
            setCursor(pane, DefaultCursor);
            loadPage(pane, evt);
        }
        else if (evt.getEventType() == HyperlinkEvent.EventType.ENTERED)
        {
            regionEntered(pane, evt);
        }
        else if (evt.getEventType() == HyperlinkEvent.EventType.EXITED)
        {
            regionExited(pane, evt);
        }
    }

    /**
     * Loads given page as HyperlinkEvent.
     * 
     * @param pane
     *            the pane
     * @param evt
     *            the event
     */
    protected void loadPage(JEditorPane pane, HyperlinkEvent evt)
    {
        // if some security, or other interaction is needed, override this
        // method
        try
        {
            pane.setPage(evt.getURL());
        } catch (IOException e)
        {
            System.err.println(e.getLocalizedMessage());
        }
    }

    /**
     * Region entered. Called when mouse poionter is over some link
     * 
     * @param pane
     *            the pane
     * @param evt
     *            the event
     */
    protected void regionEntered(JEditorPane pane, HyperlinkEvent evt)
    {
        setCursor(pane, HandCursor);
    }

    /**
     * Region exited. Called when mouse pointer leaves a link
     * 
     * @param pane
     *            the pane
     * @param evt
     *            the event
     */
    protected void regionExited(JEditorPane pane, HyperlinkEvent evt)
    {
        setCursor(pane, DefaultCursor);
    }

    /**
     * Sets the mouse cursor cursor.
     * 
     * @param editor
     *            the editor
     * @param cursor
     *            the cursor
     */
    protected void setCursor(JEditorPane editor, Cursor cursor)
    {
        if (editor.getCursor() != cursor)
        {
            editor.setCursor(cursor);
        }
    }

}
