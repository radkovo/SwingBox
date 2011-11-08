package org.fit.cssbox.swingbox.view;

import javax.swing.text.AttributeSet;

/**
 * This interface is like Serializable. It only marks OUR objects.
 * If this interface is implemented, the Box reference MUST be in properties.
 * @author Peter Bielik
 * @version 1.0
 * @since 1.0 - 12.3.2011
 */
public interface CSSBoxView {
/*
 * toto rozhranie je podobne, ako serializable, len oznacujuce iste objekty,
 * v tomto pripade obejkty typu View, znazornujuce vystup CSSBoxu.
 * Tieto objektu maju garantovane, ze maju medzi svojimi atribytmi z metody
 * getAttributes aj referenciu na original *Box, ktory reprezentuju a celkovo
 * tak "kopiruju" hierarchiu, aka je medzi boxmi.
 */


    /**
 * Gets the attributes of a View object
 *
 * @return the attributes
 */
public AttributeSet getAttributes();
}
