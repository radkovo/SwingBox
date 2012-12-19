/**
 * CSSBoxAnalyzer.java
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
import java.net.URL;
import java.nio.charset.Charset;

import org.fit.cssbox.io.DocumentSource;
import org.fit.cssbox.layout.ElementBox;
import org.xml.sax.InputSource;

/**
 * @author Peter Bielik
 * @author Radek Burget
 */
public interface CSSBoxAnalyzer
{

    /**
     * Analyzes content from InputSource and constructs a tree of Boxes, which
     * is further processed.
     * 
     * @param is
     *            the document source implementation.
     * @param dim
     *            the dimension of rendering area.
     * @return the tree of boxes.
     * @throws Exception
     *             some exception may be throwen during processing.
     */
    public ElementBox analyze(DocumentSource docSource, Dimension dim) throws Exception;

    /**
     * Updates the layout according to the new dimmension (the tree structure of
     * view objects has to be modified).
     * 
     * @param root
     *            the root box at which to perform update.
     * @param dim
     *            the new dimension of rendering area.
     * @return the box
     * @throws Exception
     *             some exception may be throwen during processing.
     */
    public ElementBox update(ElementBox root, Dimension dim) throws Exception;

    /**
     * Gets parsed W3C document. After calling
     * {@link CSSBoxAnalyzer#analyze(InputSource, URL, Dimension, Charset)
     * analyze} there should exist such representation.
     * 
     * @return parsed W3C document.
     * @see org.w3c.dom.Document
     * @see CSSBoxAnalyzer#analyze(InputSource, URL, Dimension, Charset)
     */
    public org.w3c.dom.Document getDocument();
    
    /**
     * Obtains the title from the DOM tree of the current document.
     * The {@link CSSBoxAnalyzer#analyze(InputSource, URL, Dimension, Charset)} method must be called before calling this.
     * @return The document title or <code>null</code> if there is not title defined.
     */
    public String getDocumentTitle();
    
}
