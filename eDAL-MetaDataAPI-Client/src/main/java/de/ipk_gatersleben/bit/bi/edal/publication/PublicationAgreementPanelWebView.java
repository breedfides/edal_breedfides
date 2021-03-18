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
package de.ipk_gatersleben.bit.bi.edal.publication;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.sample.SimpleSwingBrowserFrame;

/**
 * Class to show a html page in a panel.
 * 
 * @author arendd
 */
@Deprecated
public class PublicationAgreementPanelWebView extends JPanel implements MouseListener, ChangeListener {

	private static final long serialVersionUID = 3713361378618251786L;

	private static JCheckBox CHECKBOX = new JCheckBox("Accept");
	private static JLabel TEXT = new JLabel("Please read and accept the ");
	private static JLabel LINK = new JLabel("Data Deposition and License Agreement");

	public PublicationAgreementPanelWebView() {

		LINK.addMouseListener(this);
		LINK.setForeground(Color.BLUE);
		LINK.setToolTipText("Open Deposition and License Agreement");
		LINK.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		LINK.setBackground(PropertyLoader.HEADER_FOOTER_COLOR);
		CHECKBOX.setBackground(PropertyLoader.HEADER_FOOTER_COLOR);

		if (PropertyLoader.userValues.getProperty("AGREEMENT") != null) {
			CHECKBOX.setEnabled(true);
			CHECKBOX.setSelected(false);
		} else {
			CHECKBOX.setEnabled(false);
			CHECKBOX.setSelected(false);
		}

		CHECKBOX.addChangeListener(this);

		this.setLayout(new FlowLayout());
		this.setBackground(PropertyLoader.HEADER_FOOTER_COLOR);
		this.add(CHECKBOX);

		this.add(TEXT);
		this.add(LINK);

	}

	@Override
	public void mouseClicked(MouseEvent event) {
		if (event.getSource().equals(LINK)) {

			SimpleSwingBrowserFrame browser = new SimpleSwingBrowserFrame(false);

			WebViewWorker ww = new WebViewWorker(browser);
			ww.execute();

		}

	}

	private class WebViewWorker extends SwingWorker<Integer, Integer> {

		private SimpleSwingBrowserFrame browser = null;

		public WebViewWorker(SimpleSwingBrowserFrame browser) {
			this.browser = browser;

		}

		@Override
		protected Integer doInBackground() throws Exception {
			InetSocketAddress i = EdalConfiguration.guessProxySettings();
			System.setProperty("http.proxyHost", i.getHostName());
			System.setProperty("http.proxyPort", String.valueOf(i.getPort()));
			System.setProperty("https.proxyHost", i.getHostName());
			System.setProperty("https.proxyPort", String.valueOf(i.getPort()));

			CountDownLatch latch = new CountDownLatch(1);

			SwingUtilities.invokeLater(new Runnable() {

				public void run() {
					browser.setVisible(true);
					browser.loadURL("https://edal.ipk-gatersleben.de/repos/pgp/agreement.html");
				}
			});
			try {
				latch.await(10, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void done() {

			if (browser.isLoadingFinished()) {
				CHECKBOX.setEnabled(true);
			} else {
				JOptionPane.showMessageDialog(null,
						"<html>Unable to load agreement website, <br/>please check your internet connection & proxy configuration</html>",
						"No Internet Connection", JOptionPane.ERROR_MESSAGE);
				browser.dispose();
			}
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

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource().equals(CHECKBOX)) {
			if (CHECKBOX.isEnabled()) {
				if (CHECKBOX.isSelected()) {
					PublicationFrame.getMainPanel().enableAll();
					PropertyLoader.setUserValue("AGREEMENT", "true");
				}
				if (!CHECKBOX.isSelected()) {
					PublicationFrame.getMainPanel().disableAll();
				}
			}

		}
	}

}