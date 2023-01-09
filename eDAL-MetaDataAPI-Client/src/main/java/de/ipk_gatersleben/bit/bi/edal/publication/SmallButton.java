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

import java.awt.Font;
import java.awt.Insets;

import javax.swing.JButton;


/**
 * @author arendd
 */
public class SmallButton extends JButton {

	private static final long serialVersionUID = 8212698968883397292L;

	public SmallButton(String text) {

		super(text);
		this.setFont(new Font(Font.SANS_SERIF, Font.BOLD, PropertyLoader.SMALL_BUTTON_FONT_SIZE));
		this.setMargin(new Insets(0, 5, 0, 5));

	}

}
