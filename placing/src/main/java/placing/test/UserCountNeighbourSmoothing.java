package placing.test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import placing.util.GenerateGrid;

public class UserCountNeighbourSmoothing implements Runnable{
	
	private static Map<String, Map<String, TagProb>> gridPosting = new HashMap();
	private static long averageTagPerGrid;
	private static long totalGrid ;
	private static List<String> gridsInTrainList = new ArrayList();
	private static double lambda = 0.5;
	static String maxTagCountGrid = "";
	String testfileName;
	
	public UserCountNeighbourSmoothing(String fileName){
		this.testfileName = fileName;
	}

	public static void main(String[] args) throws IOException {

		Path gridfile = Paths.get("posting-train-1.0.txt");
		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		long totalNumberOfTags = 0;
		// maxTagCountGrid = "";
		long maxTagCount = 0;
		Map<String, Integer> gridTermCountMap = new HashMap();
		
		for (String line : (Iterable<String>) gridlines::iterator) {

			String lineArr[] = line.split("\t");
			Map<String, TagProb> mapTermCount = null;
			int totalTermCount = 0;
			for (int j = 1; j < lineArr.length; j++) {
				String termCount[] = lineArr[j].split("===");

				TagProb tagprob = new TagProb();
				if (termCount.length == 3) {
					tagprob.setLocationSpecific(true);
					tagprob.setTagProb(Double.parseDouble(termCount[2]));
				} else {
					tagprob.setLocationSpecific(false);
				}

				tagprob.setTagCount(Integer.parseInt(termCount[1]));

				if (gridPosting.containsKey(termCount[0])) {
					gridPosting.get(termCount[0]).put(lineArr[0], tagprob);
				} else {
					mapTermCount = new HashMap();
					mapTermCount.put(lineArr[0], tagprob);
					gridPosting.put(termCount[0], mapTermCount);
				}
				totalTermCount = totalTermCount + Integer.parseInt(termCount[1]);
				
			}
			totalNumberOfTags = totalNumberOfTags + totalTermCount;
			gridTermCountMap.put(lineArr[0], totalTermCount);
			if (maxTagCount < totalTermCount) {
				maxTagCount = totalTermCount;
				maxTagCountGrid = lineArr[0];
			}
			if (!gridsInTrainList.contains(lineArr[0])) {
				gridsInTrainList.add(lineArr[0]);
			}
		}
		totalGrid = gridsInTrainList.size();
		System.out.println("Total Grid:" + totalGrid);
		System.out.println("Grid with highest tag:" + maxTagCountGrid);
		System.out.println("Total number of global tags:" + totalNumberOfTags);

		averageTagPerGrid = (long) (totalNumberOfTags / totalGrid);
		System.out.println("averageTagPerGrid:" + averageTagPerGrid);
		System.out.println("Loaded Grid posting list..");

		//PrintWriter errorWriter = null;
		
		for(int i=1;i<=1;i++){
			UserCountNeighbourSmoothing obj = new UserCountNeighbourSmoothing("estimation-test-photo-me-1.0-"+i+".txt"); 
			Thread thread = new Thread(obj, "Thread-"+i);
			thread.start();
		}

		System.out.println("..Completed..");
	}

	@Override
	public void run() {
		PrintWriter resultWriter = null;
		try {
			//errorWriter = new PrintWriter(new FileWriter("errorWriter-0.01-gt.txt", true));
			resultWriter = new PrintWriter(new FileWriter("resultWriter-1.0-" +Thread.currentThread().getName()+".txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}

		int numberOfSucess = 0;
		int lineNumber = 0;
		System.out.println("Running for file:" + testfileName + "by thread:" + Thread.currentThread().getName());
		//Path file = Paths.get("estimation-test-photo-me-0.001.txt");
		Path file = Paths.get(testfileName);
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long totalNotInTrain = 0;
		for (String line : (Iterable<String>) lines::iterator) {
			String lineStr[] = line.split("\t");
			String realGridNum = lineStr[0];
			String userTags = lineStr[1];
			String userTagsArr[] = userTags.split(",");
			double maxGridScore = -1;
			String predictedGridNumber = "";
			ArrayList<GridInfo> top10Grid = new ArrayList();
			int totalGridCount = 0;
			for (int k = 0; k < gridsInTrainList.size(); k++) {

//				 if(realGridNum.equals(gridsInTrainList.get(k))){
//					 System.out.println();
//				 System.out.println("Real Grid.." + gridsInTrainList.get(k));
//				 //System.out.println(userTagsArr[i] + " " + score);
//				 }
//				
//				 if("1819787".equals(gridsInTrainList.get(k))){
//					 System.out.println();
//				 System.out.println("Predicted Grid.." +
//				 gridsInTrainList.get(k));
//				 //System.out.println(userTagsArr[i] + " " + score);
//				 }
				List<Long> neighbours = GenerateGrid.getNeighbourGrids(Long.parseLong(gridsInTrainList.get(k)) , 1.0,1);
				double gridScore = 0.0;

				for (int i = 0; i < userTagsArr.length; i++) {

					double score = 0.0;
					if (gridPosting.containsKey(userTagsArr[i])) {
						if (gridPosting.get(userTagsArr[i]).containsKey(gridsInTrainList.get(k))) {
							score = gridPosting.get(userTagsArr[i]).get(gridsInTrainList.get(k)).getTagProb();
						}
					}
					double neighbourScore = 0.0;
					for(int j=0;j<neighbours.size();j++){
						if (gridPosting.containsKey(userTagsArr[i])) {
							String neighbourGrid = String.valueOf(neighbours.get(j));
							if (gridPosting.get(userTagsArr[i]).containsKey(neighbourGrid)) {
								neighbourScore = neighbourScore + gridPosting.get(userTagsArr[i]).get(neighbourGrid).getTagProb();
							}
						}
					}
					neighbourScore = neighbourScore / ((double)neighbours.size());
					double totalScoreForTag = ((1-lambda) * score) + (lambda * neighbourScore);
					
					gridScore = gridScore + totalScoreForTag;
//					 if(realGridNum.equals(gridsInTrainList.get(k))){
//					 // System.out.println("Real Grid..");
//					 System.out.println(userTagsArr[i] + " " + score + " " +
//							 gridScore);
//					 }
//					
//					 if("1819787".equals(gridsInTrainList.get(k))){
//					 // System.out.println("Predicted Grid..");
//					 System.out.println(userTagsArr[i] + " " + score + " " +
//							 gridScore);
//					 }
				}
				// }
				if (gridScore > maxGridScore) {
					predictedGridNumber = gridsInTrainList.get(k);
					maxGridScore = gridScore;
				}
//				if(predictedGridNumber.equals("10110")){
//					System.out.println("Break");
//				}
				if (totalGridCount < 10) {
					GridInfo grid = new GridInfo();
					grid.setGrid(gridsInTrainList.get(k));
					grid.setGridScore(gridScore);
					top10Grid.add(grid);
					Collections.sort(top10Grid);
					totalGridCount++;
				} else {
					if (gridScore > top10Grid.get(totalGridCount - 1).getGridScore()) {
						boolean found = false;
						GridInfo gridInfo = null;
						for (int index = 0; index < totalGridCount; index++) {
							if (found) {
								GridInfo currentGrid = top10Grid.get(index);
								top10Grid.set(index, gridInfo);
								gridInfo = currentGrid;
							} else {
								if (top10Grid.get(index).getGridScore() < gridScore) {
									found = true;
									gridInfo = top10Grid.get(index);
									GridInfo grid = new GridInfo();
									grid.setGrid(gridsInTrainList.get(k));
									grid.setGridScore(gridScore);
									top10Grid.set(index, grid);
								}
							}
						}
					}
				}

			}

			boolean yes = false;
			if (maxGridScore == 0.0) {
				yes = true;
				predictedGridNumber = String.valueOf(maxTagCountGrid);
			}

			lineNumber = lineNumber + 1;
			if (predictedGridNumber.equals(realGridNum)) {
				numberOfSucess = numberOfSucess + 1;
			} else {

				if (!gridsInTrainList.contains(realGridNum)) {

					totalNotInTrain++;
				}

			}
			if (lineNumber % 50000 == 0) {
				System.out.println("Total number of Images:" + lineNumber + " and numberofSuccess:" + numberOfSucess
						+ " and totalNotInTrain:" + totalNotInTrain + " Thread:"+ Thread.currentThread().getName() +"\n");
				// resultWriter.flush();
			}
			String toWrite = lineNumber + "\t" + realGridNum + "\t" + predictedGridNumber;
			for (int index = 0; index < top10Grid.size(); index++) {
				toWrite = toWrite + "\t" + top10Grid.get(index).getGrid();
			}
			toWrite = toWrite + "\t" + yes;
			resultWriter.write(toWrite + "\n");
			resultWriter.flush();

		}
	//	errorWriter.close();
		System.out.println("Total number of Images:" + lineNumber + " and numberofSuccess:" + numberOfSucess
				+ " and totalNotInTrain:" + totalNotInTrain + " Thread:"+ Thread.currentThread().getName() +"\n");

		System.out.println("Done for thread:" + Thread.currentThread().getName());
		resultWriter.close();
	}


}
