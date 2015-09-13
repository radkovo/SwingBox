/**
 * BlockReplacedBoxView.java
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

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Map;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.LayeredHighlighter;
import javax.swing.text.Position;
import javax.swing.text.Position.Bias;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.View;

import org.fit.cssbox.layout.BlockReplacedBox;
import org.fit.cssbox.layout.ReplacedContent;
import org.fit.cssbox.layout.ReplacedImage;
import org.fit.cssbox.swingbox.util.Constants;

/**
 * @author Peter Bielik
 * @version 1.0
 * @since 1.0 - 14.4.2011
 */
public class BlockReplacedBoxView extends BlockBoxView
{
    // this is a block version of InlineReplacedBoxView
    // get some default system font..
    // hint: default font as constant in Constants
    private static final Font DEFAULT_FONT = new Font(null, Font.PLAIN, 13);
    private Container container;
    private ReplacedContent content;
    private ReplacedImage repImage;
    private String alt = "";
    private String title = "";

    /**
     * Instantiates a new block replaced box view.
     * 
     * @param elem
     *            the element
     */
    public BlockReplacedBoxView(Element elem)
    {
        // bonus : spravit klikaciu mapu :)
        super(elem);

        content = ((BlockReplacedBox) box).getContentObj();
        if (content instanceof ReplacedImage)
        {
            repImage = (ReplacedImage) content;
        }
        else
        {
            repImage = null;
        }

        loadElementAttributes();
    }

    private void loadElementAttributes()
    {
        /*
         * http://www.w3schools.com/TAGS/tag_img.asp html attributy : alt -
         * alternativny popisok, zobrazit ak nie je dostupny image title -
         * popup/tooltip text
         */

        // getElement().getAttribute("src");
        alt = box.getElement().getAttribute("alt");
        title = box.getElement().getAttribute("title");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paint(Graphics graphics, Shape allocation)
    {
        Graphics2D g = (Graphics2D) graphics;
        Rectangle alloc = toRect(allocation);

        if (isVisible() /*&& intersection(alloc, box.getAbsoluteBounds(), tmpRect)*/)
        {
            paintHighlights(g, alloc);
            box.getVisualContext().updateGraphics(g);
            box.drawBackground(g);

            if (content != null)
            {
                content.draw(g, box.getContentWidth(), box.getContentHeight());

                if (repImage != null && repImage.getImage() == null)
                {
                    if (!"".equals(alt))
                    {
                        // so we have replaced image, which has no image data...
                        // :)

                        g.setFont(DEFAULT_FONT);
                        g.setColor(Color.BLACK);
                        //tmpRect = box.getAbsoluteContentBounds();
                        // alternative picture representation (screen readers)
                        // TODO hint : java accessibility !!!
                        /*g.drawString(alt, tmpRect.x + 2, tmpRect.y
                                + (int) (tmpRect.height * 0.7));*/
                    }
                    else
                    {
                        drawCross(g);
                    }
                }
            }
            else
            {
                drawCross(g);
            }

        }

    }

    private void drawCross(Graphics2D g)
    {
        /*tmpRect = box.getAbsoluteContentBounds();

        g.setColor(Color.BLACK);
        g.drawLine(tmpRect.x, tmpRect.y, tmpRect.x + tmpRect.width - 1,
                tmpRect.y + tmpRect.height - 1);
        g.drawLine(tmpRect.x + tmpRect.width - 1, tmpRect.y, tmpRect.x,
                tmpRect.y + tmpRect.height - 1);*/
    }

    @Override
    public String getToolTipText(float x, float y, Shape allocation)
    {
        String val = "";
        String tmp;
        Map<String, String> elementAttributes = anchor.getProperties();

        if (title != null && !"".equals(title))
            val = val + "<b>" + title + "</b><br>";
        tmp = elementAttributes.get(Constants.ELEMENT_A_ATTRIBUTE_TITLE);
        if (tmp != null && !"".equals(tmp))
            val = val + "<i>" + tmp + "</i><br>";
        tmp = elementAttributes.get(Constants.ELEMENT_A_ATTRIBUTE_HREF);
        if (tmp != null && !"".equals(tmp)) val = val + tmp;

        return "".equals(val) ? null : "<html>" + val + "</html>";
    }

    @Override
    public boolean isVisible()
    {
        return box.isDisplayed() && box.isVisible();
    }

    @Override
    public void setParent(View parent)
    {
        super.setParent(parent);
        if (parent != null)
        {
            container = getContainer();
            if (repImage != null) repImage.setContainer(container);// getContainer());
        }
        else
        {
            repImage = null;
            content = null;
            container = null;
        }
    }

    @Override
    protected View getViewAtPoint(int x, int y, Rectangle alloc)
    {
        Rectangle rec = box.getAbsoluteBounds();
        if (rec.contains(x, y)) { return this; }

        return null;
    }

    protected SimpleAttributeSet createAttributes()
    {
        // called from getAttributes()
        SimpleAttributeSet res = super.createAttributes();
        res.addAttribute(Constants.ATTRIBUTE_REPLACED_CONTENT, content);

        return res;
    }

    @Override
    public int viewToModel(float x, float y, Shape a, Bias[] bias)
    {
        // Rectangle alloc = a instanceof Rectangle ? (Rectangle)a :
        // a.getBounds();
        // if (x < alloc.x + alloc.width) {
        // bias[0] = Position.Bias.Forward;
        // return getStartOffset(); // LTR
        // }
        // bias[0] = Position.Bias.Backward;
        // return getEndOffset(); //RTL

        Rectangle alloc = a instanceof Rectangle ? (Rectangle) a : a
                .getBounds();
        if (x < alloc.x + (alloc.width / 2))
        {
            bias[0] = Position.Bias.Forward;
            return getStartOffset();
        }
        bias[0] = Position.Bias.Backward;
        return getEndOffset();
    }

    @Override
    public Shape modelToView(int pos, Shape a, Bias b)
            throws BadLocationException
    {
        int p0 = getStartOffset();
        int p1 = getEndOffset();
        if ((pos >= p0) && (pos <= p1))
        {
            Rectangle r = a instanceof Rectangle ? (Rectangle) a : a
                    .getBounds();
            if (pos == p1)
            {
                r.x += r.width;
            }
            r.width = 0;
            return r;
        }
        // return null;
        // //return box.getAbsoluteBounds();
        throw new BadLocationException(pos + " not in range " + p0 + "," + p1,
                pos);
    }

    private void paintHighlights(Graphics g, Shape shape)
    {
        if (container instanceof JTextComponent)
        {
            JTextComponent tc = (JTextComponent) container;
            Highlighter h = tc.getHighlighter();
            if (h instanceof LayeredHighlighter)
            {
                ((LayeredHighlighter) h).paintLayeredHighlights(g,
                        getStartOffset(), getEndOffset(), shape, tc, this);
            }
        }
    }

}
