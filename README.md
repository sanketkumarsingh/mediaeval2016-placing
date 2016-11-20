# mediaeval2016-placing

This repository contain source code for experiment ran for Mediaeval workshop 2016 - The placing task.

We use the YFCC dataset and training and test set provided by organizers of Mediaeval Benchmarking -  Placing task.

1. To generate train and test data for textual feature:
	a) DataPreparator.generateTrain()
	b) DataPreparator.generateEstimationTestData()
	c) DataPreparator.generateVerificationTestData()

2. For Tamura feature, data preprocessing is required so call:
	a) GenerateTamuraTrain.generateCountyBasedTrain()
	b) GenerateTamuraTrain.generateCountyBasedTest()

3. To convert the train data in format required by Vowpal Wabbit call:
	a) GenerateTamuraTrain.generateTrainForSVM()
	b) GenerateTamuraTrain.generateTestForSVM()

4. After this, we find the UserCount ratio over each grid for usertags - generates the posting list for each grid with usertag ratio:
	a) OrderingLocationByUserCount.generateUserIdTagFile()
	b) sort the file by usertags column 
	c) then run, OrderingLocationByUserCount.generateWeightBasedGridFile()

5. With the files generated above and with the test file, run UserCountNeighbourSmoothing.java to get the test result for estimation task. To generate the file required to be submitted for the evaluation run, GenerateEstimateData.java
6. For getting the result of Verification over test instances, run Verification.java with files obtained above
