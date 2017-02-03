/**
 * BlockBoxView.java
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
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.AttributeSet;
import javax.swing.text.Element;

import org.fit.cssbox.layout.BlockBox;

/**
 * This class represents BlockBox.
 * 
 * @author Peter Bielik
 * @version 1.0
 * @since 1.0 - 22.2.2011
 */
public class BlockBoxView extends ElementBoxView
{
    private String overflowX;

    /**
     * @param elem
     *            the element.
     */
    public BlockBoxView(Element elem)
    {
        super(elem);
        overflowX = ((BlockBox) box).getOverflowXString();
    }

    @Override
    public AttributeSet getAttributes()
    {

        return super.getAttributes();
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

    @Override
    public void paint(Graphics g, Shape a)
    {
        /*if (box.toString().contains("box-in") && box.getParent() != null && box.getParent().toString().contains("discblog"))
            System.out.println("jo22!");
        if (box.toString().contains("discblog-prep-2"))
            System.out.println("jo!");*/
        if (box.isDisplayed() && box.isDeclaredVisible())
        {
            Rectangle tmpRect = new Rectangle();
            if ("visible".equals(overflowX))
            {
                // just let it be
                tmpRect = toRect(a);
            }
            else
            {
                // cut it !
                intersection(box.getAbsoluteBounds(), toRect(a), tmpRect);
            }

            // System.err.println("BlockBox : " + overflow + " - " + tmpRect);
            super.paint(g, tmpRect);

        }
    }

    @Override
    public boolean isVisible()
    {
        return box.isVisible();
    }

}
