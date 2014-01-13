/**
 * InlineBoxView.java
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
import java.awt.Shape;

import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.View;

/**
 * @author Peter Bielik
 * @version 1.0
 * @since 1.0 - 15.2.2011
 */
public class InlineBoxView extends ElementBoxView
{
    /**
     * Constructs a new view wrapped on an element.
     * 
     * @param elem
     *            the element
     */
    public InlineBoxView(Element elem)
    {
        super(elem);
        setAxis(X_AXIS); // we are 'inline' !
    }

    @Override
    public AttributeSet getAttributes()
    {
        return super.getAttributes();
    }

    @Override
    public void paint(Graphics graphics, Shape allocation)
    {
        if (box.isDisplayed())
        {
            Graphics2D g = (Graphics2D) graphics;
            // super.paint(g, allocation);

            box.getVisualContext().updateGraphics(g);
            box.drawBackground(g);
            int n = getViewCount();
            // Rectangle alloc = allocation instanceof Rectangle ? (Rectangle)
            // allocation : allocation.getBounds();

            for (int i = 0; i < n; i++)
            {
                View v = getView(i);
                v.paint(g, allocation);
            }

        }
    }

    @Override
    public boolean isVisible()
    {
        return box.isVisible();
    }

    @Override
    public float getPreferredSpan(int axis)
    {
        if (!isVisible()) { return 0; }
        return super.getPreferredSpan(axis);
    }

    @Override
    public float getMinimumSpan(int axis)
    {
        if (!isVisible()) { return 0; }
        return super.getMinimumSpan(axis);
    }

    @Override
    public float getMaximumSpan(int axis)
    {
        if (!isVisible()) { return 0; }
        return super.getMaximumSpan(axis);
    }

}
