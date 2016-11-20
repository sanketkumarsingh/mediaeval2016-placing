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

public class UserCountBaseModel {

	private static Map<String, Map<String, TagProb>> gridPosting = new HashMap();
	static long averageTagPerGrid;
	// private static Map<String, String> tagGridCountMap = new HashMap();
	static long totalGrid = 6480000;
	static List<String> gridsInTrainList = new ArrayList();

	public static void main(String[] args) throws IOException {

		//Path gridfile = Paths.get("sorted-posting-userid-tag-title-grid-0.1.txt");
		List<GridInfo> list = new ArrayList();
		for(int i=1;i<10;i++){
			GridInfo grid = new GridInfo();
			grid.setGrid(i+"");
			grid.setGridScore(Double.parseDouble(String.valueOf(i)));
			list.add(grid);
		}
		
		for(int i=0;i<9;i++){
			System.out.println(list.get(i).getGrid() + " : " + list.get(i).getGridScore());
		}
		Collections.sort(list);
		System.out.println("After sorting");
		for(int i=0;i<9;i++){
			System.out.println(list.get(i).getGrid() + " : " + list.get(i).getGridScore());
		}
		Path gridfile = Paths.get("sorted-posting-userid-tag-title-grid-0.1-remove1tf1scorevals-gt0.001.txt");
		
		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		long totalNumberOfTags = 0;
		String maxTagCountGrid = "";
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
				double gridScore = 0.0;

				for (int i = 0; i < userTagsArr.length; i++) {

					double score = 0.0;
					if (gridPosting.containsKey(userTagsArr[i])) {
						if (gridPosting.get(userTagsArr[i]).containsKey(gridsInTrainList.get(k))) {
							score = gridPosting.get(userTagsArr[i]).get(gridsInTrainList.get(k)).getTagProb();
							//score = score * gridPosting.get(userTagsArr[i]).get(gridsInTrainList.get(k)).getTagCount();
						}
					}

					gridScore = gridScore + score;
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
			if (lineNumber % 1000 == 0) {
				System.out.println("Total number of Images:" + lineNumber + " and numberofSuccess:" + numberOfSucess
						+ " and totalNotInTrain:" + totalNotInTrain + "\n");
				// resultWriter.flush();
			}
			String toWrite = lineNumber + "\t" + realGridNum + "\t" + predictedGridNumber;
			for (int index = 0; index < top10Grid.size(); index++) {
				toWrite = toWrite + "\t" + top10Grid.get(index).getGrid();
			}
			toWrite = toWrite + "\t" + yes;
			resultWriter.write(toWrite + "\n");
			// resultWriter.write("Image:" + lineNumber + "\t" + "Predicted:" +
			// predictedGridNumber + "\t" + "Real:"
			// + realGridNum + "\n");
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

class TagProb {
	int tagCount;
	double tagProb;
	double photoratio; // not used in this class
	long cellCount; // not used in this class
	public long getCellCount() {
		return cellCount;
	}

	public void setCellCount(long cellCount) {
		this.cellCount = cellCount;
	}

	public double getPhotoratio() {
		return photoratio;
	}

	public void setPhotoratio(double photoratio) {
		this.photoratio = photoratio;
	}

	public double getTfratio() {
		return tfratio;
	}

	public void setTfratio(double tfratio) {
		this.tfratio = tfratio;
	}

	double tfratio;

	public int getTagCount() {
		return tagCount;
	}

	public double getTagProb() {
		return tagProb;
	}

	public void setTagProb(double tagProb) {
		this.tagProb = tagProb;
	}

	public void setTagCount(int tagCount) {
		this.tagCount = tagCount;
	}

	public boolean isLocationSpecific() {
		return isLocationSpecific;
	}

	public void setLocationSpecific(boolean isLocationSpecific) {
		this.isLocationSpecific = isLocationSpecific;
	}

	boolean isLocationSpecific;
}

class GridInfo implements Comparable {
	String grid;
	double gridScore;

	public String getGrid() {
		return grid;
	}

	public void setGrid(String grid) {
		this.grid = grid;
	}

	public double getGridScore() {
		return gridScore;
	}

	public void setGridScore(double gridScore) {
		this.gridScore = gridScore;
	}

	@Override   // ascending order 
	public int compareTo(Object o) {
		if (this.gridScore > ((GridInfo) o).getGridScore()) {
			return 1;
		} else if (this.gridScore < ((GridInfo) o).getGridScore()) {
			return -1;
		} else {
			return 0;
		}
	}

}