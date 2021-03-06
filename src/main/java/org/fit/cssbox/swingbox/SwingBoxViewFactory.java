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

package org.fit.cssbox.swingbox;

import org.fit.cssbox.swingbox.util.Constants;
import org.fit.cssbox.swingbox.view.*;

import javax.swing.text.*;

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
        if (name == null) {
            name = elem.getName();
        }

        if (name != null) {
            switch( name ) {
                case Constants.EMPTY:
                    return new LabelView( elem );
                case Constants.BACKGROUND:
                    return new BackgroundView( elem );
                case Constants.TEXT_BOX:
                    return new TextBoxView( elem );
                case Constants.BLOCK_BOX:
                    return new BlockBoxView( elem );
                case Constants.INLINE_BOX:
                    return new InlineBoxView( elem );
                case Constants.BLOCK_REPLACED_BOX:
                    return new BlockReplacedBoxView( elem );
                case Constants.INLINE_REPLACED_BOX:
                    return new InlineReplacedBoxView( elem );
                case Constants.LIST_ITEM_BOX:
                    return new ListItemBoxView( elem );
                case Constants.BLOCK_TABLE_BOX:
                    return new BlockTableBoxView( elem );
                case Constants.TABLE_BOX:
                    return new TableBoxView( elem );
                case Constants.TABLE_BODY_BOX:
                    return new TableBodyBoxView( elem );
                case Constants.TABLE_CAPTION_BOX:
                    return new TableCaptionBoxView( elem );
                case Constants.TABLE_CELL_BOX:
                    return new TableCellBoxView( elem );
                case Constants.TABLE_COLUMN:
                    return new TableColumnView( elem );
                case Constants.TABLE_COLUMN_GROUP:
                    return new TableColumnGroupView( elem );
                case Constants.TABLE_ROW_BOX:
                    return new TableRowBoxView( elem );
                case Constants.VIEWPORT:
                    viewport = new ViewportView( elem );
                    return viewport;
                case Constants.DELEGATE:
                    return new DelegateView( elem );

                // -- javax.swing.text views --------------------------------
                case AbstractDocument.SectionElementName:
                    return new BoxView( elem, View.Y_AXIS );
                case StyleConstants.ComponentElementName:
                    return new ComponentView( elem );
                case StyleConstants.IconElementName:
                    return new IconView( elem );
            }
        }

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
