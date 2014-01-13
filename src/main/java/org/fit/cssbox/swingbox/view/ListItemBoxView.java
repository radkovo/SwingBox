/**
 * ListItemBoxView.java
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

import javax.swing.text.Element;

import org.fit.cssbox.layout.ListItemBox;

/**
 * @author Peter Bielik
 * @version 1.0
 * @since 1.0 - 19.4.2011
 */
public class ListItemBoxView extends BlockBoxView
{

    /**
     * @param elem
     */
    public ListItemBoxView(Element elem)
    {
        super(elem);
    }

    @Override
    public void paint(Graphics g, Shape a)
    {
        super.paint(g, a);
        if (isVisible() && box instanceof ListItemBox)
            ((ListItemBox) box).drawMarker((Graphics2D) g);
    }

    
    
}
