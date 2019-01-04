/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class AuthorCellRenderer extends DefaultTableCellRenderer {

	private static final String MESSAGE_ERROR_RESEARCH_GROUP = "\"Group Authors\" and natural person can't be used in parallel. To specify a research group, please clear the field \"Given Name\" and \"Surename\".";
	private static final String MESSAGE_ERROR_SURENAME = "\"Surename\" and group authors can't be used in parallel. To specify a natural person, please clear the field \"Group Authors\".";
	private static final String MESSAGE_ERROR_GIVEN_NAME = "\"Given Name\" and group authors can't be used in parallel. To specify a natural person, please clear the field \"Group Authors\".";
	private static final String MESSAGE_ERROR_ORCID = "\"Group Authors\" and natural person can't be used in parallel. To specify a research group, please clear the field \"Given Name\" and \"Surename\" and \"ORCID\".";

	private static final long serialVersionUID = 936915644995188068L;

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {

		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (!table.isCellEditable(row, column)) {
			this.setBackground(Color.LIGHT_GRAY);
			this.setOpaque(true);
			if (column == 1) {
				this.setToolTipText(AuthorCellRenderer.MESSAGE_ERROR_GIVEN_NAME);
			} else if (column == 2) {
				this.setToolTipText(AuthorCellRenderer.MESSAGE_ERROR_SURENAME);
			} else if (column == 3) {
				this.setToolTipText(AuthorCellRenderer.MESSAGE_ERROR_RESEARCH_GROUP);
			} else if (column == 7) {
				this.setToolTipText(AuthorCellRenderer.MESSAGE_ERROR_ORCID);
			}
		} else {
			this.setBackground(Color.WHITE);
			this.setOpaque(true);
		}

		return this;
	}

}
