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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Desktop.Action;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import de.ipk_gatersleben.bit.bi.edal.primary_data.EdalConfiguration;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.ErrorDialog;

/**
 * Utility class for some static functions for handling and converting
 * {@link String}s and so on.
 * 
 * @author arendd
 */
public class Utils {

	public static int NumberOfDirectories = 0;
	public static int NumberOfFiles = 0;

	public static boolean checkIfStringIsEmpty(String string) {

		String temp = string.replaceAll(" ", "");
		temp = temp.replaceAll("\t", "");
		temp = temp.replaceAll("\n", "");

		return temp.isEmpty();
	}

	private static class ProgressPanelSwingWorker extends SwingWorker<Object, Object> {

		private Path srcDir = null;
		private FileVisitorCounter fileVisitorCounter = null;
		private ProgressPanel panel = null;
		private boolean finished = false;

		public ProgressPanelSwingWorker(ProgressPanel panel, Path srcDir, FileVisitorCounter fileVisitorCounter) {
			this.srcDir = srcDir;
			this.fileVisitorCounter = fileVisitorCounter;
			this.panel = panel;
			this.finished = false;
		}

		@Override
		protected Object doInBackground() throws Exception {
			Files.walkFileTree(srcDir, fileVisitorCounter);
			return true;
		}

		@Override
		protected void done() {
			super.done();
			try {
				finished = (boolean) get();
			} catch (ExecutionException | InterruptedException e) {
				ErrorDialog.showError(e);
			} catch (CancellationException e) {
				finished = false;
			}
			this.panel.dispose();
		}

		public boolean isFinished() {
			return finished;
		}
	}

	private static class ProgressInformationPanelSwingWorker extends SwingWorker<Object, Object> {

		private Path srcDir = null;
		private FileVisitorCounterWithErrorsAndWarnings fileVisitorCounter = null;
		private ProgressPanelWithInformation panel = null;
		private boolean finished = false;

		public ProgressInformationPanelSwingWorker(ProgressPanelWithInformation panel, Path srcDir,
				FileVisitorCounterWithErrorsAndWarnings fileVisitorCounter) {
			this.srcDir = srcDir;
			this.fileVisitorCounter = fileVisitorCounter;
			this.panel = panel;
			this.finished = false;
		}

		@Override
		protected Object doInBackground() throws Exception {
			Files.walkFileTree(srcDir, fileVisitorCounter);
			return true;
		}

		@Override
		protected void done() {
			super.done();
			try {
				finished = (boolean) get();
			} catch (ExecutionException | InterruptedException e) {
				ErrorDialog.showError(e);
			} catch (CancellationException e) {
				finished = false;
			}
			this.panel.dispose();
		}

		public boolean isFinished() {
			return finished;
		}
	}

	private static class ProgressPanel extends JDialog {

		private static final long serialVersionUID = 4135429680729049866L;
		private JProgressBar pendingBar;

		public ProgressPanel(Frame parent) {

			super(parent, "Validating Files...");

			this.setFocusable(true);
			this.setModal(true);

			this.pendingBar = new JProgressBar();
			this.pendingBar.setIndeterminate(true);
			this.pendingBar.setStringPainted(true);

			final JPanel barPanel = new JPanel(new BorderLayout());
			barPanel.setMaximumSize(new Dimension(400, 60));
			barPanel.add(this.pendingBar, BorderLayout.CENTER);

			this.setContentPane(barPanel);
			this.setResizable(false);
			this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			this.setPreferredSize(new Dimension(400, 60));
			this.pack();
			this.setLocationRelativeTo(null);

			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					int result = JOptionPane.showConfirmDialog(null, "Stop File Validation ?", "EXIT",
							JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION) {
						dispose();
					}
				}
			});
		}

		public JProgressBar getPendingBar() {
			return this.pendingBar;
		}
	}

	private static class ProgressPanelWithInformation extends JDialog {

		private static final long serialVersionUID = 4135429680729049866L;
		private JProgressBar pendingBar;
		private JLabel errorLabel = new JLabel("Errors: ");
		private JLabel errorNumberLabel = new JLabel("0");
		private JLabel warningLabel = new JLabel("Warnings: ");
		private JLabel warningNumberLabel = new JLabel("0");

		public ProgressPanelWithInformation(Frame parent) {

			super(parent, "Validating Files...");

			this.setFocusable(true);
			this.setIconImage(PropertyLoader.EDAL_ICON);
			this.setModal(true);

			this.pendingBar = new JProgressBar();
			this.pendingBar.setIndeterminate(true);
			this.pendingBar.setStringPainted(true);

			final JPanel barPanel = new JPanel(new BorderLayout());
			barPanel.setMaximumSize(new Dimension(400, 80));
			barPanel.add(this.pendingBar, BorderLayout.CENTER);

			JPanel mainPanel = new JPanel(new BorderLayout());

			mainPanel.add(barPanel, BorderLayout.NORTH);

			JPanel middlePanel = new JPanel(new GridLayout(1, 4));

			middlePanel.add(errorLabel);
			middlePanel.add(errorNumberLabel);
			middlePanel.add(warningLabel);
			middlePanel.add(warningNumberLabel);

			mainPanel.add(middlePanel, BorderLayout.CENTER);
			this.setContentPane(mainPanel);
			this.setResizable(false);
			this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			this.setPreferredSize(new Dimension(400, 80));
			this.pack();
			this.setLocationRelativeTo(null);

			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					int result = JOptionPane.showConfirmDialog(null, "Stop File Validation ?", "EXIT",
							JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION) {
						dispose();
					}
				}
			});
		}

		public JProgressBar getPendingBar() {
			return this.pendingBar;
		}

		public JLabel getErrorNumberLabel() {
			return this.errorNumberLabel;
		}

		public JLabel getWarningNumberLabel() {
			return this.warningNumberLabel;
		}
	}

	private static class ErrorPanel extends JDialog implements ActionListener, WindowListener {

		private static final long serialVersionUID = 4135429680729049866L;

		private JButton retryButton = new JButton("Retry");
		private JButton ignoreButton = new JButton("Ignore");
		private JButton abortButton = new JButton("Abort");
		private int result = 0;
		private JEditorPane pane = new JEditorPane();
		private JScrollPane scrollPane = new JScrollPane();

		public ErrorPanel(Frame parent, List<Path> errorFiles, List<Path> nullByteFiles) {

			super(parent, "Errors/Warnings found, please correct to continue !");
			this.setIconImage(PropertyLoader.EDAL_ICON);
			this.setFocusable(true);
			this.setModal(true);

			JPanel mainPanel = new JPanel(new BorderLayout());

			JPanel buttonPanel = new JPanel(new FlowLayout());

			retryButton.addActionListener(this);
			ignoreButton.addActionListener(this);
			abortButton.addActionListener(this);

			this.pane.setContentType("text/html");

			StringBuffer buffer = new StringBuffer();
			buffer.append("<HTML><BODY>");

			if (errorFiles.size() == 0) {
				ignoreButton.setEnabled(true);
			} else {
				ignoreButton.setEnabled(false);
				buffer.append("ERRORS -> Symbolic Links : <br>");
				for (Path path : errorFiles) {
					buffer.append(path.toString() + "<br>");
				}
				buffer.append("\n");
			}
			if (nullByteFiles.size() > 0) {
				buffer.append("WARNING -> Null Byte Files : <br>");
				for (Path path : nullByteFiles) {
					buffer.append(path.toString() + "<br>");
				}
			}

			buffer.append("</BODY></HTML>");

			this.pane.setText(buffer.toString());
			this.pane.setPreferredSize(new Dimension(600, 300));

			scrollPane = new JScrollPane(this.pane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setPreferredSize(new Dimension(600, 300));

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					scrollPane.getViewport().setViewPosition(new java.awt.Point(0, 0));
				}
			});

			mainPanel.add(scrollPane, BorderLayout.CENTER);

			buttonPanel.add(retryButton);
			buttonPanel.add(ignoreButton);
			buttonPanel.add(abortButton);
			mainPanel.add(buttonPanel, BorderLayout.SOUTH);

			this.setContentPane(mainPanel);

			this.addWindowListener(this);

			this.setResizable(false);
			this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			this.setPreferredSize(new Dimension(600, 400));
			this.pack();
			this.setLocationRelativeTo(null);
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			if (e.getSource().equals(retryButton)) {
				this.setResult(0);
				this.dispose();
			}
			if (e.getSource().equals(ignoreButton)) {
				this.setResult(1);
				this.dispose();
			}
			if (e.getSource().equals(abortButton)) {
				this.setResult(2);
				this.dispose();
			}
		}

		public int getResult() {
			return result;
		}

		private void setResult(int result) {
			this.result = result;
		}

		@Override
		public void windowOpened(WindowEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void windowClosing(WindowEvent e) {
			this.setResult(2);
			this.dispose();
		}

		@Override
		public void windowClosed(WindowEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			// TODO Auto-generated method stub
		}

	}

	private static class FileVisitorCounter implements FileVisitor<Path> {

		private int counter = 0;
		private ProgressPanel panel = null;

		private FileVisitorCounter(ProgressPanel panel, int counter) {
			this.counter = counter;
			this.panel = panel;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

			if (Files.isSymbolicLink(file) || file.toFile().getName().endsWith(".lnk")) {
				throw new IOException("Please remove or resolve symbolic link : " + file.toString());
			}

			this.panel.getPendingBar().setString(file.getFileName().toString());

			this.counter++;

			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
			return FileVisitResult.CONTINUE;

		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

			if (Files.isSymbolicLink(dir) || dir.toFile().getName().endsWith(".lnk")) {
				throw new IOException("Please remove or resolve symbolic link : " + dir.toString());
			}

			this.panel.getPendingBar().setString(dir.getFileName().toString());

			this.counter++;

			return FileVisitResult.CONTINUE;

		}

		public int getCounter() {
			return this.counter;
		}

	}

	private static class FileVisitorCounterWithErrorsAndWarnings implements FileVisitor<Path> {

		private int counter = 0;
		private ProgressPanelWithInformation panel = null;
		private List<Path> symbolicLinks = new ArrayList<Path>();
		private int numberSymbolicLinks = 0;

		private List<Path> nullByteFiles = new ArrayList<Path>();
		private int numberNullByteFiles = 0;

		private FileVisitorCounterWithErrorsAndWarnings(ProgressPanelWithInformation panel, int counter) {
			Utils.NumberOfDirectories = 0;
			Utils.NumberOfFiles = 0;
			this.counter = counter;
			this.panel = panel;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			NumberOfDirectories++;
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			NumberOfFiles++;
			if (Files.isSymbolicLink(file) || file.toFile().getName().endsWith(".lnk")) {
				symbolicLinks.add(file);
				numberSymbolicLinks++;
				this.panel.getErrorNumberLabel().setText(String.valueOf(numberSymbolicLinks));
			}

			if (Files.size(file) == 0) {
				nullByteFiles.add(file);
				numberNullByteFiles++;
				this.panel.getWarningNumberLabel().setText(String.valueOf(numberNullByteFiles));
			}

			String parent = file.getParent().toString();

			if (parent.toString().length() <= 20) {
				this.panel.getPendingBar().setString(file.toString());

			} else {
				String label = parent.substring(0, 19) + "..." + File.separatorChar + file.getFileName().toString();
				this.panel.getPendingBar().setString(label);
			}

			this.counter++;

			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
			return FileVisitResult.CONTINUE;

		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

			if (Files.isSymbolicLink(dir) || dir.toFile().getName().endsWith(".lnk")) {

				symbolicLinks.add(dir);
				numberSymbolicLinks++;
				this.panel.getErrorNumberLabel().setText(String.valueOf(numberSymbolicLinks));
			}

			this.panel.getPendingBar().setString(dir.getFileName().toString());

			this.counter++;

			return FileVisitResult.CONTINUE;

		}

		public int getCounter() {
			return this.counter;
		}

		public List<Path> getFoundedSymbolicLinks() {
			return this.symbolicLinks;
		}

		public List<Path> getNullByteFiles() {
			return this.nullByteFiles;
		}

	}

	public static int countObjects(final Frame parent, final Path srcDir) {

		ProgressPanel panel = new ProgressPanel(parent);

		FileVisitorCounter fileVisitorCounter = new FileVisitorCounter(panel, 0);

		ProgressPanelSwingWorker worker = new ProgressPanelSwingWorker(panel, srcDir, fileVisitorCounter);

		worker.execute();

		panel.setVisible(true);

		if (worker.isFinished() == true) {
			return fileVisitorCounter.getCounter();
		} else {
			worker.cancel(true);
			return 0;
		}
	}

	public static int countObjectsWithErrorsAndWarnings(final Frame parent, final Path srcDir) {

		ProgressPanelWithInformation panel = new ProgressPanelWithInformation(parent);

		FileVisitorCounterWithErrorsAndWarnings fileVisitorCounter = new FileVisitorCounterWithErrorsAndWarnings(panel,
				0);

		ProgressInformationPanelSwingWorker worker = new ProgressInformationPanelSwingWorker(panel, srcDir,
				fileVisitorCounter);

		worker.execute();
		panel.setVisible(true);
		if (worker.isFinished() == true) {

			ErrorPanel errorPanel = new ErrorPanel(parent, fileVisitorCounter.getFoundedSymbolicLinks(),
					fileVisitorCounter.getNullByteFiles());

			while (fileVisitorCounter.getFoundedSymbolicLinks().size() > 0
					|| fileVisitorCounter.getNullByteFiles().size() > 0 && errorPanel.getResult() == 0) {

				errorPanel.setVisible(true);

				if (errorPanel.getResult() == 0) {
					panel = new ProgressPanelWithInformation(parent);

					fileVisitorCounter = new FileVisitorCounterWithErrorsAndWarnings(panel, 0);

					worker = new ProgressInformationPanelSwingWorker(panel, srcDir, fileVisitorCounter);

					worker.execute();

					panel.setVisible(true);

					errorPanel = new ErrorPanel(parent, fileVisitorCounter.getFoundedSymbolicLinks(),
							fileVisitorCounter.getNullByteFiles());
				}

				else if (errorPanel.getResult() == 1) {
					break;
				} else if (errorPanel.getResult() == 2) {
					return 0;
				}

			}

			return fileVisitorCounter.getCounter();
		} else {
			worker.cancel(true);
			return 0;
		}
	}

	public static int countFilesAndDirectoriesInEdalDirectory(ClientPrimaryDataEntity srcDir)
			throws RemoteException, PrimaryDataDirectoryException {

		if (!srcDir.isDirectory()) {
			return 1;
		}

		ClientPrimaryDataDirectory directory = (ClientPrimaryDataDirectory) srcDir;

		int count = 0;
		for (ClientPrimaryDataEntity file : directory.listPrimaryDataEntities()) {
			count++;
			if (file.isDirectory()) {
				count += countFilesAndDirectoriesInEdalDirectory((ClientPrimaryDataDirectory) file);
			}
		}
		return count;
	}

	public static void addComponent(Container cont, GridBagLayout gbl, Component c, int x, int y, int width, int height,
			double weightx, double weighty, int ipadx, int ipady, Insets insets) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.ipadx = ipadx;
		gbc.ipady = ipady;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbc.insets = insets;
		gbl.setConstraints(c, gbc);
		cont.add(c);
	}

	public static void add(Container container, GridBagLayout gridBagLayout, Component c, int gridx, int gridy) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.insets = new Insets(2, 5, 2, 5);
		gridBagLayout.setConstraints(c, gbc);
		container.add(c);
	}

	public static void add(Container cont, GridBagLayout gbl, Component c, int x, int y, int width, int height,
			double weightx, double weighty, int ipadx, int ipady) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.ipadx = ipadx;
		gbc.ipady = ipady;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbl.setConstraints(c, gbc);
		cont.add(c);
	}

	public static void add(Container container, GridBagLayout gridBagLayout, Component c, int gridx, int gridy,
			int ipadx, int ipady) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.ipadx = ipadx;
		gbc.ipady = ipady;
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gridBagLayout.setConstraints(c, gbc);
		container.add(c);
	}

	/**
	 * Function to open a browser and loading the given URL.
	 * 
	 * @param url
	 *            the URL to open in a browser
	 * @return true if the function was able to open the browser
	 */
	public static boolean openURL(String url) {

		InetSocketAddress proxy = EdalConfiguration.guessProxySettings();
		if (proxy != null) {
			System.setProperty("http.proxyHost", proxy.getHostName());
			System.setProperty("http.proxyPort", String.valueOf(proxy.getPort()));
			System.setProperty("https.proxyHost", proxy.getHostName());
			System.setProperty("https.proxyPort", String.valueOf(proxy.getPort()));
		}

		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
			try {
				if (url.startsWith("mailto")) {
					Desktop.getDesktop().mail(URI.create(url));
				} else {
					Desktop.getDesktop().browse(URI.create(url));
				}
				return true;
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "unable to open Browser :",
						e.getMessage() + "\n please enter the URL manually into your browser: '" + url + "'", 0);
				return false;
			}
		} else {
			String os = System.getProperty("os.name").toLowerCase();
			Runtime rt = Runtime.getRuntime();

			try {

				if (os.indexOf("win") >= 0) {

					// this doesn't support showing urls in the form of
					// "page.html#nameLink"
					rt.exec("rundll32 url.dll,FileProtocolHandler " + url);

				} else if (os.indexOf("mac") >= 0) {

					rt.exec("open " + url);

				} else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {

					// Do a best guess on unix until we get a platform
					// independent way
					// Build a list of browsers to try, in this order.
					String[] browsers = { "epiphany", "firefox", "mozilla", "konqueror", "netscape", "opera", "links",
							"lynx" };

					// Build a command string which looks like "browser1
					// "url" || browser2 "url" ||..."
					StringBuffer cmd = new StringBuffer();
					for (int i = 0; i < browsers.length; i++)
						cmd.append((i == 0 ? "" : " || ") + browsers[i] + " \"" + url + "\" ");

					rt.exec(new String[] { "sh", "-c", cmd.toString() });

				} else {
					JOptionPane.showMessageDialog(null, "unable to find OS versino to open Browser ",
							"\n Please enter the URL manually into your browser: '" + url + "'", 0);
					return false;
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "unable to open Browser :",
						e.getMessage() + "\n please enter the URL manually into your browser: '" + url + "'", 0);
				return false;
			}
			return true;
		}

	}
}