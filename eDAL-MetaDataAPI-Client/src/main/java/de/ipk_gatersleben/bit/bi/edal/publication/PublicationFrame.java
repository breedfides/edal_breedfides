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
package de.ipk_gatersleben.bit.bi.edal.publication;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.apache.commons.codec.digest.DigestUtils;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.publication.listener.ShutdownWindowsListener;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataDirectory;

public class PublicationFrame extends JFrame {

	private static final long serialVersionUID = 3152855359717852975L;

	private static PublicationMainPanel mainPanel;

	private static PublicationHeadPanel headPanel;

	private static PublicationButtonLinePanel buttonPanel;

	public static PublicationButtonLinePanel getButtonPanel() {
		return buttonPanel;
	}

	public static PublicationMainPanel getMainPanel() {
		return mainPanel;
	}

	public static ClientPrimaryDataDirectory rootDirectory = null;

	public static String loggedUser = "";

	public static void updateUI(Color color) {

		PublicationFrame.mainPanel.revalidate();
		PublicationFrame.mainPanel.repaint();
		PublicationFrame.buttonPanel.revalidate();
		PublicationFrame.buttonPanel.repaint();
		PublicationFrame.headPanel.getHtmlPanel().updateHtml();
		PublicationFrame.headPanel.getCitationPanel().updateHtml(color);
	}

	public static void updateUI() {

		PublicationFrame.mainPanel.revalidate();
		PublicationFrame.mainPanel.repaint();
		PublicationFrame.buttonPanel.revalidate();
		PublicationFrame.buttonPanel.repaint();
		PublicationFrame.headPanel.getHtmlPanel().updateHtml();
		PublicationFrame.headPanel.getCitationPanel().updateHtml();
	}

	public PublicationFrame(ClientDataManager clientDataManager, boolean showPublisherField, boolean showResourceField)
			throws Exception {

		PublicationFrame.loggedUser = clientDataManager.getAuthentication().getName();

		checkLoggedUserForCorrectEmailAddress(PublicationFrame.loggedUser);

		this.setTitle(PropertyLoader.PROGRAM_NAME);
		this.setIconImage(PropertyLoader.EDAL_ICON);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new ShutdownWindowsListener());

		PublicationFrame.mainPanel = new PublicationMainPanel(showPublisherField, showResourceField);
		PublicationFrame.headPanel = new PublicationHeadPanel();
		PublicationFrame.buttonPanel = new PublicationButtonLinePanel(clientDataManager);

		checkLicenseProperty();

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(PublicationFrame.headPanel, BorderLayout.NORTH);
		this.getContentPane().add(PublicationFrame.mainPanel, BorderLayout.CENTER);
		this.getContentPane().add(PublicationFrame.buttonPanel, BorderLayout.SOUTH);

		this.setMinimumSize(PropertyLoader.MINIMUM_DIM_PUBLICATION_FRAME);
		this.setPreferredSize(PropertyLoader.MINIMUM_DIM_PUBLICATION_FRAME);

		this.pack();
		/* place windows centered */
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	/**
	 * Check if the user already read the current version of the License agreement
	 * and activate/deactivate the MainPanel
	 **/
	private void checkLicenseProperty() throws IOException {
		if (PropertyLoader.userValues.getProperty(PropertyLoader.AGREEMENT_PDF_PROPERTY) != null) {

			String checkSumOld = PropertyLoader.userValues.getProperty(PropertyLoader.AGREEMENT_PDF_PROPERTY);
			String checkSumNew = DigestUtils.md5Hex(PropertyLoader.PGP_CONTRACT_URL.openStream());

			if (checkSumOld.equals(checkSumNew)) {
				PublicationFrame.mainPanel.enableAll();
				PublicationButtonLinePanel.getNextButton().setEnabled(true);
				
			} else {
				PublicationFrame.mainPanel.disableAll();
				PublicationButtonLinePanel.getNextButton().setEnabled(false);
				PublicationButtonLinePanel.getNextButton()
						.addMouseListener(PublicationMainPanel.blockedFieldMouseAdapter);
			}
		} else if (PropertyLoader.userValues.getProperty(PropertyLoader.AGREEMENT_PANEL_PROPERTY) != null) {
			String hashCodeOld = PropertyLoader.userValues.getProperty(PropertyLoader.AGREEMENT_PANEL_PROPERTY);
			String hashCodeNew = new AgreementPanel().getContentHash();

			if (hashCodeOld.equals(hashCodeNew)) {
				PublicationFrame.mainPanel.enableAll();
				PublicationButtonLinePanel.getNextButton().setEnabled(true);
			} else {
				PublicationFrame.mainPanel.disableAll();
				PublicationButtonLinePanel.getNextButton().setEnabled(false);
				PublicationButtonLinePanel.getNextButton()
						.addMouseListener(PublicationMainPanel.blockedFieldMouseAdapter);
			}
		} else {
			PublicationFrame.mainPanel.disableAll();
			PublicationButtonLinePanel.getNextButton().setEnabled(false);
			PublicationButtonLinePanel.getNextButton().addMouseListener(PublicationMainPanel.blockedFieldMouseAdapter);
		}
	}

	private void checkLoggedUserForCorrectEmailAddress(String loggedUser) throws Exception {

		Pattern p = Pattern.compile(".+@.+\\.[a-zA-Z]+");
		Matcher m = p.matcher(loggedUser);
		boolean matchFound = m.matches();
		if (!matchFound) {
			throw new Exception("Unable to start Publication Tool. No correct email address found ");
		} else {
			ClientDataManager.logger.info("Publication-Tool initiated for user '" + loggedUser + "'");
		}

	}

	/**
	 * Get the {@link ClientPrimaryDataDirectory} for the logged user or create a
	 * new one is not exist.
	 * 
	 * @return the {@link ClientPrimaryDataDirectory} of the current user.
	 */
	public static ClientPrimaryDataDirectory getUserDirectory() {

		ClientPrimaryDataDirectory userDirectory = null;

		try {
			userDirectory = (ClientPrimaryDataDirectory) PublicationFrame.rootDirectory
					.getPrimaryDataEntity(PublicationFrame.loggedUser);

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (PrimaryDataDirectoryException e) {
			try {
				userDirectory = PublicationFrame.rootDirectory.createPrimaryDataDirectory(PublicationFrame.loggedUser);
			} catch (RemoteException | PrimaryDataDirectoryException e1) {
				e1.printStackTrace();
			}
		}
		return userDirectory;
	}
}