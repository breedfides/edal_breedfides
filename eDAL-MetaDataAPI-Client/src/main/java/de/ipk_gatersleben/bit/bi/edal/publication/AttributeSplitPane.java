/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
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
