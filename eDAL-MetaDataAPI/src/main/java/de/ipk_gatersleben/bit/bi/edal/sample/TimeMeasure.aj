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
package de.ipk_gatersleben.bit.bi.edal.sample;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Scanner;
import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.SuppressAjWarnings;

import de.ipk_gatersleben.bit.bi.edal.primary_data.DataManager;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.implementation.PermissionProviderImplementation;

/**
 * Aspect to measure the necessary time for different functions.
 * 
 * @author arendd
 */
public aspect TimeMeasure {

	private static final boolean RUN = false;

	private static final Path path = Paths.get(System.getProperty("user.home"), "performance");

	private static final String nameSuffix = "_time_3_100_100_100KB.txt";

	pointcut setDefaultPermissions(): execution(protected * PrimaryDataEntity+.setDefaultPermissions (..));

	pointcut findPermissions(): execution(public * PermissionProviderImplementation + .findPermissions (..));

	pointcut exist(): execution(public * PrimaryDataDirectory+.exist (..));

	pointcut createFile(): execution(public * PrimaryDataDirectory+.createPrimaryDataFile (..));

	pointcut read(): execution(public * PrimaryDataFile+.read (..));

	pointcut store(): execution(public * PrimaryDataFile+.store (..)) && !cflowbelow(execution(public * PrimaryDataDirectory+.createPrimaryDataFile (..)));

	pointcut shutdown(): execution(public * DataManager+.shutdown (..));

	/**
	 * Advice to weave performance measurement around every method
	 * 
	 * @return
	 */
	@SuppressAjWarnings({ "adviceDidNotMatch" })
	Object around(): read() || store() {
		if (RUN) {
			long startTime = System.currentTimeMillis();
			Object o = proceed();
			measureTime(thisJoinPoint, System.currentTimeMillis() - startTime);
			return o;
		} else
			return proceed();
	}

	protected void measureTime(JoinPoint joinPoint, Long endtime) {

		String methodName = joinPoint.getSignature().getName();

		Path filePath = Paths.get(TimeMeasure.path.toString(), methodName + nameSuffix);

		try {
			if (Files.notExists(filePath, LinkOption.NOFOLLOW_LINKS)) {
				Files.createDirectories(filePath.getParent());
				Files.createFile(filePath);
			}
	
			Files.write(filePath, (String.valueOf(endtime) + "\n").getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Advice to add a new line at the end of all files
	 */
	@SuppressAjWarnings({ "adviceDidNotMatch" })
	after() : shutdown(){
		if (RUN) {
			try {
				Files.walkFileTree(TimeMeasure.path, new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						if (file.getFileName().toString().contains(TimeMeasure.nameSuffix)) {
							// Files.write(file, ("\n".getBytes()), StandardOpenOption.APPEND);

						}
						return FileVisitResult.CONTINUE;
					}
				});

//				System.out.println("READY");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
