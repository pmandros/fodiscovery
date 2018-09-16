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

import de.unibonn.realkd.algorithms.functional.BeamFunctionalPatternSearch;
import de.unibonn.realkd.algorithms.functional.BeamFunctionalPatternSearch.OptimisticEstimatorOption;
import de.unibonn.realkd.common.workspace.Workspace;
import de.unibonn.realkd.common.workspace.Workspaces;
import de.unibonn.realkd.data.table.DataTable;
import de.unibonn.realkd.data.table.DataTables;
import de.unibonn.realkd.data.table.DiscreteDataTable;
import de.unibonn.realkd.data.table.XarfImport;
import de.unibonn.realkd.patterns.Pattern;
import de.unibonn.realkd.patterns.functional.FunctionalPattern;
import utils.Utilities;

/**
 * A class for discovery using Beam search
 * 
 * @author Panagiotis Mandros
 *
 */
public class FoBeam {
	public static void main(String[] args) throws Exception {
		String dataset = Utilities.dataset(args);
		String outputFolder = Utilities.outputFolder(args);
		int target = Utilities.target(args);
		int k = Utilities.numResults(args);
		OptimisticEstimatorOption optOption = Utilities.optGreedy(args);
		int beamWidth = Utilities.beamWidth(args);
		int numBins = Utilities.numBins(args);
		
		
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

		BeamFunctionalPatternSearch functionalPatternSearch = new BeamFunctionalPatternSearch(workspace);
		if (target == 0) {
			functionalPatternSearch.target(dataDiscreteTable.attribute(dataTable.numberOfAttributes() - 1));
			target = dataTable.numberOfAttributes();
		} else {
			functionalPatternSearch.target(dataDiscreteTable.attribute(target - 1));
		}
		functionalPatternSearch.topK(k);
		functionalPatternSearch.beamWidth(beamWidth);
		functionalPatternSearch.optimisticOption(optOption);

		String datasetName = dataTable.caption();
		String experimentResultsFile = outputFolder + File.separator + datasetName + "_Target-" + (target) + "_opt-"
				+ optOption.toString() + "_beamWidth-" + beamWidth + "_topk-" + k + "_" + timeStamp + ".txt";
		Collection<FunctionalPattern> resultPatterns = functionalPatternSearch.call();
		long time = functionalPatternSearch.runningTime();
		int nodes = functionalPatternSearch.nodesCreated();
		int nodesDiscardedPotential = functionalPatternSearch.nodesDiscarded();
		int max_depth = functionalPatternSearch.maxAttainedDepth();
		int solution_depth = functionalPatternSearch.bestDepth();
		int boundary_max_size = functionalPatternSearch.maxAttainedBoundarySize();

		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(experimentResultsFile))) {
			writer.write("time: " + time / 1000);
			writer.write("\n");
			writer.write("Search space: " + (Math.pow(2, dataTable.numberOfAttributes() - 1)));
			writer.write("\n");
			writer.write("Nodes created: " + nodes);
			writer.write("\n");
			writer.write("Nodes discarded potential: " + nodesDiscardedPotential);
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
}
