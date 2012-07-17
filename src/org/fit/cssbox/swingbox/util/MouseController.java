
package org.fit.cssbox.swingbox.util;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.Position.Bias;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import org.fit.cssbox.swingbox.SwingBoxDocument;

/**
 * This class adds the "mouse support" to BrowserPane - generates
 * HyperlinkEvents.
 * 
 * @author Peter Bielik
 * @version 1.0
 * @since 1.0 - 14.4.2011
 */
public class MouseController extends MouseAdapter
{
    private Element prevElem;
    private Anchor prevAnchor;

    @Override
    public void mouseClicked(MouseEvent e)
    {
        JEditorPane editor = (JEditorPane) e.getSource();

        if (!editor.isEditable() && SwingUtilities.isLeftMouseButton(e))
        {
            Point pt = new Point(e.getX(), e.getY());
            int pos = editor.viewToModel(pt);
            // System.err.println("found position : " + pos);
            if (pos >= 0)
            {
                Element el = ((SwingBoxDocument) editor.getDocument())
                        .getCharacterElement(pos);
                AttributeSet attr = el.getAttributes();
                Anchor anchor = (Anchor) attr
                        .getAttribute(Constants.ATTRIBUTE_ANCHOR_REFERENCE);

                if (anchor != null && anchor.isActive())
                    createHyperLinkEvent(editor, el, anchor,
                            EventType.ACTIVATED);
            }

        }

    }

    private void createHyperLinkEvent(JEditorPane editor, Element elem,
            Anchor anchor, EventType type)
    {
        HyperlinkEvent linkEvent;
        String href = (String) anchor.getProperties().get(
                Constants.ELEMENT_A_ATTRIBUTE_HREF);
        String target = (String) anchor.getProperties().get(
                Constants.ELEMENT_A_ATTRIBUTE_TARGET);
        URL url;
        URL base = (URL) editor.getDocument().getProperty(
                DefaultStyledDocument.StreamDescriptionProperty);
        try
        {
            url = new URL(base, href);
        } catch (MalformedURLException ignored)
        {
            url = null;
        }

        linkEvent = new HTMLFrameHyperlinkEvent(editor, type, url, href, elem,
                target);
        editor.fireHyperlinkUpdate(linkEvent);

    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        JEditorPane editor = (JEditorPane) e.getSource();

        if (!editor.isEditable())
        {
            Bias[] bias = new Bias[1];
            Point pt = new Point(e.getX(), e.getY());
            int pos = editor.getUI().viewToModel(editor, pt, bias);

            if (bias[0] == Position.Bias.Backward && pos > 0) pos--;

            if (pos >= 0 && (editor.getDocument() instanceof StyledDocument))
            {
                Element elem = ((StyledDocument) editor.getDocument())
                        .getCharacterElement(pos);
                Anchor anchor = (Anchor) elem.getAttributes().getAttribute(
                        Constants.ATTRIBUTE_ANCHOR_REFERENCE);

                if (anchor != null)
                {
                    if (prevAnchor == null)
                    {
                        if (anchor.isActive())
                        {
                            createHyperLinkEvent(editor, elem, anchor,
                                    EventType.ENTERED);
                        }
                        prevElem = elem;
                        prevAnchor = anchor;

                    }
                    else if (!prevAnchor
                            .equalProperties(anchor.getProperties()))
                    {
                        if (prevAnchor.isActive())
                        {
                            createHyperLinkEvent(editor, prevElem, prevAnchor,
                                    EventType.EXITED);
                        }

                        if (anchor.isActive())
                        {
                            createHyperLinkEvent(editor, elem, anchor,
                                    EventType.ENTERED);
                        }
                        prevElem = elem;
                        prevAnchor = anchor;
                    }

                }
            }
        }
    }
}
