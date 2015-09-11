/**
 * BrowserComparison.java
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

package org.fit.cssbox.swingbox.demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

import org.fit.cssbox.css.CSSNorm;
import org.fit.cssbox.css.DOMAnalyzer;
import org.fit.cssbox.css.DOMAnalyzer.Origin;
import org.fit.cssbox.io.DOMSource;
import org.fit.cssbox.io.DefaultDOMSource;
import org.fit.cssbox.io.DefaultDocumentSource;
import org.fit.cssbox.io.DocumentSource;
import org.fit.cssbox.layout.Box;
import org.fit.cssbox.layout.BrowserCanvas;
import org.fit.cssbox.layout.ElementBox;
import org.fit.cssbox.swingbox.BrowserPane;
import org.fit.cssbox.swingbox.util.GeneralEvent;
import org.fit.cssbox.swingbox.util.GeneralEvent.EventType;
import org.fit.cssbox.swingbox.util.GeneralEventListener;
import org.w3c.dom.Document;

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
    BrowserPane swingbox = new BrowserPane();
    BrowserCanvas cssbox = null;
    JEditorPane editorkit = new JEditorPane();
    JTextField txt = new JTextField("http://www.aktualne.cz", 60);
    JScrollPane contentScroll = new JScrollPane();

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

        btn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Thread t = new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        loadPage(txt.getText());
                    }
                });
                t.setDaemon(true);
                t.start();
            }
        });
        txt.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Thread t = new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        loadPage(txt.getText());
                    }
                });
                t.setDaemon(true);
                t.start();
            }
        });

        JTabbedPane tab = new JTabbedPane(JTabbedPane.TOP);
        tab.addTab("SwingBox", new JScrollPane(swingbox));
        tab.addTab("CSSBox", contentScroll);
        tab.addTab("JEditorPane + HTMLEditorKit", new JScrollPane(editorkit));

        panel.add(tmp, BorderLayout.NORTH);
        panel.add(tab, BorderLayout.CENTER);
        setContentPane(panel);

        swingbox.addHyperlinkListener(new BrowserComparisonHyperlinkHandler(this));
        swingbox.addGeneralEventListener(new GeneralEventListener()
        {
            private long time;

            @Override
            public void generalEventUpdate(GeneralEvent e)
            {
                if (e.event_type == EventType.page_loading_begin)
                {
                    time = System.currentTimeMillis();
                }
                else if (e.event_type == EventType.page_loading_end)
                {
                    System.out.println("SwingBox: page loaded in: "
                            + (System.currentTimeMillis() - time) + " ms");
                }
            }
        });

        editorkit.setEditorKit(new HTMLEditorKit());
        editorkit.setEditable(false);
        editorkit.addHyperlinkListener(new HyperlinkListener()
        {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e)
            {
                //txt.setText(e.getURL().toString());
                //loadPage(txt.getText());
            }
        });

        contentScroll.setViewportView(cssbox);
        contentScroll.addComponentListener(new java.awt.event.ComponentAdapter()
                {
                    @Override
                    public void componentResized(java.awt.event.ComponentEvent e)
                    {
                        if (cssbox != null && cssbox instanceof BrowserCanvas)
                        {
                            ((BrowserCanvas) cssbox).createLayout(contentScroll.getSize());
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

    public void loadPage(String page)
    {
        if (!page.startsWith("http:") && !page.startsWith("ftp:")
                && !page.startsWith("file:"))
        {
            page = "http://" + page;
        }

        txt.setText(page);
        loadPage_editorkit(page);
        loadPage_cssbox(page);
        loadPage_swingbox(page);
    }

    private void loadPage_swingbox(String url)
    {
        try
        {
            swingbox.setPage(new URL(url));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void loadPage_cssbox(String urlstring)
    {
        try
        {
            DocumentSource docSource = new DefaultDocumentSource(urlstring);
            
            DOMSource parser = new DefaultDOMSource(docSource);
            Document doc = parser.parse();
            
            DOMAnalyzer da = new DOMAnalyzer(doc, docSource.getURL());
            
            da.attributesToStyles();
            da.addStyleSheet(null, CSSNorm.stdStyleSheet(), Origin.AGENT);
            da.addStyleSheet(null, CSSNorm.userStyleSheet(), Origin.AGENT);
            da.getStyleSheets();

            cssbox = new BrowserCanvas(da.getRoot(), da, docSource.getURL());
            cssbox.getConfig().setLoadBackgroundImages(true);
            cssbox.getConfig().setLoadImages(true);
            cssbox.createLayout(contentScroll.getSize());
            
            cssbox.addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e)
                {
                    System.out.println("Click: " + e.getX() + ":" + e.getY());
                    //canvasClick(e.getX(), e.getY());
                    Box node = locateBox(cssbox.getViewport(), e.getX(), e.getY());
                    System.out.println("Box: " + node);
                    if (node != null)
                    {
                        node.drawExtent(cssbox.getImageGraphics());
                        cssbox.repaint();
                    }
                }
                public void mousePressed(MouseEvent e) { }
                public void mouseReleased(MouseEvent e) { }
                public void mouseEntered(MouseEvent e) { }
                public void mouseExited(MouseEvent e) { }
            });            
            contentScroll.setViewportView(cssbox);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPage_editorkit(String url)
    {
        try {
            editorkit.setPage(new URL(url));
        } catch (IOException e) {
            e.printStackTrace();
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
            //Rectangle bounds = root.getAbsoluteContentBounds().intersection(root.getClipBlock().getAbsoluteContentBounds());
            Rectangle bounds = root.getAbsoluteBounds();
            if (bounds.contains(x, y))
            {
                found = root;
                System.out.println("Fnd: " + found);
                found.drawExtent(((BrowserCanvas)cssbox).getImageGraphics());
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
        else
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
        SwingUtilities.invokeLater(new Runnable()
        {

            @Override
            public void run()
            {
                new BrowserComparison();
            }
        });
    }

}
