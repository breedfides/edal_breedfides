/**
 * Copyright (c) 2019 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.data_manager;

import junit.framework.Assert;

import org.junit.Test;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.AsynchronList;

/**
 * @author lange
 */
public class TestAsynchronousList implements Runnable {
	private static final int sleep = 50;
	private static final int bucket_size = 2;
	private static final int SizeTestList = 50;

	protected AsynchronList<String> list;
	private Thread producer_thread;

	public void init() {
		this.list = new AsynchronList<String>();
		this.producer_thread = new Thread(this);
		this.producer_thread.start();
	}

	public void run() {
		try {
			Thread.sleep(TestAsynchronousList.sleep);
			for (int i = 0; i < TestAsynchronousList.SizeTestList; i++) {
				this.list.add("" + i);
				// finished to insert a bucket
				if (i % TestAsynchronousList.bucket_size == 0) {
					this.list.notifyNewDataAvailable();

					Thread.sleep(TestAsynchronousList.sleep);

				}

			}
		} catch (final InterruptedException e) {
		}

		this.list.notifyNoMoreNewData();
	}

	@Test
	public final void testClear() {
		this.init();
		this.list.clear();
		try {
			Thread.sleep(TestAsynchronousList.sleep * 2);
		} catch (final InterruptedException e) {
		}
		Assert.assertEquals(0, this.list.size());
	}

	@Test
	public final void testContains() {
		this.init();
		Assert.assertTrue(this.list.contains(""
				+ (TestAsynchronousList.SizeTestList - 1)));
	}

	/**
	 * Test method for
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.AsynchronList#get(int)}
	 * .
	 */
	@Test(timeout = (int) (TestAsynchronousList.SizeTestList * 2.0)
			* TestAsynchronousList.sleep / TestAsynchronousList.bucket_size)
	public final void testGet() {
		int i;
		this.init();
		for (i = 0; i < TestAsynchronousList.SizeTestList; i++) {
			this.list.get(i);
			try {
				Thread.sleep(0 + (int) (Math.random()
						* TestAsynchronousList.sleep * 0.85));
			} catch (final InterruptedException e) {

			}
		}
		Assert.assertEquals(TestAsynchronousList.SizeTestList, i);
	}

	@Test
	public final void testIndexOf() {
		this.init();
		// System.out.println(this.list.indexOf(""
		// + (TestAsynchronousList.SizeTestList - 1)));
		Assert.assertTrue(this.list.indexOf(""
				+ (TestAsynchronousList.SizeTestList - 1)) >= 0);
	}

	@Test
	public final void testIsEmpty() {
		this.init();
		Assert.assertFalse(this.list.isEmpty());
	}

	/**
	 * Test method for
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.AsynchronList#get(int)}
	 * .
	 */
	@Test
	public final void testIterator() {
		this.init();

		int count = 0;
		for (final String elem : this.list) {

			System.out.println(elem);
			count++;
			try {
				Thread.sleep(0 + (int) (Math.random()
						* TestAsynchronousList.sleep * 0.85));
			} catch (final InterruptedException e) {

			}

		}
		Assert.assertEquals(TestAsynchronousList.SizeTestList, count);
	}

	@Test
	public final void testLastIndexOf() {
		this.init();
		Assert.assertEquals(
				TestAsynchronousList.SizeTestList - 1,
				this.list.lastIndexOf(""
						+ (TestAsynchronousList.SizeTestList - 1)));
	}

	@Test
	public final void testRemove() {
		this.init();
		this.list.remove(TestAsynchronousList.SizeTestList / 2);
	}

	@Test
	public final void testSize() {
		this.init();
		Assert.assertEquals(TestAsynchronousList.SizeTestList, this.list.size());
	}

	@Test
	public final void testSubList() {
		this.init();
		Assert.assertEquals(TestAsynchronousList.SizeTestList / 2, this.list
				.subList(0, TestAsynchronousList.SizeTestList / 2).size());
	}

	@Test
	public final void testToArray() {
		this.init();
		Assert.assertEquals(TestAsynchronousList.SizeTestList,
				this.list.toArray().length);
	}

	@Test
	public final void testToArrayCopy() {
		this.init();
		final String[] a = new String[TestAsynchronousList.SizeTestList];
		Assert.assertEquals(
				this.list.get(TestAsynchronousList.SizeTestList - 1),
				this.list.toArray(a)[TestAsynchronousList.SizeTestList - 1]);
	}

}
