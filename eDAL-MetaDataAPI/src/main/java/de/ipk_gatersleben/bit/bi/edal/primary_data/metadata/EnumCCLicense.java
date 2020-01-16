/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
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
