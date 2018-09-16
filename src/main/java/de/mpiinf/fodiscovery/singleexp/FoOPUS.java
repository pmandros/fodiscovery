/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-16 The Contributors of the realKD Project
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package de.mpiinf.fodiscovery.singleexp;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import de.unibonn.realkd.algorithms.branchbound.OPUS.OperatorOrder;
import de.unibonn.realkd.algorithms.branchbound.OPUS.TraverseOrder;
import de.unibonn.realkd.algorithms.functional.OPUSFunctionalPatternSearch;
import de.unibonn.realkd.algorithms.functional.OPUSFunctionalPatternSearch.LanguageOption;
import de.unibonn.realkd.algorithms.functional.OPUSFunctionalPatternSearch.OptimisticEstimatorOption;
import de.unibonn.realkd.common.workspace.Workspace;
import de.unibonn.realkd.common.workspace.Workspaces;
import de.unibonn.realkd.data.table.DataTable;
import de.unibonn.realkd.data.table.DataTables;
import de.unibonn.realkd.data.table.DiscreteDataTable;
import de.unibonn.realkd.data.table.XarfImport;
import de.unibonn.realkd.patterns.Pattern;
import de.unibonn.realkd.patterns.functional.FunctionalPattern;

/**
 * A class for discovery using OPUS branch-and-bound search
 * 
 * 
 * @author Panagiotis Mandros
 *
 */
public class FoOPUS {
	public static String dataset;

	public static int target = 0;

	public static int k = 1;

	public static OperatorOrder operatorOrder = OperatorOrder.OPUS_PAPER;

	public static LanguageOption langOption = LanguageOption.ALL;

	public static OptimisticEstimatorOption optOption = OptimisticEstimatorOption.CHAIN;

	public static TraverseOrder traverseOption = TraverseOrder.BREADTHFSPOTENTIAL;

	public static double alpha = 1;

	public static int numBins = 5;

	public static String outputFolder;

	public static void main(String[] args) throws Exception {
		readInputString(args);
		Path pathToOutput = Paths.get(outputFolder);
		if (!Files.exists(pathToOutput)) {
			Files.createDirectory(pathToOutput);
			System.out.println("Did not find the output folder. Created one instead.");
		}
		String timeStamp = new SimpleDateFormat("dd.MM.yyyy.HH.mm.ss").format(new Date());

		Workspace workspace = Workspaces.workspace();
		XarfImport builder = XarfImport.xarfImport(dataset);
		DataTable dataTable = builder.get();
		DiscreteDataTable dataDiscreteTable = DataTables.discretization(dataTable,
				DataTables.equalFrequencyDiscretization(numBins));
		workspace.add(dataDiscreteTable);

		OPUSFunctionalPatternSearch functionalPatternSearch = new OPUSFunctionalPatternSearch(workspace);
		if (target == 0) {
			functionalPatternSearch.target(dataDiscreteTable.attribute(dataTable.numberOfAttributes() - 1));
			target = dataTable.numberOfAttributes();
		} else {
			functionalPatternSearch.target(dataDiscreteTable.attribute(target - 1));
		}

		functionalPatternSearch.topK(k);
		functionalPatternSearch.alpha(alpha);
		functionalPatternSearch.operatorOrder(operatorOrder);
		functionalPatternSearch.languageOption(langOption);
		functionalPatternSearch.optimisticOption(optOption);
		functionalPatternSearch.traverseOrderOption(traverseOption);

		String datasetName = dataTable.caption();
		String experimentResultsFile = outputFolder + File.separator + datasetName + "_target-" + (target) + "_"
				+ traverseOption.toString() + "_" + optOption.toString() + "_a-" + alpha + "_topk-" + k + "_"
				+ timeStamp + ".txt";
		Collection<FunctionalPattern> resultPatterns = functionalPatternSearch.call();
		long time = functionalPatternSearch.runningTime();
		int nodes = functionalPatternSearch.nodesCreated();
		int nodesDiscardedPotential = functionalPatternSearch.nodesDiscarded();
		int nodesDiscardedPruning = functionalPatternSearch.nodesDiscardedPruningRules();
		int max_depth = functionalPatternSearch.maxAttainedDepth();
		int solution_depth = functionalPatternSearch.bestDepth();
		int boundary_max_size = functionalPatternSearch.maxAttainedBoundarySize();

		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(experimentResultsFile))) {
			writer.write("time: " + time / 1000);
			writer.write("\n");
			writer.write("Alpha used: " + alpha);
			writer.write("\n");
			writer.write("Search space: " + (Math.pow(2, dataTable.numberOfAttributes() - 1)));
			writer.write("\n");
			writer.write("Nodes created: " + nodes);
			writer.write("\n");
			writer.write("Nodes discarded potential: " + nodesDiscardedPotential);
			writer.write("\n");
			writer.write("Nodes discarded rules: " + nodesDiscardedPruning);
			writer.write("\n");
			writer.write("Pruned search space percentage: "
					+ (100 - (1.0 * 100 * nodes) / (Math.pow(2, dataTable.numberOfAttributes() - 1))));
			writer.write("\n");
			writer.write("Max depth: " + max_depth);
			writer.write("\n");
			writer.write("Solution depth: " + solution_depth);
			writer.write("\n");
			writer.write("Max boundary size: " + boundary_max_size);
			writer.write("\n");
			writer.write("Dependencies: ");
			writer.write("\n");

			for (Pattern<?> pattern : resultPatterns) {
				writer.write(pattern.toString());
				writer.write("\n");
			}
		}
	}

	public static void readInputString(String[] args) throws Exception {
		int i;
		int total = args.length - 1;

		// take dataset
		boolean found = false;

		for (i = 0; i < total; i++) {
			if (args[i].equals("-DATASET")) {
				dataset = args[i + 1];
				found = true;
				break;
			}
		}
		if (found == false) {
			throw new IllegalStateException("Missing -DATASET");
		}

		// take outfolder
		found = false;
		for (i = 0; i < total; i++) {
			if (args[i].equals("-OUTPUTFOLDER")) {
				outputFolder = args[i + 1];
				found = true;
				break;
			}
		}
		if (found == false) {
			throw new IllegalStateException("Missing -OUTPUTFOLDER");
		}

		// take target
		found = false;
		for (i = 0; i < total; i++) {
			if (args[i].equals("-TARGET")) {
				target = Integer.parseInt(args[i + 1]);
				if (target < 1) {
					throw new IllegalArgumentException(
							"Illegal value target attribute index. Should be between 1 and total number of attributes");
				}
				found = true;
				break;
			}
		}

		// take topk
		found = false;
		for (i = 0; i < total; i++) {
			if (args[i].equals("-K")) {
				k = Integer.parseInt(args[i + 1]);
				if (k < 1) {
					throw new IllegalArgumentException(
							"Illegal value for number of results. Should be greater or equal to 1");
				}
				found = true;
				break;
			}
		}

		// take alpha
		found = false;
		for (i = 0; i < total; i++) {
			if (args[i].equals("-ALPHA")) {
				alpha = Double.parseDouble(args[i + 1]);
				if (alpha <= 0 && alpha > 1) {
					throw new IllegalArgumentException("Illegal value for alpha. Range should be (0,1]");
				}
				found = true;
				break;
			}
		}

		// take bins
		found = false;
		for (i = 0; i < total; i++) {
			if (args[i].equals("-BINS")) {
				numBins = Integer.parseInt(args[i + 1]);
				if (numBins <= 1) {
					throw new IllegalArgumentException(
							"Illegal value for number of bins for discretization. Should be greater than 1");
				}

				found = true;
				break;
			}
		}

		// take traverse order option
		found = false;
		for (i = 0; i < total; i++) {
			if (args[i].equals("-TRAVERSEORDER")) {
				String trOrderToStr = (args[i + 1]);
				if (trOrderToStr.equals("BFSPOTENTIAL")) {
					traverseOption = TraverseOrder.BREADTHFSPOTENTIAL;
				} else if (trOrderToStr.equals("BFSVALUE")) {
					traverseOption = TraverseOrder.BESTFSVALUE;
				} else if (trOrderToStr.equals("BESTFSPOTENTIAL")) {
					traverseOption = TraverseOrder.BESTFSPOTENTIAL;
				} else if (trOrderToStr.equals("BESTFSVALUE")) {
					traverseOption = TraverseOrder.BESTFSVALUE;
				} else if (trOrderToStr.equals("DFS")) {
					traverseOption = TraverseOrder.DFS;
				} else {
					throw new IllegalArgumentException(
							"Traverse order argument is wrong. Choose on of the following: BFSPOTENTIAL, BFSVALUE, BESTFSPOTENTIAL, BESTFSVALUE, DFS");
				}
				found = true;
				break;
			}
		}

		// take optimistic estimator
		found = false;
		for (i = 0; i < total; i++) {
			if (args[i].equals("-OPT")) {
				String optEstimatorToStr = (args[i + 1]);
				if (optEstimatorToStr.equals("MON")) {
					optOption = OptimisticEstimatorOption.MON;
				} else if (optEstimatorToStr.equals("CHAIN")) {
					optOption = OptimisticEstimatorOption.CHAIN;
				} else if (optEstimatorToStr.equals("SPC")) {
					optOption = OptimisticEstimatorOption.SPC;
				} else {
					throw new IllegalArgumentException(
							"Wrong optimistic estimator argument. Valid options are MON, SPC, CHAIN");
				}
				found = true;
				break;
			}
		}
	}

}
