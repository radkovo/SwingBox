/**
 * DefaultAnalyzer.java
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
import java.awt.image.BufferedImage;
import org.fit.cssbox.css.CSSNorm;
import org.fit.cssbox.css.DOMAnalyzer;
import org.fit.cssbox.io.DOMSource;
import org.fit.cssbox.io.DefaultDOMSource;
import org.fit.cssbox.io.DocumentSource;
import org.fit.cssbox.layout.BrowserCanvas;
import org.fit.cssbox.layout.ElementBox;
import org.w3c.dom.NodeList;

/**
 * This is customizable default implementation of CSSBoxAnalyzer.
 * 
 * @author Peter Bielik
 * @author Radek Burget
 */
public class DefaultAnalyzer implements CSSBoxAnalyzer
{
    protected org.w3c.dom.Document w3cdoc;
    protected BrowserCanvas canvas;

    @Override
    public ElementBox analyze(DocumentSource docSource, Dimension dim)
            throws Exception
    {
        DOMSource parser = new DefaultDOMSource(docSource);
        w3cdoc = parser.parse();

        // Create the CSS analyzer
        DOMAnalyzer da = new DOMAnalyzer(w3cdoc, docSource.getURL());
        da.attributesToStyles();
        da.addStyleSheet(null, CSSNorm.stdStyleSheet(), DOMAnalyzer.Origin.AGENT);
        da.addStyleSheet(null, CSSNorm.userStyleSheet(), DOMAnalyzer.Origin.AGENT);
        da.getStyleSheets();
        
        BufferedImage tmpImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        canvas = new BrowserCanvas(da.getRoot(), da, docSource.getURL());
        canvas.setImage(tmpImg);
        canvas.getConfig().setLoadImages(true);
        canvas.getConfig().setLoadBackgroundImages(true);
        canvas.createLayout(dim);

        return canvas.getViewport();
    }

    @Override
    public ElementBox update(ElementBox elem, Dimension dim)
            throws Exception
    {
        canvas.createLayout(dim);
        return canvas.getViewport();
    }

    @Override
    public org.w3c.dom.Document getDocument()
    {
        return w3cdoc;
    }
    
    @Override
    public String getDocumentTitle()
    {
        NodeList titles = w3cdoc.getElementsByTagName("title");
        if (titles.getLength() > 0)
            return titles.item(0).getTextContent();
        else
            return null;
    }

}
