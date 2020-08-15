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

package org.fit.cssbox.swingbox.demo;

import org.fit.cssbox.css.CSSNorm;
import org.fit.cssbox.css.DOMAnalyzer;
import org.fit.cssbox.css.DOMAnalyzer.Origin;
import org.fit.cssbox.io.DefaultDOMSource;
import org.fit.cssbox.layout.Box;
import org.fit.cssbox.layout.BrowserCanvas;
import org.fit.cssbox.layout.ElementBox;
import org.fit.cssbox.swingbox.BrowserPane;
import org.fit.cssbox.swingbox.performance.FastDocumentSource;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;

/**
 * This demo provides 3 result of same location by 3 renderers. You will see CSSBox, SwingBox and JEditorPane + HTMLEditorKit.
 * Use the "GO!" button to start action.
 * 
 * @author Peter Bielik
 * @version 1.0
 * @since 1.0 - 22.4.2011
 */
public class BrowserComparison extends JFrame
{
    private static final long serialVersionUID = 3078719188136612454L;
    private final BrowserPane swingbox = new BrowserPane();
    private BrowserCanvas cssbox = null;
    private final JEditorPane editorkit = new JEditorPane();
    private final JTextField txt = new JTextField("http://www.aktualne.cz", 60);
    private final JScrollPane contentScroll = new JScrollPane();

    /**
     * Creates new instance of this demo application.
     */
    public BrowserComparison()
    {
        init();
        loadPage(txt.getText());
    }

    private void init()
    {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel tmp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btn = new JButton("  GO!  ");
        tmp.add(txt);
        tmp.add(btn);

        btn.addActionListener( e -> {
            Thread t = new Thread( () -> loadPage( txt.getText()) );
            t.setDaemon(true);
            t.start();
        } );
        txt.addActionListener( e -> {
            Thread t = new Thread( () -> loadPage( txt.getText()) );
            t.setDaemon(true);
            t.start();
        } );

        JTabbedPane tab = new JTabbedPane(JTabbedPane.TOP);
        tab.addTab("SwingBox", new JScrollPane(swingbox));
        tab.addTab("CSSBox", contentScroll);
        tab.addTab("JEditorPane + HTMLEditorKit", new JScrollPane(editorkit));

        panel.add(tmp, BorderLayout.NORTH);
        panel.add(tab, BorderLayout.CENTER);
        setContentPane(panel);

        swingbox.addHyperlinkListener(new BrowserComparisonHyperlinkHandler(this));

        editorkit.setEditorKit(new HTMLEditorKit());
        editorkit.setEditable(false);

        contentScroll.setViewportView(cssbox);
        contentScroll.addComponentListener(new ComponentAdapter()
                {
                    @Override
                    public void componentResized(java.awt.event.ComponentEvent e)
                    {
                        if ( cssbox != null )
                        {
                            cssbox.createLayout( contentScroll.getSize());
                            contentScroll.repaint();
                        }
                    }
                });

        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1200, 1000));
        setTitle("Demo");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void loadPage(final URL url) {
        txt.setText(url.toExternalForm());
        loadPage_editorkit(url);
        loadPage_cssbox(url);
        loadPage_swingbox(url);
    }

    public void loadPage(String page)
    {
        if (!page.startsWith("http:") && !page.startsWith("ftp:")
                && !page.startsWith("file:"))
        {
            page = "http://" + page;
        }

        try {
            loadPage( new URL( page ) );
        }
        catch(IOException ignored) {
        }
    }

    private void loadPage_swingbox(final URL url)
    {
        try {
            swingbox.setPage(url);
        } catch (IOException ignored) {
        }
    }

    private void loadPage_cssbox(final URL url) {
        try {
            final var docSource = new FastDocumentSource( url);
            final var parser = new DefaultDOMSource(docSource);
            final var dsUrl = docSource.getURL();
            final var doc = parser.parse();
            final var da = new DOMAnalyzer(doc, dsUrl);
            
            da.attributesToStyles();
            da.addStyleSheet(null, CSSNorm.stdStyleSheet(), Origin.AGENT);
            da.addStyleSheet(null, CSSNorm.userStyleSheet(), Origin.AGENT);
            da.getStyleSheets();

            cssbox = new BrowserCanvas(da.getRoot(), da, dsUrl);
            cssbox.getConfig().setLoadBackgroundImages(true);
            cssbox.getConfig().setLoadImages(true);
            cssbox.createLayout(contentScroll.getSize());
            
            cssbox.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(final MouseEvent e)
                {
                    final var node = locateBox(cssbox.getViewport(), e.getX(), e.getY());
                    if (node != null)
                    {
                        node.drawExtent(cssbox.getImageGraphics());
                        cssbox.repaint();
                    }
                }
            });
            contentScroll.setViewportView(cssbox);
            
        } catch (Exception ignored) {
        }
    }

    private void loadPage_editorkit(final URL url) {
        try {
            editorkit.setPage(url);
        } catch (IOException ignored) {
        }
    }

    /**
     * Locates a box from its position
     */
    private Box locateBox(Box root, int x, int y)
    {
        if (root.isVisible())
        {
            Box found = null;
            Rectangle bounds = root.getAbsoluteBounds();
            if (bounds.contains(x, y))
            {
                found = root;
                found.drawExtent( cssbox.getImageGraphics());
            }
            
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

        return null;
    }

    /**
     * The main method.
     * 
     * @param args
     *            no arguments needed.
     */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater( BrowserComparison::new );
    }
}
