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
package de.ipk_gatersleben.bit.bi.edal.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.glassfish.grizzly.utils.Charsets;

public class BundleLogFiles {

	private static final Path LOG_FILES = Paths.get(System.getProperty("user.home"), "edal", "jetty_log");

	private static final Path LOG_2015 = Paths.get(LOG_FILES.toString(), "jetty-2015_all.log");
	private static final Path LOG_2016 = Paths.get(LOG_FILES.toString(), "jetty-2016_all.log");
	private static final Path LOG_2017 = Paths.get(LOG_FILES.toString(), "jetty-2017_all.log");
	private static final Path LOG_2018 = Paths.get(LOG_FILES.toString(), "jetty-2018_all.log");
	private static final Path LOG_2019 = Paths.get(LOG_FILES.toString(), "jetty-2019_all.log");
	private static final Path LOG_2020 = Paths.get(LOG_FILES.toString(), "jetty-2020_all.log");
	private static final Path LOG_2021 = Paths.get(LOG_FILES.toString(), "jetty-2021_all.log");

	public static void main(String[] args) throws IOException {

		Path logFiles = Paths.get(System.getProperty("user.home"), "edal", "jetty_log");

		for (final File file : logFiles.toFile().listFiles()) {
			System.out.println(file);

			switch (file.getName().substring(0, 10)) {
			case "jetty-2015": {

				String all = FileUtils.readFileToString(file, Charsets.UTF8_CHARSET);

				FileUtils.writeStringToFile(LOG_2015.toFile(), all, Charsets.UTF8_CHARSET, true);

			}
			case "jetty-2016": {

				String all = FileUtils.readFileToString(file, Charsets.UTF8_CHARSET);

				FileUtils.writeStringToFile(LOG_2016.toFile(), all, Charsets.UTF8_CHARSET, true);

			}
			case "jetty-2017": {

				String all = FileUtils.readFileToString(file, Charsets.UTF8_CHARSET);

				FileUtils.writeStringToFile(LOG_2017.toFile(), all, Charsets.UTF8_CHARSET, true);
			}
			case "jetty-2018": {

				String all = FileUtils.readFileToString(file, Charsets.UTF8_CHARSET);

				FileUtils.writeStringToFile(LOG_2018.toFile(), all, Charsets.UTF8_CHARSET, true);

			}
			case "jetty-2019": {

				String all = FileUtils.readFileToString(file, Charsets.UTF8_CHARSET);

				FileUtils.writeStringToFile(LOG_2019.toFile(), all, Charsets.UTF8_CHARSET, true);
			}
			case "jetty-2020": {

				String all = FileUtils.readFileToString(file, Charsets.UTF8_CHARSET);

				FileUtils.writeStringToFile(LOG_2020.toFile(), all, Charsets.UTF8_CHARSET, true);
			}
			case "jetty-2021": {

				String all = FileUtils.readFileToString(file, Charsets.UTF8_CHARSET);

				FileUtils.writeStringToFile(LOG_2021.toFile(), all, Charsets.UTF8_CHARSET, true);

			}
			default: {
			}

			}
		}
	}
}
