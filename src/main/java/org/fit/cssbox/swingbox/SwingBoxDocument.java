/**
 * SwingBoxDocument.java
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

package org.fit.cssbox.swingbox;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;

import org.fit.cssbox.swingbox.util.Anchor;
import org.fit.cssbox.swingbox.util.Constants;

/**
 * @author Peter Bielik
 * @version 1.0
 * @since 1.0 - 28.9.2010
 */
public class SwingBoxDocument extends DefaultStyledDocument
{
    private static final long serialVersionUID = 5342259762698268312L;

    /**
     * Custom implementation of Document, used in SwingBox
     */
    public SwingBoxDocument()
    {
        super();
        // we do not support any inserting, removing or replacing of string & no
        // filters
        setDocumentFilter(null);
    }

    @Override
    public void insert(int offset, ElementSpec[] data)
            throws BadLocationException
    {
        // we need this method, so we are "re-visibling" it
        super.insert(offset, data);
    }

    @Override
    public void create(ElementSpec[] data)
    {
        // we need this method, so we are "re-visibling" it
        super.create(data);
    }

    @Override
    protected AbstractElement createDefaultRoot()
    {
        try {
            writeLock();
            BranchElement delegate = new DelegateElement(Constants.VIEWPORT);
            delegate.addAttribute(Constants.ATTRIBUTE_ANCHOR_REFERENCE, new Anchor());
            return delegate;
        } finally {
            writeUnlock();
        }
    }

    /**
     * The Class DelegateElement.
     */
    public class DelegateElement extends BranchElement
    {
        private static final long serialVersionUID = 5636867648057150930L;
        private LeafElement DEFAULT_CONTENT;
        private String delegateName;

        /**
         * Creates a new SectionElement.
         * 
         * @param delegateName
         *            the name of element, we are interested in.
         */
        public DelegateElement(String delegateName)
        {
            super(null, null);

            this.delegateName = delegateName;
            DEFAULT_CONTENT = new LeafElement(this, null, 0, 1);
            replace(0, 0, new Element[] { DEFAULT_CONTENT });
        }

        /**
         * Gets the delegate name.
         * 
         * @return the delegate name
         */
        public String getDelegateName()
        {
            // this is not a class-name, but name of a Element
            // this name is used in DelegateView to filter & get desired view
            return delegateName;
        }

        /**
         * Gets the default content.
         * 
         * @return the default content, instance of LabelView.
         */
        public Element getDefaultContent()
        {
            return DEFAULT_CONTENT;
        }

        @Override
        public void replace(int offset, int length, Element[] elems)
        {
            javax.swing.text.Element els[];
            if (elems.length > 0)
            {
                javax.swing.text.Element data = elems[0];

                for (Element elem : elems)
                {
                    if (delegateName.equals(elem.getName()))
                    {
                        data = elem;
                        break;
                    }
                }

                els = new Element[] { data };
            }
            else
            {
                // there are no elems
                els = new Element[] { DEFAULT_CONTENT };
            }

            super.replace(0, getElementCount(), els);
        }

        /**
         * Gets the name of the element.
         * 
         * @return the name
         */
        public String getName()
        {
            return Constants.DELEGATE;
            // return AbstractDocument.SectionElementName;
        }
    }

}
