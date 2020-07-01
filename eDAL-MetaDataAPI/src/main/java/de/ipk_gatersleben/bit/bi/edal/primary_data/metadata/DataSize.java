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
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata;

/**
 * Store the size of an object.
 * 
 * @author lange
 * @author arendd
 */
public class DataSize extends UntypedData {
	public enum StorageUnit {
		BYTE(DataSize.BYTE, 1L), KILOBYTE(DataSize.KILOBYTE, 1L << DataSize.TEN), MEGABYTE(DataSize.MEGABYTE, 1L << DataSize.TWENTY), GIGABYTE(DataSize.GIGABYTE, 1L << DataSize.THIRTY), TERABYTE(DataSize.TERABYTE, 1L << DataSize.FOURTY), PETABYTE(DataSize.PETABYTE, 1L << DataSize.FIVTY), EXABYTE(DataSize.EXABYTE, 1L << DataSize.SIXTY);

		private final String symbol;
		/* divider of BASE unit */
		private final long divider;
		private static java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
		static {
			StorageUnit.nf.setGroupingUsed(false);
			StorageUnit.nf.setMinimumFractionDigits(0);
			StorageUnit.nf.setMaximumFractionDigits(1);
		}

		public static StorageUnit of(final long number) {
			final long n = number > 0 ? -number : number;
			if (n > -(1L << DataSize.TEN)) {
				return BYTE;
			} else if (n > -(1L << DataSize.TWENTY)) {
				return KILOBYTE;
			} else if (n > -(1L << DataSize.THIRTY)) {
				return MEGABYTE;
			} else if (n > -(1L << DataSize.FOURTY)) {
				return GIGABYTE;
			} else if (n > -(1L << DataSize.FIVTY)) {
				return TERABYTE;
			} else if (n > -(1L << DataSize.SIXTY)) {
				return PETABYTE;
			} else {
				/* n >= Long.MIN_VALUE */
				return EXABYTE;
			}
		}

		StorageUnit(final String name, final long divider) {
			this.symbol = name;
			this.divider = divider;
		}

		public String format(final long number) {
			return StorageUnit.nf.format((double) number / this.divider) + " " + this.symbol;
		}
	}

	private static final int TEN = 10;
	private static final int TWENTY = 20;
	private static final int THIRTY = 30;
	private static final int FOURTY = 40;
	private static final int FIVTY = 50;
	private static final int SIXTY = 60;
	private static final String BYTE = "B";
	private static final String KILOBYTE = "KB";
	private static final String MEGABYTE = "MB";
	private static final String GIGABYTE = "GB";
	private static final String TERABYTE = "TB";
	private static final String PETABYTE = "PB";
	private static final String EXABYTE = "EB";

	private static final Long UNKNOWN_FILESIZE = Long.valueOf(0);

	/**
	 * generated serialization id
	 */
	private static final long serialVersionUID = 7128164555143263330L;

	private Long fileSize = DataSize.UNKNOWN_FILESIZE;

	/**
	 * Default constructor create {@link DataSize} with
	 * {@link DataSize#UNKNOWN_FILESIZE}.
	 */
	DataSize() {
		this.setFileSize(DataSize.UNKNOWN_FILESIZE);
	}

	/**
	 * Public constructor create {@link DataSize} with specified file size.
	 * 
	 * @param size
	 *            the file size to set.
	 */
	public DataSize(final Long size) {
		this.setFileSize(size);
	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(final UntypedData datatype) {

		if (datatype instanceof DataSize) {

			DataSize size = (DataSize) datatype;

			if (this.getFileSize().compareTo(size.getFileSize()) == 0) {
				return super.compareTo(datatype);
			} else {
				return this.getFileSize().compareTo(size.getFileSize());
			}
		} else {
			return super.compareTo(datatype);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileSize == null) ? 0 : fileSize.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataSize other = (DataSize) obj;
		if (fileSize == null) {
			if (other.fileSize != null)
				return false;
		} else if (!fileSize.equals(other.fileSize))
			return false;
		return true;
	}

	/**
	 * Getter for the file size of this {@link DataSize} object.
	 * 
	 * @return the file size.
	 */
	public Long getFileSize() {
		return this.fileSize != null ? this.fileSize : DataSize.UNKNOWN_FILESIZE;
	}

	/**
	 * Setter for the file size of this {@link DataSize} object.
	 * 
	 * @param fileSize
	 *            the file size to set.
	 */
	public void setFileSize(final Long fileSize) {
		this.fileSize = fileSize;
	}

	@Override
	public String toString() {
		return this.getFileSize() != null ? StorageUnit.of(this.getFileSize()).format(this.getFileSize()) : "0";
	}

}
