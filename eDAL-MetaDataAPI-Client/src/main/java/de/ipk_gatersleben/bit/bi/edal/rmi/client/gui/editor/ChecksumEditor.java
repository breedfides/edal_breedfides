/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
