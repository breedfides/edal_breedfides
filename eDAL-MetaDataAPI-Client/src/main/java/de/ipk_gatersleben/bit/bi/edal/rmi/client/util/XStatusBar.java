/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.rmi.client.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.TexturePaint;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.Border;


public class XStatusBar extends JPanel{
	private static final long serialVersionUID = 1L;
	private Image backgroundLeftImage;
	private Image backgroundRightImage;
	private ImageIcon backgroundImageIcon;
	private TexturePaint paint;
	private JPanel leftPane;
	private JPanel rightPane;
	private Border border;
	private XStatusLabel serverinfo;
	private XStatusLabel userinfo;
	
	public XStatusBar(String host, int port, String username)
	{
		backgroundLeftImage = ImageUtil.getImage("statusbar_background_left.png");
		backgroundRightImage = ImageUtil.getImage("statusbar_background_right.png");
		backgroundImageIcon = ImageUtil.createImageIcon("statusbar_background.png","");
		paint = ImageUtil.createTexturePaint("outlook_bar_background.png");
		leftPane = new JPanel(new BorderLayout());
		rightPane = new JPanel(new FlowLayout(3, 0, 0));
		border = BorderFactory.createEmptyBorder(0, 10, 0, 0);
		init(host, port, username);
	}
	private void init(String host, int port, String username)
	{
		setLayout(new BorderLayout());
		add(leftPane, "Center");
		add(rightPane, "East");
		setBorder(border);
		leftPane.setOpaque(false);
		rightPane.setOpaque(false);
		
		addDefaultSubLabel(host, port, username);
	}
	private void addDefaultSubLabel(String host, int port, String username)
	{
		leftPane.add(new XStatusMessageLabel(),BorderLayout.CENTER);
		addSeparator();
		serverinfo = new XStatusLabel(host+":"+port,ImageUtil.createImageIcon("server_12x12.png",""));
		rightPane.add(serverinfo);
		addSeparator();
		userinfo = new XStatusLabel(username,ImageUtil.createImageIcon("user_12x12.png",""));
		rightPane.add(userinfo);
		addSeparator();
		rightPane.add(new XStatusTimeLabel());	
		
		
	}
	
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setPaint(paint);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		g2d.drawImage(backgroundLeftImage, 0, 0, null);
		g2d.drawImage(backgroundRightImage, getWidth() - backgroundRightImage.getWidth(null), 0, null);
	}
	

	public JPanel getLeftPane()
	{
		return leftPane;
	}

	public JPanel getRightPane()
	{
		return rightPane;
	}

	public void addSeparator()
	{
		rightPane.add(new XStatusSeparator());
	}

	public Dimension getPreferredSize()
	{
		return new Dimension(super.getPreferredSize().width, backgroundImageIcon.getIconHeight()-5);
	}
	
	
	public void updateStatus(final String host, final int port, final String username)
	{
		Thread thread = new Thread()
		{
			public void run()
			{
									
				serverinfo.setText(host+":"+port);					
				userinfo.setText(username);
			}
		};
		thread.start();
	}
}
