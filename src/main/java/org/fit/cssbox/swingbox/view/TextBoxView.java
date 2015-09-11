/**
 * TextBoxView.java
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;

import javax.swing.event.DocumentEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.LayeredHighlighter;
import javax.swing.text.Position;
import javax.swing.text.Position.Bias;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

import org.fit.cssbox.layout.BlockBox;
import org.fit.cssbox.layout.TextBox;
import org.fit.cssbox.swingbox.util.Anchor;
import org.fit.cssbox.swingbox.util.Constants;

import cz.vutbr.web.css.CSSProperty.FontVariant;
import cz.vutbr.web.css.CSSProperty.TextDecoration;

/**
 * The Class TextBoxView. This renders a text.
 * 
 * @author Peter Bielik
 * @author Radek Burget
 */
public class TextBoxView extends View implements CSSBoxView
{
    private TextBox box;
    private Font font;
    private Color foreground;
    private List<TextDecoration> textDecoration;
    private String fontVariant;
    private TextLayout layout;
    private AffineTransform transform;
    private int order;

    /** the cache of attributes */
    private AttributeSet attributes;
    /** decides whether to construct a cache from current working properties */
    private boolean refreshAttributes;
    private boolean refreshProperties;
    private boolean refreshTextLayout;

    private boolean underline;
    private boolean strike;
    private boolean overline;

    private Container container;

    private Anchor anchor;

    /**
     * Instantiates a new text based view, able to display rich text. This view
     * corresponds to TextBox in CSSBox. <br>
     * <a href="http://www.w3.org/TR/CSS21/box.html">Box Model</a>
     * 
     * @param elem
     *            the elem
     * 
     */

    public TextBoxView(Element elem)
    {
        super(elem);
        AttributeSet tmpAttr = elem.getAttributes();
        Object obj = tmpAttr.getAttribute(Constants.ATTRIBUTE_BOX_REFERENCE);
        anchor = (Anchor) tmpAttr.getAttribute(Constants.ATTRIBUTE_ANCHOR_REFERENCE);
        Integer i = (Integer) tmpAttr.getAttribute(Constants.ATTRIBUTE_DRAWING_ORDER);
        order = (i == null) ? -1 : i;

        if (obj instanceof TextBox)
        {
            box = (TextBox) obj;
        }
        else
        {
            throw new IllegalArgumentException("Box reference is not an instance of TextBox");
        }
        
        if (box.getNode() != null && box.getNode().getParentNode() instanceof org.w3c.dom.Element)
        {
            org.w3c.dom.Element pelem = Anchor.findAnchorElement((org.w3c.dom.Element) box.getNode().getParentNode());
            Map<String, String> elementAttributes = anchor.getProperties();
    
            if (pelem != null)
            {
                anchor.setActive(true);
                elementAttributes.put(Constants.ELEMENT_A_ATTRIBUTE_HREF, pelem.getAttribute("href"));
                elementAttributes.put(Constants.ELEMENT_A_ATTRIBUTE_NAME, pelem.getAttribute("name"));
                elementAttributes.put(Constants.ELEMENT_A_ATTRIBUTE_TITLE, pelem.getAttribute("title"));
                String target = pelem.getAttribute("target");
                if ("".equals(target))
                {
                    target = "_self";
                }
                elementAttributes.put(Constants.ELEMENT_A_ATTRIBUTE_TARGET, target);
                // System.err.println("## Anchor at : " + this + " attr: "+
                // elementAttributes);
            }
            else
            {
                anchor.setActive(false);
                elementAttributes.clear();
            }
        }

    }

    @Override
    public int getDrawingOrder()
    {
        return order;
    }
    
    // --- View methods ---------------------------------------------

    @Override
    public void setParent(View parent)
    {
        super.setParent(parent);
        if (parent != null)
        {
            transform = new AffineTransform();
            setPropertiesFromAttributes(getElement().getAttributes());
            refreshAttributes = true;
            refreshProperties = false;
            container = getContainer();
            /*if (parent instanceof ElementBoxView)
            {
                // avoid a RootView or any other non-SwingBox views
                Anchor parentAnchor = ((ElementBoxView) parent).getAnchor();
                if (parentAnchor.isActive())
                {
                    // share elementAttributes
                    anchor.setActive(true);
                    anchor.getProperties().putAll(parentAnchor.getProperties());
                }
            }*/
        }
        else
        {
            anchor = null;
            transform = null;
            container = null;
        }
    }

    @Override
    public View createFragment(int p0, int p1)
    {
        // this method will return THIS object
        // -- currently, fragmenting not supported
        // we are fragmented by CSSBox !
        return this;
    }

    @Override
    public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f)
    {
        //assume that attributes have changed, reflect changes immediately
        invalidateProperties();
        syncProperties();
        invalidateTextLayout();
        super.changedUpdate(e, a, f);
    }

    @Override
    public float getMaximumSpan(int axis)
    {
        // currently we do not support dynamic sizing, we are pre-computed by CSSBox!
        return getPreferredSpan(axis);
    }

    @Override
    public float getPreferredSpan(int axis)
    {
        // returns total width including margins and borders
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

    @Override
    public float getMinimumSpan(int axis)
    {
        // currently we do not support dynamic sizing, we are pre-computed by CSSBox!
        return getPreferredSpan(axis);
    }

    /**
     * Checks if is visible.
     * 
     * @return true, if is visible
     */
    @Override
    public boolean isVisible()
    {
        return box.isVisible();
    }

    @Override
    public int getResizeWeight(int axis)
    {
        // not resizable
        return 0;
    }

    @Override
    public Shape modelToView(int pos, Shape a, Bias b)
            throws BadLocationException
    {
        TextLayout layout = getTextLayout();
        int offs = pos - getStartOffset(); // the start position this view is responsible for
        Rectangle alloc = new Rectangle(toRect(a));
        TextHitInfo hit = ((b == Position.Bias.Forward) ? TextHitInfo.afterOffset(offs) : TextHitInfo.beforeOffset(offs));
        float[] locs = layout.getCaretInfo(hit);

        // hint: nie je lepsie to prepisat na setBounds, ktory berie int ?
        alloc.setRect(alloc.getX() + locs[0], alloc.getY(), 1D,
                alloc.getHeight());

        return alloc;
    }

    @Override
    public int viewToModel(float x, float y, Shape a, Bias[] biasReturn)
    {
        Rectangle alloc = toRect(a);
        // Move the y co-ord of the hit onto the baseline. This is because
        // TextLayout supports
        // italic carets and we do not.
        TextLayout layout = getTextLayout();
        TextHitInfo hit = layout.hitTestChar(x - (float) alloc.getX(), 0f);
        // TextHitInfo hit = layout.hitTestChar(x - box.getAbsoluteContentX(),
        // 0f);
        int pos = hit.getInsertionIndex();
        biasReturn[0] = hit.isLeadingEdge() ? Position.Bias.Forward
                : Position.Bias.Backward;
        return pos + getStartOffset();
    }

    @Override
    public AttributeSet getAttributes()
    {
        if (refreshAttributes)
        {
            attributes = createAttributes();
            refreshAttributes = false;
            refreshProperties = false;
        }
        // always returns the same instance.
        // We need to know, if somebody modifies us outside..
        return attributes;
    }

    private AttributeSet createAttributes()
    {
        // get all 'working variables' and make an AttributeSet.
        SimpleAttributeSet res = new SimpleAttributeSet();

        res.addAttribute(Constants.ATTRIBUTE_BOX_REFERENCE, box);
        res.addAttribute(Constants.ATTRIBUTE_ANCHOR_REFERENCE, anchor);
        res.addAttribute(Constants.ATTRIBUTE_FONT_VARIANT, fontVariant);
        res.addAttribute(Constants.ATTRIBUTE_TEXT_DECORATION, textDecoration);
        res.addAttribute(Constants.ATTRIBUTE_FONT, font);
        res.addAttribute(Constants.ATTRIBUTE_FOREGROUND, foreground);

        return res;
    }

    @Override
    public String getToolTipText(float x, float y, Shape allocation)
    {
        if (anchor.isActive())
        {
            Map<String, String> elementAttributes = anchor.getProperties();
            String val = "";
            String tmp;
            tmp = elementAttributes.get(Constants.ELEMENT_A_ATTRIBUTE_TITLE);
            if (tmp != null && !"".equals(tmp))
                val = val + "<i>" + tmp + "</i><br>";
            tmp = elementAttributes.get(Constants.ELEMENT_A_ATTRIBUTE_HREF);
            if (tmp != null && !"".equals(tmp)) val = val + tmp;

            return "".equals(val) ? null : "<html>" + val + "</html>";
        }

        //return "NotLink: " + this;
        return null;
    }

    @Override
    public void paint(Graphics gg, Shape a)
    {
        //System.out.println("Paint text: " + this + " in " + a);

        if (isVisible())
        {
            processPaint(gg, a);
        }
    }

    /**
     * Process paint.
     * 
     * @param gg
     *            the graphics context
     * @param a
     *            the allocation
     */
    protected void processPaint(Graphics gg, Shape a)
    {
        Graphics2D g = (Graphics2D) gg;
        AffineTransform tmpTransform = g.getTransform();
        if (!tmpTransform.equals(transform))
        {
            transform = tmpTransform;
            invalidateTextLayout();
        }

        Component c = container;
        int p0 = getStartOffset();
        int p1 = getEndOffset();
        Color fg = getForeground();

        if (c instanceof JTextComponent)
        {
            JTextComponent tc = (JTextComponent) c;
            if (!tc.isEnabled())
            {
                fg = tc.getDisabledTextColor();
            }

            // javax.swing.plaf.basic.BasicTextUI $ BasicHighlighter
            // >> DefaultHighlighter
            // >> DefaultHighlightPainter

            Highlighter highLighter = tc.getHighlighter();
            if (highLighter instanceof LayeredHighlighter)
            {
                ((LayeredHighlighter) highLighter).paintLayeredHighlights(g, p0, p1, box.getAbsoluteContentBounds(), tc, this);
                // (g, p0, p1, a, tc, this);
            }
        }
        // nothing is selected
        if (!box.isEmpty() && !getText().isEmpty())
            renderContent(g, a, fg, p0, p1);

    }

    /**
     * Renders content.
     * 
     * @param g
     *            the graphics
     * @param a
     *            the allocation
     * @param fg
     *            the color of foreground
     * @param p0
     *            start position
     * @param p1
     *            end position
     */
    protected void renderContent(Graphics2D g, Shape a, Color fg, int p0, int p1)
    {
        TextLayout layout = getTextLayout();
        Rectangle absoluteBounds = box.getAbsoluteBounds();
        Rectangle absoluteContentBounds = box.getAbsoluteContentBounds();

        int pStart = getStartOffset();
        int pEnd = getEndOffset();
        int x = absoluteBounds.x;
        int y = absoluteBounds.y;

        Shape oldclip = g.getClip();
        BlockBox clipblock = box.getClipBlock();
        if (clipblock != null)
        {
            Rectangle newclip = clipblock.getClippedContentBounds();
            Rectangle clip = toRect(oldclip).intersection(newclip);
            g.setClip(clip);
        }
        
        g.setFont(getFont());
        g.setColor(fg);

        // -- Draw the string at specified positions --

        if (p0 > pStart || p1 < pEnd)
        {
            try
            {
                // TextLayout can't render only part of it's range, so if a
                // partial range is required, add a clip region.
                Shape s = modelToView(p0, Position.Bias.Forward, p1, Position.Bias.Backward, a);
                absoluteContentBounds = absoluteContentBounds.intersection(toRect(s));
            } catch (BadLocationException ignored)
            {
            }
        }

        // render the text
        layout.draw(g, x, y + layout.getAscent());

        //render the decoration
        if (underline || strike || overline)
        {
            Stroke origStroke = g.getStroke();

            int w;
            if (getFont().isBold())
                w = getFont().getSize() / 8;
            else
                w = getFont().getSize() / 10;
            if (w < 1) w = 1;
            y += w / 2;
            
            g.setStroke(new BasicStroke(w));

            int xx = absoluteContentBounds.x + absoluteContentBounds.width;
            if (overline)
            {
                g.drawLine(absoluteContentBounds.x, y, xx, y);
            }
            if (underline)
            {
                int yy = y + absoluteContentBounds.height - (int) layout.getDescent();
                g.drawLine(absoluteContentBounds.x, yy, xx, yy);
            }
            if (strike)
            {
                int yy = y + absoluteContentBounds.height / 2;
                g.drawLine(absoluteContentBounds.x, yy, xx, yy);
            }
            
            g.setStroke(origStroke);
        }

        g.setClip(oldclip);
    }

    /**
     * Repaints the content, used by blink decoration.
     * 
     * @param ms
     *            time - the upper bound of delay
     * @param bounds
     *            the bounds
     */
    protected void repaint(final int ms, final Rectangle bounds)
    {
        if (container != null)
        {
            container.repaint(ms, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    // --- Custom methods -------------------------------------------------

    /**
     * Gets the string bounds.
     * 
     * @param tl
     *            textlayout instance
     * @return the string bounds
     */
    protected Rectangle2D getStringBounds(TextLayout tl)
    {
        return new Rectangle2D.Float(0, -tl.getAscent(), tl.getAdvance(),
                tl.getAscent() + tl.getDescent() + tl.getLeading());
    }

    /**
     * Gets the text.
     * 
     * @return the text
     */
    protected String getText()
    {
        return getText(getStartOffset(), getEndOffset());
    }

    /**
     * Gets the text.
     * 
     * @param p0
     *            start position
     * @param p1
     *            end position
     * @return the text
     */
    protected String getText(int p0, int p1)
    {
        return getTextEx(p0, p1 - p0);
    }

    /**
     * Gets the text.
     * 
     * @param position
     *            the position, where to begin
     * @param len
     *            the length of text portion
     * @return the text
     */
    protected String getTextEx(int position, int len)
    {
        try {
            return getDocument().getText(position, len);
        } catch (BadLocationException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Sets the properties from the attributes.
     * 
     * @param attr
     *            the new properties from attributes
     */
    protected void setPropertiesFromAttributes(AttributeSet attr)
    {
        if (attr != null)
        {
            Font newFont = (Font) attr.getAttribute(Constants.ATTRIBUTE_FONT);
            if (newFont != null)
            {
                setFont(newFont);
            }
            else
            {
                // the font is the most important for us
                throw new IllegalStateException("Font can not be null !");
            }

            setForeground((Color) attr.getAttribute(Constants.ATTRIBUTE_FOREGROUND));
            setFontVariant((String) attr.getAttribute(Constants.ATTRIBUTE_FONT_VARIANT));

            @SuppressWarnings("unchecked")
            List<TextDecoration> attribute = (List<TextDecoration>) attr.getAttribute(Constants.ATTRIBUTE_TEXT_DECORATION);
            setTextDecoration(attribute);
        }
    }

    @Override
    public String toString()
    {
        return getText();
    }

    /**
     * Update properties.
     */
    public void updateProperties()
    {
        invalidateProperties();
    }

    private void invalidateCache()
    {
        refreshAttributes = true;
    }

    private void invalidateProperties()
    {
        refreshProperties = true;
    }

    private void invalidateTextLayout()
    {
        refreshTextLayout = true;
    }

    private void syncProperties()
    {
        if (refreshProperties)
        {
            setPropertiesFromAttributes(attributes);
            // now, properties == attributes, so no need to refresh something
            refreshProperties = false;
            refreshAttributes = false;
        }
    }

    /**
     * Sets the font.
     * 
     * @param newFont
     *            the new font
     */
    protected void setFont(Font newFont)
    {
        if (font == null || !font.equals(newFont))
        {
            font = new Font(newFont.getAttributes());
            invalidateCache();
            invalidateTextLayout();
        }
    }

    /**
     * Sets the foreground.
     * 
     * @param newColor
     *            the new foreground
     */
    protected void setForeground(Color newColor)
    {
        if (foreground == null || !foreground.equals(newColor))
        {
            foreground = new Color(newColor.getRGB());
            invalidateCache();
        }
    }

    /**
     * Sets the font variant.
     * 
     * @param newFontVariant
     *            the new font variant
     */
    protected void setFontVariant(FontVariant newFontVariant)
    {
        setFontVariant(newFontVariant.toString());
    }

    /**
     * Sets the font variant.
     * 
     * @param newFontVariant
     *            the new font variant
     */
    protected void setFontVariant(String newFontVariant)
    {
        if (fontVariant == null || !fontVariant.equals(newFontVariant))
        {
            FontVariant val[] = FontVariant.values();

            for (FontVariant aVal : val)
            {
                if (aVal.toString().equals(newFontVariant))
                {
                    fontVariant = newFontVariant;
                    invalidateCache();
                    return;
                }
            }

        }
    }

    /**
     * Sets the text decoration.
     * 
     * @param newTextDecoration
     *            the new text decoration
     */
    protected void setTextDecoration(List<TextDecoration> newTextDecoration)
    {
        if (textDecoration == null || !textDecoration.equals(newTextDecoration))
        {
            textDecoration = newTextDecoration;
            reflectTextDecoration(textDecoration);
            invalidateCache();
        }

    }

    private void reflectTextDecoration(List<TextDecoration> decor)
    {
        underline = false;
        strike = false;
        overline = false;

        for (TextDecoration aDecor : decor)
        {
            if (TextDecoration.UNDERLINE == aDecor)
            {
                underline = true;
            }
            else if (TextDecoration.LINE_THROUGH == aDecor)
            {
                strike = true;
            }
            else if (TextDecoration.OVERLINE == aDecor)
            {
                overline = true;
            }
        }
    }

    /**
     * Gets the text layout.
     * 
     * @return the text layout
     */
    protected TextLayout getTextLayout()
    {
        if (refreshTextLayout)
        {
            refreshTextLayout = false;
            layout = new TextLayout(getText(), getFont(), new FontRenderContext(transform, true, false));
        }

        return layout;
    }

    /**
     * Gets the font.
     * 
     * @return the font
     */
    public Font getFont()
    {
        syncProperties();
        return font;
    }

    /**
     * Gets the foreground.
     * 
     * @return the foreground
     */
    public Color getForeground()
    {
        syncProperties();
        return foreground;
    }

    /**
     * Gets the font variant.
     * 
     * @return the font variant
     */
    public String getFontVariant()
    {
        syncProperties();
        return fontVariant;
    }

    /**
     * Gets the text decoration.
     * 
     * @return the text decoration
     */
    public List<TextDecoration> getTextDecoration()
    {
        syncProperties();
        return textDecoration;
    }

    /**
     * converts a shape to rectangle
     * 
     * @param a
     *            the allocation shape
     * @return the rectangle
     */
    public static final Rectangle toRect(Shape a)
    {
        return a instanceof Rectangle ? (Rectangle) a : a.getBounds();
    }

}
