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
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.jena.riot.Lang;

public class Main {

	public static void main(String[] args) throws ParseException {
		Linker linker = new Linker();
		Lang backgroundLang = Lang.NTRIPLES;

		Options options = new Options();

		Option indexOpt = Option.builder("index").required(false).desc("create an index based on the information background.").hasArg(false).build();
		options.addOption(indexOpt);

		Option linkOpt = Option.builder("link").required(false).desc("replace literals (link) existent in the files in toLink directory to the respective resources in information background.").hasArg(false).build();
		options.addOption(linkOpt);

		Option persistentOpt = Option.builder("persistent").required(false).desc("enable the persistent index. It assumes in memory if ommited.").hasArg(false).build();
		options.addOption(persistentOpt);

		Option backgroundLangOpt = Option.builder("backgroundLang").required(false).desc("set the information background RDF serialization format. It assumes NTRIPLES if ommited.").hasArg(false).build();
		options.addOption(backgroundLangOpt);

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);

		if (cmd.hasOption("persistent")) {
			linker.persistentIndex();
		}

		if (cmd.hasOption("backgroundLang")) {
			String lang = cmd.getOptionValue("backgroundLang");
			if (lang.equalsIgnoreCase("N3")) {
				backgroundLang = Lang.N3;
			} else if (lang.equalsIgnoreCase("TURTLE")) {
				backgroundLang = Lang.TURTLE;
			} else if (lang.equalsIgnoreCase("JSONLD")) {
				backgroundLang = Lang.JSONLD;
			} else if (lang.equalsIgnoreCase("RDFXML")) {
				backgroundLang = Lang.RDFXML;
			} else if (lang.equalsIgnoreCase("TRIG")) {
				backgroundLang = Lang.TRIG;
			} else if (lang.equalsIgnoreCase("TRIX")) {
				backgroundLang = Lang.TRIX;
			}
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
						L2RDataMgr.link(out, backgroundLang, in, backgroundLang, linker);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println("Done!");
		}
	}
}
