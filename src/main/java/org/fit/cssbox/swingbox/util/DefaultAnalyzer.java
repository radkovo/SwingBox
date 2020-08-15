/*
 * (c) Peter Bielik and Radek Burget, 2011-2012
 * Copyright 2020 White Magic Software, Ltd.
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

import org.fit.cssbox.css.CSSNorm;
import org.fit.cssbox.css.DOMAnalyzer;
import org.fit.cssbox.io.DocumentSource;
import org.fit.cssbox.layout.BrowserCanvas;
import org.fit.cssbox.layout.Viewport;
import org.fit.cssbox.swingbox.performance.FastBrowserConfig;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static org.apache.commons.codec.CharEncoding.UTF_8;
import static org.fit.cssbox.css.DOMAnalyzer.Origin.AGENT;

/**
 * This is customizable default implementation of CSSBoxAnalyzer.
 *
 * @author Peter Bielik
 * @author Radek Burget
 */
@SuppressWarnings("unused")
public class DefaultAnalyzer implements CSSBoxAnalyzer {
  private final W3CDom mDom = new W3CDom();
  private final FastBrowserConfig mBrowserConfig = new FastBrowserConfig();

  private org.w3c.dom.Document w3cdoc;
  private BrowserCanvas canvas;

  public DefaultAnalyzer() {
    mBrowserConfig.setLoadImages( true );
    mBrowserConfig.setLoadBackgroundImages( true );
  }

  @Override
  public Viewport analyze( final DocumentSource docSource, final Dimension dim )
      throws Exception {
    final var url = docSource.getURL();
    final var uri = url.toURI();

    final var document = Jsoup.parse(
        docSource.getInputStream(), UTF_8, uri.toString() );
    docSource.close();

    w3cdoc = mDom.fromJsoup( document );

    // Create the CSS analyzer
    final var da = new DOMAnalyzer( w3cdoc, url );
    da.attributesToStyles();
    da.addStyleSheet( null, CSSNorm.stdStyleSheet(), AGENT );
    da.addStyleSheet( null, CSSNorm.userStyleSheet(), AGENT );
    da.getStyleSheets();

    final var image = new BufferedImage( 1, 1, TYPE_INT_RGB );
    canvas = new BrowserCanvas( da.getRoot(), da, url );
    canvas.setConfig( mBrowserConfig );
    canvas.setImage( image );
    canvas.createLayout( dim );

    return canvas.getViewport();
  }

  @Override
  public Viewport update( Dimension dim ) {
    canvas.createLayout( dim );
    return canvas.getViewport();
  }

  @Override
  public org.w3c.dom.Document getDocument() {
    return w3cdoc;
  }

  @Override
  public String getDocumentTitle() {
    final var titles = w3cdoc.getElementsByTagName( "title" );

    if( titles.getLength() > 0 ) {
      return titles.item( 0 ).getTextContent();
    }

    return "";
  }
}
