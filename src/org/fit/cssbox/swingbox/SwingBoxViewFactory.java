/**
 * SwingBoxViewFactory.java
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

package org.fit.cssbox.swingbox;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

import org.fit.cssbox.swingbox.util.Constants;
import org.fit.cssbox.swingbox.view.BackgroundView;
import org.fit.cssbox.swingbox.view.BlockBoxView;
import org.fit.cssbox.swingbox.view.BlockReplacedBoxView;
import org.fit.cssbox.swingbox.view.BlockTableBoxView;
import org.fit.cssbox.swingbox.view.DelegateView;
import org.fit.cssbox.swingbox.view.InlineBoxView;
import org.fit.cssbox.swingbox.view.InlineReplacedBoxView;
import org.fit.cssbox.swingbox.view.ListItemBoxView;
import org.fit.cssbox.swingbox.view.TableBodyBoxView;
import org.fit.cssbox.swingbox.view.TableBoxView;
import org.fit.cssbox.swingbox.view.TableCaptionBoxView;
import org.fit.cssbox.swingbox.view.TableCellBoxView;
import org.fit.cssbox.swingbox.view.TableColumnGroupView;
import org.fit.cssbox.swingbox.view.TableColumnView;
import org.fit.cssbox.swingbox.view.TableRowBoxView;
import org.fit.cssbox.swingbox.view.TextBoxView;
import org.fit.cssbox.swingbox.view.ViewportView;

/**
 * This is implementation of ViewFactory interface for SwingBox View objects.
 * The name of element is defined in its attributes under
 * AbstractDocument.ElementNameAttribute key. If not found, a name of getName()
 * method is used. If no such name is recognized, LabelView is returned.
 * 
 * @author Peter Bielik
 * @version 1.0
 * @since 1.0 - 29.9.2010
 */
public class SwingBoxViewFactory implements ViewFactory
{
    private ViewportView viewport;

    @Override
    public View create(Element elem)
    {
        AttributeSet attr = elem.getAttributes();
        String name = (String) attr.getAttribute(AbstractDocument.ElementNameAttribute);
        if (name == null)
            name = elem.getName();

        if (name != null)
        {
            if (name.equals(Constants.EMPTY))
            {
                return new LabelView(elem);
            }
            else if (name.equals(Constants.BACKGROUND))
            {
                return new BackgroundView(elem);
            }
            else if (name.equals(Constants.TEXT_BOX))
            {
                return new TextBoxView(elem);
            }
            else if (name.equals(Constants.BLOCK_BOX))
            {
                return new BlockBoxView(elem);
            }
            else if (name.equals(Constants.INLINE_BOX))
            {
                return new InlineBoxView(elem);
            }
            else if (name.equals(Constants.BLOCK_REPLACED_BOX))
            {
                return new BlockReplacedBoxView(elem);
            }
            else if (name.equals(Constants.INLINE_REPLACED_BOX))
            {
                return new InlineReplacedBoxView(elem);
            }
            else if (name.equals(Constants.LIST_ITEM_BOX))
            {
                return new ListItemBoxView(elem);
            }
            else if (name.equals(Constants.BLOCK_TABLE_BOX))
            {
                return new BlockTableBoxView(elem);
            }
            else if (name.equals(Constants.TABLE_BOX))
            {
                return new TableBoxView(elem);
            }
            else if (name.equals(Constants.TABLE_BODY_BOX))
            {
                return new TableBodyBoxView(elem);
            }
            else if (name.equals(Constants.TABLE_CAPTION_BOX))
            {
                return new TableCaptionBoxView(elem);
            }
            else if (name.equals(Constants.TABLE_CELL_BOX))
            {
                return new TableCellBoxView(elem);
            }
            else if (name.equals(Constants.TABLE_COLUMN))
            {
                return new TableColumnView(elem);
            }
            else if (name.equals(Constants.TABLE_COLUMN_GROUP))
            {
                return new TableColumnGroupView(elem);
            }
            else if (name.equals(Constants.TABLE_ROW_BOX))
            {
                return new TableRowBoxView(elem);
            }
            else if (name.equals(Constants.VIEWPORT))
            {
                viewport = new ViewportView(elem);
                return viewport;
            }
            else if (name.equals(Constants.DELEGATE))
            {
                return new DelegateView(elem);
            }
            // -- javax.swing.text views --------------------------------
            else if (name.equals(AbstractDocument.SectionElementName))
            {
                return new BoxView(elem, View.Y_AXIS);
            }
            else if (name.equals(StyleConstants.ComponentElementName))
            {
                return new ComponentView(elem);
            }
            else if (name.equals(StyleConstants.IconElementName))
            {
                return new IconView(elem);
            }

        }

        // System.err.println("returning default LabelView ! " +
        // elem.toString());
        return new LabelView(elem);
    }

    /**
     * Gets the instance of ViewportView. Can be used for saving a page as an
     * image.
     * 
     * @return Current viewport.
     */
    public ViewportView getViewport()
    {
        return viewport;
    }

}
