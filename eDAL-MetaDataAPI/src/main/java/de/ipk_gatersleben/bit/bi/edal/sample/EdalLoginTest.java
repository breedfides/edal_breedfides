package de.ipk_gatersleben.bit.bi.edal.sample;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class EdalLoginTest {

	private static String providerURLValue = "";
	private static String baseDNValue = "";

	public static void main(String[] args) {

		final Options options = new Options();

		Option helpOption = new Option("h", "help", false, "print help");

		Option providerURLOption = new Option("p", "providerURL", true, "providerURL");
		Option baseDNOption = new Option("b", "baseDN", true, "baseDN");


		options.addOption(helpOption);
		options.addOption(providerURLOption);

		final CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (final ParseException e) {
			System.exit(0);
		}

		if (cmd.hasOption(providerURLOption.getOpt())) {
			providerURLValue = cmd.getOptionValue(providerURLOption.getOpt());
		}
		if (cmd.hasOption(baseDNOption.getOpt())) {
			baseDNValue = cmd.getOptionValue(baseDNOption.getOpt());

		}

//		EdalHelpers.authenticateSubjectWithLDAP(providerURL, baseDN, user)

	}

}
