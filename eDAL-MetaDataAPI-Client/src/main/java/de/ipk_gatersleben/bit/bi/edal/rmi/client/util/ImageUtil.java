/**
 * Copyright (c) 2022 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 *
 * We have chosen to apply the GNU General Public License (GPL) Version 3 (https://www.gnu.org/licenses/gpl-3.0.html)
 * to the copyrightable parts of e!DAL, which are the source code, the executable software, the training and
 * documentation material. This means, you must give appropriate credit, provide a link to the license, and indicate
 * if changes were made. You are free to copy and redistribute e!DAL in any medium or format. You are also free to
 * adapt, remix, transform, and build upon e!DAL for any purpose, even commercially.
 *
 *  Contributors:
 *       Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.util;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;

/**
 * A class used to get ImageIcon object from classpath. fixed by @author arendd
 * 
 * @version 1.0
 * @author Jinbo Chen
 * 
 */
public class ImageUtil {
	public static final Color DEFAULT_TEXT_COLOR = new Color(37, 81, 54);
	// public static final Color DEFAULT_TEXT_COLOR = new Color(37, 81, 54);
	public static final Font FONT_12_BOLD = new Font("Courier New", 1, 12);
	public static final Font FONT_14_BOLD = new Font("Courier New", 1, 14);

	public static ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = ImageUtil.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			ClientDataManager.logger.error("Couldn't find file: " + path);
			return null;
		}
	}

	public static Image getImage(String imgPath) {

		BufferedImage img = null;
		try {

			img = ImageIO.read(ImageUtil.class.getResource(imgPath));
		} catch (IOException e) {
			ClientDataManager.logger.error("can't find image in path: "
					+ imgPath);
		}
		return img;
	}

	public static TexturePaint createTexturePaint(String imgPath) {
		ImageIcon icon = createImageIcon(imgPath, "");
		int imageWidth = icon.getIconWidth();
		int imageHeight = icon.getIconHeight();
		BufferedImage bi = new BufferedImage(imageWidth, imageHeight, 2);
		Graphics2D g2d = bi.createGraphics();
		g2d.drawImage(icon.getImage(), 0, 0, null);
		g2d.dispose();
		return new TexturePaint(bi,
				new Rectangle(0, 0, imageWidth, imageHeight));
	}
	
	public static Image createWindowTitleShadowImage(Graphics2D g2d, String title, boolean activeWindow) {
        FontRenderContext context = g2d.getFontMetrics().getFontRenderContext();
        //add a "-" to make shadow tail a little bit longer than the text outline.
        GlyphVector vector = g2d.getFont().createGlyphVector(context, title + "");
        Shape textShape = vector.getOutline();

        int strokeWidth = 15;
        Stroke stroke = new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        Shape strokedTextShape = stroke.createStrokedShape(textShape);
        Rectangle strokedTextBounds = strokedTextShape.getBounds();

        BufferedImage image = new BufferedImage(strokedTextBounds.width,
                strokedTextBounds.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D imageGraphics = (Graphics2D) image.createGraphics();

        imageGraphics.translate(-strokedTextShape.getBounds().x, -strokedTextShape.getBounds().y);
        imageGraphics.setColor(Color.white);
        imageGraphics.fill(strokedTextShape);
        imageGraphics.dispose();

        if (activeWindow) {
            image = createDropShadow(image, 15, new Color(255, 255, 255, 200));
        } else {
            image = createDropShadow(image, 15, new Color(200, 200, 200, 200));
        }

        return image;
    }
	
	 public static BufferedImage createDropShadow(BufferedImage image, int size) {
	        return createDropShadow(image, size, Color.black);
	    }

	    public static BufferedImage createDropShadow(BufferedImage image, int size, Color renderColor) {
	        BufferedImage shadow = new BufferedImage(
	                image.getWidth() + 4 * size,
	                image.getHeight() + 4 * size,
	                BufferedImage.TYPE_INT_ARGB);

	        Graphics2D g2 = shadow.createGraphics();
	        g2.drawImage(image, size * 2, size * 2, null);

	        g2.setComposite(AlphaComposite.SrcIn);
	        g2.setColor(renderColor);
	        g2.fillRect(0, 0, shadow.getWidth(), shadow.getHeight());

	        g2.dispose();

	        shadow = getGaussianBlurFilter(size, true).filter(shadow, null);
	        shadow = getGaussianBlurFilter(size, false).filter(shadow, null);

	        return shadow;
	    }
	    
	    public static ConvolveOp getGaussianBlurFilter(int radius, boolean horizontal) {
	        if (radius < 1) {
	            throw new IllegalArgumentException("Radius must be >= 1");
	        }

	        int size = radius * 2 + 1;
	        float[] data = new float[size];

	        float sigma = radius / 3.0f;
	        float twoSigmaSquare = 2.0f * sigma * sigma;
	        float sigmaRoot = (float) Math.sqrt(twoSigmaSquare * Math.PI);
	        float total = 0.0f;

	        for (int i = -radius; i <= radius; i++) {
	            float distance = i * i;
	            int index = i + radius;
	            data[index] = (float) Math.exp(-distance / twoSigmaSquare) / sigmaRoot;
	            total += data[index];
	        }

	        for (int i = 0; i < data.length; i++) {
	            data[i] /= total;
	        }

	        Kernel kernel = null;
	        if (horizontal) {
	            kernel = new Kernel(size, 1, data);
	        } else {
	            kernel = new Kernel(1, size, data);
	        }
	        return new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
	    }


}
