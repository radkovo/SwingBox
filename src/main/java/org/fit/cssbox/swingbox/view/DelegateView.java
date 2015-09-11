/**
 * DelegateView.java
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

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.CompositeView;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

import org.fit.cssbox.swingbox.SwingBoxDocument.DelegateElement;

/**
 * @author Peter Bielik
 * @version 1.0
 * @since 1.0 - 3.4.2011
 */
/**
 * Root view that acts as a gateway between the component and the View
 * hierarchy.
 */
public class DelegateView extends CompositeView
{
    private View view;
    private View parent;

    /**
     * Instantiates a new delegate view.
     * 
     * @param elem
     *            the element
     */
    public DelegateView(Element elem)
    {
        super(elem);
    }

    /**
     * Sets the view parent.
     * 
     * @param parent
     *            the parent view
     */
    @Override
    public void setParent(View parent)
    {

        if (parent == null && view != null) view.setParent(null);
        this.parent = parent;

        // if set new parent and has some element, try to load children
        // this element is a BranchElement ("collection"),
        // so we should have some LeafElements ("children")
        if ((parent != null) && (getElement() != null))
        {
            ViewFactory f = getViewFactory();
            loadChildren(f);
        }
    }

    @Override
    public View getParent()
    {
        return this.parent;
    }

    @Override
    protected void loadChildren(ViewFactory f)
    {
        if (f == null) { return; }
        Element e = getElement();

        if (e.getElementCount() > 0)
        {
            View[] added = new View[1];
            // load children (element) using ViewFactory (a new View)
            // elements should contain only 1 LeafElement
            added[0] = f.create(e.getElement(0));
            replace(0, 1, added);
        }
    }

    @Override
    public void replace(int offset, int length, View[] views)
    {
        // update parent reference on removed views
        if (offset < offset + length && views.length > 0)
        {
            /*
             * Actually, we remove old view only, if we have some view to add. We
             * can not stay empty (no child, no LeafElement-View), otherwise run
             * into troubles...
             */
            if (view != null)
            {
                view.setParent(null);
                view = null;
            }
        }

        if (views.length > 0)
        {
            View tmp = views[0];
            String name = getDelegateName();
            for (int i = 0; name != null && i < views.length; i++)
            {
                if (name.equals(views[i].getElement().getName()))
                {
                    if (tmp != views[i]) tmp.setParent(null);
                    tmp = views[i];
                }
                else
                {
                    if (tmp != views[i]) views[i].setParent(null);
                }
            }

            view = tmp;
            /*
             * setParent is guaranteed to be first method called after new
             * instance is created.
             */
            view.setParent(this);
        }

    }

    /**
     * Gets the name of delegate
     * 
     * @return the name. It may not be a name of a Class !
     */
    public String getDelegateName()
    {
        javax.swing.text.Element data = getElement();
        if (data instanceof DelegateElement) { return ((DelegateElement) data)
                .getDelegateName(); }
        return null;
    }

    /**
     * Fetches the attributes to use when rendering. At the root level there are
     * no attributes. If an attribute is resolved up the view hierarchy this is
     * the end of the line.
     */
    @Override
    public AttributeSet getAttributes()
    {
        return null;
    }

    /**
     * Determines the preferred span for this view along an axis.
     * 
     * @param axis
     *            may be either X_AXIS or Y_AXIS
     * @return the span the view would like to be rendered into. Typically the
     *         view is told to render into the span that is returned, although
     *         there is no guarantee. The parent may choose to resize or break
     *         the view.
     */
    @Override
    public float getPreferredSpan(int axis)
    {
        if (view != null) { return view.getPreferredSpan(axis); }
        return 10;
    }

    /**
     * Determines the minimum span for this view along an axis.
     * 
     * @param axis
     *            may be either X_AXIS or Y_AXIS
     * @return the span the view would like to be rendered into. Typically the
     *         view is told to render into the span that is returned, although
     *         there is no guarantee. The parent may choose to resize or break
     *         the view.
     */
    @Override
    public float getMinimumSpan(int axis)
    {
        if (view != null) { return view.getMinimumSpan(axis); }
        return 10;
    }

    /**
     * Determines the maximum span for this view along an axis.
     * 
     * @param axis
     *            may be either X_AXIS or Y_AXIS
     * @return the span the view would like to be rendered into. Typically the
     *         view is told to render into the span that is returned, although
     *         there is no guarantee. The parent may choose to resize or break
     *         the view.
     */
    @Override
    public float getMaximumSpan(int axis)
    {
        if (view != null) { return view.getMaximumSpan(axis); }
        return Integer.MAX_VALUE;
    }

    /**
     * Specifies that a preference has changed. Child views can call this on the
     * parent to indicate that the preference has changed. The root view routes
     * this to invalidate on the hosting component.
     * <p>
     * This can be called on a different thread from the event dispatching
     * thread and is basically unsafe to propagate into the component. To make
     * this safe, the operation is transferred over to the event dispatching
     * thread for completion. It is a design goal that all view methods be safe
     * to call without concern for concurrency, and this behavior helps make
     * that true.
     * 
     * @param child
     *            the child view
     * @param width
     *            true if the width preference has changed
     * @param height
     *            true if the height preference has changed
     */
    @Override
    public void preferenceChanged(View child, boolean width, boolean height)
    {
        if (parent != null)
        {
            parent.preferenceChanged(child, width, height);
        }
    }

    /**
     * Determines the desired alignment for this view along an axis.
     * 
     * @param axis
     *            may be either X_AXIS or Y_AXIS
     * @return the desired alignment, where 0.0 indicates the origin and 1.0 the
     *         full span away from the origin
     */
    @Override
    public float getAlignment(int axis)
    {
        if (view != null) { return view.getAlignment(axis); }
        return 0;
    }

    /**
     * Renders the view.
     * 
     * @param g
     *            the graphics context
     * @param allocation
     *            the region to render into
     */
    @Override
    public void paint(Graphics g, Shape allocation)
    {
        if (view != null)
        {
            view.paint(g, allocation);
        }
    }

    /**
     * Returns the number of views in this view. Since this view simply wraps
     * the root of the view hierarchy it has exactly one child.
     * 
     * @return the number of views
     * @see #getView
     */
    @Override
    public int getViewCount()
    {
        return view != null ? 1 : 0;
    }

    /**
     * Gets the n-th view in this container.
     * 
     * @param n
     *            the number of the view to get
     * @return the view
     */
    @Override
    public View getView(int n)
    {
        return view;
    }

    /**
     * Returns the child view index representing the given position in the
     * model. This is implemented to return the index of the only child.
     * 
     * @param pos
     *            the position >= 0
     * @return index of the view representing the given position, or -1 if no
     *         view represents that position
     * @since 1.3
     */
    @Override
    public int getViewIndex(int pos, Position.Bias b)
    {
        return view != null ? 0 : -1;
    }

    /**
     * Fetches the allocation for the given child view. This enables finding out
     * where various views are located, without assuming the views store their
     * location. This returns the given allocation since this view simply acts
     * as a gateway between the view hierarchy and the associated component.
     * 
     * @param index
     *            the index of the child
     * @param a
     *            the allocation to this view.
     * @return the allocation to the child
     */
    @Override
    public Shape getChildAllocation(int index, Shape a)
    {
        if (view != null) return view.getChildAllocation(index, a);
        return a;
    }

    /**
     * Provides a mapping from the document model coordinate space to the
     * coordinate space of the view mapped to it.
     * 
     * @param pos
     *            the position to convert
     * @param a
     *            the allocated region to render into
     * @return the bounding box of the given position
     */
    @Override
    public Shape modelToView(int pos, Shape a, Position.Bias b)
            throws BadLocationException
    {
        if (view != null) { return view.modelToView(pos, a, b); }
        return null;
    }

    /**
     * Provides a mapping from the document model coordinate space to the
     * coordinate space of the view mapped to it.
     * 
     * @param p0
     *            the position to convert >= 0
     * @param b0
     *            the bias toward the previous character or the next character
     *            represented by p0, in case the position is a boundary of two
     *            views.
     * @param p1
     *            the position to convert >= 0
     * @param b1
     *            the bias toward the previous character or the next character
     *            represented by p1, in case the position is a boundary of two
     *            views.
     * @param a
     *            the allocated region to render into
     * @return the bounding box of the given position is returned
     * @exception BadLocationException
     *                if the given position does not represent a valid location
     *                in the associated document
     * @exception IllegalArgumentException
     *                for an invalid bias argument
     * @see View#viewToModel
     */
    @Override
    public Shape modelToView(int p0, Position.Bias b0, int p1,
            Position.Bias b1, Shape a) throws BadLocationException
    {
        if (view != null) { return view.modelToView(p0, b0, p1, b1, a); }
        return null;
    }

    /**
     * Provides a mapping from the view coordinate space to the logical
     * coordinate space of the model.
     * 
     * @param x
     *            x coordinate of the view location to convert
     * @param y
     *            y coordinate of the view location to convert
     * @param a
     *            the allocated region to render into
     * @return the location within the model that best represents the given
     *         point in the view
     */
    @Override
    public int viewToModel(float x, float y, Shape a, Position.Bias[] bias)
    {
        if (view != null)
        {
            int retValue = view.viewToModel(x, y, a, bias);
            return retValue;
        }
        return -1;
    }

    /**
     * Provides a way to determine the next visually represented model location
     * that one might place a caret. Some views may not be visible, they might
     * not be in the same order found in the model, or they just might not allow
     * access to some of the locations in the model.
     * 
     * @param pos
     *            the position to convert >= 0
     * @param a
     *            the allocated region to render into
     * @param direction
     *            the direction from the current position that can be thought of
     *            as the arrow keys typically found on a keyboard. This may be
     *            SwingConstants.WEST, SwingConstants.EAST,
     *            SwingConstants.NORTH, or SwingConstants.SOUTH.
     * @return the location within the model that best represents the next
     *         location visual position.
     * @exception BadLocationException
     * @exception IllegalArgumentException
     *                for an invalid direction
     */
    @Override
    public int getNextVisualPositionFrom(int pos, Position.Bias b, Shape a,
            int direction, Position.Bias[] biasRet) throws BadLocationException
    {
        if (view != null)
        {
            int nextPos = view.getNextVisualPositionFrom(pos, b, a, direction,
                    biasRet);
            if (nextPos != -1)
            {
                pos = nextPos;
            }
            else
            {
                biasRet[0] = b;
            }
        }
        return pos;
    }

    /**
     * Returns the document model underlying the view.
     * 
     * @return the model
     */
    @Override
    public Document getDocument()
    {
        return parent.getDocument();
    }

    /**
     * Returns the starting offset into the model for this view.
     * 
     * @return the starting offset
     */
    @Override
    public int getStartOffset()
    {
        if (view != null) { return view.getStartOffset(); }
        return getElement().getStartOffset();
    }

    /**
     * Returns the ending offset into the model for this view.
     * 
     * @return the ending offset
     */
    @Override
    public int getEndOffset()
    {
        if (view != null) { return view.getEndOffset(); }
        return getElement().getEndOffset();
    }

    /**
     * Determines the resizability of the view along the given axis. A value of
     * 0 or less is not resizable.
     * 
     * @param axis
     *            may be either X_AXIS or Y_AXIS
     * @return the weight
     */
    @Override
    public int getResizeWeight(int axis)
    {
        if (view != null) { return view.getResizeWeight(axis); }
        return 0;
    }

    /**
     * Sets the view size.
     * 
     * @param width
     *            the width
     * @param height
     *            the height
     */
    @Override
    public void setSize(float width, float height)
    {
        if (view != null)
        {
            view.setSize(width, height);
        }
    }

    @Override
    public String getToolTipText(float x, float y, Shape allocation)
    {
        if (view != null)
            return view.getToolTipText(x, y, allocation);
        else
            return super.getToolTipText(x, y, allocation);
    }   
    
    @Override
    protected boolean isBefore(int x, int y, Rectangle alloc)
    {
        return false;
    }

    @Override
    protected boolean isAfter(int x, int y, Rectangle alloc)
    {
        return false;
    }

    @Override
    protected View getViewAtPoint(int x, int y, Rectangle alloc)
    {
        return view;
    }

    @Override
    protected void childAllocation(int index, Rectangle a)
    {
        //nothing
    }
    
}
