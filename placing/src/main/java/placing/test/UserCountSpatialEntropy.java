package placing.test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class UserCountSpatialEntropy {

	private static Map<String, Map<String, TagProb>> gridPosting = new HashMap();
	static long averageTagPerGrid;
	static long totalGrid = 6480000;
	static List<String> gridsInTrainList = new ArrayList();

	public static void main(String[] args) throws IOException {

		Path gridfile = Paths.get("userid-tag-title-grid-0.1.txt");
		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		long totalNumberOfTags = 0;
		String maxTagCountGrid = "";
		long maxTagCount = 0;
		for (String line : (Iterable<String>) gridlines::iterator) {

			String lineArr[] = line.split("\t");
			Map<String, TagProb> mapTermCount = null;
			int totalTermCount = 0;
			for (int j = 1; j < lineArr.length; j++) {
				String termCount[] = lineArr[j].split("===");
				// System.out.println(termCount[1]);
//				mapTermCount.put(termCount[0], Integer.parseInt(termCount[1]));
				TagProb tagprob = new TagProb();
				if(termCount.length == 3){
					tagprob.setLocationSpecific(true);
					tagprob.setTagProb(Double.parseDouble(termCount[2]));
				}else{
					tagprob.setLocationSpecific(false);
				}
				
				tagprob.setTagCount(Integer.parseInt(termCount[1]));
				if(gridPosting.containsKey(termCount[0])){
					gridPosting.get(termCount[0]).put(lineArr[0], tagprob);
				}else{
					mapTermCount = new HashMap();
					mapTermCount.put(lineArr[0], tagprob);
					gridPosting.put(termCount[0], mapTermCount);
				}
				totalTermCount = totalTermCount + Integer.parseInt(termCount[1]);
				totalNumberOfTags = totalNumberOfTags + totalTermCount;
			}
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
		System.out.println("Total number of global tags:" +totalNumberOfTags);

		// read the tag -  normalized entropy file (prerequisite)
		gridfile = Paths.get("tag-entropy.txt"); 
		gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineStr[] = line.split("\t");
			if(gridPosting.containsKey(lineStr[0])){
		        TagProb tagProb = new TagProb();
		        tagProb.setTagProb(Double.parseDouble(lineStr[1]));
				gridPosting.get(lineStr[0]).put("===ENTROPY===", tagProb);
			}else{
				System.out.println("Doesn't contain the tag.");
			}
		}
		
		System.out.println("Normalized Entropy for tags calculated..");

		
		averageTagPerGrid = (long) (totalNumberOfTags / totalGrid);
		System.out.println("averageTagPerGrid:" + averageTagPerGrid);
		System.out.println("Loaded Grid posting list..");

		PrintWriter errorWriter = null;
		PrintWriter resultWriter = null;
		try {
			errorWriter = new PrintWriter(new FileWriter("errorWriter-gt.txt", true));
			resultWriter = new PrintWriter(new FileWriter("resultWriter-gt.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}

		int numberOfSucess = 0;
		int lineNumber = 0;

		Path file = Paths.get("estimation-test-onlyUT-photo-me-0.1.txt");
		Stream<String> lines = null;

		lines = Files.lines(file, StandardCharsets.UTF_8);
		long totalNotInTrain = 0;
		for (String line : (Iterable<String>) lines::iterator) {
			String lineStr[] = line.split("\t");
			String realGridNum = lineStr[0];
			String userTags = lineStr[1];
			String userTagsArr[] = userTags.split(",");
			double maxGridScore = -1;
			int maxMatch = -1;
			String predGridByMatch="";
			String predictedGridNumberByScore = "";
			double matchGridScore = 0.0;
			int scoreGridMatchCount = 0;
			System.out.println("********************************");
			System.out.println();
			String predictedGridNumber = "";
			
			for (int k = 0; k < gridsInTrainList.size(); k++) {

				double gridScore = 0.0;
				int gridMatchCount = 0;
				for (int i = 0; i < userTagsArr.length; i++) {
					// String userTag = userTagsArr[i];
					//double score = getTfIdfScore(userTagsArr[i], gridsInTrainList.get(k));
					// if (realGridNum.equals(String.valueOf(k))) {
					// System.out.println("UserTag:" + score);
					// }
					// if (realGridNum.equals(predVal)) {
					// System.out.println("UserTag:" + score);
					// }
					double score = 0.0;
					if(gridPosting.containsKey(userTagsArr[i])){
						if(gridPosting.get(userTagsArr[i]).containsKey(gridsInTrainList.get(k))){
							
							 score = gridPosting.get(userTagsArr[i]).get(gridsInTrainList.get(k)).getTagProb();
							 double entropy = gridPosting.get(userTagsArr[i]).get("===ENTROPY===").getTagProb();
							 if(entropy > 0.0){
								 gridMatchCount++;
							 }
							 if(realGridNum.equals(gridsInTrainList.get(k))){
								 System.out.println(realGridNum + " Tag:" + userTagsArr[i] + " ratio=" + score + " entropy:" + entropy);
							 }
							 if(gridsInTrainList.get(k).equals("1751412")){
								 System.out.println("1751412:" + " Tag:" + userTagsArr[i] + " ratio=" + score + " entropy:" + entropy);
								 
							 }
//							 if(gridsInTrainList.get(k).equals( "1844899")){
//								 System.out.println("1844899:" + " Tag:" + userTagsArr[i] + " ratio=" + score + " entropy:" + entropy);
//								 
//							 }
							
							 score = score * gridPosting.get(userTagsArr[i]).get("===ENTROPY===").getTagProb();
							 
						}
					}
//					
//					if(realGridNum.equals(gridsInTrainList.get(k))){
//						System.out.println("Tag:" + userTagsArr[i] + " score:" + score);
//					}
				
					gridScore = gridScore + score;
					if(realGridNum.equals(gridsInTrainList.get(k))){
						System.out.println(realGridNum + " Score:" + score + " gridscore:" + gridScore);
					}
					if(gridsInTrainList.get(k).equals("1751412")){
						System.out.println("1751412:" +  " Score:" + score + " gridscore:" + gridScore);
					}
//					if(gridsInTrainList.get(k).equals("1844899")){
//						System.out.println("1844899:" +" Score:" + score + " gridscore:" + gridScore);
//					}
//					if("1603914".equals(gridsInTrainList.get(k))){
//						System.out.println("1603914" +" Tag:" + userTagsArr[i] + " score:" + score + " gridscore:" + gridScore);
//					}
//					if(realGridNum.equals(gridsInTrainList.get(k))){
//						//	System.out.println("Real Grid..");
//							System.out.println(userTagsArr[i] + " " + score + " " + gridTfIdfScore);
//						}
//						
//						if("1531981".equals(gridsInTrainList.get(k))){
//						//	System.out.println("Predicted Grid..");
//							System.out.println(userTagsArr[i] + " " + score + " " + gridTfIdfScore);
//						}
				}
				// }
				if (gridScore > maxGridScore) {
					predictedGridNumberByScore = gridsInTrainList.get(k);
					maxGridScore = gridScore;
					scoreGridMatchCount = gridMatchCount;
					predictedGridNumber = predictedGridNumberByScore;
				//	System.out.println(predictedGridNumber);
				}
				
				if(gridMatchCount > maxMatch){
					predGridByMatch = gridsInTrainList.get(k);
					maxMatch = gridMatchCount;
					matchGridScore = gridScore;
				}
				
				
			}
//			System.out.println("maxMatch:" + maxMatch + " scoreGridMatchCount:" + scoreGridMatchCount);
//			if(maxMatch > scoreGridMatchCount ){
//				predictedGridNumber = predGridByMatch;
//			}
//
//			if (maxGridScore == 0.0 &&  maxMatch ==0 ) {
//				predictedGridNumber = String.valueOf(maxTagCountGrid);
//			}
			
			if (maxGridScore == 0.0  ) {
				predictedGridNumber = String.valueOf(maxTagCountGrid);
			}
			
			lineNumber = lineNumber + 1;
			if (predictedGridNumber.equals(realGridNum)) {
				numberOfSucess = numberOfSucess + 1;
				System.out.println("Real Grid:" +realGridNum + " Predicted Grid:" + predictedGridNumber  );
			} else {
//				System.out.println("Image:" + lineNumber + "\t" + "Predicted:" + predictedGridNumber + "\t" + "Real:"
//							+ realGridNum + "\n");
				System.out.println("Real Grid:" +realGridNum + " Predicted Grid:" + predictedGridNumber  );
				if (!gridsInTrainList.contains(realGridNum)) {
//					errorWriter.write("Image:" + lineNumber + "\t" + "Predicted:" + predictedGridNumber + "\t" + "Real:"
//							+ realGridNum + "\t" + "not in train" + "\n");
//					errorWriter.flush();
					totalNotInTrain++;
				}
//				} else {
//					errorWriter.write("Image:" + lineNumber + "\t" + "Predicted:" + predictedGridNumber + "\t" + "Real:"
//							+ realGridNum + "\n");
//					errorWriter.flush();
//				}
			}
			if (lineNumber % 100000 == 0) {
				System.out.println("Total number of Images:" + lineNumber + " and numberofSuccess:" + numberOfSucess
						+ " and totalNotInTrain:" + totalNotInTrain + "\n");
				// resultWriter.flush();
			}

			resultWriter.write("Image:" + lineNumber + "\t" + "Predicted:" + predictedGridNumber + "\t" + "Real:"
					+ realGridNum + "\n");
			resultWriter.flush();


		}
		errorWriter.close();
		System.out.println("Total number of Images:" + lineNumber + " and numberofSuccess:" + numberOfSucess
				+ " and totalNotInTrain:" + totalNotInTrain + "\n");

		System.out.println("Done..");
		resultWriter.close();

		System.out.println("..Completed..");
	}

}
