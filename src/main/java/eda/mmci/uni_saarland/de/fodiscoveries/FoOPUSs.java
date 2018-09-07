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

package eda.mmci.uni_saarland.de.fodiscoveries;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
 * A class for running multiple experiments using OPUS branch-and-bound search
 * 
 * @author Panagiotis Mandros
 *
 */
public class FoOPUSs {
	// the file that contains the specifications for each
	// experiment, one in each line
	public static String inputFile;

	// folder where all the output for each experiment
	// will be written
	public static String outputFolder;

	public static String datasetPerExperiment;

	public static int target;

	public static int k;

	public static int numBins;

	public static final OperatorOrder operatorOrder = OperatorOrder.OPUS_PAPER;

	public static final LanguageOption langOption = LanguageOption.ALL;

	public static OptimisticEstimatorOption optOption;

	public static TraverseOrder traverseOption;

	public static double alpha;

	public static void main(String[] args) throws Exception {
		readInputString(args);
		Path pathToOutput = Paths.get(outputFolder);
		if (!Files.exists(pathToOutput)) {

			Files.createDirectory(pathToOutput);
			System.out.println("Did not find the output folder. Created one instead.");
		}
		try {
			List<String> lines = Files.readAllLines(Paths.get(inputFile));
			for (String s : lines) {
				// assigning defaults for each experiment
				target = 0;
				k = 1;
				optOption = OptimisticEstimatorOption.CHAIN;
				traverseOption = TraverseOrder.BREADTHFSVALUE;
				numBins = 5;
				target = 0;
				alpha = 1;

				readExperimentParameters(s.split(" "));
				String timeStamp = new SimpleDateFormat("dd.MM.yyyy.HH.mm.ss").format(new Date());

				Workspace workspace = Workspaces.workspace();
				XarfImport builder = XarfImport.xarfImport(datasetPerExperiment);
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void readInputString(String[] args) throws Exception {
		int i;
		int total = args.length - 1;

		// take dataset
		boolean found = false;
		for (i = 0; i < total; i++) {
			if (args[i].equals("-INPUT")) {
				inputFile = args[i + 1];
				found = true;
				break;
			}
		}
		if (found == false) {
			throw new IllegalStateException("Missing -INPUT");
		}

		// take output folder
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
	}

	public static void readExperimentParameters(String[] args) throws Exception {
		int i;
		int total = args.length - 1;

		// take dataset
		boolean found = false;
		for (i = 0; i < total; i++) {
			if (args[i].equals("-DATASET")) {
				datasetPerExperiment = args[i + 1];
				found = true;
				break;
			}
		}
		if (found == false) {
			throw new Exception("Missing -DATASET");
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
			throw new Exception("Missing -OUTPUTFOLDER");
		}

		// take target
		found = false;
		for (i = 0; i < total; i++) {
			if (args[i].equals("-TARGET")) {
				target = Integer.parseInt(args[i + 1]);
				found = true;
				break;
			}
		}

		// take topk
		found = false;
		for (i = 0; i < total; i++) {
			if (args[i].equals("-K")) {
				k = Integer.parseInt(args[i + 1]);
				found = true;
				break;
			}
		}

		// take alpha
		found = false;
		for (i = 0; i < total; i++) {
			if (args[i].equals("-ALPHA")) {
				alpha = Double.parseDouble(args[i + 1]);
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
				} else {
					throw new Exception(
							"Traverse order argument is wrong. Chose on of the following: BFSPOTENTIAL, BFSVALUE, BESTFSPOTENTIAL, BESTFSVALUE");
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
				if (optEstimatorToStr.equals("OLD")) {
					optOption = OptimisticEstimatorOption.OLD;
				} else if (optEstimatorToStr.equals("CHAIN")) {
					optOption = OptimisticEstimatorOption.CHAIN;
				} else {
					throw new Exception("Wrong optimistic estimator argument. Valid options are OLD, CHAIN");
				}
				found = true;
				break;
			}
		}
	}

}
