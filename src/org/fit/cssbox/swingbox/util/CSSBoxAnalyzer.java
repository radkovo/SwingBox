
package org.fit.cssbox.swingbox.util;

import java.awt.Dimension;
import java.net.URL;
import java.nio.charset.Charset;

import org.fit.cssbox.layout.ElementBox;
import org.xml.sax.InputSource;

/**
 * @author Peter Bielik
 * @version 1.0
 * @since 1.0 - 29.1.2011
 */
public interface CSSBoxAnalyzer
{

    /**
     * Analyzes content from InputSource and constructs a tree of Boxes, which
     * is further processed.
     * 
     * @param is
     *            the InputSource object, which encapsulates InputStream or
     *            Reader. This is the source of data.
     * @param url
     *            the URL where the data comes from.
     * @param dim
     *            the dimension of rendering area.
     * @param charset
     *            the charset used to encode the character data.
     * @return the tree of boxes.
     * @throws Exception
     *             some exception may be throwen during processing.
     */
    public ElementBox analyze(InputSource is, URL url, Dimension dim,
            Charset charset) throws Exception;

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
}
