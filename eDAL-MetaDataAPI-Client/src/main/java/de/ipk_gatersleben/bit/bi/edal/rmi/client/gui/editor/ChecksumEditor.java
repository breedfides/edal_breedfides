/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.editor;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.CheckSum;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * The <code>ChecksumEditor</code> wrappers class <code>ChecksumDialog</code> as
 * a <code>MetadataEditor</code> Dialog to edit Checksum datatype. we can use it
 * with a couple of lines of code:
 * 
 * <pre>
 * ChecksumEditor checksumEditor = new ChecksumEditor();
 * checksumEditor.showOpenDialog();
 * </pre>
 * 
 * @version 1.0
 * @author Jinbo Chen
 */
public class ChecksumEditor extends AbstractMetaDataEditor {
	private CheckSum checkSum;

	/**
	 * pop up a IdentifierRelationDialog Dialog
	 */
	public int showOpenDialog() {
		ChecksumDialog dlg = new ChecksumDialog(this.checkSum);
		int returnVal = dlg.showOpenDialog();
		if (returnVal == ChecksumDialog.APPROVE_OPTION) {
			this.checkSum = dlg.getCheckSum();
		}
		return returnVal;
	}

	/**
	 * set the Checksum value.
	 * 
	 * set the Checksum value.
	 */
	public void setValue(UntypedData checkSum) {
		if (checkSum != null) {
			this.checkSum = (CheckSum) checkSum;
		}
	}

	/**
	 * Returns the Checksum inputted by user.
	 * 
	 * @return the Checksum inputted by user.
	 */
	public UntypedData getValue() {
		return this.checkSum;
	}

}
