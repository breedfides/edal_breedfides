/**
 * Copyright (c) 2021 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.ORCID;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.orcid.ORCIDException;
import de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel.AuthorsPanel.AuthorTableModel;

/**
 * Dialog to show the result of the ORICD search for a given author name
 * 
 * @author arendd
 *
 */
public class OrcidSearchForNameDialog extends JDialog {

	private static final long serialVersionUID = 1381135721709418596L;

	private String orcid;
	private NaturalPerson naturalPerson;
	private JLabel searchLabel = new JLabel("Searching for Name...");

	public OrcidSearchForNameDialog(Component parent, AuthorTableModel model, String orcid) {

		this.setModal(true);
		this.setAlwaysOnTop(true);
		this.orcid = orcid;
		this.setLocationRelativeTo(parent);
		this.setTitle("Searching for Name...");
		this.setMinimumSize(new Dimension(400, 100));
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.getContentPane().add(searchLabel);
		this.pack();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				searchOrcid();
			}
		});
		this.setVisible(true);
	}

	public NaturalPerson getNaturalPerson() {
		return naturalPerson;
	}

	protected void searchOrcid() {
		this.naturalPerson = null;
		try {
			this.naturalPerson = ORCID.getPersonByOrcid(this.orcid);
			this.dispose();
		} catch (ORCIDException e) {
			e.printStackTrace();
			this.dispose();
		}

	}

}
