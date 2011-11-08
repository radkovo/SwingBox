package org.fit.cssbox.swingbox.util;

import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.HashMap;
import java.util.Map;

/**
 * The Class ImageLoader.
 * This class is intended to be used for back-ground image loading.
 *
 * @author Peter Bielik
 * @version 1.0
 * @since 1.0 - 5.5.2011
 */
public class ImageLoader implements ImageObserver{

    /**
     * The Interface ImageLoaderCallback. Used to notify the oposite site.
     */
    public interface ImageLoaderCallback {

	/**
     * Image complete.
     *
     * @param success
     *            the result, if success, then true.
     * @param img
     *            the image data.
     */
	public void imageComplete(boolean success, Image img);
    }

    private static volatile ImageLoader INSTANCE = null;
    private ImageLoaderCallback callback;
    private Map<Image, ImageLoaderCallback> cache = new HashMap<Image, ImageLoader.ImageLoaderCallback>(24);

    private ImageLoader() {
	//private constructor
    }

    /**
     * Gets the single instance of ImageLoader.
     *
     * @return single instance of ImageLoader
     */
    public static final ImageLoader getInstance() {
	if (INSTANCE == null) {
	    synchronized (ImageLoader.class) {
		if (INSTANCE == null) {
		    INSTANCE = new ImageLoader();
		}
	    }
	}

	return INSTANCE;
    }

    /**
     * Adds the image for proccessing.
     *
     * @param img
     *            the imgage
     * @param ilc
     *            the callback
     * @return the image loader
     */
    public synchronized ImageLoader add(Image img, ImageLoaderCallback ilc) {
	cache.put(img, ilc);
	return this;
    }

    /**
     * Removes image form proccessing.
     *
     * @param img
     *            the imgage, used to add request to ImageLoader.
     * @return the image loader
     */
    public synchronized ImageLoader remove(Image img) {
	cache.remove(img);
	return this;
    }

    /* (non-Javadoc)
     * @see java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
     */
    @Override
    public boolean imageUpdate(Image img, int flags, int x, int y,
	    int width, int height) {

	//System.err.println(img + " : " +flags);

	if ((flags & ALLBITS) > 0) {
	    synchronized (this) {
		callback = cache.get(img);
		if (callback != null)
		    callback.imageComplete(true, img);
		cache.remove(img);
	    }
	    return false;
	}else if ((flags & (ABORT|ERROR)) > 0) {
	    synchronized (this) {
		callback = cache.get(img);
		if (callback != null)
		    callback.imageComplete(false, img);
		cache.remove(img);
	    }
	    return false;
	}

	return true;
    }

}
