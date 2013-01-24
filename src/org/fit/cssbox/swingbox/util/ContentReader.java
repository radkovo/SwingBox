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

import javax.swing.text.DefaultStyledDocument.ElementSpec;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;

import org.fit.cssbox.io.DocumentSource;
import org.fit.cssbox.layout.BlockBox;
import org.fit.cssbox.layout.BlockReplacedBox;
import org.fit.cssbox.layout.BlockTableBox;
import org.fit.cssbox.layout.Box;
import org.fit.cssbox.layout.ElementBox;
import org.fit.cssbox.layout.InlineBox;
import org.fit.cssbox.layout.InlineReplacedBox;
import org.fit.cssbox.layout.ListItemBox;
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
 * @version 1.0
 * @since 1.0 - 29.1.2011
 */
public class ContentReader
{

    /**
     * Instantiates a new content reader.
     */
    public ContentReader()
    {
    }

    /**
     * Reads input data and converts them to "elements"
     * 
     * @param is
     *            the input source
     * @param url
     *            the source of data
     * @param cba
     *            the instance of {@link CSSBoxAnalyzer}
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
            throw new IllegalArgumentException(
                    "CSSBoxAnalyzer can not be NULL !!!\nProvide your custom implementation or check instantiation of DefaultAnalyzer object...");

        List<ElementSpec> elements = new LinkedList<ElementSpec>();// ArrayList<ElementSpec>(1024);
        elements.add(new ElementSpec(SimpleAttributeSet.EMPTY, ElementSpec.EndTagType));

        // System.err.print("used Reader and encoding ? " +
        // is.getCharacterStream() + "  ,  ");
        // InputStreamReader r = (InputStreamReader)is.getCharacterStream();
        // System.err.println(r.getEncoding());

        ElementBox root;
        try
        {
            // System.err.println("analyzing...");
            root = cba.analyze(docSource, dim);
            // System.err.println("analyzing finished...");
        } catch (Exception e)
        {
            throw new IOException(e);
        }

        if (root instanceof Viewport)
        {
            // root should by an instance of Viewport
            buildViewport(elements, (Viewport) root);
        }
        else
        {
            buildElements(elements, root);
        }

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
    public List<ElementSpec> update(ElementBox root, Dimension newDimension, CSSBoxAnalyzer cba) throws IOException
    {
        if (cba == null)
            throw new IllegalArgumentException("CSSBoxAnalyzer can not be NULL !!!\nProvide your custom implementation or check instantiation of DefaultAnalyzer object...");

        List<ElementSpec> elements = new LinkedList<ElementSpec>();
        elements.add(new ElementSpec(SimpleAttributeSet.EMPTY, ElementSpec.EndTagType));

        ElementBox tmp;
        try
        {
            tmp = cba.update(root, newDimension);
        } catch (Exception e)
        {
            throw new IOException(e);
        }

        if (tmp instanceof Viewport)
        {
            // tmp should by an instance of Viewport
            buildViewport(elements, (Viewport) tmp);
        }
        else
        {
            buildElements(elements, tmp);
        }

        return elements;
    }

    private void buildElements(List<ElementSpec> elements, ElementBox box)
    {
        Box tmp;

        int total = box.getEndChild();
        for (int i = box.getStartChild(); i < total; i++)
        {
            tmp = box.getSubBox(i);

            if (tmp instanceof TextBox)
            { // -- text box
                buildText(elements, (TextBox) tmp);
            }
            else if (tmp instanceof InlineReplacedBox)
            { // -- inline boxes
                buildInlineReplacedBox(elements, (InlineReplacedBox) tmp);
            }
            else if (tmp instanceof InlineBox)
            {
                buildInlineBox(elements, (InlineBox) tmp);
            }
            else if (tmp instanceof Viewport)
            { // -- the boxes
                buildViewport(elements, (Viewport) tmp);
            }
            else if (tmp instanceof BlockReplacedBox)
            {
                buildBlockReplacedBox(elements, (BlockReplacedBox) tmp);
            }
            else if (tmp instanceof TableBox)
            { // -- tables
                buildTableBox(elements, (TableBox) tmp);
            }
            else if (tmp instanceof TableCaptionBox)
            {
                buildTableCaptionBox(elements, (TableCaptionBox) tmp);
            }
            else if (tmp instanceof TableBodyBox)
            {
                buildTableBodyBox(elements, (TableBodyBox) tmp);
            }
            else if (tmp instanceof TableRowBox)
            {
                buildTableRowBox(elements, (TableRowBox) tmp);
            }
            else if (tmp instanceof TableCellBox)
            {
                buildTableCellBox(elements, (TableCellBox) tmp);
            }
            else if (tmp instanceof TableColumnGroup)
            {
                buildTableColumnGroup(elements, (TableColumnGroup) tmp);
            }
            else if (tmp instanceof TableColumn)
            {
                buildTableColumn(elements, (TableColumn) tmp);
            }
            else if (tmp instanceof BlockTableBox)
            {
                buildBlockTableBox(elements, (BlockTableBox) tmp);
            }
            else if (tmp instanceof ListItemBox)
            {
                buildListItemBox(elements, (ListItemBox) tmp);
            }
            else if (tmp instanceof BlockBox)
            {
                buildBlockBox(elements, (BlockBox) tmp);
            }
            else
            {
                // todo log this !
                System.err.println("Unknowen BOX : " + tmp.getClass().getName());
            }

        }

    }

    private void buildText(List<ElementSpec> elements, TextBox box)
    {
        String text = box.getText();
        VisualContext vc = box.getVisualContext();
        MutableAttributeSet attr = new SimpleAttributeSet();

        attr.addAttribute(Constants.ATTRIBUTE_FONT_VARIANT, vc.getFontVariant());
        attr.addAttribute(Constants.ATTRIBUTE_TEXT_DECORATION, vc.getTextDecoration());
        attr.addAttribute(Constants.ATTRIBUTE_FONT, vc.getFont());
        attr.addAttribute(Constants.ATTRIBUTE_FOREGROUND, vc.getColor());

        attr.addAttribute(SwingBoxDocument.ElementNameAttribute, Constants.TEXT_BOX);
        attr.addAttribute(Constants.ATTRIBUTE_ANCHOR_REFERENCE, new Anchor());
        attr.addAttribute(Constants.ATTRIBUTE_BOX_REFERENCE, box);

        // elements.add(new ElementSpec(attr, ElementSpec.StartTagType));
        elements.add(new ElementSpec(attr, ElementSpec.ContentType, text.toCharArray(), 0, text.length()));
        // elements.add(new ElementSpec(attr, ElementSpec.EndTagType));

    }

    private void buildInlineReplacedBox(List<ElementSpec> elements,
            InlineReplacedBox box)
    {
        org.w3c.dom.Element elem = box.getElement();
        String text = "";
        // add some textual info, if picture
        if ("img".equalsIgnoreCase(elem.getTagName()))
        {
            text = " [" + elem.getAttribute("alt") + " Location: "
                    + elem.getAttribute("src") + "] ";
        }

        MutableAttributeSet attr = new SimpleAttributeSet();
        attr.addAttribute(SwingBoxDocument.ElementNameAttribute,
                Constants.INLINE_REPLACED_BOX);
        attr.addAttribute(Constants.ATTRIBUTE_BOX_REFERENCE, box);
        attr.addAttribute(Constants.ATTRIBUTE_ANCHOR_REFERENCE, new Anchor());
        attr.addAttribute(Constants.ATTRIBUTE_REPLACED_CONTENT,
                box.getContentObj());

        elements.add(new ElementSpec(attr, ElementSpec.ContentType, text.toCharArray(), 0, text.length()));
    }

    private void buildBlockReplacedBox(List<ElementSpec> elements, BlockReplacedBox box)
    {
        // some content
        org.w3c.dom.Element elem = box.getElement();
        String text = "";
        // add some textual info, if picture
        if ("img".equalsIgnoreCase(elem.getTagName()))
        {
            text = " [" + elem.getAttribute("alt") + " Location: "
                    + elem.getAttribute("src") + "] ";
        }

        MutableAttributeSet attr = new SimpleAttributeSet();
        attr.addAttribute(SwingBoxDocument.ElementNameAttribute, Constants.BLOCK_REPLACED_BOX);
        attr.addAttribute(Constants.ATTRIBUTE_BOX_REFERENCE, box);
        attr.addAttribute(Constants.ATTRIBUTE_ANCHOR_REFERENCE, new Anchor());
        attr.addAttribute(Constants.ATTRIBUTE_REPLACED_CONTENT,
                box.getContentObj());

        elements.add(new ElementSpec(attr, ElementSpec.ContentType, text.toCharArray(), 0, text.length()));
    }

    private void buildBlockBox(List<ElementSpec> elements, BlockBox box)
    {
        commonBuild(elements, box, Constants.BLOCK_BOX);
    }

    private void buildInlineBox(List<ElementSpec> elements, InlineBox box)
    {
        commonBuild(elements, box, Constants.INLINE_BOX);
    }

    private void buildViewport(List<ElementSpec> elements, Viewport box)
    {
        commonBuild(elements, box, Constants.VIEWPORT);
    }

    private void buildBlockTableBox(List<ElementSpec> elements, BlockTableBox box)
    {
        commonBuild(elements, box, Constants.BLOCK_TABLE_BOX);
    }

    private void buildTableBox(List<ElementSpec> elements, TableBox box)
    {
        commonBuild(elements, box, Constants.TABLE_BOX);
    }

    private void buildTableCaptionBox(List<ElementSpec> elements, TableCaptionBox box)
    {
        commonBuild(elements, box, Constants.TABLE_CAPTION_BOX);
    }

    private void buildTableBodyBox(List<ElementSpec> elements, TableBodyBox box)
    {
        commonBuild(elements, box, Constants.TABLE_BODY_BOX);
    }

    private void buildTableRowBox(List<ElementSpec> elements, TableRowBox box)
    {
        commonBuild(elements, box, Constants.TABLE_ROW_BOX);
    }

    private void buildTableCellBox(List<ElementSpec> elements, TableCellBox box)
    {
        commonBuild(elements, box, Constants.TABLE_CELL_BOX);
    }

    private void buildTableColumn(List<ElementSpec> elements, TableColumn box)
    {
        commonBuild(elements, box, Constants.TABLE_COLUMN);
    }

    private void buildTableColumnGroup(List<ElementSpec> elements, TableColumnGroup box)
    {
        commonBuild(elements, box, Constants.TABLE_COLUMN_GROUP);
    }

    private void buildListItemBox(List<ElementSpec> elements, ListItemBox box)
    {
        commonBuild(elements, box, Constants.LIST_ITEM_BOX);
    }

    private final void commonBuild(List<ElementSpec> elements, ElementBox box, Object elementNameValue)
    {
        // when there are no special requirements to build an element, use this
        // one
        SimpleAttributeSet attr = new SimpleAttributeSet();
        attr.addAttribute(SwingBoxDocument.ElementNameAttribute,
                elementNameValue);
        attr.addAttribute(Constants.ATTRIBUTE_ANCHOR_REFERENCE, new Anchor());
        attr.addAttribute(Constants.ATTRIBUTE_BOX_REFERENCE, box);
        attr.addAttribute(Constants.ATTRIBUTE_ELEMENT_ID, box.getElement().getAttribute("id"));

        elements.add(new ElementSpec(attr, ElementSpec.StartTagType));

        buildElements(elements, box);

        elements.add(new ElementSpec(attr, ElementSpec.EndTagType));
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
