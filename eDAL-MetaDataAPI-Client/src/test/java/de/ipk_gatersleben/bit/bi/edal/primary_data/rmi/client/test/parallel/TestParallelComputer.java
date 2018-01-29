/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.rmi.client.test.parallel;

import org.junit.Test;
import org.junit.experimental.ParallelComputer;
import org.junit.runner.JUnitCore;

import de.ipk_gatersleben.bit.bi.edal.primary_data.rmi.client.test.ClientServerTest;

public class TestParallelComputer extends ClientServerTest {
	public void test() {
		Class<?>[] cls = { RmiUploadDownloadOne.class, RmiUploadDownloadTwo.class };

		// Parallel among classes
		// JUnitCore.runClasses(ParallelComputer.classes(), cls);

		// Parallel among methods in a class
		// JUnitCore.runClasses(ParallelComputer.methods(), cls);

		// Parallel all methods in all classes
		JUnitCore.runClasses(new ParallelComputer(true, true), cls);
	}

	public static class ParallelTest1 {
		@Test
		public void a() throws Exception {
			System.out.println("1a");

		}

		@Test
		public void b() throws Exception {
			System.out.println("1b");
		}
	}

	public static class ParallelTest2 {
		@Test
		public void a() {
			System.out.println("2a");
		}

		@Test
		public void b() {
			System.out.println("2b");
		}
	}
}
