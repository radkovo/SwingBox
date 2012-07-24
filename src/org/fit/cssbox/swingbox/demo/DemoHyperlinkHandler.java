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
 * The default behaviour is to open the link in all the tabs.
 * @author burgetr
 */
public class DemoHyperlinkHandler extends DefaultHyperlinkHandler
{
    private BrowserComparison browser;
    
    public DemoHyperlinkHandler(BrowserComparison browser)
    {
        this.browser = browser;
    }

    @Override
    protected void loadPage(JEditorPane pane, HyperlinkEvent evt)
    {
        browser.loadPage(evt.getURL().toString());
    }

}
