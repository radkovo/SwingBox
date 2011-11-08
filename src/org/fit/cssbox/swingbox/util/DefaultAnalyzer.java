package org.fit.cssbox.swingbox.util;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.xerces.parsers.DOMParser;
import org.cyberneko.html.HTMLConfiguration;
import org.fit.cssbox.css.CSSNorm;
import org.fit.cssbox.css.DOMAnalyzer;
import org.fit.cssbox.layout.BoxFactory;
import org.fit.cssbox.layout.ElementBox;
import org.fit.cssbox.layout.Viewport;
import org.fit.cssbox.layout.VisualContext;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This is customizable default implementation of CSSBoxAnalyzer.
 * 
 * @author Peter Bielik
 * @version 1.0
 * @since 1.0 - 29.1.2011
 */
public class DefaultAnalyzer implements CSSBoxAnalyzer {

    private org.w3c.dom.Document w3cdoc;
    /**
     * {@inheritDoc}
     */
    @Override
    public ElementBox analyze(InputSource is, URL url, Dimension dim, Charset charset) throws Exception {
	w3cdoc = parseDocument(is, charset);

	//Create the CSS analyzer
	DOMAnalyzer da = new DOMAnalyzer(w3cdoc, url);
	da.attributesToStyles(); //convert the HTML presentation attributes to inline styles
	da.addStyleSheet(null, CSSNorm.stdStyleSheet()); //use the standard style sheet
	da.addStyleSheet(null, CSSNorm.userStyleSheet()); //use the additional style sheet
	da.getStyleSheets(); //load the author style sheets

	// create boxes, the tree, init them,...
	Graphics2D g = new BufferedImage(1,1, BufferedImage.TYPE_INT_RGB).createGraphics();
	// font, color, size ,...
	//parent is null
	VisualContext ctx = new VisualContext(null);


	BoxFactory factory = new BoxFactory(da, url);
	factory.reset();
	Viewport viewport = factory.createViewportTree(da.getRoot(), g, ctx, 0, 0); // zadavat rozmery
	viewport.initSubtree();

	viewport.doLayout(dim.width, true, true);
	viewport.updateBounds();
	viewport.absolutePositions();


	//System.err.println("Input dimension : "+dim+" Resulting size: " + viewport.getWidth() + "x" + viewport.getHeight() + " (" + viewport + ")");

	return viewport;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ElementBox update(ElementBox elem, Dimension dim) throws Exception {
	Viewport viewport = elem instanceof Viewport ? (Viewport)elem : elem.getViewport();

	//viewport.setSize(dim.width, dim.height);
	viewport.doLayout(dim.width, true, true);
	//viewport.updateSizes();
	viewport.updateBounds();
	viewport.absolutePositions();

	return viewport;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public org.w3c.dom.Document getDocument() {
	return w3cdoc;
    }

    protected org.w3c.dom.Document parseDocument(org.xml.sax.InputSource is, Charset charset) throws SAXException, IOException 
    {
	// if custom implemetation is needed, override this method

	DOMParser parser = new DOMParser(new HTMLConfiguration());
	parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
	if (charset != null)
	    parser.setProperty("http://cyberneko.org/html/properties/default-encoding", charset.name());
	parser.parse(is);
	return  parser.getDocument();
    }


}
