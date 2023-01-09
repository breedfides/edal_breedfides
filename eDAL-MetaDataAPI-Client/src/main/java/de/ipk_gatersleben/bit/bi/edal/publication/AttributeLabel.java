/**
 * Copyright (c) 2023 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.publication;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

/**
 * Class to define the layout of the {@link AttributeLabel}s on the left side of
 * the frame.
 * 
 * @author arendd
 */
public class AttributeLabel extends JLabel {

	private static final long serialVersionUID = 602825678934024556L;

	public AttributeLabel(String label, String tooltip) {

		super(label);

		this.setHorizontalAlignment(SwingConstants.LEFT);
		this.setVerticalAlignment(SwingConstants.TOP);
		this.setFont(new Font(Font.SANS_SERIF, Font.BOLD, PropertyLoader.ATTRIBUTE_LABEL_FONT_SIZE));
		this.setForeground(PropertyLoader.LABEL_COLOR);
		this.setOpaque(true);
		this.setBackground(PropertyLoader.HEADER_FOOTER_COLOR);

		if (label.equals(PropertyLoader.props.getProperty("LICENSE_LABEL"))) {

			this.setCursor(new Cursor(Cursor.HAND_CURSOR));

			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent event) {

					String url = "http://creativecommons.org/licenses/";

					EdalHelpers.openURL(url);
				}
			});
		}
		EmptyBorder inBorder = new EmptyBorder(2, 5, 0, 0);
		EmptyBorder outBorder = new EmptyBorder(0, 0, 0, 0);
		this.setBorder(BorderFactory.createCompoundBorder(outBorder, inBorder));

		this.setToolTipText(tooltip);
	}
}
