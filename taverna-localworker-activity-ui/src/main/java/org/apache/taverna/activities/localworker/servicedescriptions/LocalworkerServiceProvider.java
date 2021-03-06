package org.apache.taverna.activities.localworker.servicedescriptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;

import org.apache.taverna.servicedescriptions.ServiceDescription;
import org.apache.taverna.servicedescriptions.ServiceDescriptionProvider;
import org.apache.taverna.workflowmodel.processor.activity.Activity;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LocalworkerServiceProvider implements ServiceDescriptionProvider {

	private static final String LOCALWORKER_NAMES = "/localworker_names";

	private static final String LOCALWORKER_SERVICE = "Local service";

	private static Logger logger = Logger.getLogger(Logger.class);

	private static final URI providerId = URI
			.create("http://taverna.sf.net/2010/service-provider/localworker");

	/** Used to deserialize the Activities stored on disk */
	private ObjectMapper objectMapper = new ObjectMapper();

	private static Map<String, String> localWorkerToScript = new HashMap<String, String>();

	static {
		localWorkerToScript.put("org.embl.ebi.escience.scuflworkers.java.ByteArrayToString",
				"Byte Array To String");
		localWorkerToScript.put("org.embl.ebi.escience.scuflworkers.java.DecodeBase64",
				"Decode Base 64 to byte Array");
		localWorkerToScript.put("org.embl.ebi.escience.scuflworkers.java.EchoList", "Echo List");
		localWorkerToScript.put("org.embl.ebi.escience.scuflworkers.java.EmitLotsOfStrings",
				"Create Lots Of Strings");
		localWorkerToScript.put("org.embl.ebi.escience.scuflworkers.java.EncodeBase64",
				"Encode Byte Array to Base 64");
		localWorkerToScript.put("org.embl.ebi.escience.scuflworkers.java.ExtractImageLinks",
				"Get image URLs from HTTP document");
		localWorkerToScript.put("org.embl.ebi.escience.scuflworkers.java.FilterStringList",
				"Filter List of Strings by regex");
		localWorkerToScript.put("org.embl.ebi.escience.scuflworkers.java.FlattenList",
				"Flatten List");
		localWorkerToScript.put("org.embl.ebi.escience.scuflworkers.java.PadNumber",
				"Pad numeral with leading 0s");
		localWorkerToScript.put(
				"org.embl.ebi.escience.scuflworkers.java.RegularExpressionStringList",
				"Filter list of strings extracting match to a regex");
		localWorkerToScript.put("org.embl.ebi.escience.scuflworkers.java.SendEmail",
				"Send an Email");
		localWorkerToScript.put("org.embl.ebi.escience.scuflworkers.java.SliceList",
				"Extract Elements from a List");
		localWorkerToScript.put("org.embl.ebi.escience.scuflworkers.java.SplitByRegex",
				"Split string into string list by regular expression");
		localWorkerToScript.put("org.embl.ebi.escience.scuflworkers.java.StringConcat",
				"Concatenate two strings");
		localWorkerToScript.put("org.embl.ebi.escience.scuflworkers.java.StringListMerge",
				"Merge String List to a String");
		localWorkerToScript.put("org.embl.ebi.escience.scuflworkers.java.StringSetDifference",
				"String List Difference");
		localWorkerToScript.put("org.embl.ebi.escience.scuflworkers.java.StringSetIntersection",
				"String List Intersection");
		localWorkerToScript.put("org.embl.ebi.escience.scuflworkers.java.StringSetUnion",
				"String List Union");
		localWorkerToScript.put("org.embl.ebi.escience.scuflworkers.java.StringStripDuplicates",
				"Remove String Duplicates");
		localWorkerToScript.put(
				"org.embl.ebi.escience.scuflworkers.java.TestAlwaysFailingProcessor",
				"Always Fails");
		localWorkerToScript.put("org.embl.ebi.escience.scuflworkers.java.TestSometimesFails",
				"Sometimes Fails");
		localWorkerToScript.put("org.embl.ebi.escience.scuflworkers.java.WebImageFetcher",
				"Get Image From URL");
		localWorkerToScript.put("org.embl.ebi.escience.scuflworkers.java.WebPageFetcher",
				"Get Web Page from URL");

		// xml:XPathText
		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.xml.XPathTextWorker",
				"XPath From Text");
		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.xml.XSLTWorker",
				"Transform XML");
		localWorkerToScript.put(
				"net.sourceforge.taverna.scuflworkers.xml.XSLTWorkerWithParameters",
				"Transform XML with parameters");

		// biojava
		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.biojava.GenBankParserWorker",
				"Read Gen Bank File");
		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.biojava.ReverseCompWorker",
				"Reverse Complement DNA");
		localWorkerToScript.put(
				"net.sourceforge.taverna.scuflworkers.biojava.SwissProtParserWorker",
				"Read Swiss Prot File");
		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.biojava.TranscribeWorker",
				"Transcribe DNA");

		// io
		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.io.TextFileReader",
				"Read Text File");
		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.io.TextFileWriter",
				"Write Text File");
		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.io.LocalCommand",
				"Execute Command Line App");
		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.io.FileListByExtTask",
				"List Files by Extension");
		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.io.FileListByRegexTask",
				"List Files By Regex");
		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.io.DataRangeTask",
				"Select Data Range From File");
		localWorkerToScript.put(
				"net.sourceforge.taverna.scuflworkers.io.ConcatenateFileListWorker",
				"Concatenate Files");
		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.io.EnvVariableWorker",
				"Get Environment Variables as XML");

		// ui
		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.ui.AskWorker", "Ask");
		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.ui.SelectWorker", "Select");
		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.ui.ChooseWorker", "Choose");
		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.ui.TellWorker", "Tell");
		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.ui.WarnWorker", "Warn");
		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.ui.SelectFileWorker",
				"Select File");
		// ncbi
		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.ncbi.NucleotideFastaWorker",
				"Get Nucleotide FASTA");
		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.ncbi.NucleotideGBSeqWorker",
				"Get Nucleotide GBSeq XML");
		localWorkerToScript.put(
				"net.sourceforge.taverna.scuflworkers.ncbi.NucleotideINSDSeqXMLWorker",
				"Get Nucleotide INSDSeq XML");
		localWorkerToScript.put(
				"net.sourceforge.taverna.scuflworkers.ncbi.NucleotideTinySeqXMLWorker",
				"Get Nucleotide TinySeq XML");

		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.ncbi.ProteinFastaWorker",
				"Get Protein FASTA");
		localWorkerToScript.put(
				"net.sourceforge.taverna.scuflworkers.ncbi.ProteinINSDSeqXMLWorker",
				"Get Protein INSDSeq XML");
		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.ncbi.ProteinGBSeqWorker",
				"Get Protein GBSeq XML");
		localWorkerToScript.put(
				"net.sourceforge.taverna.scuflworkers.ncbi.ProteinTinySeqXMLWorker",
				"Get Protein TinySeq XML");

		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.ncbi.PubMedESearchWorker",
				"Search PubMed XML");
		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.ncbi.PubMedEFetchWorker",
				"Get PubMed XML By PMID");

		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.jdbc.SQLQueryWorker",
				"Execute SQL Query");
		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.jdbc.SQLUpdateWorker",
				"Execute SQL Update");

		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.net.BrowseUrl",
				"Open web browser at a URL");
		localWorkerToScript.put("net.sourceforge.taverna.scuflworkers.net.ExtractHeader",
				"Extract HTTP Header");
	}

	public String getName() {
		return LOCALWORKER_SERVICE;
	}

	/**
	 * Use the {@link net.sf.taverna.t2.activities.localworker.translator.LocalworkerTranslator} to
	 * get a {@link Map} of all the local workers. Use the keys in this map
	 * to load all the serialized activities from disk by using
	 * <code> getClass().getResourceAsStream("/" + className) </code> to get
	 * them and then the {@link ActivityXMLDeserializer} to get the actual {@link Activity}. Create
	 * the {@link LocalworkerActivityItem} by
	 * populating them with the correct ports and depths. Sets the category to
	 * match the T1 version so that a query by category will split the local
	 * workers in to the correct place.
	 */
	public void findServiceDescriptionsAsync(FindServiceDescriptionsCallBack callBack) {

		List<ServiceDescription> items = new ArrayList<ServiceDescription>();

		InputStream inputStream = getClass().getResourceAsStream(LOCALWORKER_NAMES);
		if (inputStream == null) {
			logger.error("Could not find resource " + LOCALWORKER_NAMES);
			return;
		}
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
		String line = "";
		String category = null;
		try {
			while ((line = inputReader.readLine()) != null) {
				if (line.startsWith("category")) {
					String[] split = line.split(":");
					category = split[1];
				} else {
					LocalworkerServiceDescription createItem;
					try {
						createItem = createItem(line);
					} catch (ItemCreationException e) {
						logger.warn("Could not create item for: " + line, e);
						continue;
					}
					createItem.setCategory(category);
					createItem.setProvider("myGrid");
					items.add(createItem);
				}
			}
		} catch (IOException e1) {
			logger.warn("Could not read local worker definitions from " + LOCALWORKER_NAMES);
		}
		callBack.partialResults(items);
		callBack.finished();

	}

    @Override
    public ServiceDescriptionProvider newInstance() {
        return new LocalworkerServiceProvider();
    }

	@SuppressWarnings("serial")
	public class ItemCreationException extends Exception {

		public ItemCreationException() {
			super();
		}

		public ItemCreationException(String message, Throwable cause) {
			super(message, cause);
		}

		public ItemCreationException(String message) {
			super(message);
		}

		public ItemCreationException(Throwable cause) {
			super(cause);
		}

	}

	/**
	 * Loads the deserialised local worker from disk and creates a {@link LocalworkerActivityItem}
	 * with the correct ports and script from it
	 *
	 * @param line
	 * @return a LocalWorker with the appropriate Input/Output ports and script
	 * @throws ItemCreationException
	 */
	private LocalworkerServiceDescription createItem(String line) throws ItemCreationException {
		// get the file from disk
		String resource = "/" + line + ".json";
		InputStream resourceAsStream = getClass().getResourceAsStream(resource);
		if (resourceAsStream == null) {
			throw new ItemCreationException("Could not find resource " + resource);
		}

		JsonNode json;
		try {
			json = objectMapper.readTree(resourceAsStream);
		} catch (IOException e) {
			throw new ItemCreationException("Could not read resource " + resource, e);
		}

		LocalworkerServiceDescription item = new LocalworkerServiceDescription();
		item.setJson(json);
		item.setLocalworkerName(line);
		item.setOperation(localWorkerToScript.get(line));
		return item;

	}

	public Icon getIcon() {
		return LocalworkerActivityIcon.getLocalworkerIcon();
	}

	@Override
	public String toString() {
		return "Local workers provider";
	}

	public static String getServiceNameFromClassname(String classname) {
		return (localWorkerToScript.get(classname));
	}

	public String getId() {
		return providerId.toString();
	}

}
