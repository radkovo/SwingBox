/**
 * BackgroundView.java
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

package org.fit.cssbox.swingbox.view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Map;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Position.Bias;
import javax.swing.text.View;

import org.fit.cssbox.layout.ElementBox;
import org.fit.cssbox.swingbox.util.Anchor;
import org.fit.cssbox.swingbox.util.Constants;

/**
 * @author Peter Bielik
 * @version 1.0
 * @since 1.0 - 14.4.2011
 */
public class BackgroundView extends View implements CSSBoxView
{
    private ElementBox box;
    private int order;
    
    /** the cache of attributes */
    private AttributeSet attributes;
    /** decides whether to construct a cache from current working properties */
    private boolean refreshAttributes;
    private Anchor anchor;

    /**
     * 
     */
    public BackgroundView(Element elem)
    {
        super(elem);
        AttributeSet tmpAttr = elem.getAttributes();
        Object obj = tmpAttr.getAttribute(Constants.ATTRIBUTE_BOX_REFERENCE);
        anchor = (Anchor) tmpAttr.getAttribute(Constants.ATTRIBUTE_ANCHOR_REFERENCE);
        Integer i = (Integer) tmpAttr.getAttribute(Constants.ATTRIBUTE_DRAWING_ORDER);
        order = (i == null) ? -1 : i;

        if (obj instanceof ElementBox)
        {
            box = (ElementBox) obj;
        }
        else
        {
            throw new IllegalArgumentException("Box reference is not an instance of ElementBox");
        }
        
        if (box.toString().contains("\"btn\""))
            System.out.println("jo!");
        
        if (box.getElement() != null)
        {
            Map<String, String> elementAttributes = anchor.getProperties();
            org.w3c.dom.Element pelem = Anchor.findAnchorElement(box.getElement());
            if (pelem != null)
            {
                anchor.setActive(true);
                elementAttributes.put(Constants.ELEMENT_A_ATTRIBUTE_HREF, pelem.getAttribute("href"));
                elementAttributes.put(Constants.ELEMENT_A_ATTRIBUTE_NAME, pelem.getAttribute("name"));
                elementAttributes.put(Constants.ELEMENT_A_ATTRIBUTE_TITLE, pelem.getAttribute("title"));
                String target = pelem.getAttribute("target");
                if ("".equals(target))
                {
                    target = "_self";
                }
                elementAttributes.put(Constants.ELEMENT_A_ATTRIBUTE_TARGET, target);
            }
            else
            {
                anchor.setActive(false);
                elementAttributes.clear();
            }
        }
        
    }

    @Override
    public String toString()
    {
        return "Background " + order +": " + box;
    }
    
    @Override
    public void paint(Graphics graphics, Shape allocation)
    {
        Graphics2D g;
        if (graphics instanceof Graphics2D)
            g = (Graphics2D) graphics;
        else
            throw new RuntimeException("Unknown graphics environment, java.awt.Graphics2D required !");
        
        box.getVisualContext().updateGraphics(g);
        box.drawBackground(g);
    }
    
    @Override
    public void setParent(View parent)
    {
        super.setParent(parent);
        refreshAttributes = true;
    }

    @Override
    public boolean isVisible()
    {
        return box.isVisible();
    }

    @Override
    public int viewToModel(float x, float y, Shape a, Bias[] bias)
    {
        Rectangle alloc = a instanceof Rectangle ? (Rectangle) a : a.getBounds();
        if (x < alloc.x + (alloc.width / 2))
        {
            bias[0] = Position.Bias.Forward;
            return getStartOffset();
        }
        bias[0] = Position.Bias.Backward;
        return getEndOffset();
    }

    @Override
    public Shape modelToView(int pos, Shape a, Bias b)
            throws BadLocationException
    {
        int p0 = getStartOffset();
        int p1 = getEndOffset();
        if ((pos >= p0) && (pos <= p1))
        {
            Rectangle r = a instanceof Rectangle ? (Rectangle) a : a.getBounds();
            if (pos == p1)
            {
                r.x += r.width;
            }
            r.width = 0;
            return r;
        }
        throw new BadLocationException(pos + " not in range " + p0 + "," + p1, pos);
    }

    @Override
    public float getPreferredSpan(int axis)
    {
        switch (axis)
        {
            case View.X_AXIS:
                return 10f; //box.getWidth();
            case View.Y_AXIS:
                return 10f; //box.getHeight();
            default:
                throw new IllegalArgumentException("Invalid axis: " + axis);
        }
    }

    @Override
    public int getDrawingOrder()
    {
        return order;
    }
    
    @Override
    public AttributeSet getAttributes()
    {
        if (refreshAttributes)
        {
            attributes = createAttributes();
            refreshAttributes = false;
        }
        // always returns the same instance.
        // We need to know, if somebody modifies us outside..
        return attributes;
    }

    protected SimpleAttributeSet createAttributes()
    {
        SimpleAttributeSet res = new SimpleAttributeSet();
        res.addAttribute(Constants.ATTRIBUTE_ANCHOR_REFERENCE, anchor);
        res.addAttribute(Constants.ATTRIBUTE_BOX_REFERENCE, box);
        return res;
    }

    
}
