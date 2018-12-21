/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - initial API and implementation
 */
package de.ipk_gatersleben.bit.bi.edal.primary_data;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipOutputStream;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.lang3.EnumUtils;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataFormat;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DataSize;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.ContentNegotiationType;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.EdalApprovalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.reference.PersistentIdentifier;

public class EdalHttpHandler extends AbstractHandler {

	private static HashMap<Integer, List<String>> reviewerHashMap = new HashMap<Integer, List<String>>();
	private static HashMap<Integer, List<String>> userHashMap = new HashMap<Integer, List<String>>();
	private static HashMap<String, Long> requestTimeoutMap = new HashMap<String, Long>();

	private static ThreadPoolExecutor zipExecutor;

	private static final int MIN_NUMBER_OF_THREADS_IN_POOL = 2;
	private static final int MAX_NUMBER_OF_THREADS_IN_EXECUTOR_QUEUE = 60;
	private static final int EXCUTOR_THREAD_KEEP_ALIVE_SECONDS = 60;
	private static final int USEABLE_CORES = (int) Math.ceil(Runtime.getRuntime().availableProcessors() * 1 / 2);

	private static final Long LIMIT_INLINE_FILE_SIZE = Long.valueOf(10 * 1024 * 1024);
	private static final String EXECUTOR_NAME = "ZipExecutor";

	public static WebPageCache contentPageCache = new WebPageCache("contentpage");
	public static WebPageCache reportPageCache = new WebPageCache("reportpage");

	static VeloCityHtmlGenerator velocityHtmlGenerator;

	static {

		if (MIN_NUMBER_OF_THREADS_IN_POOL < USEABLE_CORES) {
			zipExecutor = new EdalThreadPoolExcecutor(USEABLE_CORES, USEABLE_CORES, EXCUTOR_THREAD_KEEP_ALIVE_SECONDS,
					TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(MAX_NUMBER_OF_THREADS_IN_EXECUTOR_QUEUE),
					EXECUTOR_NAME);
		} else {

			zipExecutor = new EdalThreadPoolExcecutor(MIN_NUMBER_OF_THREADS_IN_POOL, MIN_NUMBER_OF_THREADS_IN_POOL,
					EXCUTOR_THREAD_KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
					new ArrayBlockingQueue<Runnable>(MAX_NUMBER_OF_THREADS_IN_EXECUTOR_QUEUE), EXECUTOR_NAME);
		}

		contentPageCache.init();
		reportPageCache.init();
		velocityHtmlGenerator = new VeloCityHtmlGenerator();
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		if (checkForRobots(request)) {
			this.sendMessage(response, HttpStatus.Code.FORBIDDEN, "blocked");
		} else {
			if (request.getMethod().equalsIgnoreCase("GET")) {

				final String url = request.getRequestURI().toString();

				DataManager.getImplProv().getLogger().debug(url);

				final StringTokenizer tokenizer = new StringTokenizer(url, EdalHttpServer.EDAL_PATH_SEPARATOR);

				if (!tokenizer.hasMoreTokens()) {
					/* no method defined -> break */
					this.sendMessage(response, HttpStatus.Code.NOT_FOUND, "no ID and version specified");
				} else {
					final String methodToken = tokenizer.nextToken().toUpperCase();

					try {
						switch (EdalHttpFunctions.valueOf(methodToken)) {

						case DOI:
						case URL:
							if (tokenizer.hasMoreTokens()) {
								final String internalId = tokenizer.nextToken();

								if (tokenizer.hasMoreTokens()) {

									final String uuidToken = tokenizer.nextToken();

									if (tokenizer.hasMoreTokens()) {
										final String versionToken = tokenizer.nextToken();
										try {
											final long versionNumber = Long.parseLong(versionToken);

											PrimaryDataEntity entity = null;

											/** code for reviewer landing page */
											if (tokenizer.hasMoreTokens()) {
												final String reviewerToken = tokenizer.nextToken();

												if (tokenizer.hasMoreElements()) {
													final String methodTokenForReviewer = tokenizer.nextToken();

													/** process for reviewer : */

													if (methodTokenForReviewer
															.equalsIgnoreCase(EdalHttpFunctions.DOWNLOAD.name())) {

														this.responseDownloadRequest(response, entity, uuidToken,
																versionNumber, internalId, methodToken, reviewerToken);
														break;

													} else if (methodTokenForReviewer
															.equalsIgnoreCase(EdalHttpFunctions.ZIP.name())) {

														this.responseZipRequest(response, entity, uuidToken,
																versionNumber, internalId, methodToken, reviewerToken);
														break;

													} else {

														this.sendMessage(response, HttpStatus.Code.FORBIDDEN,
																"unable to process '" + methodTokenForReviewer + "'");
														break;
													}

												} else {
													/**
													 * check if request is for DOWNLOAD function
													 */
													if (reviewerToken
															.equalsIgnoreCase(EdalHttpFunctions.DOWNLOAD.name())) {

														this.responseDownloadRequest(response, entity, uuidToken,
																versionNumber, internalId, methodToken, null);

													}
													/**
													 * check if request is for ZIP function function
													 */
													else if (reviewerToken
															.equalsIgnoreCase(EdalHttpFunctions.ZIP.name())) {

														this.responseZipRequest(response, entity, uuidToken,
																versionNumber, internalId, methodToken, null);
														break;
													}
													/**
													 * check if request is for CONTENT NEGOTIATION
													 */
													else if (EnumUtils.isValidEnum(ContentNegotiationType.class,
															reviewerToken)) {

														this.sendContentNegotiation(response, entity, uuidToken,
																versionNumber, internalId,
																PersistentIdentifier.valueOf(methodToken),
																ContentNegotiationType.valueOf(reviewerToken));

														break;

													}

													else {
														/**
														 * check if request is a reviewer code
														 */
														int reviewer = 0;
														try {
															reviewer = Integer.parseInt(reviewerToken);
														} catch (final NumberFormatException e) {
															this.sendMessage(response, HttpStatus.Code.FORBIDDEN,
																	"unable to process reviewer ID '" + reviewerToken
																			+ "', please check again");
															break;
														}

														/**
														 * check if reviewer code exists
														 */
														entity = DataManager.getPrimaryDataEntityForReviewer(uuidToken,
																versionNumber, internalId, reviewer);
														this.sendEntityMetaDataForReviewer(response, entity,
																versionNumber, internalId,
																PersistentIdentifier.valueOf(methodToken), reviewer);
														break;
													}
												}
											}
											/**
											 * end code for reviewer landing page
											 */
											else {

												entity = DataManager.getPrimaryDataEntityForPersistenIdentifier(
														uuidToken, versionNumber,
														PersistentIdentifier.valueOf(methodToken));
												this.sendEntityMetaDataForPersistentIdentifier(response, entity,
														versionNumber, PersistentIdentifier.valueOf(methodToken),
														internalId);
												break;
											}

										} catch (final EdalException e) {

											this.sendMessage(response, HttpStatus.Code.LOCKED, e.getMessage());
										}
									} else {
										this.sendMessage(response, HttpStatus.Code.FORBIDDEN, "no version number set");
									}
								} else {
									this.sendMessage(response, HttpStatus.Code.FORBIDDEN, "no entity id set");
								}
							} else {
								this.sendMessage(response, HttpStatus.Code.FORBIDDEN, "no internal id set");
							}
							break;

						/* request an object */
						case EDAL:

							final String uuidToken = tokenizer.nextToken();

							if (tokenizer.hasMoreTokens()) {
								final String versionToken = tokenizer.nextToken();
								try {
									final int versionNumber = Integer.parseInt(versionToken);
									PrimaryDataEntity entity = null;

									if (tokenizer.hasMoreElements()) {

										final String downloadToken = tokenizer.nextToken();
										if (EdalHttpFunctions.valueOf(downloadToken.toUpperCase())
												.equals(EdalHttpFunctions.DOWNLOAD)) {

											entity = DataManager.getPrimaryDataEntityByID(uuidToken, versionNumber);

											if (!entity.isDirectory()) {
												this.sendFile((PrimaryDataFile) entity, versionNumber, response);
											} else {
												this.sendEntityMetaData(response, entity);
											}
										}
									} else {
										entity = DataManager.getPrimaryDataEntityByID(uuidToken, versionNumber);

										this.sendEntityMetaData(response, entity);
									}

								} catch (final NumberFormatException | EdalException e) {
									if (e.getClass().equals(NumberFormatException.class)) {
										this.sendMessage(response, HttpStatus.Code.NOT_FOUND,
												"unable to cast '" + versionToken + "' to a version number");
									} else {
										this.sendMessage(response, HttpStatus.Code.NOT_FOUND,
												"unable to send data : " + e.getMessage());
									}
								}
							} else {
								this.sendMessage(response, HttpStatus.Code.NOT_FOUND, "no version number set");
							}
							break;

						/* accept an object */
						case ACCEPT:

							final String ticketAccept = tokenizer.nextToken();

							if (tokenizer.hasMoreElements()) {

								try {
									final int reviewerHashCode = Integer.parseInt(tokenizer.nextToken());

									if (!ticketForReviewerAlreadyClicked(reviewerHashCode, ticketAccept)) {

										DataManager.getImplProv().getApprovalServiceProvider().newInstance()
												.accept(ticketAccept, reviewerHashCode);
										this.sendMessage(response, HttpStatus.Code.OK, "Thank you");

									} else {
										this.sendMessage(response, HttpStatus.Code.FORBIDDEN,
												"Ticket already accepted");
									}
								} catch (EdalApprovalException | InstantiationException | NumberFormatException
										| IllegalAccessException e) {

									EdalHttpHandler.deleteTicketFromReviewerHashMap(ticketAccept);
									this.sendMessage(response, HttpStatus.Code.NOT_FOUND,
											"Can not to accept ticket it seems to be already accepted: "
													+ e.getMessage());
								}
							} else {
								this.sendMessage(response, HttpStatus.Code.NOT_FOUND, "No ReviewerCode definded");
							}

							break;

						/* reject an object */
						case REJECT:

							final String ticketReject = tokenizer.nextToken();

							if (tokenizer.hasMoreElements()) {

								try {
									final int reviewerHashCode = Integer.parseInt(tokenizer.nextToken());

									if (!ticketForReviewerAlreadyClicked(reviewerHashCode, ticketReject)) {
										DataManager.getImplProv().getApprovalServiceProvider().newInstance()
												.reject(ticketReject, reviewerHashCode);
										this.sendMessage(response, HttpStatus.Code.OK, "Thank you");
									} else {
										this.sendMessage(response, HttpStatus.Code.FORBIDDEN,
												"Ticket already rejected");
									}

								} catch (EdalApprovalException | InstantiationException | NumberFormatException
										| IllegalAccessException e) {

									EdalHttpHandler.deleteTicketFromReviewerHashMap(ticketReject);
									this.sendMessage(response, HttpStatus.Code.NOT_FOUND,
											"Can not to reject ticket  it seems to be already rejected: "
													+ e.getMessage());
								}
							} else {
								this.sendMessage(response, HttpStatus.Code.NOT_FOUND, "No ReviewerCode definded");
							}

							break;

						case LOGIN:

							final String uuid = tokenizer.nextToken();
							final String emailAddress = tokenizer.nextToken();

							Boolean successful = false;
							try {
								successful = DataManager.getImplProv().getPermissionProvider().newInstance()
										.validateRootUser(new InternetAddress(emailAddress), UUID.fromString(uuid));
							} catch (final AddressException | InstantiationException | IllegalAccessException e) {
								this.sendMessage(response, HttpStatus.Code.NOT_FOUND,
										"eDAL-Server Admin confirmation failed");
							}

							if (successful) {
								try {
									this.sendMessage(response, HttpStatus.Code.OK,
											"Thank you <br/>You have successfully registered as administrator for eDAL-Server on "
													+ EdalHttpServer.getServerURL()
													+ "<br/>The server is now started in working mode.");
								} catch (final EdalException e) {
									this.sendMessage(response, HttpStatus.Code.NOT_FOUND, "unable to load server URL");
								}
							} else {
								this.sendMessage(response, HttpStatus.Code.NOT_FOUND, "root user validation failed");
							}

							break;

						case LOGO:
							if (tokenizer.hasMoreTokens()) {
								final String logoUrl = tokenizer.nextToken();

								if (logoUrl.equalsIgnoreCase("edal_logo.png")) {
									this.sendEmbeddedFile(response, "edal_logo.png", "image/png");
								} else if (logoUrl.equalsIgnoreCase("ipk_logo.jpg")) {
									this.sendEmbeddedFile(response, "ipk_logo.jpg", "image/jpg");
								} else if (logoUrl.equalsIgnoreCase("header_bg2.png")) {
									this.sendEmbeddedFile(response, "header_bg2.png", "image/png");
								} else if (logoUrl.equalsIgnoreCase("edal_icon.png")) {
									this.sendEmbeddedFile(response, "edal_icon.png", "image/png");
								}
							}
							break;

						case CSS:
							if (tokenizer.hasMoreTokens()) {

								final String fileUrl = tokenizer.nextToken();

								this.sendEmbeddedFile(response, "css" + "/" + fileUrl, "text/css");
							}
							break;
						case JS:
							if (tokenizer.hasMoreTokens()) {

								final String fileUrl = tokenizer.nextToken();

								this.sendEmbeddedFile(response, "js" + "/" + fileUrl, "application/javascript");
							}
							break;

						case USER_ACCEPT:
							final String userAcceptTicket = tokenizer.nextToken();

							if (tokenizer.hasMoreElements()) {

								try {
									final int reviewerHashCode = Integer.parseInt(tokenizer.nextToken());

									if (!ticketForUserAlreadyClicked(reviewerHashCode, userAcceptTicket)) {

										DataManager.getImplProv().getApprovalServiceProvider().newInstance()
												.acceptTicketByUser(userAcceptTicket, reviewerHashCode);

										this.sendMessage(response, HttpStatus.Code.OK, "Thank you");

									} else {
										this.sendMessage(response, HttpStatus.Code.FORBIDDEN,
												"Ticket already accepted");
									}
								} catch (NumberFormatException | InstantiationException | IllegalAccessException
										| EdalApprovalException e) {

									EdalHttpHandler.deleteTicketFromUserHashMap(userAcceptTicket);

									this.sendMessage(response, HttpStatus.Code.NOT_FOUND,
											"Can not to accept ticket it seems to be already accepted: "
													+ e.getMessage());
								}
							} else {
								this.sendMessage(response, HttpStatus.Code.NOT_FOUND, "No ReviewerCode definded");
							}

							break;

						case USER_REJECT:

							final String userRejectTicket = tokenizer.nextToken();

							if (tokenizer.hasMoreElements()) {

								try {
									final int reviewerHashCode = Integer.parseInt(tokenizer.nextToken());

									if (!ticketForUserAlreadyClicked(reviewerHashCode, userRejectTicket)) {

										DataManager.getImplProv().getApprovalServiceProvider().newInstance()
												.rejectTicketByUser(userRejectTicket, reviewerHashCode);

										this.sendMessage(response, HttpStatus.Code.OK, "Thank you");

									} else {
										this.sendMessage(response, HttpStatus.Code.FORBIDDEN,
												"Ticket already rejected");
									}
								} catch (NumberFormatException | InstantiationException | IllegalAccessException
										| EdalApprovalException e) {

									EdalHttpHandler.deleteTicketFromUserHashMap(userRejectTicket);

									this.sendMessage(response, HttpStatus.Code.NOT_FOUND,
											"Can not to accept ticket it seems to be already rejected: "
													+ e.getMessage());
								}
							} else {
								this.sendMessage(response, HttpStatus.Code.NOT_FOUND, "No ReviewerCode definded");
							}

							break;

						case REPORT:
							try {

								this.sendReport(response, HttpStatus.Code.OK);

							} catch (EdalException e) {
								e.printStackTrace();
							}
							break;

						case OAI:

							// String oaiRequest = request.getQueryString();
							//
							// /** cut set **/
							// if (oaiRequest.contains("set=")) {
							//
							// oaiRequest = oaiRequest.substring(0,
							// oaiRequest.indexOf("set=")) + oaiRequest.substring(
							// oaiRequest.indexOf("&", oaiRequest.indexOf("set=")),
							// oaiRequest.length());
							//
							// System.out.println(oaiRequest);
							//
							// }
							//
							// if (oaiRequest.contains("verb=Identify") ||
							// oaiRequest.contains("verb=ListMetadataFormats")
							// || oaiRequest.contains("verb=ListSets")) {
							// response.sendRedirect("http://oai.datacite.org/oai?"
							// + oaiRequest);
							// } else {
							// response.sendRedirect("http://oai.datacite.org/oai?set=TIB.IPK&"
							// + oaiRequest);
							// }
							//
							// break;

							if (!DataManager.getConfiguration().isInTestMode()) {

								String dataCenter = null;
								try {
									dataCenter = DataManager.getConfiguration().getDataCiteUser();
								} catch (EdalConfigurationException e) {
									e.printStackTrace();
								}

								String oaiRequest = request.getQueryString();

								if (oaiRequest != null) {
									if (oaiRequest.contains("set=")) {
										if (oaiRequest.indexOf("set=") > oaiRequest.lastIndexOf("&")) {

											oaiRequest = oaiRequest.substring(0, oaiRequest.indexOf("set=")) + "set="
													+ dataCenter;

											response.sendRedirect("http://oai.datacite.org/oai?" + oaiRequest);

										} else {

											String prefix = oaiRequest.substring(0, oaiRequest.indexOf("set="));
											String suffix = oaiRequest.substring(
													oaiRequest.indexOf("&", oaiRequest.indexOf("set=")),
													oaiRequest.length());

											oaiRequest = prefix + suffix + "&set=" + dataCenter;
											response.sendRedirect("http://oai.datacite.org/oai?" + oaiRequest);
										}

									} else if (oaiRequest.contains("verb=Identify")
											|| oaiRequest.contains("verb=ListMetadataFormats")
											|| oaiRequest.contains("verb=ListRecords&metadataPrefix=oai_dc")
											|| oaiRequest.contains("verb=ListSets")
											|| oaiRequest.contains("verb=ListIdentifiers&metadataPrefix=oai_dc")) {
										response.sendRedirect("http://oai.datacite.org/oai?" + oaiRequest);
									}
								} else {
									response.sendRedirect("http://oai.datacite.org/oai");
								}
							}

							break;

						default:
							this.sendMessage(response, HttpStatus.Code.FORBIDDEN,
									"Unknown function '" + methodToken + "' used !");
							break;
						}

					} catch (IllegalArgumentException e) {

						if (methodToken.toLowerCase().equals("robots.txt")) {
							this.sendEmbeddedFile(response, "robots.txt", "text/plain");
						} else {
							this.sendMessage(response, HttpStatus.Code.FORBIDDEN,
									"Unknown function '" + methodToken + "' used !");
						}
					}
				}
			}
			response.flushBuffer();
		}
	}

	/**
	 * Function to check if a "bot" request a file or a zip download or if he
	 * request more than one resource per second (1000 ms interval between request
	 * necessary)
	 * 
	 * @param request
	 * 
	 * @return true if request should be blocked. false if the request should be
	 *         answered
	 */
	private boolean checkForRobots(HttpServletRequest request) {

		String userAgent = request.getHeader(HttpHeader.USER_AGENT.toString().toLowerCase());

		final String url = request.getRequestURI().toString();

		// System.out.println("Check for " + userAgent + ": " + url);

		if (userAgent == null || userAgent.contains("bot")) {

			if (url.endsWith(EdalHttpFunctions.DOWNLOAD.toString())
					|| url.endsWith(EdalHttpFunctions.DOWNLOAD.toString().toLowerCase())
					|| url.endsWith(EdalHttpFunctions.ZIP.toString())
					|| url.endsWith(EdalHttpFunctions.ZIP.toString().toLowerCase())) {
				return true;
			} else if (url.toLowerCase().endsWith("robots.txt")) {
				return false;
			} else {
				final StringTokenizer tokenizer = new StringTokenizer(url, EdalHttpServer.EDAL_PATH_SEPARATOR);

				if (tokenizer.hasMoreTokens()) {

					String methodToken = tokenizer.nextToken().toUpperCase();

					if (EdalHttpFunctions.valueOf(methodToken).equals(EdalHttpFunctions.DOI)) {
						if (requestTimeoutMap.containsKey(userAgent)) {

							if (System.currentTimeMillis() - requestTimeoutMap.get(userAgent) <= 1000) {
								requestTimeoutMap.put(userAgent, System.currentTimeMillis());
								return true;
							}
						}
						requestTimeoutMap.put(userAgent, System.currentTimeMillis());
					}
				}
				return false;
			}
		} else {
			return false;
		}

	}

	private void sendContentNegotiation(HttpServletResponse response, PrimaryDataEntity entity, String uuidToken,
			long versionNumber, String internalId, PersistentIdentifier persistentIdentifier,
			ContentNegotiationType contentNegotiationType) {

		try {
			entity = DataManager.getPrimaryDataEntityForPersistenIdentifier(uuidToken, versionNumber,
					persistentIdentifier);

			StringBuffer buffer = ContentNegotiator.generateContentNogitiation(entity, versionNumber, internalId,
					persistentIdentifier, contentNegotiationType);

			if (buffer != null) {

				response.setStatus(HttpStatus.Code.OK.getCode());

				String filename = entity.getName() + "." + contentNegotiationType.getFileEnding();

				response.setCharacterEncoding("UTF-8");
				response.setContentType(contentNegotiationType.getType());
				response.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");

				final OutputStream responseBody = response.getOutputStream();

				responseBody.write(buffer.toString().getBytes(Charset.forName("UTF-8")));

				responseBody.flush();
				responseBody.close();
			} else {
				sendMessage(response, HttpStatus.Code.NOT_FOUND, "unable to find citation information");
			}

		} catch (EdalException | IOException e1) {
			e1.printStackTrace();
		}

	}

	private boolean ticketForReviewerAlreadyClicked(int reviewerHashCode, String ticketAccept) {
		List<String> list = EdalHttpHandler.reviewerHashMap.get(reviewerHashCode);

		if (list != null) {

			if (list.contains(ticketAccept)) {
				return true;
			} else {
				list.add(ticketAccept);
				EdalHttpHandler.reviewerHashMap.put(reviewerHashCode, list);
				return false;
			}
		} else {
			List<String> newlist = new ArrayList<String>();
			newlist.add(ticketAccept);
			EdalHttpHandler.reviewerHashMap.put(reviewerHashCode, newlist);
			return false;
		}
	}

	private boolean ticketForUserAlreadyClicked(int reviewerHashCode, String ticketAccept) {
		List<String> list = EdalHttpHandler.userHashMap.get(reviewerHashCode);

		if (list != null) {

			if (list.contains(ticketAccept)) {
				return true;
			} else {
				list.add(ticketAccept);
				EdalHttpHandler.userHashMap.put(reviewerHashCode, list);
				return false;
			}
		} else {
			List<String> newlist = new ArrayList<String>();
			newlist.add(ticketAccept);
			EdalHttpHandler.userHashMap.put(reviewerHashCode, newlist);
			return false;
		}
	}

	/**
	 * Function to response a {@link EdalHttpFunctions#DOWNLOAD} request.
	 * 
	 * @param response
	 * @param entity
	 * @param uuid
	 * @param versionNumber
	 * @param internalId
	 * @param identifierType
	 * @param reviewerCode
	 * @throws EdalException
	 */
	private void responseDownloadRequest(HttpServletResponse response, PrimaryDataEntity entity, String uuid,
			Long versionNumber, String internalId, String identifierType, String reviewerCode) throws EdalException {

		if (reviewerCode == null) {
			entity = DataManager.getPrimaryDataEntityForPersistenIdentifier(uuid, versionNumber,
					PersistentIdentifier.valueOf(identifierType));
			if (!entity.isDirectory()) {
				this.sendFile((PrimaryDataFile) entity, versionNumber, response);
			} else {
				this.sendEntityMetaDataForPersistentIdentifier(response, entity, versionNumber,
						PersistentIdentifier.valueOf(identifierType), internalId);
			}

		} else {
			entity = DataManager.getPrimaryDataEntityForReviewer(uuid, versionNumber, internalId,
					Integer.parseInt(reviewerCode));

			if (!entity.isDirectory()) {
				this.sendFile((PrimaryDataFile) entity, versionNumber, response);
			} else {
				this.sendEntityMetaDataForReviewer(response, entity, versionNumber, internalId,
						PersistentIdentifier.valueOf(identifierType), Integer.parseInt(reviewerCode));

			}
		}
	}

	private void responseZipRequest(HttpServletResponse response, PrimaryDataEntity entity, String uuid,
			long versionNumber, String internalId, String identifierType, String reviewerId) throws EdalException {

		if (!zipExecutor.isShutdown() && (zipExecutor.getActiveCount() >= zipExecutor.getMaximumPoolSize())) {

			sendMessage(response, HttpStatus.Code.INSUFFICIENT_STORAGE,
					"No more free slots for downloading zip archive, please try again later");
		} else {

			if (reviewerId == null) {
				entity = DataManager.getPrimaryDataEntityForPersistenIdentifier(uuid, versionNumber,
						PersistentIdentifier.valueOf(identifierType));

			} else {
				entity = DataManager.getPrimaryDataEntityForReviewer(uuid, versionNumber, internalId,
						Integer.parseInt(reviewerId));
			}

			if (entity.isDirectory()) {

				try {

					response.setContentType("application/zip");
					response.setHeader("Content-Disposition", "inline; filename=\"" + entity.getName() + ".zip" + "\"");
					response.setStatus(HttpStatus.Code.OK.getCode());

					CountDownLatch countDownLatch = new CountDownLatch(1);

					OutputStream responseBody = response.getOutputStream();
					// // copy entity bytes stream to HTTP stream
					ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(responseBody));

					ZipThread zipThread = new ZipThread(countDownLatch, zipOutputStream, (PrimaryDataDirectory) entity);

					if (zipExecutor.isShutdown()) {
						if (MIN_NUMBER_OF_THREADS_IN_POOL < USEABLE_CORES) {
							zipExecutor = new EdalThreadPoolExcecutor(USEABLE_CORES, USEABLE_CORES,
									EXCUTOR_THREAD_KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
									new ArrayBlockingQueue<Runnable>(MAX_NUMBER_OF_THREADS_IN_EXECUTOR_QUEUE),
									EXECUTOR_NAME);
						} else {

							zipExecutor = new EdalThreadPoolExcecutor(MIN_NUMBER_OF_THREADS_IN_POOL,
									MIN_NUMBER_OF_THREADS_IN_POOL, EXCUTOR_THREAD_KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
									new ArrayBlockingQueue<Runnable>(MAX_NUMBER_OF_THREADS_IN_EXECUTOR_QUEUE),
									EXECUTOR_NAME);
						}
					}

					zipExecutor.execute(zipThread);

					try {
						countDownLatch.await();
					} catch (InterruptedException e) {
						throw new EdalException("Unable to send zip file '" + entity.getName() + ".zip'", e);
					} finally {
						try {
							zipOutputStream.flush();
							zipOutputStream.close();
						} catch (EofException e) {
							DataManager.getImplProv().getLogger()
									.warn("Zip Download for '" + entity.getName() + ".zip' canceled by user");
							zipThread.stopListThread();
							responseBody.close();
						}
						responseBody.flush();
						responseBody.close();
					}
				} catch (IOException e) {
					throw new EdalException("Unable to send zip file '" + entity.getName() + ".zip'", e);
				}
			} else {
				this.sendFile((PrimaryDataFile) entity, versionNumber, response);
			}
		}
	}

	private void sendEmbeddedFile(HttpServletResponse response, String fileName, String contentType) {

		try {

			InputStream file = EdalHttpHandler.class.getResourceAsStream(fileName);

			int fileSize = file.available();

			response.setContentType(contentType);

			response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
			response.setContentLength(fileSize);

			response.setStatus(HttpStatus.Code.OK.getCode());

			CountDownLatch countDownLatch = new CountDownLatch(1);

			final OutputStream responseBody = response.getOutputStream();
			// copy entity bytes stream to HTTP stream
			final PipedInputStream httpIn = new PipedInputStream(EdalConfiguration.STREAM_BUFFER_SIZE);
			final PipedOutputStream pipedOut = new PipedOutputStream(httpIn);

			final PipedWriteThread pipedWriteThread = new PipedWriteThread(httpIn, responseBody, countDownLatch,
					fileSize);
			final PipedReadEmbeddedFileThread pipedReadThread = new PipedReadEmbeddedFileThread(fileName, file,
					pipedOut);

			DataManager.getJettyExecutorService().execute(pipedReadThread);
			DataManager.getJettyExecutorService().execute(pipedWriteThread);

			try {
				countDownLatch.await();
				file.close();
				pipedOut.close();
				httpIn.close();
			} catch (InterruptedException e) {
				DataManager.getImplProv().getLogger().error("Unable to wait for sending file: " + e.getMessage());
			}
			responseBody.close();
		} catch (IOException e) {
			DataManager.getImplProv().getLogger().error("Unable to send file: " + e.getMessage());
		}

	}

	/**
	 * Generate a HTML output with the
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData} of the
	 * given {@link PrimaryDataEntity} and send the output over HTTP.
	 *
	 * @param response the corresponding HTTP exchange.
	 * @param entity   the the corresponding {@link PrimaryDataEntity}.
	 * @throws EdalException if unable to send metadata
	 */
	private void sendEntityMetaData(final HttpServletResponse response, final PrimaryDataEntity entity)
			throws EdalException {

		try {

			StringWriter w = null;
			if (entity.isDirectory()) {
				w = velocityHtmlGenerator.generateHtmlForDirectory((PrimaryDataDirectory) entity);
			} else {
				w = velocityHtmlGenerator.generateHtmlForFile((PrimaryDataFile) entity);
			}

			ByteArrayInputStream bis = new ByteArrayInputStream(String.valueOf(w.getBuffer()).getBytes());

			response.setContentType("text/html");
			response.setStatus(HttpStatus.Code.OK.getCode());
			final OutputStream responseBody = response.getOutputStream();

			CountDownLatch countDownLatch = new CountDownLatch(1);

			final PipedInputStream httpIn = new PipedInputStream(EdalConfiguration.STREAM_BUFFER_SIZE);
			final PipedOutputStream pipedOut = new PipedOutputStream(httpIn);

			final PipedWriteThread pipedThread = new PipedWriteThread(httpIn, responseBody, countDownLatch,
					bis.available());
			final PipedReadEmbeddedFileThread pipedReadThread = new PipedReadEmbeddedFileThread(entity.getName(), bis,
					pipedOut);

			DataManager.getJettyExecutorService().execute(pipedThread);
			DataManager.getJettyExecutorService().execute(pipedReadThread);

			try {
				countDownLatch.await();
			} catch (InterruptedException e) {
				throw new EdalException("unable to send html page", e);
			} finally {
				try {
					bis.close();
					pipedOut.close();
					httpIn.close();
					responseBody.close();
				} catch (Exception e) {
					DataManager.getImplProv().getLogger().error("unable to close html streams : " + e.getMessage());
				}
			}
		} catch (IOException e) {
			throw new EdalException("unable to send the html page over HTTP", e);
		} catch (EdalException e) {
			throw e;
		}

	}

	/**
	 * Generate a HTML output with the
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData} of the
	 * given {@link PrimaryDataEntity} and send the output over HTTP.
	 * 
	 * @param response       the corresponding HTTP exchange.
	 * @param entity         the the corresponding {@link PrimaryDataEntity}.
	 * @param versionNumber  the version number of this {@link PrimaryDataEntity}
	 * @param internalId     the id of the {@link PublicReference}
	 * @param identifierType the type of the {@link PublicReference}
	 * @throws EdalException if unable to send {@link MetaData}
	 */
	private void sendEntityMetaDataForPersistentIdentifier(final HttpServletResponse response,
			final PrimaryDataEntity entity, final long versionNumber, final PersistentIdentifier identifierType,
			final String internalId) throws EdalException {

		String cacheKey = generateCacheKey(identifierType, internalId, entity, versionNumber, 0);

		if (contentPageCache.get(cacheKey) == null) {

			long start = System.currentTimeMillis();

			DataManager.getImplProv().getLogger().debug("Load new Contentpage : " + cacheKey);

			try {

				ByteArrayOutputStream cacheFileOutputStream = new ByteArrayOutputStream();

				TeeOutputStream teeOutputStream = new TeeOutputStream(response.getOutputStream(),
						cacheFileOutputStream);

				CountDownLatch latch = new CountDownLatch(1);

				OutputStreamWriter outputStreamWriter = null;

				if (entity.isDirectory()) {
					outputStreamWriter = velocityHtmlGenerator.generateHtmlForDirectoryOfSnapshot(
							(PrimaryDataDirectory) entity, versionNumber, internalId, identifierType, teeOutputStream,
							latch);
				} else {
					outputStreamWriter = velocityHtmlGenerator.generateHtmlForFileOfSnapshot((PrimaryDataFile) entity,
							versionNumber, identifierType, internalId, teeOutputStream, latch);
				}

				response.setContentType("text/html");
				response.setStatus(HttpStatus.Code.OK.getCode());

				try {
					latch.await();
					outputStreamWriter.flush();
					outputStreamWriter.close();
					teeOutputStream.flush();
					teeOutputStream.close();

					contentPageCache.put(cacheKey, cacheFileOutputStream);
				} catch (EofException eof) {

					DataManager.getImplProv().getLogger()
							.warn("HTTP Request for '" + entity.getName() + "' canceled by user!");

					outputStreamWriter.close();
					teeOutputStream.flush();
					teeOutputStream.close();

				} catch (InterruptedException e) {
					DataManager.getImplProv().getLogger()
							.warn("HTTP Request for '" + entity.getName() + "' canceled by user!");
					throw new EdalException(e);
				}

			} catch (IOException e) {
				throw new EdalException(e);
			}

			DataManager.getImplProv().getLogger()
					.debug("Load new Contentpage (" + cacheKey + ") in " + (System.currentTimeMillis() - start) + " ms");

		}

		else {

			long start = System.currentTimeMillis();

			DataManager.getImplProv().getLogger().debug("Reload Contentpage from Cache : " + cacheKey);

			response.setContentType("text/html");
			response.setStatus(HttpStatus.Code.OK.getCode());

			try {
				ByteArrayOutputStream cacheFileInputStream = contentPageCache.get(cacheKey);
				try {
					cacheFileInputStream.writeTo(response.getOutputStream());
					cacheFileInputStream.close();
					response.getOutputStream().flush();
					response.getOutputStream().close();

				} catch (EofException eof) {
					DataManager.getImplProv().getLogger()
							.warn("HTTP Request for '" + entity.getName() + "' failed, unable to reload from cache");
					cacheFileInputStream.close();
					response.getOutputStream().flush();
					response.getOutputStream().close();
				}
			} catch (IOException e) {
				throw new EdalException(e);
			}

			DataManager.getImplProv().getLogger()
					.debug("Reload Contentpage (" + cacheKey + ")  in " + (System.currentTimeMillis() - start) + " ms");

		}
	}

	/**
	 * Generate a HTML output with the
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData} of the
	 * given {@link PrimaryDataEntity} and send the output over HTTP. This function
	 * is especially for the Output of the landing page for a reviewer, who should
	 * approve the requested
	 * {@link de.ipk_gatersleben.bit.bi.edal.primary_data.file.PublicReference}.
	 * 
	 * @param response       the corresponding HTTP exchange.
	 * @param entity         the the corresponding {@link PrimaryDataEntity}.
	 * @param reviewerCode   the reviewerCode to identify a reviewer
	 * @param versionNumber  the version number of this {@link PrimaryDataEntity}
	 * @param internalId     the id of the {@link PublicReference}
	 * @param identifierType the type of the {@link PublicReference}
	 * @throws EdalException if unable to send {@link MetaData}
	 */
	private void sendEntityMetaDataForReviewer(final HttpServletResponse response, final PrimaryDataEntity entity,
			final long versionNumber, final String internalId, final PersistentIdentifier identifierType,
			final int reviewerCode) throws EdalException {

		String cacheKey = generateCacheKey(identifierType, internalId, entity, versionNumber, reviewerCode);

		if (contentPageCache.get(cacheKey) == null) {

			DataManager.getImplProv().getLogger().debug("Regenerate Webpage : " + cacheKey);

			try {

				ByteArrayOutputStream cacheFileOutputStream = new ByteArrayOutputStream();

				TeeOutputStream teeOutputStream = new TeeOutputStream(response.getOutputStream(),
						cacheFileOutputStream);

				CountDownLatch latch = new CountDownLatch(1);

				OutputStreamWriter outputStreamWriter = null;

				if (entity.isDirectory()) {
					outputStreamWriter = velocityHtmlGenerator.generateHtmlForDirectoryForReviewer(
							(PrimaryDataDirectory) entity, versionNumber, internalId, identifierType, reviewerCode,
							teeOutputStream, latch);
				} else {
					outputStreamWriter = velocityHtmlGenerator.generateHtmlForFileForReviewer((PrimaryDataFile) entity,
							versionNumber, internalId, identifierType, reviewerCode, teeOutputStream, latch);
				}

				response.setContentType("text/html");
				response.setStatus(HttpStatus.Code.OK.getCode());

				try {
					latch.await();
					outputStreamWriter.flush();
					outputStreamWriter.close();
					teeOutputStream.flush();
					teeOutputStream.close();

					// cache.put(cacheKey, cacheFileOutputStream);

				} catch (EofException eof) {

					DataManager.getImplProv().getLogger()
							.warn("HTTP Request for '" + entity.getName() + "'canceled by user!");

					outputStreamWriter.close();
					teeOutputStream.flush();
					teeOutputStream.close();

				} catch (InterruptedException e) {
					throw new EdalException(e);
				}

			} catch (IOException e) {
				throw new EdalException(e);
			}
		}

		else {
			DataManager.getImplProv().getLogger().debug("Reload Webpage from Cache : " + cacheKey);

			response.setContentType("text/html");
			response.setStatus(HttpStatus.Code.OK.getCode());

			try {

				ByteArrayOutputStream cacheFileInputStream = contentPageCache.get(cacheKey);

				cacheFileInputStream.writeTo(response.getOutputStream());
				cacheFileInputStream.close();

				response.getOutputStream().flush();
				response.getOutputStream().close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	private final String generateCacheKey(final PersistentIdentifier identifierType, final String internalId,
			final PrimaryDataEntity entity, final long versionNumber, final int reviewerCode) {
		return new String(identifierType.toString() + "-" + internalId + "-" + entity.getID() + "-"
				+ String.valueOf(versionNumber) + "-" + String.valueOf(reviewerCode));
	}

	/**
	 * Send the data of the corresponding {@link PrimaryDataFile} to the HTTP
	 * {@link OutputStream}.
	 * 
	 * @param file     the corresponding {@link PrimaryDataFile} to send.
	 * @param response
	 * @throws EdalException if unable to send {@link PrimaryDataFile}
	 */
	private void sendFile(final PrimaryDataFile file, final long versionNumber, final HttpServletResponse response)
			throws EdalException {

		final PrimaryDataFile currentFile = file;
		try {
			currentFile.switchCurrentVersion(currentFile.getVersionByRevisionNumber(versionNumber));
		} catch (PrimaryDataEntityVersionException e1) {
			this.sendMessage(response, HttpStatus.Code.NOT_FOUND, "no file found");
		}

		try {
			String type = "";
			Long size = null;
			try {
				type = ((DataFormat) currentFile.getCurrentVersion().getMetaData()
						.getElementValue(EnumDublinCoreElements.FORMAT)).getMimeType();
				size = ((DataSize) currentFile.getCurrentVersion().getMetaData()
						.getElementValue(EnumDublinCoreElements.SIZE)).getFileSize();

			} catch (final MetaDataException e) {
				throw new EdalException("unable to load the MIME type/file size", e);
			}

			response.setContentType(type);
			if (size > (LIMIT_INLINE_FILE_SIZE)) {
				response.setHeader("Content-Disposition", "attachment; filename=\"" + currentFile.getName() + "\"");
			} else {
				response.setHeader("Content-Disposition", "inline; filename=\"" + currentFile.getName() + "\"");
			}
			/* Do not use setContentLenght, because int could be too small */
			/* response.setContentLength(size.intValue()); */
			response.setContentLengthLong(size);
			response.setStatus(HttpStatus.Code.OK.getCode());

			CountDownLatch countDownLatch = new CountDownLatch(1);

			final OutputStream responseBody = response.getOutputStream();
			// copy entity bytes stream to HTTP stream
			final PipedInputStream httpIn = new PipedInputStream(EdalConfiguration.STREAM_BUFFER_SIZE);
			final PipedOutputStream pipedOut = new PipedOutputStream(httpIn);

			final PipedWriteThread pipedWriteThread = new PipedWriteThread(httpIn, responseBody, countDownLatch, size);
			final PipedReadEdalFileThread pipedReadThread = new PipedReadEdalFileThread(currentFile, pipedOut);

			DataManager.getJettyExecutorService().execute(pipedWriteThread);
			DataManager.getJettyExecutorService().execute(pipedReadThread);
			// currentFile.read(pipedOut);
			try {
				countDownLatch.await();
			} catch (InterruptedException e) {
				throw new EdalException("Unable to send file: " + e.getMessage(), e);
			} finally {
				try {
					pipedOut.close();
					httpIn.close();
					responseBody.close();
				} catch (Exception e) {
					DataManager.getImplProv().getLogger().error("Unable to close download streams: " + e.getMessage());
				}
			}
		} catch (final IOException e) {
			throw new EdalException("Unable to send file: " + e.getMessage(), e);
		}

	}

	/**
	 * Send a response with the given message and responseCode.
	 * 
	 * @param response     the corresponding HTTP exchange.
	 * @param responseCode the response code for this message (e.g. 200,404...).
	 * @param message      the message to send.
	 */
	private void sendMessage(final HttpServletResponse response, final HttpStatus.Code responseCode,
			final String message) {

		try {

			final String htmlOutput = velocityHtmlGenerator.generateHtmlErrorMessage(responseCode, message).toString();

			response.setStatus(responseCode.getCode());

			response.setContentType("text/html");

			OutputStream responseBody = response.getOutputStream();
			responseBody.write(htmlOutput.getBytes());
			responseBody.close();

		} catch (EofException eof) {
			// Do nothing, because response was already send
		} catch (IOException | EdalException e) {
			DataManager.getImplProv().getLogger().error("Unable to send " + responseCode + "-message : " + e);
		}
	}

	/**
	 * Send a response with the given message and responseCode.
	 * 
	 * @param response     the corresponding HTTP exchange.
	 * @param responseCode the response code for this message (e.g. 200,404...).
	 * @throws EdalException
	 */
	private void sendReport(final HttpServletResponse response, final HttpStatus.Code responseCode)
			throws EdalException {

		String cacheKey = "ReportCache";

		if (reportPageCache.get(cacheKey) == null) {

			long start = System.currentTimeMillis();

			DataManager.getImplProv().getLogger().debug("Load new Reportpage : " + cacheKey);

			try {

				response.setStatus(responseCode.getCode());

				response.setContentType("text/html");

				ByteArrayOutputStream cacheFileOutputStream = new ByteArrayOutputStream();

				TeeOutputStream teeOutputStream = new TeeOutputStream(response.getOutputStream(),
						cacheFileOutputStream);

				CountDownLatch latch = new CountDownLatch(1);

				OutputStreamWriter outputStreamWriter = null;

				outputStreamWriter = velocityHtmlGenerator.generateHtmlForReport(responseCode, teeOutputStream, latch);

				try {
					latch.await();
					outputStreamWriter.flush();
					outputStreamWriter.close();
					teeOutputStream.flush();
					teeOutputStream.close();

					reportPageCache.put(cacheKey, cacheFileOutputStream);

				} catch (EofException eof) {
					DataManager.getImplProv().getLogger().warn("HTTP Request for report page canceled by user!");
					outputStreamWriter.close();
					teeOutputStream.flush();
					teeOutputStream.close();
					reportPageCache.clean();
				} catch (InterruptedException e) {
					DataManager.getImplProv().getLogger().warn("HTTP Request for report page canceled by user!");
					reportPageCache.clean();
					throw new EdalException(e);
				}

			} catch (Exception e) {
				DataManager.getImplProv().getLogger()
						.warn("Unable to send " + responseCode + "-message : " + e.getClass());
			}

			DataManager.getImplProv().getLogger()
					.debug("Load new Reportpage in " + (System.currentTimeMillis() - start) + " ms");

		} else {

			long start = System.currentTimeMillis();

			DataManager.getImplProv().getLogger().debug("Reload Reportpage from Cache : " + cacheKey);

			response.setContentType("text/html");
			response.setStatus(HttpStatus.Code.OK.getCode());

			try {
				ByteArrayOutputStream cacheFileInputStream = reportPageCache.get(cacheKey);
				try {
					cacheFileInputStream.writeTo(response.getOutputStream());
					cacheFileInputStream.close();
					response.getOutputStream().flush();
					response.getOutputStream().close();

				} catch (EofException eof) {
					DataManager.getImplProv().getLogger()
							.warn("HTTP Request for reportpage failed, unable to reload from cache");
					cacheFileInputStream.close();
					response.getOutputStream().flush();
					response.getOutputStream().close();
					reportPageCache.clean();
				}
			} catch (IOException e) {
				reportPageCache.clean();
				throw new EdalException(e);
			}
			DataManager.getImplProv().getLogger()
					.debug("Reload Reportpage from Cache in " + (System.currentTimeMillis() - start) + " ms");

		}
	}

	public static void deleteTicketFromReviewerHashMap(String ticket) {

		for (Entry<Integer, List<String>> entry : reviewerHashMap.entrySet()) {

			List<String> list = entry.getValue();

			if (list.contains(ticket)) {
				list.remove(ticket);
			}
		}
	}

	public static void deleteTicketFromUserHashMap(String ticket) {

		for (Entry<Integer, List<String>> entry : userHashMap.entrySet()) {

			List<String> list = entry.getValue();

			if (list.contains(ticket)) {
				list.remove(ticket);
			}
		}
	}
}