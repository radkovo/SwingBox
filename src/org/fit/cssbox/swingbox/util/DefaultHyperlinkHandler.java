package org.fit.cssbox.swingbox.util;

import java.awt.Cursor;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * Customizable implementation of HyperlinListener.
 *
 * @author Peter Bielik
 * @version 1.0
 * @since 1.0 - 5.5.2011
 */
public class DefaultHyperlinkHandler implements HyperlinkListener{
    private static final Cursor HandCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    private static final Cursor DefaultCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

    /* (non-Javadoc)
     * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
     */
    @Override
    public void hyperlinkUpdate(HyperlinkEvent evt) {
	JEditorPane pane = (JEditorPane) evt.getSource();
	if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
		setCursor(pane, DefaultCursor);
		loadPage(pane, evt);
	} else if (evt.getEventType() == HyperlinkEvent.EventType.ENTERED) {
	    regionEntered(pane, evt);
	} else if (evt.getEventType() == HyperlinkEvent.EventType.EXITED) {
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
    protected void loadPage(JEditorPane pane, HyperlinkEvent evt) {
	//if some security, or other interaction is needed, override this method
	try {
	    pane.setPage(evt.getURL());
	} catch (IOException e) {
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
    protected void regionEntered(JEditorPane pane, HyperlinkEvent evt) {
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
    protected void regionExited(JEditorPane pane, HyperlinkEvent evt) {
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
    protected void setCursor(JEditorPane editor, Cursor cursor) {
	if (editor.getCursor() != cursor) {
	    editor.setCursor(cursor);
	}
    }


}
