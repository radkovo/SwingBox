/**
 * ViewportView.java
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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JViewport;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.View;

import org.fit.cssbox.swingbox.SwingBoxDocument;
import org.fit.cssbox.swingbox.SwingBoxEditorKit;

/**
 * The Class ViewportView.
 * 
 * @author Peter Bielik
 * @version 1.0
 * @since 1.0 - 22.2.2011
 */
public class ViewportView extends BlockBoxView implements ComponentListener
{
    private Reference<JViewport> cachedViewPort;
    private JEditorPane editor;
    private Dimension tmpDimension;

    /**
     * Instantiates a new viewport view.
     * 
     * @param elem
     *            the elem
     */
    public ViewportView(Element elem)
    {
        super(elem);
        tmpDimension = new Dimension();
    }

    @Override
    protected boolean validateLayout(Dimension dim)
    {
        /*
         * if a new layout is created, everything is built from scratch and
         * valid.. if did not succeed, then world has not changed and mark it is
         * valid
         */
        boolean result = checkSize(dim);
        if (!result) super.validateLayout(dim);

        return result;
    }

    @Override
    public void paint(Graphics graphics, Shape allocation)
    {
        Graphics2D g;
        if (graphics instanceof Graphics2D)
            g = (Graphics2D) graphics;
        else
            throw new RuntimeException("Unknown graphics enviroment, java.awt.Graphics2D required !");
        
        box.getVisualContext().updateGraphics(g);
        box.drawBackground(g);
        super.paint(graphics, allocation);
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }

    private void hook()
    {
        Container container = getContainer();
        Container parentContainer;

        if (container != null && (container instanceof javax.swing.JEditorPane)
                && (parentContainer = container.getParent()) != null
                && (parentContainer instanceof javax.swing.JViewport))
        {

            editor = (JEditorPane) container;

            // our parent is a JScrollPane (JViewPort)
            JViewport viewPort = (JViewport) parentContainer;
            Object cachedObject;

            if (cachedViewPort != null)
            {
                if ((cachedObject = cachedViewPort.get()) != null)
                {
                    if (cachedObject != viewPort)
                    {
                        // parent is different from previous, remove listener
                        ((JComponent) cachedObject)
                                .removeComponentListener(this);
                    }
                }
                else
                {
                    // parent has been garbage-collected
                    cachedViewPort = null;
                }
            }

            if (cachedViewPort == null)
            {
                // hook it
                viewPort.addComponentListener(this);
                cachedViewPort = new WeakReference<JViewport>(viewPort);
            }

            // System.err.println("Hooked at : " + viewPort.getExtentSize());
            // checkSize(viewPort.getExtentSize());

        }
        else
        {
            unhook();
        }
    }

    private void unhook()
    {
        if (cachedViewPort != null)
        {
            Object cachedObject;
            if ((cachedObject = cachedViewPort.get()) != null)
            {
                ((JComponent) cachedObject).removeComponentListener(this);
            }
            cachedViewPort = null;
        }
    }

    @Override
    public void setParent(View parent)
    {
        // do what we need
        super.setParent(parent);
        if (parent == null)
        {
            unhook();
            editor = null;
        }
        else
        {
            hook();
        }
    }

    @Override
    public void componentResized(ComponentEvent e)
    {
        if ((e.getSource() instanceof JViewport))
        {
            checkSize(((JViewport) e.getSource()).getSize());
        }
    }

    @Override
    public void componentHidden(ComponentEvent e)
    {
    }

    @Override
    public void componentMoved(ComponentEvent e)
    {
    }

    @Override
    public void componentShown(ComponentEvent e)
    {
    }

    private boolean checkSize(Dimension extentSize)
    {
        if (extentSize.width == 0 || extentSize.height == 0)
            return false;
        
        int diffx = Math.abs(extentSize.width - box.getViewport().getBounds().width);
        int diffy = Math.abs(extentSize.height - box.getViewport().getBounds().height);
        if (diffx > 20 || diffy > 20) //prevent loops caused by displaying scrollbars and too small changes.
        {                             //TODO this is a provisional solution
            Document doc = getDocument();
            tmpDimension.setSize(extentSize);

            if (doc instanceof SwingBoxDocument)
            { 
                return doLayout((SwingBoxDocument) doc, tmpDimension);
            }
        }

        return false;
    }

    private boolean doLayout(SwingBoxDocument doc, Dimension dim)
    {
        try
        {
            EditorKit kit = editor.getEditorKit();

            if (kit instanceof SwingBoxEditorKit)
            {
                ((SwingBoxEditorKit) kit).update(doc, box.getViewport(), dim);
            }

            preferenceChanged(null, true, true);
            return true;
        } 
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return false;
    }
    
}
