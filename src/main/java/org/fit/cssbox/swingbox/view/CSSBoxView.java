/**
 * CSSBoxView.java
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

import javax.swing.text.AttributeSet;

/**
 * This interface is like Serializable. It only marks OUR objects. If this
 * interface is implemented, the Box reference MUST be in properties.
 * 
 * @author Peter Bielik
 * @version 1.0
 * @since 1.0 - 12.3.2011
 */
public interface CSSBoxView
{
    /*
     * toto rozhranie je podobne, ako serializable, len oznacujuce iste objekty,
     * v tomto pripade obejkty typu View, znazornujuce vystup CSSBoxu. Tieto
     * objektu maju garantovane, ze maju medzi svojimi atribytmi z metody
     * getAttributes aj referenciu na original *Box, ktory reprezentuju a
     * celkovo tak "kopiruju" hierarchiu, aka je medzi boxmi.
     */

    /**
     * Gets the attributes of a View object
     * 
     * @return the attributes
     */
    AttributeSet getAttributes();
    
    /**
     * Obtains the drawing order of this view
     */
    int getDrawingOrder();
    
}
