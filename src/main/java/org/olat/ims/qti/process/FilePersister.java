/**
 * OLAT - Online Learning and Training<br>
 * http://www.olat.org
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Copyright (c) since 2004 at Multimedia- & E-Learning Services (MELS),<br>
 * University of Zurich, Switzerland.
 * <p>
 */

package org.olat.ims.qti.process;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.olat.core.id.Identity;
import org.olat.core.logging.OLATRuntimeException;
import org.olat.core.logging.Tracing;
import org.olat.core.util.FileUtils;
import org.olat.core.util.WebappHelper;
import org.olat.core.util.xml.XMLParser;
import org.olat.ims.resources.IMSEntityResolver;

/**
 */
public class FilePersister implements Persister {
	private static final String QTI_SER = "qtiser";
	private static final String RES_REPORTING = "resreporting";
	private static final String QTI_FILE = "qti.ser";
	private final String subjectName;
	private final String resourcePathInfo; // <Course_ID>/<Node_ID>

	/**
	 * Assuming: only one test can be persisted per combination of a dlPointer and a SubjectName, = a certain test for a specific user can only be persisted at one place
	 * FIXME:pb:a identity and repositoryEntryKey are not enough as one test may be reference n times from within one course.
	 * 
	 * @param subj the user
	 * @param repositoryPath path information e.g. <Course_ID>/<Node_ID>
	 */
	public FilePersister(final Identity subj, final String resourcePathInfo) {
		super();
		this.resourcePathInfo = resourcePathInfo;
		this.subjectName = subj.getName();
	}

	/**
	 * serialize the current test in case of a stop and later resume (e.g. the browser of the user crashes, and the user wants to resume the test or survey in a later
	 * session)
	 * 
	 * @see org.olat.ims.qti.process.Persister#persist(Object, String)
	 */
	@Override
	public void persist(final Object o, final String info) {
		final File fSerialDir = new File(getFullQtiPath());
		OutputStream os = null;
		try {
			long start = -1;
			final boolean debugOn = Tracing.isDebugEnabled(FilePersister.class);
			if (debugOn) {
				start = System.currentTimeMillis();
			}
			fSerialDir.mkdirs();
			os = new FileOutputStream(new File(fSerialDir, QTI_FILE));
			// big tests (>5MB) produce heavy load on the system without buffered output. 256K seem to be a performant value
			// BufferedOutputStream bos = new BufferedOutputStream(os, 262144);
			// above stmt. incorrect, big buffer does not mean faster storage
			final BufferedOutputStream bos = FileUtils.getBos(os);

			final ObjectOutputStream oostream = new ObjectOutputStream(bos);
			oostream.writeObject(o);
			oostream.close();
			os.close();
			if (debugOn) {
				final long stop = System.currentTimeMillis();
				Tracing.logDebug("time in ms to save ims qti ser file:" + (stop - start), FilePersister.class);
			}
		} catch (final Exception e) {
			try {
				if (os != null) {
					os.close();
				}
			} catch (final IOException e1) {
				throw new OLATRuntimeException(this.getClass(), "Error while closing file stream: ", e1);
			}
			throw new OLATRuntimeException(this.getClass(), "user " + subjectName + " stream could not be saved to path:" + getFullQtiPath(), e);
		}
	}

	/**
	 * returns (at the moment) only AssessmentInstances, see persist()
	 */
	@Override
	public Object toRAM() {
		// File path e.g. qtiser/<Unique_Course_ID>/<Node_ID>/test/qti.ser
		File fSerialDir = new File(getFullQtiPath());
		if (!fSerialDir.exists()) {
			// file not found => try older path version ( < V5.1) e.g. qtiser/test/360459/qti.ser
			final String path = QTI_SER + File.separator + subjectName + File.separator + resourcePathInfo;
			fSerialDir = new File(WebappHelper.getUserDataRoot() + File.separator + path);
		}
		Object o = null;
		InputStream is = null;
		try {
			long start = -1;
			final boolean debugOn = Tracing.isDebugEnabled(FilePersister.class);
			if (debugOn) {
				start = System.currentTimeMillis();
			}
			is = new FileInputStream(new File(fSerialDir, QTI_FILE));
			final BufferedInputStream bis = new BufferedInputStream(is, 262144);
			final ObjectInputStream oistream = new ObjectInputStream(bis);
			o = oistream.readObject();
			oistream.close();
			is.close();
			if (debugOn) {
				final long stop = System.currentTimeMillis();
				Tracing.logDebug("time in ms to load ims qti ser file:" + (stop - start), FilePersister.class);
			}

		} catch (final Exception e) {
			try {
				if (is != null) {
					is.close();
				}
			} catch (final IOException e1) {
				// did our best to close the inputstream
			}
		}
		return o;
	}

	@Override
	public void cleanUp() {
		final File fSerialDir = new File(getFullQtiPath());
		try {
			FileUtils.deleteDirsAndFiles(fSerialDir, true, true);
		} catch (final Exception e) {
			throw new OLATRuntimeException(FilePersister.class, "could not delete qti.ser file in clean-up process " + getFullQtiPath() + File.separator + QTI_FILE, e);
		}
	}

	/**
	 * Persist results for this user/aiid as an XML document. dlPointer is aiid in this case.
	 * 
	 * @param doc
	 * @param type
	 * @param info
	 */
	public static void createResultsReporting(final Document doc, final Identity subj, final String type, final long aiid) {
		final File fUserdataRoot = new File(WebappHelper.getUserDataRoot());
		final String path = RES_REPORTING + File.separator + subj.getName() + File.separator + type;
		final File fReportingDir = new File(fUserdataRoot, path);
		try {
			fReportingDir.mkdirs();
			final OutputStream os = new FileOutputStream(new File(fReportingDir, aiid + ".xml"));
			final Element element = doc.getRootElement();
			final XMLWriter xw = new XMLWriter(os, new OutputFormat("  ", true));
			xw.write(element);
			// closing steams
			xw.close();
			os.close();
		} catch (final Exception e) {
			throw new OLATRuntimeException(FilePersister.class,
					"Error persisting results reporting for subject: '" + subj.getName() + "'; assessment id: '" + aiid + "'", e);
		}
	}

	/**
	 * Retreive results for this user/aiid
	 * 
	 * @param type The type of results
	 * @return
	 */
	public static Document retreiveResultsReporting(final Identity subj, final String type, final long aiid) {
		final File fUserdataRoot = new File(WebappHelper.getUserDataRoot());
		final String path = RES_REPORTING + File.separator + subj.getName() + File.separator + type + File.separator + aiid + ".xml";
		final File fDoc = new File(fUserdataRoot, path);
		Document doc = null;
		try {
			final InputStream is = new FileInputStream(fDoc);
			final BufferedInputStream bis = new BufferedInputStream(is);
			final XMLParser xmlParser = new XMLParser(new IMSEntityResolver());
			doc = xmlParser.parse(bis, false);
			is.close();
			bis.close();
		} catch (final Exception e) {
			throw new OLATRuntimeException(FilePersister.class,
					"Error retrieving results reporting for subject: '" + subj.getName() + "'; assessment id: '" + aiid + "'", e);
		}
		return doc;
	}

	/**
	 * Return full directory path which is used to store qti.ser file. Is unique for certain identity and resource Format <resource_path>/<identity_name> e.g.
	 * bcroot/qtiser/7400000102/7411112222/test
	 * 
	 * @return Full directory path
	 */
	private String getFullQtiPath() {
		return WebappHelper.getUserDataRoot() + File.separator + QTI_SER + File.separator + resourcePathInfo + File.separator + subjectName;
	}

	/**
	 * Delete all qti data dirs for certain user. Includes : /qtiser/<REPO_ID>/<COURSE_ID>/<USER_NAME>, /qtiser/<USER_NAME> /resreporting/<USER_NAME>
	 * 
	 * @param identity
	 */
	public static void deleteUserData(final Identity identity) {
		try {
			// 1. Delete temp file qti.ser @ /qtiser/<REPO_ID>/<COURSE_ID>/<USER_NAME>
			// Loop over all repo-id-dirs and loop over all course-id-dirs and look for username
			final File qtiserBaseDir = new File(WebappHelper.getUserDataRoot() + File.separator + QTI_SER);
			class OlatResidFilter implements FilenameFilter {
				@Override
				public boolean accept(final File dir, final String name) {
					return (name.matches("[0-9]*"));
				}
			}
			final File[] dirs = qtiserBaseDir.listFiles(new OlatResidFilter());
			if (dirs != null) {
				for (int i = 0; i < dirs.length; i++) {
					final File[] subDirs = dirs[i].listFiles(new OlatResidFilter());
					for (int j = 0; j < subDirs.length; j++) {
						final File userDir = new File(subDirs[j], identity.getName());
						if (userDir.exists()) {
							FileUtils.deleteDirsAndFiles(userDir, true, true);
							Tracing.logDebug("Delete qti.ser Userdata dir=" + userDir.getAbsolutePath(), FilePersister.class);
						}
					}
				}
			}

			// 2. Delete temp file qti.ser @ /qtiser/<USER_NAME> (old <5.1 path)
			final File qtiserDir = new File(WebappHelper.getUserDataRoot() + File.separator + QTI_SER + File.separator + identity.getName());
			if (qtiserDir != null) {
				FileUtils.deleteDirsAndFiles(qtiserDir, true, true);
				Tracing.logDebug("Delete qti.ser Userdata dir=" + qtiserDir.getAbsolutePath(), FilePersister.class);
			}
			// 3. Delete resreporting @ /resreporting/<USER_NAME>
			final File resReportingDir = new File(WebappHelper.getUserDataRoot() + File.separator + RES_REPORTING + File.separator + identity.getName());
			if (resReportingDir != null) {
				FileUtils.deleteDirsAndFiles(resReportingDir, true, true);
				Tracing.logDebug("Delete qti resreporting Userdata dir=" + qtiserDir.getAbsolutePath(), FilePersister.class);
			}
		} catch (final Exception e) {
			throw new OLATRuntimeException(FilePersister.class, "could not delete QTI resreporting dir for identity=" + identity, e);
		}
	}

	/**
	 * Return full directory path which is used to store qti.ser file in course node context.
	 * 
	 * @param course course resourceable id
	 * @param node node ident
	 * @return
	 */
	public static String getFullPathToCourseNodeDirectory(final String course, final String node) {
		return WebappHelper.getUserDataRoot() + File.separator + QTI_SER + File.separator + course + File.separator + node;
	}

}