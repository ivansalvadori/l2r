package br.ufsc.inf.lapesd.l2r;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.jena.riot.Lang;

public class Main {

	public static void main(String[] args) throws ParseException {
		Linker linker = new Linker();
		Lang backgroundLang = Lang.NTRIPLES;
		Lang toLinkLang = Lang.NTRIPLES;
		Lang linkedLang = Lang.NTRIPLES;
		linkedLang = Lang.JSONLD;

		Options options = new Options();

		Option indexOpt = Option.builder("index").required(false).desc("create an index based on the information background.").hasArg(false).build();
		options.addOption(indexOpt);

		Option linkOpt = Option.builder("link").required(false).desc("replace literals (link) existent in the files in toLink directory to the respective resources in information background.").hasArg(false).build();
		options.addOption(linkOpt);

		Option persistentOpt = Option.builder("persistent").required(false).desc("enable the persistent index. It assumes in memory if ommited.").hasArg(false).build();
		options.addOption(persistentOpt);

		Option backgroundLangOpt = Option.builder("backgroundLang").required(false).desc("set the information background RDF serialization format. It assumes NTRIPLES if ommited.").hasArg(true).build();
		options.addOption(backgroundLangOpt);

		Option toLinkLangOpt = Option.builder("toLinkLang").required(false).desc("set RDF serialization format for all files in toLink directory. It assumes NTRIPLES if ommited.").hasArg(true).build();
		options.addOption(toLinkLangOpt);

		Option linkedLangOpt = Option.builder("linkedLang").required(false).desc("set RDF serialization format for the resulting linked files. It assumes NTRIPLES if ommited.").hasArg(true).build();
		options.addOption(linkedLangOpt);

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);

		if (cmd.getOptions().length == 0) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("l2r", options);
		}

		if (cmd.hasOption("persistent")) {
			linker.persistentIndex();
		}

		if (cmd.hasOption("backgroundLang")) {
			String lang = cmd.getOptionValue("backgroundLang");
			backgroundLang = getLang(lang);
		}

		if (cmd.hasOption("toLinkLang")) {
			String lang = cmd.getOptionValue("toLinkLang");
			toLinkLang = getLang(lang);
		}

		if (cmd.hasOption("linkedLang")) {
			String lang = cmd.getOptionValue("linkedLang");
			linkedLang = getLang(lang);
		}

		if (cmd.hasOption("index")) {
			System.out.println("Indexing....");
			String csvFilesFolder = "background";
			Collection<File> files = FileUtils.listFiles(new File(csvFilesFolder), null, true);
			for (File file : files) {
				try (InputStream in = new FileInputStream(file)) {
					L2RDataMgr.index(in, backgroundLang, linker);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println("Done!");
		}

		if (cmd.hasOption("link")) {
			System.out.println("Linking....");
			String toLinkFolder = "toLink";
			Collection<File> files = FileUtils.listFiles(new File(toLinkFolder), null, true);
			for (File file : files) {
				try (InputStream in = new FileInputStream(file)) {
					try (OutputStream out = new FileOutputStream(new File(file.getPath() + "_linked"))) {
						L2RDataMgr.link(out, linkedLang, in, toLinkLang, linker);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println("Done!");
		}
	}

	private static Lang getLang(String value) {
		if (value.equalsIgnoreCase("N3")) {
			return Lang.N3;
		} else if (value.equalsIgnoreCase("TURTLE")) {
			return Lang.TURTLE;
		} else if (value.equalsIgnoreCase("JSONLD")) {
			throw new RuntimeException("JSON-LD not supported");
		} else if (value.equalsIgnoreCase("RDFXML")) {
			return Lang.RDFXML;
		} else if (value.equalsIgnoreCase("TRIG")) {
			return Lang.TRIG;
		} else if (value.equalsIgnoreCase("TRIX")) {
			return Lang.TRIX;
		} else if (value.equalsIgnoreCase("NQUADS")) {
			return Lang.NQUADS;
		} else if (value.equalsIgnoreCase("RDFTHRIFT")) {
			return Lang.RDFTHRIFT;
		}
		return Lang.NTRIPLES;
	}

}
