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
import java.awt.Shape;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Position.Bias;
import javax.swing.text.View;

import org.fit.cssbox.layout.ElementBox;
import org.fit.cssbox.swingbox.util.Constants;

/**
 * @author Peter Bielik
 * @version 1.0
 * @since 1.0 - 14.4.2011
 */
public class BackgroundView extends View
{
    private ElementBox box;

    /**
     * 
     */
    public BackgroundView(Element elem)
    {
        super(elem);
        AttributeSet tmpAttr = elem.getAttributes();
        Object obj = tmpAttr.getAttribute(Constants.ATTRIBUTE_BOX_REFERENCE);

        if (obj instanceof ElementBox)
        {
            box = (ElementBox) obj;
        }
        else
        {
            throw new IllegalArgumentException("Box reference is not an instance of ElementBox");
        }
    }

    @Override
    public String toString()
    {
        return "Background: " + box;
    }
    
    @Override
    public void paint(Graphics graphics, Shape allocation)
    {
        Graphics2D g;
        if (graphics instanceof Graphics2D)
            g = (Graphics2D) graphics;
        else
            throw new RuntimeException("Unknown graphics enviroment, java.awt.Graphics2D required !");
        
        box.getVisualContext().updateGraphics(g);
        box.drawBackground(g);
    }

    @Override
    public boolean isVisible()
    {
        return box.isVisible();
    }

    @Override
    public int viewToModel(float x, float y, Shape a, Bias[] bias)
    {
        return 0;
    }

    @Override
    public Shape modelToView(int pos, Shape a, Bias b)
            throws BadLocationException
    {
        throw new BadLocationException("Should not be applied here", pos);
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

}
