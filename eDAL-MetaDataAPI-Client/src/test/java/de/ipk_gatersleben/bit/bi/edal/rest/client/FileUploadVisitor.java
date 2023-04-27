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

package de.ipk_gatersleben.bit.bi.edal.rest.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

public class FileUploadVisitor implements FileVisitor<Path> {

	public Path uploadDirectory;
	public int lengthOfRootPath;
	public String jsonString;
	public WebTarget uploadRequest;
	public String jwt;

	public FileUploadVisitor(Path uploadDirectory, String jsonString, WebTarget uploadRequest, String jwt) {

		this.lengthOfRootPath = uploadDirectory.getParent().toString().length() + 1;
		this.uploadDirectory = uploadDirectory;
		this.uploadRequest = uploadRequest;
		this.jwt = jwt;
		this.jsonString = jsonString;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

//		System.out.println("preVisitDirectory Path " + dir);

//		String relativePathWithoutRoot = dir.toString().substring(lengthOfRootPath);
//		
//		System.out.println("preVisitDirectory Root " + relativePathWithoutRoot);

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

		System.out.println("visitFile Path " + file);

		String relativePathWithoutRoot = file.toString().substring(lengthOfRootPath);

//		System.out.println("visitFile File " + relativePathWithoutRoot);

		String relativePathFileRoot = relativePathWithoutRoot.substring(0,
				relativePathWithoutRoot.lastIndexOf(File.separator));

//		System.out.println("visitFile Root " + relativePathFileRoot);

		FileDataBodyPart filePart = new FileDataBodyPart("file", file.toFile());
		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();

		FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("path", relativePathFileRoot)
				.field("metaData", this.jsonString).bodyPart(filePart);

		Response uploadResponse = this.uploadRequest.request().header("Authorization", "Bearer " + this.jwt)
				.post(Entity.entity(multipart, multipart.getMediaType()));

//		// Use response object to verify upload success
		System.out.println("Respose (" + uploadResponse.getStatus() + " - " + uploadResponse.getStatusInfo() + ")");

		formDataMultiPart.close();
		multipart.close();

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

}
