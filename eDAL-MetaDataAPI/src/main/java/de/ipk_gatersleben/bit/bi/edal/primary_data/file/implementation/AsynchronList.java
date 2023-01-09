/**
 * Copyright (c) 2023 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
 * 
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;

/**
 * Asynchronous {@link List} implementation to provide a blocking list for the
 * {@link PrimaryDataDirectory#listPrimaryDataEntities()} function.
 * 
 * @author lange
 * @author arendd
 *
 * @param <E>
 *            the type of the list
 */
public final class AsynchronList<E> extends LinkedList<E> {

	private class Itr implements Iterator<E> {
		/**
		 * Index of element to be returned by subsequent call to next.
		 */
		int cursor = 0;

		/**
		 * Index of element returned by most recent call to next or previous.
		 * Reset to -1 if this element is deleted by a call to remove.
		 */
		int lastRet = -1;

		public boolean hasNext() {
			AsynchronList.this.checkVolatileElement(this.cursor + 1);

			if (!finish.get()) {
				return true;
			} else {
				return this.cursor != AsynchronList.this.size();
			}
		}

		public E next() {

			try {
				final int i = this.cursor;
				final E next = AsynchronList.this.get(i);
				this.lastRet = i;
				this.cursor = i + 1;
				return next;
			} catch (final IndexOutOfBoundsException e) {

				throw new NoSuchElementException();
			}
		}

		public void remove() {
			if (this.lastRet < 0) {
				throw new IllegalStateException();
			}

			try {
				AsynchronList.this.remove(this.lastRet);
				if (this.lastRet < this.cursor) {
					this.cursor--;
				}
				this.lastRet = -1;

			} catch (final IndexOutOfBoundsException e) {
				throw new ConcurrentModificationException();
			}
		}
	}

	private class ListItr extends Itr implements ListIterator<E> {
		ListItr(final int index) {
			this.cursor = index;
		}

		public void add(final E e) {

			try {
				final int i = this.cursor;
				AsynchronList.this.add(i, e);
				this.lastRet = -1;
				this.cursor = i + 1;

			} catch (final IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}

		public boolean hasPrevious() {
			return this.cursor != 0;
		}

		public int nextIndex() {
			return this.cursor;
		}

		public E previous() {

			try {
				final int i = this.cursor - 1;
				final E previous = AsynchronList.this.get(i);
				this.lastRet = this.cursor = i;
				return previous;
			} catch (final IndexOutOfBoundsException e) {

				throw new NoSuchElementException();
			}
		}

		public int previousIndex() {
			return this.cursor - 1;
		}

		public void set(final E e) {
			if (this.lastRet < 0) {
				throw new IllegalStateException();
			}

			try {
				AsynchronList.this.set(this.lastRet, e);

			} catch (final IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 876424262765176354L;

	private final AtomicBoolean finish = new AtomicBoolean(false);
	private boolean stopped = false;

	/**
	 * 
	 */
	public AsynchronList() {
		super();
	}

	/**
	 * @param collection
	 *            the collection to create {@link AsynchronList}
	 */
	public AsynchronList(final Collection<? extends E> collection) {
		super(collection);
	}

	/*
	 * wait until all element are ready
	 */
	protected void checkAllElementsPresent() {
		// wait until some more new data arrived

		while (!this.finish.get()) {
			// System.out.println("try get lock");
			try {
				synchronized (this) {
					this.wait();
				}
			} catch (final InterruptedException e) {
			}

		}
	}

	private boolean checkPresentElementAtIndex(final int index) {
		try {
			super.get(index);
		} catch (final IndexOutOfBoundsException ex) {
			return false;
		}
		return true;
	}

	/*
	 * check if the element is already filled; if not: block until it is or no
	 * more elements available; if yes: return immediately
	 */
	protected void checkVolatileElement(final int index) {
		// wait until some more new data arrived

		while (!this.checkPresentElementAtIndex(index) && !this.finish.get()) {
			// System.out.println("try get lock: " + index);
			try {
				synchronized (this) {
					this.wait();
				}
			} catch (final InterruptedException e) {

			}
		}
		return;
	}

	@Override
	public void clear() {
		this.checkAllElementsPresent();
		super.clear();
	}

	@Override
	public E get(final int index) {

		this.checkVolatileElement(index);

		return super.get(index);

	}

	@Override
	public int indexOf(final Object elem) {
		this.checkAllElementsPresent();
		return super.indexOf(elem);
	}

	@Override
	public boolean isEmpty() {
		this.checkAllElementsPresent();
		return super.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {

		return this.listIterator();
	}

	@Override
	public int lastIndexOf(final Object o) {
		this.checkAllElementsPresent();
		return super.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return this.listIterator(0);
	}

	@Override
	public ListIterator<E> listIterator(final int index) {
		// this.rangeCheckForAdd(index);

		return new ListItr(index);
	}

	/*
	 * call method to notify there is an insertion of a bucket of new data is
	 * finish
	 */
	public void notifyNewDataAvailable() {
		synchronized (this) {
			this.notifyAll();
		}
	}

	/*
	 * call method to notify there all data was inserted
	 */
	public void notifyNoMoreNewData() {
		this.finish.set(true);
		this.notifyNewDataAvailable();

	}

	// private void rangeCheckForAdd(final int index) {
	// if (index < 0 || index > this.size()) {
	// throw new IndexOutOfBoundsException("Index: " + index + ", Size: "
	// + this.size());
	// }
	// }

	@Override
	public E remove(final int index) {
		this.checkVolatileElement(index);

		return super.remove(index);
	}

	@Override
	public E set(final int index, final E elem) {
		this.checkVolatileElement(index);

		return super.set(index, elem);
	}

	/**
	 * beware!!! size is not fixed, can potential increase, use iterator not
	 * loop! Otherwise, you will wait until all elements where loaded
	 */
	@Override
	public int size() {

		this.checkAllElementsPresent();
		return super.size();
	}

	@Override
	public Object[] toArray() {
		this.checkAllElementsPresent();
		return super.toArray();
	}

	public boolean isStopped() {
		return stopped;
	}

	public void setStopped() {
		this.stopped = true;
	}

}
