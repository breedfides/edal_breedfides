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
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.apache.commons.codec.digest.DigestUtils;

import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

/**
 * Class to build the agreement panel on top of the application
 * 
 * @author arendd
 */
public class PublicationAgreementPanelSwingView extends JPanel implements MouseListener {

	private static final long serialVersionUID = 3713361378618251786L;

	private JLabel textLabel = new JLabel("Before you fill in the form, please read the");
	private JLabel linkLabel = new JLabel("Deposition and License Agreement");
	private JLabel textLabel2 = new JLabel("[");
	private JLabel pdfLabel = new JLabel(
			"<html><a href=\"" + PropertyLoader.PGP_CONTRACT_URL.toString() + "\">PDF</a></html>",
			PropertyLoader.PDF_ICON, SwingConstants.CENTER);
	private JLabel textLabel3 = new JLabel("file, get free Adobe Reader");
	private JLabel adobeLabel = new JLabel(
			"<html><a href=\"" + PropertyLoader.ADOBE_GET_URL.toString() + "\">here</a></html>",
			PropertyLoader.ADOBE_ICON, SwingConstants.CENTER);
	private JLabel textLabel4 = new JLabel("]. It has to be accepted when you submit data.");

	public PublicationAgreementPanelSwingView() {

		this.pdfLabel.setIconTextGap(1);

		this.linkLabel.addMouseListener(this);
		this.linkLabel.setForeground(Color.BLUE);
		this.linkLabel.setFont(PropertyLoader.AGREEMENT_PANEL_FONT);
		this.linkLabel.setToolTipText("Open Deposition and License Agreement");
		this.linkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.linkLabel.setBackground(PropertyLoader.HEADER_FOOTER_COLOR);

		this.pdfLabel.addMouseListener(this);
		this.pdfLabel.setForeground(Color.BLUE);
		this.pdfLabel.setFont(PropertyLoader.AGREEMENT_PANEL_FONT);
		this.pdfLabel.setToolTipText("Download Deposition and License Agreement");
		this.pdfLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.adobeLabel.addMouseListener(this);
		this.adobeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.adobeLabel.setHorizontalTextPosition(JLabel.RIGHT);
		this.textLabel.setFont(PropertyLoader.AGREEMENT_PANEL_FONT);

		this.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 5));
		this.setBackground(PropertyLoader.HEADER_FOOTER_COLOR);

		this.add(this.textLabel);
		this.add(this.linkLabel);
		this.add(this.textLabel2);
		this.add(this.pdfLabel);
		this.add(this.textLabel3);
		this.add(this.adobeLabel);
		this.add(this.textLabel4);
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		if (event.getSource().equals(this.linkLabel)) {
			AgreementPanel dialog = new AgreementPanel();

			dialog.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent event) {

				}
			});
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setModal(true);
			dialog.setVisible(true);

			PublicationFrame.getMainPanel().enableAll();
			PublicationButtonLinePanel.getNextButton().setEnabled(true);
			PublicationButtonLinePanel.getNextButton()
					.removeMouseListener(PublicationMainPanel.blockedFieldMouseAdapter);
			PropertyLoader.setUserValue(PropertyLoader.AGREEMENT_PANEL_PROPERTY, dialog.getContentHash());

		}
		if (event.getSource().equals(this.pdfLabel)) {
			boolean loadingSuccessful = EdalHelpers.openURL(PropertyLoader.PGP_CONTRACT_URL.toString());

			if (loadingSuccessful) {
				PublicationFrame.getMainPanel().enableAll();
				PublicationButtonLinePanel.getNextButton().setEnabled(true);
				PublicationButtonLinePanel.getNextButton()
						.removeMouseListener(PublicationMainPanel.blockedFieldMouseAdapter);

				try {
					String checkSum = DigestUtils.md5Hex(PropertyLoader.PGP_CONTRACT_URL.openStream());
					PropertyLoader.setUserValue(PropertyLoader.AGREEMENT_PDF_PROPERTY, checkSum);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
		if (event.getSource().equals(this.adobeLabel)) {
			EdalHelpers.openURL(PropertyLoader.ADOBE_GET_URL.toString());
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

}