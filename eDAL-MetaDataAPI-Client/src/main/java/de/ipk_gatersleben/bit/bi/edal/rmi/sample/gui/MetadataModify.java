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
package de.ipk_gatersleben.bit.bi.edal.rmi.sample.gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.rmi.RemoteException;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalAuthenticateException;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.EdalFileChooser;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.ErrorDialog;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.editor.MetaDataSaveDialog;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.StackTraceUtil;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.Authentication;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class MetadataModify {

	public static JFrame frame;
	private static ClientPrimaryDataEntity[] files;
	private static Map<EnumDublinCoreElements, UntypedData> metadatavalue;
	private static String serveraddress = null;
	private static int serverport = 0;

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage:    de.ipk_gatersleben.bit.bi.edal.rmi.sample.gui.Main option(s)");
			System.out.println("Options:    the first parameter is servername, the second parametr is serverport");
			System.exit(-1);
		} else {
			serveraddress = args[0].trim();
			String sport = args[1].trim();
			try {
				serverport = Integer.parseInt(sport);
			} catch (Exception e) {
				System.out.println("Usage:    de.ipk_gatersleben.bit.bi.edal.rmi.sample.gui.Main option(s)");
				System.out.println("Options:    the first parameter is servername, the second parametr is serverport");
				System.exit(-1);
			}
		}

		for (final LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
			if (laf.getName().equals("Nimbus")) {
				try {
					UIManager.setLookAndFeel(laf.getClassName());
				} catch (final ClassNotFoundException e) {

				} catch (final InstantiationException e) {

				} catch (final IllegalAccessException e) {

				} catch (final UnsupportedLookAndFeelException e) {

				}
			}
		}

		frame = new JFrame("MetadataDemo");

		JPanel pane = new JPanel();
		pane.add(new JButton(openfileAction));
		pane.add(new JButton(openmetadataAction));

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(pane, BorderLayout.CENTER);
		frame.setSize(600, 300);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	static Action openfileAction = new AbstractAction("Open EDAL File") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			Window window = frame.getContentPane() instanceof Window ? (Window) frame.getContentPane()
					: SwingUtilities.getWindowAncestor(frame.getContentPane());

			try {
				Authentication auth = new Authentication(EdalHelpers.authenticateSampleUser());
				ClientDataManager client = new ClientDataManager(serveraddress, serverport, auth);

				EdalFileChooser dlg = window instanceof Frame ? new EdalFileChooser((Frame) window, client)
						: new EdalFileChooser((Dialog) window, client);
				dlg.setMultiSelectionEnabled(false);
				dlg.setFileSelectionMode(EdalFileChooser.FILES_AND_DIRECTORIES);
				int returnVal = dlg.showOpenDialog();
				if (returnVal == EdalFileChooser.APPROVE_OPTION) {
					files = dlg.getSelectedFiles();
				}
			} catch (EdalAuthenticateException e1) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
				ErrorDialog.showError(e1);
			}

		}
	};

	static Action openmetadataAction = new AbstractAction("Show Metadata") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if (files != null) {
				for (ClientPrimaryDataEntity file : files) {
					try {
						if (file.isDirectory()) {
							continue;
						}
					} catch (RemoteException e1) {
						ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
						ErrorDialog.showError(e1);
					}
					MetaDataSaveDialog metadlg = new MetaDataSaveDialog(file);
					int returnVal = metadlg.showOpenDialog();
					if (returnVal == MetaDataSaveDialog.APPROVE_OPTION) {
						metadatavalue = metadlg.getMetaDataValues();
						System.out.println(metadatavalue);
					}
				}
			}
		}
	};
}
