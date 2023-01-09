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
package de.ipk_gatersleben.bit.bi.edal.rmi.client.util;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;

public class XStatusLabel extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7780350176508210689L;

	public XStatusLabel() {
		this(null, null);
	}

	public XStatusLabel(String text) {
		this(text, null);
	}

	public XStatusLabel(Icon icon) {
		this(null, icon);
	}

	public XStatusLabel(String text, Icon icon) {
		super(text, icon, 10);
		init();
	}

	protected void init() {
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		setFont(ImageUtil.FONT_12_BOLD);
		setForeground(ImageUtil.DEFAULT_TEXT_COLOR);
		setVerticalAlignment(0);
		setVerticalTextPosition(0);
		setIconTextGap(5);
	}
}