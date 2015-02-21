/**
 * ContentReader.java
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

import java.awt.Dimension;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.text.DefaultStyledDocument.ElementSpec;
import javax.swing.text.SimpleAttributeSet;

import org.fit.cssbox.io.DocumentSource;
import org.fit.cssbox.layout.BlockBox;
import org.fit.cssbox.layout.BlockReplacedBox;
import org.fit.cssbox.layout.BlockTableBox;
import org.fit.cssbox.layout.ElementBox;
import org.fit.cssbox.layout.InlineBlockReplacedBox;
import org.fit.cssbox.layout.InlineBox;
import org.fit.cssbox.layout.ListItemBox;
import org.fit.cssbox.layout.ReplacedBox;
import org.fit.cssbox.layout.TableBodyBox;
import org.fit.cssbox.layout.TableBox;
import org.fit.cssbox.layout.TableCaptionBox;
import org.fit.cssbox.layout.TableCellBox;
import org.fit.cssbox.layout.TableColumn;
import org.fit.cssbox.layout.TableColumnGroup;
import org.fit.cssbox.layout.TableRowBox;
import org.fit.cssbox.layout.TextBox;
import org.fit.cssbox.layout.Viewport;
import org.fit.cssbox.layout.VisualContext;
import org.fit.cssbox.swingbox.SwingBoxDocument;

/**
 * This class is used by editor kit to convert input data to elements used in
 * document.
 * 
 * @author Peter Bielik
 * @author burgetr
 */
public class ContentReader implements org.fit.cssbox.render.BoxRenderer
{
    /** Resulting element list */
    private List<ElementSpec> elements;

    /** Element counter for determining the drawing order */
    private int order;
    
    /**
     * Instantiates a new content reader.
     */
    public ContentReader()
    {
        //boxMap = new HashMap<Box, SimpleAttributeSet>();
    }

    /**
     * Reads input data and converts them to "elements"
     * 
     * @param docSource
     *            the document source
     * @param cba
     *            the instance of {@link CSSBoxAnalyzer}
     * @param dim
     *            the dimension
     * @return the list of elements. Note that, this method returns instance of
     *         LinkedList.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public List<ElementSpec> read(DocumentSource docSource, CSSBoxAnalyzer cba, Dimension dim)
            throws IOException
    {
        // ale ked sa pouzije setText() neviem nic o url, nic sa nenastavuje ,
        // moze byt null
        // (URL) doc.getProperty(Document.StreamDescriptionProperty)

        if (cba == null)
            throw new IllegalArgumentException("CSSBoxAnalyzer can not be NULL !!!\nProvide your custom implementation or check instantiation of DefaultAnalyzer object...");

        elements = new Vector<ElementSpec>();// ArrayList<ElementSpec>(1024);
        elements.add(new ElementSpec(SimpleAttributeSet.EMPTY, ElementSpec.EndTagType));
        order = 0;

        // System.err.print("used Reader and encoding ? " +
        // is.getCharacterStream() + "  ,  ");
        // InputStreamReader r = (InputStreamReader)is.getCharacterStream();
        // System.err.println(r.getEncoding());

        Viewport vp;
        try
        {
            // System.err.println("analyzing...");
            vp = cba.analyze(docSource, dim);
            // System.err.println("analyzing finished...");
        } catch (Exception e)
        {
            throw new IOException(e);
        }

        //Use this for "drawing" the boxes. This constructs the element list.
        vp.draw(this);

        // System.err.println("num. of elements : " + elements.size());
        // System.err.println("Root min width : " + root.getMinimalWidth() +
        // " ,normal width : " + root.getWidth() + " ,maximal width : " +
        // root.getMaximalWidth());

        // TODO po skonceni nacitavania aj nejake info spravit
        // >> Document.TitleProperty - observer, metainfo
        return elements;
    }

    /**
     * Updates the layout. It is designed to do a re-layout only, not to process
     * input data again.
     * 
     * @param root
     *            the root
     * @param newDimension
     *            the new dimension
     * @param cba
     *            the CSSBoxAnalyzer
     * @return the list
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public List<ElementSpec> update(Viewport root, Dimension newDimension, CSSBoxAnalyzer cba) throws IOException
    {
        if (cba == null)
            throw new IllegalArgumentException("CSSBoxAnalyzer can not be NULL !!!\nProvide your custom implementation or check instantiation of DefaultAnalyzer object...");

        elements = new LinkedList<ElementSpec>();
        elements.add(new ElementSpec(SimpleAttributeSet.EMPTY, ElementSpec.EndTagType));
        order = 0;

        Viewport vp;
        try
        {
            vp = cba.update(newDimension);
        } catch (Exception e)
        {
            throw new IOException(e);
        }

        vp.draw(this);
        
        return elements;
    }

    private SimpleAttributeSet buildElement(ElementBox box)
    {
        if (box instanceof InlineBox)
        {
            return buildInlineBox((InlineBox) box);
        }
        else if (box instanceof Viewport)
        { // -- the boxes
            return buildViewport((Viewport) box);
        }
        else if (box instanceof TableBox)
        { // -- tables
            return buildTableBox((TableBox) box);
        }
        else if (box instanceof TableCaptionBox)
        {
            return buildTableCaptionBox((TableCaptionBox) box);
        }
        else if (box instanceof TableBodyBox)
        {
            return buildTableBodyBox((TableBodyBox) box);
        }
        else if (box instanceof TableRowBox)
        {
            return buildTableRowBox((TableRowBox) box);
        }
        else if (box instanceof TableCellBox)
        {
            return buildTableCellBox((TableCellBox) box);
        }
        else if (box instanceof TableColumnGroup)
        {
            return buildTableColumnGroup((TableColumnGroup) box);
        }
        else if (box instanceof TableColumn)
        {
            return buildTableColumn((TableColumn) box);
        }
        else if (box instanceof BlockTableBox)
        {
            return buildBlockTableBox((BlockTableBox) box);
        }
        else if (box instanceof ListItemBox)
        {
            return buildListItemBox((ListItemBox) box);
        }
        else if (box instanceof BlockBox)
        {
            return buildBlockBox((BlockBox) box);
        }
        else
        {
            System.err.println("Unknown BOX : " + box.getClass().getName());
            return null;
        }
    }

    private SimpleAttributeSet buildText(TextBox box)
    {
        VisualContext vc = box.getVisualContext();
        SimpleAttributeSet attr = new SimpleAttributeSet();

        attr.addAttribute(Constants.ATTRIBUTE_FONT_VARIANT, vc.getFontVariant());
        attr.addAttribute(Constants.ATTRIBUTE_TEXT_DECORATION, vc.getTextDecoration());
        attr.addAttribute(Constants.ATTRIBUTE_FONT, vc.getFont());
        attr.addAttribute(Constants.ATTRIBUTE_FOREGROUND, vc.getColor());

        attr.addAttribute(SwingBoxDocument.ElementNameAttribute, Constants.TEXT_BOX);
        attr.addAttribute(Constants.ATTRIBUTE_ANCHOR_REFERENCE, new Anchor());
        attr.addAttribute(Constants.ATTRIBUTE_BOX_REFERENCE, box);

        return attr;
    }

    private SimpleAttributeSet buildReplacedBox(ReplacedBox box)
    {
        SimpleAttributeSet attr = new SimpleAttributeSet();
        if (box instanceof BlockReplacedBox)
            attr.addAttribute(SwingBoxDocument.ElementNameAttribute, Constants.BLOCK_REPLACED_BOX);
        else if (box instanceof InlineBlockReplacedBox)
            attr.addAttribute(SwingBoxDocument.ElementNameAttribute, Constants.INLINE_BLOCK_REPLACED_BOX);
        else
            attr.addAttribute(SwingBoxDocument.ElementNameAttribute, Constants.INLINE_REPLACED_BOX);
        attr.addAttribute(Constants.ATTRIBUTE_BOX_REFERENCE, box);
        attr.addAttribute(Constants.ATTRIBUTE_ANCHOR_REFERENCE, new Anchor());
        attr.addAttribute(Constants.ATTRIBUTE_REPLACED_CONTENT, box.getContentObj());

        return attr;
    }

    private SimpleAttributeSet buildElementBackground(ElementBox box)
    {
        return commonBuild(box, Constants.BACKGROUND);
    }
    
    private SimpleAttributeSet buildBlockBox(BlockBox box)
    {
        return commonBuild(box, Constants.BLOCK_BOX);
    }

    private SimpleAttributeSet buildInlineBox(InlineBox box)
    {
        return commonBuild(box, Constants.INLINE_BOX);
    }

    private SimpleAttributeSet buildViewport(Viewport box)
    {
        return commonBuild(box, Constants.VIEWPORT);
    }

    private SimpleAttributeSet buildBlockTableBox(BlockTableBox box)
    {
        return commonBuild(box, Constants.BLOCK_TABLE_BOX);
    }

    private SimpleAttributeSet buildTableBox(TableBox box)
    {
        return commonBuild(box, Constants.TABLE_BOX);
    }

    private SimpleAttributeSet buildTableCaptionBox(TableCaptionBox box)
    {
        return commonBuild(box, Constants.TABLE_CAPTION_BOX);
    }

    private SimpleAttributeSet buildTableBodyBox(TableBodyBox box)
    {
        return commonBuild(box, Constants.TABLE_BODY_BOX);
    }

    private SimpleAttributeSet buildTableRowBox(TableRowBox box)
    {
        return commonBuild(box, Constants.TABLE_ROW_BOX);
    }

    private SimpleAttributeSet buildTableCellBox(TableCellBox box)
    {
        return commonBuild(box, Constants.TABLE_CELL_BOX);
    }

    private SimpleAttributeSet buildTableColumn(TableColumn box)
    {
        return commonBuild(box, Constants.TABLE_COLUMN);
    }

    private SimpleAttributeSet buildTableColumnGroup(TableColumnGroup box)
    {
        return commonBuild(box, Constants.TABLE_COLUMN_GROUP);
    }

    private SimpleAttributeSet buildListItemBox(ListItemBox box)
    {
        return commonBuild(box, Constants.LIST_ITEM_BOX);
    }
    
    private final SimpleAttributeSet commonBuild(ElementBox box, Object elementNameValue)
    {
        // when there are no special requirements to build an element, use this
        // one
        SimpleAttributeSet attr = new SimpleAttributeSet();
        attr.addAttribute(SwingBoxDocument.ElementNameAttribute, elementNameValue);
        attr.addAttribute(Constants.ATTRIBUTE_ANCHOR_REFERENCE, new Anchor());
        attr.addAttribute(Constants.ATTRIBUTE_BOX_REFERENCE, box);
        attr.addAttribute(Constants.ATTRIBUTE_ELEMENT_ID, box.getElement().getAttribute("id"));

        return attr;
    }

    //======================================================================================================================
    //BoxRenderer implementation
    
    @Override
    public void startElementContents(ElementBox elem)
    {
        if (!elem.isReplaced())
        {
            SimpleAttributeSet attr = buildElement(elem);
            attr.addAttribute(Constants.ATTRIBUTE_DRAWING_ORDER, order++);
            elements.add(new ElementSpec(attr, ElementSpec.StartTagType, "{".toCharArray(), 1, 0));
        }
    }

    @Override
    public void finishElementContents(ElementBox elem)
    {
        if (!elem.isReplaced())
        {
            /*if (lastStarted == elem)
            {
                //rendering an empty element -- we must insert an empty string in order to preserve the element
                SimpleAttributeSet content = buildEmptyContent();
                elements.add(new ElementSpec(content, ElementSpec.ContentType, "".toCharArray(), 0, 0));
            }*/
            SimpleAttributeSet attr = buildElement(elem);
            elements.add(new ElementSpec(attr, ElementSpec.EndTagType, "}".toCharArray(), 1, 0));
        }
    }

    @Override
    public void renderElementBackground(ElementBox elem)
    {
        SimpleAttributeSet attr = buildElementBackground(elem);
        attr.addAttribute(Constants.ATTRIBUTE_DRAWING_ORDER, order++);
        elements.add(new ElementSpec(attr, ElementSpec.ContentType, "*".toCharArray(), 0, 1));
    }

    @Override
    public void renderTextContent(TextBox box)
    {
        String text = box.getText();
        SimpleAttributeSet attr = buildText(box);
        attr.addAttribute(Constants.ATTRIBUTE_DRAWING_ORDER, order++);
        elements.add(new ElementSpec(attr, ElementSpec.ContentType, text.toCharArray(), 0, text.length()));
    }

    @Override
    public void renderReplacedContent(ReplacedBox box)
    {
        org.w3c.dom.Element elem = ((ElementBox) box).getElement();
        String text = "";
        // add some textual info, if picture
        if ("img".equalsIgnoreCase(elem.getTagName()))
        {
            text = " [" + elem.getAttribute("alt") + " Location: "
                    + elem.getAttribute("src") + "] ";
        }
        else
            text = "{object}";

        SimpleAttributeSet attr = buildReplacedBox(box);
        attr.addAttribute(Constants.ATTRIBUTE_DRAWING_ORDER, order++);
        elements.add(new ElementSpec(attr, ElementSpec.ContentType, text.toCharArray(), 0, text.length()));
    }

    @Override
    public void close()
    {
    }

    // block attributes, in general
    // FirstLineIndent
    // LeftIndent
    // RightIndent
    // LineSpacing
    // SpaceAbove
    // SpaceBelow
    // Alignment
    // TabSet

    // Text attributes, in general
    // FontFamily
    // FontSize
    // Bold
    // Italic
    // Underline
    // StrikeThrough
    // Superscript
    // Subscript
    // Foreground
    // Background
    // ComponentAttribute
    // IconAttribute
}
