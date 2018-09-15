The code creates as an output a file in the -OUTPUTFOLDER folder, with the dataset name, alpha used, k for top-k and a timestamp of the run. The file contains various statistics of the search, and the top-k patterns discovered.  Every pattern contains the entropy of the target, the mutual information, the expected mutual information under the null, the reliable fraction of information score, and the uncorrected reliable fraction of information score. 

valid dataset formats:
	-arff
	-xarf (https://bitbucket.org/realKD/realkd/wiki/model/data/xarf)
	-csv (with or without header)

arguments:
	Obligatory
		-DATASET  (dataset filename)
		-OUTPUTFOLDER  (output folder)
	Optional
		-TARGET (the index of the target variable (starting from 1), default is the last column)
		-K		(number of results the user wants, default is 1)
		-BINS   (number of bins for equal-frequency discretization, default is 5)
		For greedy only
			-BEAMWIDTH (the size of the beam, default is 1)
		For Branch-and-bound with OPUS only
			-ALPHA   (alpha-approximation to use, default is 1 which yields the optimal solution)

example run for Branch and Bound:
	For the example dataset abalone.arff located in this folder, the following command

		nohup java -cp  FoDiscovery.jar de.mpiinf.fodiscovery.singleexp.FoOPUS -DATASET abalone.arff -OUTPUTFOLDER exampleOutput/ -K 5 -ALPHA 1 &

	produces the output found in the exampleOutput folder with 5 dependencies returned. An example pattern output is the following:

		FunctionalPatternImplementation(([Shell_weight, Sex, Shucked_weight],[Rings]), [Co-domain entropy: 3.602136152750491,mutual information: 0.850694568131174,expected mutual information: 0.17471427784508936,fraction of information: 0.23616391276093418,reliable fraction of information: 0.18766094939801897])

	which means that we have the three dimensional descriptor [Shell_weight, Sex, Shucked_weight] for the target [Rings] with reliable fraction of information score 0.187.

example run for Greedy algorithm:	
	If Branch and Bound is slow, one can try the greedy algorithm that is not optimal, but in practice yields close to optimal solutions fast. The following command

		nohup java -cp  FoDiscovery.jar de.mpiinf.fodiscovery.singleexp.FoBeam -DATASET abalone.arff -OUTPUTFOLDER exampleOutput/ -K 5  &

	produces the output found in the exampleOutput folder with 5 dependencies returned. An example pattern output is the following:

		FunctionalPatternImplementation(([Shucked_weight, Shell_weight, Sex],[Rings]), [Co-domain entropy: 3.602136152750491,mutual inf.: 0.850694568131174,expected mutual information: 0.17471427784508925,fraction of information: 0.23616391276093412,reliable fraction of information: 0.18766094939801897])

	which means that we have the three dimensional descriptor [Shell_weight, Sex, Shucked_weight] for the target [Rings] with reliable fraction of information score 0.187. As we see, the result is the same as in the BnB algorithm.