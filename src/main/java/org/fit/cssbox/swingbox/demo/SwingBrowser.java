/**
 * SwingBrowser.java
 * (c) Radek Burget, 2012
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

import javax.swing.*;

import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import org.fit.cssbox.swingbox.BrowserPane;
import org.fit.cssbox.swingbox.util.GeneralEvent;
import org.fit.cssbox.swingbox.util.GeneralEventListener;
import org.fit.cssbox.swingbox.util.GeneralEvent.EventType;
import org.fit.net.DataURLHandler;

import java.awt.GridBagConstraints;

import javax.swing.text.Document;

import java.awt.Rectangle;
import java.awt.GridBagLayout;

import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * This demo implements a simple Swing-based browser.
 * 
 * @author burgetr
 */
public class SwingBrowser
{
    protected Vector<URL> history;
    protected int historyPos;
    public static SwingBrowser browser;
    
    protected JFrame mainWindow = null;  //  @jve:decl-index=0:visual-constraint="67,17"
    protected JPanel mainPanel = null;
    protected JPanel urlPanel = null;
    protected JPanel statusPanel = null;
    protected JTextField statusText = null;
    protected JLabel jLabel = null;
    protected JTextField urlText = null;
    protected JButton okButton = null;
    private JTabbedPane tabs;
    private JButton backButton;
    
    BrowserPane swingbox = null;

    
    public SwingBrowser()
    {
        history = new Vector<URL>();
        historyPos = 0;
    }

    public void displayURL(String urlstring)
    {
        try {
            if (!urlstring.startsWith("http:") &&
                    !urlstring.startsWith("https:") &&
                    !urlstring.startsWith("ftp:") &&
                    !urlstring.startsWith("file:") &&
                    !urlstring.startsWith("data:"))
                        urlstring = "http://" + urlstring;
                
            URL url = DataURLHandler.createURL(null, urlstring);
            urlText.setText(url.toString());

            while (historyPos < history.size())
                history.remove(history.size() - 1);
            history.add(url);
            historyPos++;

            displayURLSwingBox(url);
        } catch (Exception e) {
            System.err.println("*** Error: "+e.getMessage());
            e.printStackTrace();
        }
    }
    
    protected void displayURLSwingBox(URL url) throws IOException
    {
        if (swingbox == null)
        {
            swingbox = createSwingbox();
            tabs.add("New Tab", new JScrollPane(swingbox));
        }
        swingbox.setPage(url);
    }
    
    //===========================================================================
    
    /**
     * This method initializes jFrame	
     * 	
     * @return javax.swing.JFrame	
     */
    public JFrame getMainWindow()
    {
        if (mainWindow == null)
        {
            mainWindow = new JFrame();
            mainWindow.setTitle("Swing Browser");
            mainWindow.setVisible(true);
            mainWindow.setBounds(new Rectangle(0, 0, 583, 251));
            mainWindow.setContentPane(getMainPanel());
            mainWindow.addWindowListener(new java.awt.event.WindowAdapter()
            {
                public void windowClosing(java.awt.event.WindowEvent e)
                {
                    mainWindow.setVisible(false);
                    System.exit(0);
                }
            });
        }
        return mainWindow;
    }

    /**
     * This method initializes jContentPane	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getMainPanel()
    {
        if (mainPanel == null)
        {
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.insets = new Insets(10, 10, 5, 10);
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.gridy = 0;
            mainPanel = new JPanel();
            GridBagLayout gbl_mainPanel = new GridBagLayout();
            gbl_mainPanel.rowWeights = new double[]{0.0, 0.0, 0.0};
            gbl_mainPanel.columnWeights = new double[]{1.0};
            mainPanel.setLayout(gbl_mainPanel);
            mainPanel.add(getUrlPanel(), gridBagConstraints);
            GridBagConstraints gbc_tabs = new GridBagConstraints();
            gbc_tabs.weighty = 1.0;
            gbc_tabs.weightx = 1.0;
            gbc_tabs.fill = GridBagConstraints.BOTH;
            gbc_tabs.gridx = 0;
            gbc_tabs.gridy = 1;
            mainPanel.add(getTabs(), gbc_tabs);
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridwidth = 1;
            gridBagConstraints3.gridy = 2;
            mainPanel.add(getStatusPanel(), gridBagConstraints3);
        }
        return mainPanel;
    }

    /**
     * This method initializes jPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getUrlPanel()
    {
        if (urlPanel == null)
        {
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.gridx = 1;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 3;
            gridBagConstraints7.insets = new Insets(0, 0, 0, 7);
            gridBagConstraints7.gridy = 0;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 2;
            gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.gridy = 0;
            gridBagConstraints6.weightx = 1.0;
            gridBagConstraints6.insets = new java.awt.Insets(0,5,0,5);
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridy = 0;
            gridBagConstraints5.anchor = java.awt.GridBagConstraints.CENTER;
            gridBagConstraints5.insets = new Insets(0, 6, 0, 5);
            gridBagConstraints5.gridx = 1;
            jLabel = new JLabel();
            jLabel.setText("Location :");
            urlPanel = new JPanel();
            urlPanel.setLayout(new GridBagLayout());
            GridBagConstraints gbc_backButton = new GridBagConstraints();
            gbc_backButton.insets = new Insets(0, 0, 5, 5);
            gbc_backButton.gridx = 0;
            gbc_backButton.gridy = 0;
            urlPanel.add(getBackButton(), gbc_backButton);
            urlPanel.add(jLabel, gridBagConstraints5);
            urlPanel.add(getUrlText(), gridBagConstraints6);
            urlPanel.add(getOkButton(), gridBagConstraints7);
        }
        return urlPanel;
    }

    /**
     * This method initializes jPanel2	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getStatusPanel()
    {
        if (statusPanel == null)
        {
            statusPanel = new JPanel();
            GridBagLayout gbl_statusPanel = new GridBagLayout();
            gbl_statusPanel.columnWidths = new int[]{550, 91, 0};
            gbl_statusPanel.rowHeights = new int[]{19, 0};
            gbl_statusPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
            gbl_statusPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
            statusPanel.setLayout(gbl_statusPanel);
            GridBagConstraints gbc_statusText = new GridBagConstraints();
            gbc_statusText.fill = GridBagConstraints.HORIZONTAL;
            gbc_statusText.anchor = GridBagConstraints.EAST;
            gbc_statusText.gridx = 0;
            gbc_statusText.gridy = 0;
            statusPanel.add(getStatusText(), gbc_statusText);
        }
        return statusPanel;
    }

    /**
     * This method initializes jTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getStatusText()
    {
        if (statusText == null)
        {
            statusText = new JTextField();
            statusText.setEditable(false);
            statusText.setText("Browser ready.");
        }
        return statusText;
    }

    /**
     * This method initializes jTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getUrlText()
    {
        if (urlText == null)
        {
            urlText = new JTextField();
            urlText.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    displayURL(urlText.getText());
                }
            });
        }
        return urlText;
    }

    /**
     * This method initializes jButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getOkButton()
    {
        if (okButton == null)
        {
            okButton = new JButton();
            okButton.setText("Go!");
            okButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    displayURL(urlText.getText());
                }
            });
        }
        return okButton;
    }

    private BrowserPane createSwingbox()
    {
        swingbox = new BrowserPane();
        swingbox.addHyperlinkListener(new SwingBrowserHyperlinkHandler(this));
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
                    Object title = swingbox.getDocument().getProperty(Document.TitleProperty);
                    if (title != null)
                        tabs.setTitleAt(0, title.toString());
                    
                    System.out.println("SwingBox: page loaded in: "
                            + (System.currentTimeMillis() - time) + " ms");
                }
            }
        });
        return swingbox;
    }
    
    private JTabbedPane getTabs() 
    {
        if (tabs == null) {
        	tabs = new JTabbedPane(JTabbedPane.TOP);
        }
        return tabs;
    }
    
    private JButton getBackButton() 
    {
        if (backButton == null) {
        	backButton = new JButton("Back");
        	backButton.addActionListener(new ActionListener() {
        	    public void actionPerformed(ActionEvent arg0) 
        	    {
        	        if (historyPos > 1)
        	        {
        	            historyPos--;
        	            URL url = history.elementAt(historyPos - 1);
        	            try
                        {
                            displayURLSwingBox(url);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
        	        }
        	    }
        	});
        }
        return backButton;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        browser = new SwingBrowser();
        JFrame main = browser.getMainWindow();
        main.setSize(1100, 850);
        main.setVisible(true);
        browser.displayURL("http://cssbox.sourceforge.net/swingbox");
    }

}
