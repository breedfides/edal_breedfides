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
/**
 * uncommented due to some problems with junit 5 and jdk 11 
 */
//package de.ipk_gatersleben.bit.bi.edal.primary_data.rmi.client.test.parallel;
//
//import org.junit.jupiter.api.Test;
//import org.junit.experimental.ParallelComputer;
//import org.junit.runner.JUnitCore;
//
//import de.ipk_gatersleben.bit.bi.edal.primary_data.rmi.client.test.ClientServerTest;
//
//public class TestParallelComputer extends ClientServerTest {
//	public void test() {
//		Class<?>[] cls = { RmiUploadDownloadOne.class, RmiUploadDownloadTwo.class };
//
//		// Parallel among classes
//		// JUnitCore.runClasses(ParallelComputer.classes(), cls);
//
//		// Parallel among methods in a class
//		// JUnitCore.runClasses(ParallelComputer.methods(), cls);
//
//		// Parallel all methods in all classes
//		JUnitCore.runClasses(new ParallelComputer(true, true), cls);
//	}
//
//	public static class ParallelTest1 {
//		@Test
//		public void a() throws Exception {
//			System.out.println("1a");
//
//		}
//
//		@Test
//		public void b() throws Exception {
//			System.out.println("1b");
//		}
//	}
//
//	public static class ParallelTest2 {
//		@Test
//		public void a() {
//			System.out.println("2a");
//		}
//
//		@Test
//		public void b() {
//			System.out.println("2b");
//		}
//	}
//}
