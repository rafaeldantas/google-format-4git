package com.google.googlejavaformat.java;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.beust.jcommander.JCommander;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.google.common.collect.TreeRangeSet;
import com.google.googlejavaformat.java.JavaFormatterOptions.JavadocFormatter;
import com.google.googlejavaformat.java.JavaFormatterOptions.SortImports;
import com.google.googlejavaformat.java.JavaFormatterOptions.Style;
import com.google.googlejavaformat.java.Main.ConstructFilesToFormatResult;

import dantas.coiffeur.git.CommandLineExecutor;
import dantas.coiffeur.git.Git;
import dantas.coiffeur.git.GitEngine;
import dantas.coiffeur.git.GitFormatterParameters;

public class CoiffeurFormatter {

	private final Git git = new GitEngine(new CommandLineExecutor());

	private static final int MAX_THREADS = 20;

	static PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out, UTF_8), true);
	static PrintWriter err = new PrintWriter(new OutputStreamWriter(System.err, UTF_8), true);

	public static void main(String[] args) {

		GitFormatterParameters gitParameters = new GitFormatterParameters();
		JCommander jCommander = new JCommander(gitParameters);
		jCommander.setProgramName("google-java-format-git");
		jCommander.parse(args);

		if (!gitParameters.useGit()) {
			Main.main(args);
		} else {

			JavaFormatterOptions formatterOptions = new JavaFormatterOptions(JavadocFormatter.ECLIPSE, Style.AOSP, SortImports.ALSO) {
				@Override
				public int maxLineLength() {
					return gitParameters.maxLineLength();
				}
			};
			try {
				int result = new CoiffeurFormatter().format(formatterOptions, gitParameters);
				System.exit(result);
			} catch (UsageException e) {
				System.err.print(e.usage());
			}
		}
	}

	private int format(JavaFormatterOptions options, GitFormatterParameters gitParameters) throws UsageException {

		ConstructFilesToFormatResult constructFilesToFormatResult = constructFilesToFormat(gitParameters);
		boolean allOkay = constructFilesToFormatResult.allOkay;
		ImmutableList<FileToFormat> filesToFormat = constructFilesToFormatResult.filesToFormat;
		if (filesToFormat.isEmpty()) {
			return allOkay ? 0 : 1;
		}

		List<Future<Boolean>> results = new ArrayList<>();
		int numThreads = Math.min(MAX_THREADS, filesToFormat.size());
		ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

		Object outputLock = new Object();
		for (FileToFormat fileToFormat : filesToFormat) {
			results.add(
					executorService.submit(new FormatFileCallable(fileToFormat, outputLock, options, true, out, err)));
		}
		for (Future<Boolean> result : results) {
			try {
				allOkay &= result.get();
			} catch (InterruptedException e) {
				synchronized (outputLock) {
					err.println(e);
				}
				allOkay = false;
			} catch (ExecutionException e) {
				synchronized (outputLock) {
					err.println(e.getCause());
				}
				allOkay = false;
			}
		}
		return allOkay ? 0 : 1;
	}

	private ConstructFilesToFormatResult constructFilesToFormat(GitFormatterParameters gitParameters) {
		boolean allOkay = true;
		Set<Path> seenRealPaths = new HashSet<>();
		ImmutableList.Builder<FileToFormat> filesToFormat = ImmutableList.builder();

		for (String fileName : git.listModified()) {
			if (fileName.endsWith(".java")) {
				out.println("Formatting " + fileName);

				TreeRangeSet<Integer> rangeSet = TreeRangeSet.create();
				if(gitParameters.gitModifiedOnlyFlag()){
					rangeSet.addAll(git.listModifiedRanges(fileName));
				}else{
					rangeSet.add(Range.all());
				}

				try {
					Path originalPath = Paths.get(fileName);
					boolean added = seenRealPaths.add(originalPath.toRealPath());
					if (added) {
						filesToFormat.add(
								new FileToFormatPath(originalPath, rangeSet, new ArrayList<>(), new ArrayList<>()));
					}
				} catch (IOException e) {
					err.append(fileName).append(": could not read file: ").append(e.getMessage()).append('\n').flush();
					allOkay = false;
				}
			} else {
				err.println("Skipping non-Java file: " + fileName);
			}
		}

		return new ConstructFilesToFormatResult(allOkay, filesToFormat.build());
	}
}
