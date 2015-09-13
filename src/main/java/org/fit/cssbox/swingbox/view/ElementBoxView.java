/**
 * ElementBoxView.java
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Map;
import java.util.Vector;

import javax.swing.SizeRequirements;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.CompositeView;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

import org.fit.cssbox.layout.BlockBox;
import org.fit.cssbox.layout.Box;
import org.fit.cssbox.layout.ElementBox;
import org.fit.cssbox.swingbox.util.Anchor;
import org.fit.cssbox.swingbox.util.Constants;

/**
 * @author Peter Bielik
 * @author Radek Burget
 */
public class ElementBoxView extends CompositeView implements CSSBoxView
{
    protected ElementBox box;
    protected Anchor anchor;
    protected int order;

    /** the cache of attributes */
    private AttributeSet attributes;
    /** decides whether to construct a cache from current working properties */
    private boolean refreshAttributes;
    private boolean refreshProperties;
    private Dimension oldDimension;

    private int majorAxis;
    private boolean majorAllocValid;
    private boolean minorAllocValid;
    private boolean majorReqValid;
    private boolean minorReqValid;
    private SizeRequirements majorRequest;
    private SizeRequirements minorRequest;

    /**
     * @param elem
     */
    public ElementBoxView(Element elem)
    {
        // Y axis as default
        super(elem);
        majorAxis = Y_AXIS;
        AttributeSet tmpAttr = elem.getAttributes();
        Object obj = tmpAttr.getAttribute(Constants.ATTRIBUTE_BOX_REFERENCE);
        Integer i = (Integer) tmpAttr.getAttribute(Constants.ATTRIBUTE_DRAWING_ORDER);
        order = (i == null) ? -1 : i;

        if (obj != null && obj instanceof ElementBox)
        {
            box = (ElementBox) obj;
            if (box instanceof BlockBox)
            {
                if (((BlockBox) box).isFloating())
                {
                    majorAxis = X_AXIS;
                }
            }
        }
        else
        {
            throw new IllegalArgumentException("Box reference is null or not an instance of ElementBox");
        }

        obj = tmpAttr.getAttribute(Constants.ATTRIBUTE_ANCHOR_REFERENCE);
        if (obj != null && obj instanceof Anchor)
        {
            anchor = (Anchor) obj;
        }
        else
        {
            throw new IllegalArgumentException("Anchor reference is null or not an instance of Anchor");
        }

        oldDimension = new Dimension();

        loadElementAttributes();
    }

    private void loadElementAttributes()
    {
        org.w3c.dom.Element elem = Anchor.findAnchorElement(box.getElement());
        Map<String, String> elementAttributes = anchor.getProperties();

        if (elem != null)
        {
            anchor.setActive(true);
            elementAttributes.put(Constants.ELEMENT_A_ATTRIBUTE_HREF, elem.getAttribute("href"));
            elementAttributes.put(Constants.ELEMENT_A_ATTRIBUTE_NAME, elem.getAttribute("name"));
            elementAttributes.put(Constants.ELEMENT_A_ATTRIBUTE_TITLE, elem.getAttribute("title"));
            String target = elem.getAttribute("target");
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

    @Override
    public String toString()
    {
        String s = getClass().getSimpleName();
        s += " " + order;
        if (box != null)
            s = s + ": " + box;
        return s;
    }

    @Override
    public int getDrawingOrder()
    {
        return order;
    }
    
    /**
     * Fetches the tile axis property. This is the axis along which the child
     * views are tiled.
     * 
     * @return the major axis of the box, either <code>View.X_AXIS</code> or
     *         <code>View.Y_AXIS</code>
     * 
     */
    public int getAxis()
    {
        return majorAxis;
    }

    /**
     * Sets the tile axis property. This is the axis along which the child views
     * are tiled.
     * 
     * @param axis
     *            either <code>View.X_AXIS</code> or <code>View.Y_AXIS</code>
     * 
     */
    public void setAxis(int axis)
    {
        boolean axisChanged = (axis != majorAxis);
        majorAxis = axis;
        if (axisChanged)
        {
            preferenceChanged(null, true, true);
        }
    }

    /**
     * Invalidates the layout along an axis. This happens automatically if the
     * preferences have changed for any of the child views. In some cases the
     * layout may need to be recalculated when the preferences have not changed.
     * The layout can be marked as invalid by calling this method. The layout
     * will be updated the next time the <code>setSize</code> method is called
     * on this view (typically in paint).
     * 
     * @param axis
     *            either <code>View.X_AXIS</code> or <code>View.Y_AXIS</code>
     * 
     */
    public void layoutChanged(int axis)
    {
        if (axis == majorAxis)
        {
            majorAllocValid = false;
        }
        else
        {
            minorAllocValid = false;
        }
    }

    /**
     * Determines if the layout is valid along the given axis.
     * 
     * @param axis
     *            either <code>View.X_AXIS</code> or <code>View.Y_AXIS</code>
     * 
     */
    protected boolean isLayoutValid(int axis)
    {
        if (axis == majorAxis)
        {
            return majorAllocValid;
        }
        else
        {
            return minorAllocValid;
        }
    }

    /**
     * Establishes the parent view for this view. This is guaranteed to be
     * called before any other methods if the parent view is functioning
     * properly.
     * <p>
     * This is implemented to forward to the superclass as well as call the
     * setPropertiesFromAttributes() method to set the paragraph properties from
     * the css attributes. The call is made at this time to ensure the ability
     * to resolve upward through the parents view attributes.
     * 
     * Establishes the parent view for this view. This is guaranteed to be
     * called before any other methods if the parent view is functioning
     * properly. This is also the last method called, since it is called to
     * indicate the view has been removed from the hierarchy as well. When this
     * method is called to set the parent to null, this method does the same for
     * each of its children, propogating the notification that they have been
     * disconnected from the view tree. If this is reimplemented,
     * <code>super.setParent()</code> should be called.
     * 
     * @param parent
     *            the new parent, or <code>null</code> if the view is being
     *            removed from a parent
     */
    @Override
    public void setParent(View parent)
    {
        super.setParent(parent);
        if (parent != null)
        {
            setPropertiesFromAttributes(getElement().getAttributes());
            refreshAttributes = true;
            refreshProperties = false;
        }
        else
        {
            // we are removed from a hierarchy
            attributes = null;
            box = null;
            refreshAttributes = true;
            refreshProperties = false;
        }
    }

    /**
     * Sets the properties from attributes (working variables).
     * 
     * @param attributes
     *            the new properties
     */
    protected void setPropertiesFromAttributes(AttributeSet attributes)
    {

    }

    protected Anchor getAnchor()
    {
        return this.anchor;
    }

    @Override
    public void replace(int offset, int length, View[] views)
    {
        super.replace(offset, length, views);
        // System.err.println("Replace : " + views.length + " view count  " +
        // getViewCount());

        majorReqValid = false;
        majorAllocValid = false;
        minorReqValid = false;
        minorAllocValid = false;
    }

    @Override
    protected void forwardUpdate(DocumentEvent.ElementChange ec,
            DocumentEvent e, Shape a, ViewFactory f)
    {
        boolean wasValid = isLayoutValid(majorAxis);
        super.forwardUpdate(ec, e, a, f);

        // determine if a repaint is needed
        if (wasValid && (!isLayoutValid(majorAxis)))
        {
            // Repaint is needed because one of the tiled children
            // have changed their span along the major axis. If there
            // is a hosting component and an allocated shape we repaint.
            Component c = getContainer();
            if ((a != null) && (c != null))
            {
                Rectangle alloc = getInsideAllocation(a);
                c.repaint(alloc.x, alloc.y, alloc.width, alloc.height);
            }
        }
    }

    @Override
    public void preferenceChanged(View child, boolean width, boolean height)
    {
        boolean majorChanged = (majorAxis == X_AXIS) ? width : height;
        boolean minorChanged = (majorAxis == X_AXIS) ? height : width;
        if (majorChanged)
        {
            majorReqValid = false;
            majorAllocValid = false;
        }
        if (minorChanged)
        {
            minorReqValid = false;
            minorAllocValid = false;
        }
        super.preferenceChanged(child, width, height);
    }

    @Override
    public int getResizeWeight(int axis)
    {
        // checkRequests(axis);
        if (axis == majorAxis)
        {
            if ((majorRequest.preferred != majorRequest.minimum)
                    || (majorRequest.preferred != majorRequest.maximum)) { return 1; }
        }
        else
        {
            if ((minorRequest.preferred != minorRequest.minimum)
                    || (minorRequest.preferred != minorRequest.maximum)) { return 1; }
        }
        return 0;
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

    protected SimpleAttributeSet createAttributes()
    {
        SimpleAttributeSet res = new SimpleAttributeSet();
        res.addAttribute(Constants.ATTRIBUTE_ANCHOR_REFERENCE, anchor);
        res.addAttribute(Constants.ATTRIBUTE_BOX_REFERENCE, box);
        return res;
    }

    private SizeRequirements getRequirements(int axis, SizeRequirements r)
    {
        if (r == null)
        {
            r = new SizeRequirements();
        }
        r.alignment = 0f; // 0.5f;
        if (axis == X_AXIS)
        {
            r.maximum = r.minimum = r.preferred = box.getWidth();// box.getContentWidth();
        }
        else
        {
            r.maximum = r.minimum = r.preferred = box.getHeight();// box.getContentHeight();
        }

        return r;
    }

    public void updateProperties()
    {
        invalidateProperties(); // we are lazy :)
    }

    protected void invalidateCache()
    {
        refreshAttributes = true;
    }

    protected void invalidateProperties()
    {
        refreshProperties = true;
    }

    @Override
    public float getAlignment(int axis)
    {
        checkRequests(axis);
        if (axis == majorAxis)
        {
            return majorRequest.alignment;
        }
        else
        {
            return minorRequest.alignment;
        }
    }

    @Override
    public void paint(Graphics graphics, Shape allocation)
    {
        //System.out.println("Paint: " + box + " in " + allocation);
        Graphics2D g;
        if (graphics instanceof Graphics2D)
            g = (Graphics2D) graphics;
        else
            throw new RuntimeException("Unknown graphics environment, java.awt.Graphics2D required !");

        Rectangle clip = toRect(g.getClip());
        
        //box.getVisualContext().updateGraphics(g);
        //box.drawBackground(g);

        Rectangle alloc = toRect(allocation);
        int count = getViewCount();
        for (int i = 0; i < count; i++)
        {
            Rectangle bounds = new Rectangle(alloc);
            childAllocation(i, bounds);
            if (clip.intersects(bounds))
                getView(i).paint(g, allocation);
        }
    }

//    /**
//     * renders given child, possible to override and customize.
//     * 
//     * @param g
//     *            graphics context
//     * @param v
//     *            the View
//     * @param rect
//     *            an allocation
//     * @param index
//     *            the index of view
//     */
    /*protected void paintChild(Graphics g, View v, Shape rect, int index)
    {
        // System.err.println("Painting " + v);
        v.paint(g, rect);
    }*/

    @Override
    public Shape getChildAllocation(int index, Shape a)
    {
        // zvyraznovanie !
        if (a != null /* && isAllocationValid() */)
        {
            Box tmpBox = getBox(getView(index));

            Rectangle alloc = (a instanceof Rectangle) ? (Rectangle) a : a.getBounds();
            //alloc.setBounds(tmpBox.getAbsoluteBounds());
            alloc.setBounds(getCompleteBoxAllocation(tmpBox));
            return alloc;
        }
        return null;
    }

    @Override
    protected void childAllocation(int index, Rectangle alloc)
    {
        // set allocation (== the bounds) for a view
        //alloc.setBounds(getBox(getView(index)).getAbsoluteBounds());
        alloc.setBounds(getCompleteBoxAllocation(getBox(getView(index))));
    }

    /**
     * Obtains the allocation of a box together with all its child boxes.
     * @param b the box
     * @return the smallest rectangle containing the box and all its child boxes
     */
    private Rectangle getCompleteBoxAllocation(Box b)
    {
        Rectangle ret = b.getAbsoluteBounds();
        if (b instanceof ElementBox)
        {
            ElementBox eb = (ElementBox) b;
            for (int i = eb.getStartChild(); i < eb.getEndChild(); i++)
            {
                Box child = eb.getSubBox(i);
                if (child.isVisible())
                {
                    Rectangle r = getCompleteBoxAllocation(child);
                    ret.add(r);
                }
            }
        }
        return ret.intersection(b.getClipBlock().getClippedContentBounds());
    }
    
    @Override
    public float getPreferredSpan(int axis)
    {
        checkRequests(axis);
        float marginSpan = (axis == X_AXIS) ? getLeftInset() + getRightInset()
                : getTopInset() + getBottomInset();
        if (axis == majorAxis)
        {
            return ((float) majorRequest.preferred) + marginSpan;
        }
        else
        {
            return ((float) minorRequest.preferred) + marginSpan;
        }

    }

    @Override
    public float getMinimumSpan(int axis)
    {
        checkRequests(axis);
        float marginSpan = (axis == X_AXIS) ? getLeftInset() + getRightInset()
                : getTopInset() + getBottomInset();
        if (axis == majorAxis)
        {
            return ((float) majorRequest.minimum) + marginSpan;
        }
        else
        {
            return ((float) minorRequest.minimum) + marginSpan;
        }
    }

    @Override
    public float getMaximumSpan(int axis)
    {
        checkRequests(axis);
        float marginSpan = (axis == X_AXIS) ? getLeftInset() + getRightInset()
                : getTopInset() + getBottomInset();
        if (axis == majorAxis)
        {
            return ((float) majorRequest.maximum) + marginSpan;
        }
        else
        {
            return ((float) minorRequest.maximum) + marginSpan;
        }
    }

    // --- local methods ----------------------------------------------------

    /**
     * Are the allocations for the children still valid?
     * 
     * @return true if allocations still valid
     */
    protected boolean isAllocationValid()
    {
        return (majorAllocValid && minorAllocValid);
    }

    /**
     * Determines if a point falls before an allocated region.
     * 
     * @param x
     *            the X coordinate >= 0
     * @param y
     *            the Y coordinate >= 0
     * @param innerAlloc
     *            the allocated region; this is the area inside of the insets
     * @return true if the point lies before the region else false
     */
    @Override
    protected boolean isBefore(int x, int y, Rectangle innerAlloc)
    {
        // System.err.println("isBefore: " + innerAlloc + " my bounds " +
        // box.getAbsoluteBounds());
        // System.err.println("XY: " + x + " : " + y);
        innerAlloc.setBounds(box.getAbsoluteBounds());
        if (majorAxis == View.X_AXIS)
        {
            return (x < innerAlloc.x);
        }
        else
        {
            return (y < innerAlloc.y);
        }
    }

    /**
     * Determines if a point falls after an allocated region.
     * 
     * @param x
     *            the X coordinate >= 0
     * @param y
     *            the Y coordinate >= 0
     * @param innerAlloc
     *            the allocated region; this is the area inside of the insets
     * @return true if the point lies after the region else false
     */
    @Override
    protected boolean isAfter(int x, int y, Rectangle innerAlloc)
    {
        // System.err.println("isAfter: " + innerAlloc + " my bounds " +
        // box.getAbsoluteBounds());
        // System.err.println("XY: " + x + " : " + y);
        innerAlloc.setBounds(box.getAbsoluteBounds());
        if (majorAxis == View.X_AXIS)
        {
            return (x > (innerAlloc.width + innerAlloc.x));
        }
        else
        {
            return (y > (innerAlloc.height + innerAlloc.y));
        }
    }

    @Override
    protected View getViewAtPoint(int x, int y, Rectangle alloc)
    {
        View retv = null;
        int retorder = -1;
        
        Vector<View> leaves = new Vector<View>();
        findLeaves(this, leaves);
        
        for (View leaf : leaves)
        {
            View v = leaf;
            if (v instanceof CSSBoxView)
            {
                Box b = getBox(v);
                if (locateBox(b, x, y) != null)
                {
                    while (v.getParent() != null && v.getParent() != this)
                        v = v.getParent();
                    
                    //System.out.println("Candidate: " + v + " (leaf: " + leaf + ")");
                    int o = ((CSSBoxView) v).getDrawingOrder();
                    if (retv == null || o >= retorder) //next box is drawn after the current one
                    {
                        retv = v;
                        retorder = order;
                        alloc.setBounds(getCompleteBoxAllocation(b));
                    }
                }
            }
            
        }
        //System.out.println("At " + x + ":" + y + " found " + retv);
        return retv;
    }

    private void findLeaves(View root, Vector<View> leaves)
    {
        if (root instanceof ElementBoxView)
        {
            ElementBoxView ev = (ElementBoxView) root;
            if (ev.getViewCount() == 0)
                leaves.add(ev);
            else
            {
                for (int i = 0; i < ev.getViewCount(); i++)
                    findLeaves(ev.getView(i), leaves);
            }
        }
        else
            leaves.add(root);
    }
    
    /**
     * Locates a box from its position
     */
    private Box locateBox(Box root, int x, int y)
    {
        if (root.isVisible())
        {
            Box found = null;
            Rectangle bounds = root.getAbsoluteContentBounds().intersection(root.getClipBlock().getClippedContentBounds());
            if (bounds.contains(x, y))
                found = root;
            
            //find if there is something smallest that fits among the child boxes
            if (root instanceof ElementBox)
            {
                ElementBox eb = (ElementBox) root;
                for (int i = eb.getStartChild(); i < eb.getEndChild(); i++)
                {
                    Box inside = locateBox(((ElementBox) root).getSubBox(i), x, y);
                    if (inside != null)
                    {
                        if (found == null)
                            found = inside;
                        else
                        {
                            if (inside.getAbsoluteBounds().width * inside.getAbsoluteBounds().height < found.getAbsoluteBounds().width * found.getAbsoluteBounds().height)
                                found = inside;
                        }
                    }
                }
            }
            return found;
        }
        else
            return null;
    }
    
    @Override
    public void setSize(float width, float height)
    {
        if (oldDimension.width != width)
        {
            oldDimension.setSize((int) width, oldDimension.height);
            layoutChanged(X_AXIS);
        }
        if (oldDimension.height != height)
        {
            oldDimension.setSize(oldDimension.width, (int) height);
            layoutChanged(Y_AXIS);
        }

        /*
         * in current implementation we do not support propagation do childs,
         * because, if there is a change, world is rebuilt..
         */
    }

    /**
     * Validates layout.
     * 
     * @param dim
     *            the new dimension of valid area. Validation run against this
     * @return true, if layout during validation process has been changed.
     */
    protected boolean validateLayout(Dimension dim)
    {
        if (majorAxis == X_AXIS)
        {
            majorRequest = getRequirements(X_AXIS, majorRequest);
            minorRequest = getRequirements(Y_AXIS, minorRequest);
            oldDimension.setSize(majorRequest.preferred, minorRequest.preferred);
        }
        else
        {
            majorRequest = getRequirements(Y_AXIS, majorRequest);
            minorRequest = getRequirements(X_AXIS, minorRequest);
            oldDimension.setSize(minorRequest.preferred, majorRequest.preferred);
        }

        majorReqValid = true;
        minorReqValid = true;
        majorAllocValid = true;
        minorAllocValid = true;
        return false;
    }

    private void checkRequests(int axis)
    {
        if ((axis != X_AXIS) && (axis != Y_AXIS)) { throw new IllegalArgumentException(
                "Invalid axis: " + axis); }
        if (axis == majorAxis)
        {
            if (!majorReqValid)
            {
                majorRequest = getRequirements(axis, majorRequest);
                majorReqValid = true;
            }
        }
        else if (!minorReqValid)
        {
            minorRequest = getRequirements(axis, minorRequest);
            minorReqValid = true;
        }
    }

    /**
     * Converts an Shape to instance of rectangle
     * 
     * @param a
     *            the shape
     * @return the rectangle
     */
    public static final Rectangle toRect(Shape a)
    {
        return a instanceof Rectangle ? (Rectangle) a : a.getBounds();
    }

    /**
     * Calculates intersection of two rectangles
     * 
     * @param src1
     *            the src1
     * @param src2
     *            the src2
     * @param dest
     *            the dest
     * @return true, if there is non empty intersection
     */
    public static final boolean intersection(Rectangle src1, Rectangle src2, Rectangle dest)
    {
        int x1 = Math.max(src1.x, src2.x);
        int y1 = Math.max(src1.y, src2.y);
        int x2 = Math.min(src1.x + src1.width, src2.x + src2.width);
        int y2 = Math.min(src1.y + src1.height, src2.y + src2.height);
        dest.setBounds(x1, y1, x2 - x1, y2 - y1);

        if (dest.width <= 0 || dest.height <= 0)
            return false;

        return true; // non-empty intersection
    }

    /**
     * Gets the box reference from properties
     * 
     * @param v
     *            the view, instance of CSSBoxView.
     * @return the box set in properties.
     */
    public static final Box getBox(CSSBoxView v)
    {

        try
        {
            AttributeSet attr = v.getAttributes();
            return (Box) attr.getAttribute(Constants.ATTRIBUTE_BOX_REFERENCE);
        } catch (Exception e)
        {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Gets the box reference from properties.
     * 
     * @param v
     *            just a view.
     * @return the box set in properties, if there is one.
     */
    public static final Box getBox(View v)
    {
        if (v instanceof CSSBoxView) return getBox((CSSBoxView) v);

        AttributeSet attr = v.getAttributes();
        if (attr == null)
        {
            throw new NullPointerException("AttributeSet of " + v.getClass().getName() + "@"
                                + Integer.toHexString(v.hashCode()) + " is set to NULL.");
        }
        Object obj = attr.getAttribute(Constants.ATTRIBUTE_BOX_REFERENCE);
        if (obj != null && obj instanceof Box)
        {
            return (Box) obj;
        }
        else
        {
            throw new IllegalArgumentException("Box reference in attributes is not an instance of a Box.");
        }

    }

}
