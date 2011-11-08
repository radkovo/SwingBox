/**
 *
 */
package org.fit.cssbox.swingbox.util;

import java.awt.Rectangle;

import org.fit.cssbox.layout.Box;
import org.fit.cssbox.layout.ElementBox;

/**
 * @author Peter Bielik
 * @version 1.0
 * @since 1.0 - 15.2.2011
 */
public class BoxHelper {
    //		javax.swing.text.GlyphPainter1 a;
    //		javax.swing.text.GlyphPainter2 b;
    //    TextLayout layout;

    private BoxHelper() {
	//this is a library
    }

    /**
     * Locates a box from its position
     */
    public static Box locateBox(Box root, int x, int y) {
	//box.getViewport();
	if (root == null) throw new IllegalArgumentException ("The argument 'root' can not be null");
	Box tmp;
	if (root.getAbsoluteBounds().contains(x, y)) {
	    tmp = root;
	}else if (root.getViewport() != null && root.getViewport().getAbsoluteBounds().contains(x, y)) {
	    tmp = root.getViewport();
	} else {
	    return null;
	}

	return locateBoxEx(tmp, x, y);
    }

    /**
     * @param tmp
     * @param x
     * @param y
     * @return
     */
    public static Box locateBoxEx(Box root, int x, int y) {
	Box found = null;
	ElementBox tmp;
	Box inside;

	if (root.getAbsoluteBounds().contains(x, y)) {
	    found = root;
	    if (root instanceof ElementBox) {
		tmp = (ElementBox) root;

		//find if there is something smallest that fits among the child boxes
		for (int i = tmp.getStartChild(); i < tmp.getEndChild(); i++)
		{
		    inside = locateBoxEx(tmp.getSubBox(i), x, y);
		    if (inside != null)
		    {
			Rectangle fbox = found.getAbsoluteBounds();
			Rectangle ibox = inside.getAbsoluteBounds();
			if (ibox.width * ibox.height < fbox.width * fbox.height)
			    found = inside;
		    }
		}
	    }
	}

	return found;
    }
}
