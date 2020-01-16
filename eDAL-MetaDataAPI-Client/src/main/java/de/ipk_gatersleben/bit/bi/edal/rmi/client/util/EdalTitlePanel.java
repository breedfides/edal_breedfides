/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.util;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.ImageUtil;


public class EdalTitlePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	public static String TITLEORIGINALSIZE = "title_original_size";
	private String title;
	private Image icon;
	private Window window;
	private boolean textInCenter = false;
	private int xGap = 1;
	private int yGap = 5;

	private JLabel titleLabel = new JLabel();
	private JLabel iconLabel = new JLabel();
	private int titleHeight = 24;

	private Point pressPoint;

	private JButton closeButton;
	private JButton resizeButton;
	private JButton minButton;
	private Icon restoreIcon = ImageUtil.createImageIcon("window_border_reset.png", "window_border_reset.png");
	private Icon maxIcon = ImageUtil.createImageIcon("window_border_max.png", "window_border_max.png");
	private Icon minIcon = ImageUtil.createImageIcon("window_border_min.png", "window_border_min.png");
	private Icon closeIcon = ImageUtil.createImageIcon("window_border_close.png", "window_border_close.png");

	private boolean resizable = true;

	private boolean maxed;

	public EdalTitlePanel(Window window) {
		this(window, null);
	}

	public EdalTitlePanel(final Window window, String title) {
		this.window = window;
		this.setTitle(title);
		installListener();

		this.closeButton = createButton(closeIcon);
		this.resizeButton = createButton(maxIcon);
		this.minButton = createButton(minIcon);
		ActionListener buttonListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == closeButton) {
					window.setVisible(false);
					window.dispose();
				} else if (e.getSource() == resizeButton) {
					if (isMaxed()) {
						resetWindow();
					} else {
						setWindowMax();
					}
				} else if (e.getSource() == minButton) {
					((JFrame) window).setExtendedState(JFrame.ICONIFIED);
				}
			}
		};
		this.closeButton.addActionListener(buttonListener);
		this.resizeButton.addActionListener(buttonListener);
		this.minButton.addActionListener(buttonListener);

		this.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 1));
		if (window instanceof JFrame) {
			add(this.minButton);
		}
		add(this.resizeButton);
		add(this.closeButton);

	}

	public JButton createButton(Icon icon) {
		final JButton button = new JButton(icon);
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setContentAreaFilled(false);
		button.setBorderPainted(true);
		button.setFocusable(false);
		button.setPreferredSize(new Dimension(icon.getIconWidth(),icon.getIconHeight()));
		return button;
	}

	public boolean isMaxed() {
		return maxed;
	}

	public void setWindowMax() {
		this.maxed = true;
		resizeButton.setIcon(restoreIcon);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenSize.height -= 30;
		this.putClientProperty(TITLEORIGINALSIZE, window.getBounds());
		setWindowBounds(new Rectangle(screenSize));
	}

	public void resetWindow() {
		this.maxed = false;
		resizeButton.setIcon(maxIcon);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle rect = getOriginalBounds();
		if (rect == null) {
			rect = new Rectangle(screenSize.width / 4, screenSize.height / 4, screenSize.width / 2, screenSize.height / 2);
		}
		setWindowBounds(rect);
	}

	private void setWindowBounds(Rectangle rect) {
		this.putClientProperty(TITLEORIGINALSIZE, window.getBounds());
		window.setBounds(new Rectangle(rect));
	}

	private Rectangle getOriginalBounds() {
		Object obj = getClientProperty(TITLEORIGINALSIZE);
		if (obj != null) {
			return (Rectangle) obj;
		} else {
			return null;
		}
	}

	
	private void installListener() {
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				pressPoint = e.getPoint();
			}

			public void mouseReleased(MouseEvent e) {
				pressPoint = null;
				if (SwingUtilities.isRightMouseButton(e)) {
					
				}
			}

			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && isResizable()) {
					if (isMaxed()) {
						resetWindow();
					} else {
						setWindowMax();
					}
				}
			}

			public void mouseEntered(MouseEvent e) {
				setCursor(Cursor.getDefaultCursor());
			}
		});
		this.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {
			}

			public void mouseDragged(MouseEvent e) {
				if (pressPoint != null && !isMaxed()) {
					Point point = window.getLocation();
					double xoffset = e.getX() - pressPoint.getX();
					double yoffset = e.getY() - pressPoint.getY();
					window.setLocation((int) (point.getX() + xoffset), (int) (point.getY() + yoffset));
				}
			}
		});
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		titleLabel.setText(title);
		this.repaint();
	}

	public Image getIcon() {
		return icon;
	}

	public void setIcon(Image iconImage) {
		this.icon = iconImage;
		if (iconImage != null) {
			ImageIcon icon = new ImageIcon(iconImage);
			iconLabel.setIcon(icon);
			this.repaint();
		}
	}

	public Dimension getPreferredSize() {
		Dimension size = super.getPreferredSize();
		int h = titleHeight;
		int labelHeight = titleLabel.getPreferredSize().height;
		if (labelHeight > h) {
			h = labelHeight;
		}
		int iconHeight = iconLabel.getPreferredSize().height + yGap;
		if (h < iconHeight) {
			h = iconHeight;
		}
		return new Dimension(size.width, titleHeight);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		TexturePaint paint = ImageUtil.createTexturePaint("outlook_bar_background.png");
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setPaint(paint);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		Rectangle bounds = this.getBounds();
		int xOffset = xGap;
		if (this.icon != null) {
			Dimension imageSize = iconLabel.getPreferredSize();
			double y = bounds.height / 2 - imageSize.height / 2;
			SwingUtilities.paintComponent(g2d, iconLabel, this, xGap, (int) y, imageSize.width, imageSize.height);
			xOffset += imageSize.width;
		}
		if (getTitle() != null) {
			Dimension labelSize = titleLabel.getPreferredSize();
			double x = bounds.getX() + xOffset;
			if (textInCenter) {
				x = bounds.getWidth() / 2 - labelSize.width / 2.0;
			}
			double y = bounds.height / 2 - labelSize.height / 2;
			SwingUtilities.paintComponent(g2d, titleLabel, this, (int) x, (int) y, labelSize.width, labelSize.height);
		}
		g2d.dispose();

	}

	public boolean isTextInCenter() {
		return textInCenter;
	}

	public void setTextInCenter(boolean textInCenter) {
		this.textInCenter = textInCenter;
		this.repaint();
	}

	public boolean isResizable() {
		return resizable;
	}

	public void setResizable(boolean resizable) {
		this.resizable = resizable;
		resizeButton.setEnabled(resizable);
	}
}