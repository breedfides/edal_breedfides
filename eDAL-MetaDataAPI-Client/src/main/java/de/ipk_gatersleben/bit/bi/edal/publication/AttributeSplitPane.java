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
package de.ipk_gatersleben.bit.bi.edal.publication;

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JSplitPane;

public class AttributeSplitPane extends JSplitPane {

	private static final long serialVersionUID = 4176104812392966852L;

	public AttributeSplitPane(Component newLeftComponent, Component newRightComponent) {

		super(JSplitPane.VERTICAL_SPLIT, true, newLeftComponent, newRightComponent);

		this.setOneTouchExpandable(false);
		this.setDividerSize(5);
		// this.setDividerLocation(0.5);
		// this.setSize(50, 50);
		this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

	}
}
