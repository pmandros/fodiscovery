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

package utils;

import de.unibonn.realkd.algorithms.beamsearch.NewBeamSearch.RefinementPropagation;

/**
 * A class to provide utility functions
 * 
 * 
 * @author Panagiotis Mandros
 */
public class Utilities {

	// default values for experiment parameters
	public static final int NUM_MAX_BINS = 5;
	public static final int CUT_POINT_MULTIPLIER = 2;
	public static final int NUM_RESULTS = 1;
	public static final int TARGET = 0;
	public static final int NUM_BINS_TARGET = 10;

	public static final de.unibonn.realkd.algorithms.functional.OPUSFunctionalPatternSearch.OptimisticEstimatorOption OPT_OPUS = de.unibonn.realkd.algorithms.functional.OPUSFunctionalPatternSearch.OptimisticEstimatorOption.CHAIN;
	public static final de.unibonn.realkd.algorithms.functional.OPUSFunctionalPatternSearch.DiscretizationTypeOption DISC_TYPE_OPUS = de.unibonn.realkd.algorithms.functional.OPUSFunctionalPatternSearch.DiscretizationTypeOption.EF;

	public static final de.unibonn.realkd.algorithms.functional.BeamFunctionalPatternSearch.OptimisticEstimatorOption OPT_GREEDY = de.unibonn.realkd.algorithms.functional.BeamFunctionalPatternSearch.OptimisticEstimatorOption.CHAIN;
	public static final de.unibonn.realkd.algorithms.functional.BeamFunctionalPatternSearch.DiscretizationTypeOption DISC_TYPE_GREEDY = de.unibonn.realkd.algorithms.functional.BeamFunctionalPatternSearch.DiscretizationTypeOption.EF;

	public static final RefinementPropagation REF_PROP = RefinementPropagation.NON_REDUNDANT;
	public static final double ALPHA = 1;
	public static final int BEAM_WIDTH = 5;

	public static int maxBins(String args[]) {
		int numBins = NUM_MAX_BINS;
		int i;
		int total = args.length - 1;
		for (i = 0; i < total; i++) {
			if (args[i].equals("-L")) {
				numBins = Integer.parseInt(args[i + 1]);
				if (numBins <= 1) {
					throw new IllegalArgumentException(
							"Illegal value for maximum number of bins for discretization. Should be >1");
				}
				break;
			}
		}
		return numBins;
	}

	public static int cutPointMultiplier(String args[]) {
		int numBins = CUT_POINT_MULTIPLIER;
		int i;
		int total = args.length - 1;
		for (i = 0; i < total; i++) {
			if (args[i].equals("-C")) {
				numBins = Integer.parseInt(args[i + 1]);
				if (numBins < 1) {
					throw new IllegalArgumentException(
							"Illegal value for cut point multiplier for discretization. Should be >=1");
				}
				break;
			}
		}
		return numBins;
	}

	public static int numBinsForTarget(String args[]) {
		int numBins = NUM_BINS_TARGET;
		int i;
		int total = args.length - 1;
		for (i = 0; i < total; i++) {
			if (args[i].equals("-NUM_BINS_TARGET")) {
				numBins = Integer.parseInt(args[i + 1]);
				if (numBins <= 1) {
					throw new IllegalArgumentException(
							"Illegal value for number of bins for target discretization. Should be >1");
				}
				break;
			}
		}
		return numBins;
	}

	public static int numResults(String args[]) {
		int k = NUM_RESULTS;
		int i;
		int total = args.length - 1;
		for (i = 0; i < total; i++) {
			if (args[i].equals("-K")) {
				k = Integer.parseInt(args[i + 1]);
				if (k < 1) {
					throw new IllegalArgumentException(
							"Illegal value for number of results. Should be greater or equal to 1");
				}
				break;
			}
		}
		return k;
	}

	public static int target(String args[]) {
		int target = TARGET;
		int i;
		int total = args.length - 1;
		for (i = 0; i < total; i++) {
			if (args[i].equals("-TARGET")) {
				target = Integer.parseInt(args[i + 1]);
				if (target < 1) {
					throw new IllegalArgumentException(
							"Illegal value target attribute index. Should be between 1 and total number of attributes");
				}
				break;
			}
		}
		return target;
	}

	public static de.unibonn.realkd.algorithms.functional.OPUSFunctionalPatternSearch.OptimisticEstimatorOption optOPUS(
			String args[]) {
		de.unibonn.realkd.algorithms.functional.OPUSFunctionalPatternSearch.OptimisticEstimatorOption optOption = OPT_OPUS;
		int i;
		int total = args.length - 1;
		for (i = 0; i < total; i++) {
			if (args[i].equals("-OPT")) {
				String optEstimatorToStr = (args[i + 1]);
				if (optEstimatorToStr.equals("MON")) {
					optOption = de.unibonn.realkd.algorithms.functional.OPUSFunctionalPatternSearch.OptimisticEstimatorOption.MON;
				} else if (optEstimatorToStr.equals("CHAIN")) {
					optOption = de.unibonn.realkd.algorithms.functional.OPUSFunctionalPatternSearch.OptimisticEstimatorOption.CHAIN;
				} else if (optEstimatorToStr.equals("SPC")) {
					optOption = de.unibonn.realkd.algorithms.functional.OPUSFunctionalPatternSearch.OptimisticEstimatorOption.SPC;
				} else {
					throw new IllegalArgumentException(
							"Wrong optimistic estimator argument. Valid options are MON, SPC, CHAIN");
				}
				break;
			}
		}
		return optOption;
	}

	public static de.unibonn.realkd.algorithms.functional.OPUSFunctionalPatternSearch.DiscretizationTypeOption discTypeOPUS(
			String args[]) {
		de.unibonn.realkd.algorithms.functional.OPUSFunctionalPatternSearch.DiscretizationTypeOption discTypeOption = DISC_TYPE_OPUS;
		int i;
		int total = args.length - 1;
		for (i = 0; i < total; i++) {
			if (args[i].equals("-DISCTYPE")) {
				String optEstimatorToStr = (args[i + 1]);
				if (optEstimatorToStr.equals("COP")) {
					discTypeOption = de.unibonn.realkd.algorithms.functional.OPUSFunctionalPatternSearch.DiscretizationTypeOption.COP;
				} else if (optEstimatorToStr.equals("EF")) {
					discTypeOption = de.unibonn.realkd.algorithms.functional.OPUSFunctionalPatternSearch.DiscretizationTypeOption.EF;
				} else if (optEstimatorToStr.equals("PRE")) {
					discTypeOption = de.unibonn.realkd.algorithms.functional.OPUSFunctionalPatternSearch.DiscretizationTypeOption.PRE;
				} else {
					throw new IllegalArgumentException(
							"Wrong discretization type argument. Valid options are COP, EF, PRE");
				}
				break;
			}
		}
		return discTypeOption;
	}

	public static de.unibonn.realkd.algorithms.functional.BeamFunctionalPatternSearch.OptimisticEstimatorOption optGreedy(
			String args[]) {
		de.unibonn.realkd.algorithms.functional.BeamFunctionalPatternSearch.OptimisticEstimatorOption optOption = OPT_GREEDY;
		int i;
		int total = args.length - 1;
		for (i = 0; i < total; i++) {
			if (args[i].equals("-OPT")) {
				String optEstimatorToStr = (args[i + 1]);
				if (optEstimatorToStr.equals("MON")) {
					optOption = de.unibonn.realkd.algorithms.functional.BeamFunctionalPatternSearch.OptimisticEstimatorOption.MON;
				} else if (optEstimatorToStr.equals("CHAIN")) {
					optOption = de.unibonn.realkd.algorithms.functional.BeamFunctionalPatternSearch.OptimisticEstimatorOption.CHAIN;
				} else if (optEstimatorToStr.equals("NONE")) {
					optOption = de.unibonn.realkd.algorithms.functional.BeamFunctionalPatternSearch.OptimisticEstimatorOption.NONE;
				} else if (optEstimatorToStr.equals("SPC")) {
					optOption = de.unibonn.realkd.algorithms.functional.BeamFunctionalPatternSearch.OptimisticEstimatorOption.SPC;
				} else {
					throw new IllegalArgumentException(
							"Wrong optimistic estimator argument. Valid options are MON, SPC, CHAIN, NONE");
				}
				break;
			}
		}
		return optOption;
	}

	public static de.unibonn.realkd.algorithms.functional.BeamFunctionalPatternSearch.DiscretizationTypeOption discTypeGreedy(
			String args[]) {
		de.unibonn.realkd.algorithms.functional.BeamFunctionalPatternSearch.DiscretizationTypeOption discTypeOption = DISC_TYPE_GREEDY;
		int i;
		int total = args.length - 1;
		for (i = 0; i < total; i++) {
			if (args[i].equals("-DISCTYPE")) {
				String optEstimatorToStr = (args[i + 1]);
				if (optEstimatorToStr.equals("COP")) {
					discTypeOption = de.unibonn.realkd.algorithms.functional.BeamFunctionalPatternSearch.DiscretizationTypeOption.COP;
				} else if (optEstimatorToStr.equals("EF")) {
					discTypeOption = de.unibonn.realkd.algorithms.functional.BeamFunctionalPatternSearch.DiscretizationTypeOption.EF;
				} else if (optEstimatorToStr.equals("PRE")) {
					discTypeOption = de.unibonn.realkd.algorithms.functional.BeamFunctionalPatternSearch.DiscretizationTypeOption.PRE;
				} else {
					throw new IllegalArgumentException(
							"Wrong discretization type argument. Valid options are COP, EF, PRE");
				}
				break;
			}
		}
		return discTypeOption;
	}

	public static RefinementPropagation refOpt(String args[]) {
		RefinementPropagation refOption = REF_PROP;
		int i;
		int total = args.length - 1;
		for (i = 0; i < total; i++) {
			if (args[i].equals("-REFOPT")) {
				String refOptToStr = (args[i + 1]);
				if (refOptToStr.equals("ALL")) { 
					refOption = RefinementPropagation.ALL;
				} else if (refOptToStr.equals("NONRED")) {
					refOption = RefinementPropagation.NON_REDUNDANT;
				} else {
					throw new IllegalArgumentException(
							"Wrong refinement propagation argument. Valid options are ALL, NONRED");
				}
				break;
			}
		}
		return refOption;
	}

	public static int beamWidth(String args[]) {
		int beamWidth = BEAM_WIDTH;
		int i;
		int total = args.length - 1;
		for (i = 0; i < total; i++) {
			if (args[i].equals("-BEAMWIDTH")) {
				beamWidth = Integer.parseInt(args[i + 1]);
				if (beamWidth < 1) {
					throw new IllegalArgumentException("Illegal value for beam width. Should be greater or equal to 1");
				}
				break;
			}
		}
		return beamWidth;
	}

	public static double alpha(String args[]) {
		double alpha = ALPHA;
		int i;
		int total = args.length - 1;
		for (i = 0; i < total; i++) {
			if (args[i].equals("-ALPHA")) {
				alpha = Double.parseDouble(args[i + 1]);
				if (alpha <= 0 && alpha > 1) {
					throw new IllegalArgumentException("Illegal value for alpha. Range should be (0,1]");
				}
				break;
			}
		}
		return alpha;
	}

	public static String dataset(String args[]) {
		String dataset = "";
		int i;
		int total = args.length - 1;
		boolean found = false;
		for (i = 0; i < total; i++) {
			if (args[i].equals("-DATASET")) {
				dataset = args[i + 1];
				found = true;
				break;
			}
		}
		if (found == false || dataset.isEmpty()) {
			throw new IllegalStateException("Missing -DATASET");
		} else {
			return dataset;
		}
	}

	public static String outputFolder(String args[]) {
		String outputFolder = "";
		int i;
		int total = args.length - 1;
		boolean found = false;
		for (i = 0; i < total; i++) {
			if (args[i].equals("-OUTPUTFOLDER")) {
				outputFolder = args[i + 1];
				found = true;
				break;
			}
		}

		if (found == false || outputFolder.isEmpty()) {
			throw new IllegalStateException("Missing -OUTPUTFOLDER");
		} else {
			return outputFolder;
		}
	}

	public static String input(String args[]) {
		String input = "";
		int i;
		int total = args.length - 1;
		boolean found = false;
		for (i = 0; i < total; i++) {
			if (args[i].equals("-INPUT")) {
				input = args[i + 1];
				found = true;
				break;
			}
		}

		if (found == false || input.isEmpty()) {
			throw new IllegalStateException("Missing -INPUT");
		} else {
			return input;
		}
	}

	//
	// // take traverse order option
	// found = false;
	// for (i = 0; i < total; i++) {
	// if (args[i].equals("-TRAVERSEORDER")) {
	// String trOrderToStr = (args[i + 1]);
	// if (trOrderToStr.equals("BFSPOTENTIAL")) {
	// traverseOption = TraverseOrder.BREADTHFSPOTENTIAL;
	// } else if (trOrderToStr.equals("BFSVALUE")) {
	// traverseOption = TraverseOrder.BESTFSVALUE;
	// } else if (trOrderToStr.equals("BESTFSPOTENTIAL")) {
	// traverseOption = TraverseOrder.BESTFSPOTENTIAL;
	// } else if (trOrderToStr.equals("BESTFSVALUE")) {
	// traverseOption = TraverseOrder.BESTFSVALUE;
	// } else {
	// throw new Exception(
	// "Traverse order argument is wrong. Chose on of the following: BFSPOTENTIAL,
	// BFSVALUE, BESTFSPOTENTIAL, BESTFSVALUE");
	// }
	// found = true;
	// break;
	// }
	// }

}
