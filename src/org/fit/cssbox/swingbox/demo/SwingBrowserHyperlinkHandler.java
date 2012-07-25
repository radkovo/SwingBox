/**
 * DemoHyperlinkHandler.java
 *
 * Created on 17.7.2012, 13:53:56 by burgetr
 */
package org.fit.cssbox.swingbox.demo;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;

import org.fit.cssbox.swingbox.util.DefaultHyperlinkHandler;

/**
 * This hyperlink handler implements the demo browser behaviour when a link is clicked.
 * @author burgetr
 */
public class SwingBrowserHyperlinkHandler extends DefaultHyperlinkHandler
{
    private SwingBrowser browser;
    
    public SwingBrowserHyperlinkHandler(SwingBrowser browser)
    {
        this.browser = browser;
    }

    @Override
    protected void loadPage(JEditorPane pane, HyperlinkEvent evt)
    {
        browser.displayURL(evt.getURL().toString());
    }

}
