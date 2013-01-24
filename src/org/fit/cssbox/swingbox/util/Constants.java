/**
 * Constants.java
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

package org.fit.cssbox.swingbox.util;

/**
 * @author Peter Bielik
 * @version 1.0
 * @since 1.0 - 5.2.2011
 */
public final class Constants
{
    
    private Constants() 
    {
        // this is a library, private constructor
    }

    //used by environment - System.getProperty
    public static final String DOCUMENT_ASYNCHRONOUS_LOAD_PRIORITY_PROPERTY = "swingbox.document.async_load_priority";
    public static final String DEFAULT_ANALYZER_PROPERTY = "swingbox.default.analyzer";
    public static final String PROPERTY_NOT_SET = "property_not_set";

    // Attrinutes used by AttributeSet in elements and later in views
    public static final String ATTRIBUTE_BOX_REFERENCE = "attribute_box_reference";
    public static final String ATTRIBUTE_ANCHOR_REFERENCE = "attribute_anchor_reference";
    public static final String ATTRIBUTE_ELEMENT_ID = "element_id";
    public static final String ATTRIBUTE_TEXT_DECORATION = "attribute_text_decoration";
    public static final String ATTRIBUTE_FONT_VARIANT = "attribute_font_variant";
    public static final String ATTRIBUTE_FONT = "attribute_font";
    public static final String ATTRIBUTE_FOREGROUND = "attribute_foreground";
    public static final String ATTRIBUTE_BACKGROUND = "attribute_background";
    public static final String ATTRIBUTE_REPLACED_CONTENT = "attribute_replaced_content";

    //Custom elements
    //defines names for elements, used by ViewFactory to map element to a view
    public static final String TEXT_BOX = "text_box";
    public static final String BLOCK_BOX = "block_box";
    public static final String BLOCK_TABLE_BOX = "block_table_box";
    public static final String INLINE_BOX = "inline_box";
    public static final String INLINE_REPLACED_BOX = "inline_replaced_box";
    public static final String BLOCK_REPLACED_BOX = "block_replaced_box";
    public static final String VIEWPORT = "viewport";
    public static final String DELEGATE = "delegate";
    public static final String LIST_ITEM_BOX = "list_item_box";
    public static final String TABLE_BOX = "table_box";
    public static final String TABLE_BODY_BOX = "table_body_box";
    public static final String TABLE_CAPTION_BOX = "table_caption_box";
    public static final String TABLE_CELL_BOX = "table_cell_box";
    public static final String TABLE_COLUMN_GROUP = "table_column_group";
    public static final String TABLE_COLUMN = "table_column";
    public static final String TABLE_ROW_BOX = "table_row_box";

    //Element attributes
    //used by Anchor, which maps these keys to some values
    public static final String ELEMENT_A_ATTRIBUTE_HREF = "a_href";
    public static final String ELEMENT_A_ATTRIBUTE_NAME = "a_name";
    public static final String ELEMENT_A_ATTRIBUTE_TITLE = "a_title";
    public static final String ELEMENT_A_ATTRIBUTE_TARGET = "a_target";

    //for posting any data, used by JEditorPane
    public static final String PostDataProperty = "javax.swing.JEditorPane.postdata";

}
