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

package org.fit.cssbox.swingbox.view;

import javax.swing.text.AttributeSet;

/**
 * This interface is like Serializable. It only marks OUR objects. If this
 * interface is implemented, the Box reference MUST be in properties.
 * <p>
 * This interface is similar to serializable, only indicating certain
 * objects, in this case View objects, representing the output of the CSSBox.
 * These objects are arranged that they have a reference to the original
 * Box among their attributes from the getAttributes method, which they
 * represent and thus "copy" the hierarchy as it is between the boxes.
 * </p>
 *
 * @author Peter Bielik
 * @version 1.0
 * @since 1.0 - 12.3.2011
 */
public interface CSSBoxView {

  /**
   * Gets the attributes of a View object
   *
   * @return the attributes
   */
  AttributeSet getAttributes();

  /**
   * Obtains the drawing order of this view
   *
   * @return -1 if no drawing order is specified.
   */
  int getDrawingOrder();

}
