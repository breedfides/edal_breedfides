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
package de.ipk_gatersleben.bit.bi.edal.primary_data.metadata;

import java.util.EnumMap;

public enum EnumCCLicense {

	CC010("CC0 1.0 Universal (Creative Commons Public Domain Dedication)"), CCBY40(
			"CC BY 4.0 (Creative Commons Attribution)"), CCBYSA40(
					"CC BY-SA 4.0 (Creative Commons Attribution-ShareAlike)"), CCBYND40(
							"CC BY-ND 4.0 (Creative Commons Attribution-NoDerivatives)"), CCBYNC40(
									"CC BY-NC 4.0 (Creative Commons Attribution-Non-Commercial)"), CCBYNCSA40(
											"CC BY-NC-SA 4.0 (Creative Commons Attribution-Non-Commercial-ShareAlike)"), CCBYNCND40(
													"CC BY-NC-ND 4.0 (Creative Commons Attribution-Non-Commercial-NoDerivatives)");

	public static EnumMap<EnumCCLicense, String> enummap = new EnumMap<>(EnumCCLicense.class);

	static {
		enummap.put(CC010, "https://creativecommons.org/publicdomain/zero/1.0/legalcode");
		enummap.put(CCBY40, "https://creativecommons.org/licenses/by/4.0/legalcode");
		enummap.put(CCBYSA40, "https://creativecommons.org/licenses/by-sa/4.0/legalcode");
		enummap.put(CCBYND40, "https://creativecommons.org/licenses/by-nd/4.0/legalcode");
		enummap.put(CCBYNC40, "https://creativecommons.org/licenses/by-nc/4.0/legalcode");
		enummap.put(CCBYNCSA40, "https://creativecommons.org/licenses/by-nc-sa/4.0/legalcode");
		enummap.put(CCBYNCND40, "https://creativecommons.org/licenses/by-nc-nd/4.0/legalcode");
	}

	private final String description;

	private EnumCCLicense(String value) {
		description = value;
	}

	public String getDescription() {
		return description;
	}

}
