/*
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

import org.fit.cssbox.layout.TextBox;
import org.fit.cssbox.swingbox.SwingBoxDocument;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.*;
import javax.swing.text.Position.Bias;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class adds the "mouse support" to BrowserPane - generates
 * HyperlinkEvents.
 * 
 * @author Peter Bielik
 * @author Radek Burget
 */
public class MouseController extends MouseAdapter
{
    private static final Cursor defaultCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    private static final Cursor textCursor = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);
    
    private Element prevElem;
    private Anchor prevAnchor;

    @Override
    public void mouseClicked(MouseEvent e)
    {
        JEditorPane editor = (JEditorPane) e.getSource();

        if (!editor.isEditable() && SwingUtilities.isLeftMouseButton(e))
        {
            Bias[] bias = new Bias[1];
            Point pt = new Point(e.getX(), e.getY());
            int pos = editor.getUI().viewToModel2D(editor, pt, bias);

            if (bias[0] == Position.Bias.Backward && pos > 0) pos--;

            if (pos >= 0)
            {
                Element el = ((SwingBoxDocument) editor.getDocument()).getCharacterElement(pos);
                AttributeSet attr = el.getAttributes();
                Anchor anchor = (Anchor) attr.getAttribute(Constants.ATTRIBUTE_ANCHOR_REFERENCE);

                if (anchor != null && anchor.isActive())
                    createHyperLinkEvent(editor, el, anchor, EventType.ACTIVATED);
            }
        }

    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        JEditorPane editor = (JEditorPane) e.getSource();

        if (!editor.isEditable())
        {
            Bias[] bias = new Bias[1];
            Point pt = new Point(e.getX(), e.getY());
            int pos = editor.getUI().viewToModel2D(editor, pt, bias);

            if (bias[0] == Position.Bias.Backward && pos > 0) pos--;

            if (pos >= 0 && (editor.getDocument() instanceof StyledDocument))
            {
                Element elem = ((StyledDocument) editor.getDocument()).getCharacterElement(pos);
                Object bb = elem.getAttributes().getAttribute(Constants.ATTRIBUTE_BOX_REFERENCE);
                Anchor anchor = (Anchor) elem.getAttributes().getAttribute(Constants.ATTRIBUTE_ANCHOR_REFERENCE);

                if (elem != prevElem)
                {
                    prevElem = elem;
                    if (!anchor.isActive())
                    {
                        if ( bb instanceof TextBox )
                            setCursor(editor, textCursor);
                        else
                            setCursor(editor, defaultCursor);
                    }
                }
                
                if (anchor != prevAnchor)
                {
                    if (prevAnchor == null)
                    {
                        if (anchor.isActive())
                        {
                            createHyperLinkEvent(editor, elem, anchor, EventType.ENTERED);
                        }
                        prevAnchor = anchor;
                    }
                    else if (!prevAnchor.equalProperties(anchor.getProperties()))
                    {
                        if (prevAnchor.isActive())
                        {
                            createHyperLinkEvent(editor, prevElem, prevAnchor, EventType.EXITED);
                        }

                        if (anchor.isActive())
                        {
                            createHyperLinkEvent(editor, elem, anchor, EventType.ENTERED);
                        }
                        prevAnchor = anchor;
                    }

                }
            }
            else //nothing found
            {
                prevElem = null;
                if (prevAnchor != null && prevAnchor.isActive())
                {
                    createHyperLinkEvent(editor, prevElem, prevAnchor, EventType.EXITED);
                    prevAnchor = null;
                }   
                setCursor(editor, defaultCursor);
            }
        }
    }
    
    private void createHyperLinkEvent(JEditorPane editor, Element elem, Anchor anchor, EventType type)
    {
        HyperlinkEvent linkEvent;
        String href = anchor.getProperties().get(Constants.ELEMENT_A_ATTRIBUTE_HREF);
        String target = anchor.getProperties().get(Constants.ELEMENT_A_ATTRIBUTE_TARGET);
        URL url;
        URL base = (URL) editor.getDocument().getProperty(DefaultStyledDocument.StreamDescriptionProperty);
        try {
            url = new URL(base, href);
        } catch (MalformedURLException ignored) {
            url = null;
        }

        linkEvent = new HTMLFrameHyperlinkEvent(editor, type, url, href, elem, target);
        editor.fireHyperlinkUpdate(linkEvent);
    }
    
    protected void setCursor(JEditorPane editor, Cursor cursor)
    {
        if (editor.getCursor() != cursor)
        {
            editor.setCursor(cursor);
        }
    }
    
}
