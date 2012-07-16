
package org.fit.cssbox.swingbox.view;

import java.awt.Graphics;
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
    private String overflow;

    /**
     * @param elem
     *            the element.
     */
    public BlockBoxView(Element elem)
    {
        super(elem);
        overflow = ((BlockBox) box).getOverflowString();
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
        if (isVisible())
        {
            if ("visible".equals(overflow))
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
