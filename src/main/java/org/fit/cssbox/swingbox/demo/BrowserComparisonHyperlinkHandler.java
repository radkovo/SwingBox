/**
 * BrowserComparisonHyperlinkHandler.java
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
public class BrowserComparisonHyperlinkHandler extends DefaultHyperlinkHandler
{
    private BrowserComparison browser;
    
    public BrowserComparisonHyperlinkHandler(BrowserComparison browser)
    {
        this.browser = browser;
    }

    @Override
    protected void loadPage(JEditorPane pane, HyperlinkEvent evt)
    {
        browser.loadPage(evt.getURL().toString());
    }

}
