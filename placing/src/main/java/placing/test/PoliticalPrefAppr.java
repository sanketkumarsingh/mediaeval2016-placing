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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;


public class PoliticalPrefAppr {

	// static Set<String> gridsInTrainList = new HashSet();
	static int totalUserCountInTrain;
	static Map<String, Double> tagGlobalProbMap = new HashMap();
	static double lambda = 0.001;

	public static void main(String[] args) throws IOException {
		getTagImportance();
		// processing the tag importance and predicting for test instances.
		//predict();
	}

	public static void predict() throws IOException {

		Map<String, Map<String, Double>> tagCellmap = new HashMap();
		Path file = Paths.get("tag-cell-importance-0.1.txt");
		Stream<String> lines = null;
		lines = Files.lines(file, StandardCharsets.UTF_8);
		boolean first = true;
		String prevTag = "";
		Set<String> allGrids = new HashSet();
		Map<String, Double> tagImpMap = null;
		for (String line : (Iterable<String>) lines::iterator) {
			String lineStr[] = line.split("\t");
			allGrids.add(lineStr[1]);
			if (first) {
				tagImpMap = new HashMap();
				tagImpMap.put(lineStr[1], Double.parseDouble(lineStr[2]));
				prevTag = lineStr[0];
				first = false;
			} else {
				if (!lineStr[0].equals(prevTag)) {
					tagCellmap.put(prevTag, tagImpMap);
					prevTag = lineStr[0];
					tagImpMap = new HashMap();
					tagImpMap.put(lineStr[1], Double.parseDouble(lineStr[2]));
				} else {
					tagImpMap.put(lineStr[1], Double.parseDouble(lineStr[2]));
				}
			}
		}
		tagCellmap.put(prevTag, tagImpMap);
		System.out.println("Total Grids:" + allGrids.size());
		System.out.println("Loaded the tag importance file.. Going to Predict..");

		file = Paths.get("estimation-test-onlyUT-photo-me-0.1.txt");
		lines = Files.lines(file, StandardCharsets.UTF_8);
		long lineno = 0;
		int numberOfSuccess = 0;

		PrintWriter resultWriter = null;
		try {
			resultWriter = new PrintWriter(new FileWriter("pol-result-0.1.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (String line : (Iterable<String>) lines::iterator) {
			String lineStr[] = line.split("\t");
			String realGridNum = lineStr[0];
			String userTags = lineStr[1];
			String userTagsArr[] = userTags.split(",");
			Iterator allGridIterator = allGrids.iterator();
			double maxScore = Double.MIN_VALUE;
			String predictedGrid = "";
			lineno = lineno + 1;
			int totalGridCount = 0;
			ArrayList<GridInfo> top10Grid = new ArrayList();
			while (allGridIterator.hasNext()) {
				String grid = (String) allGridIterator.next();
				double scoreForGrid = 0.0;
				boolean foundAtLeastOnce = false;
				if(grid.equals(realGridNum)){
					System.out.println("RealGrid..");
				}
				if(grid.equals("4550009")){
					System.out.println("PredictedGrid..");
				}
				for (int i = 0; i < userTagsArr.length; i++) {
					if (tagCellmap.containsKey(userTagsArr[i])) {
					//	System.out.println("contains tags");
						if (tagCellmap.get(userTagsArr[i]).containsKey(grid)) {
					//		System.out.println("contains cell");
							foundAtLeastOnce = true;
							if(grid.equals(realGridNum)){
							System.out.println(userTagsArr[i] + ":" + tagCellmap.get(userTagsArr[i]).get(grid));
							}
							if(grid.equals("4550009")){
								
								System.out.println(userTagsArr[i] + ":" + tagCellmap.get(userTagsArr[i]).get(grid));
							}
							scoreForGrid = scoreForGrid + tagCellmap.get(userTagsArr[i]).get(grid);
						}
					}

				}
				if (foundAtLeastOnce && maxScore < scoreForGrid) {
					maxScore = scoreForGrid;
					predictedGrid = grid;
				}
				
				if (totalGridCount < 10) {
					GridInfo gridInfo = new GridInfo();
					gridInfo.setGrid(grid);
					gridInfo.setGridScore(scoreForGrid);
					top10Grid.add(gridInfo);
					Collections.sort(top10Grid);
					totalGridCount++;
				} else {
					if (scoreForGrid > top10Grid.get(totalGridCount - 1).getGridScore()) {
						boolean found = false;
						GridInfo gridInfo = null;
						for (int index = 0; index < totalGridCount; index++) {
							if (found) {
								GridInfo currentGrid = top10Grid.get(index);
								top10Grid.set(index, gridInfo);
								gridInfo = currentGrid;
							} else {
								if (top10Grid.get(index).getGridScore() < scoreForGrid) {
									found = true;
									gridInfo = top10Grid.get(index);
									GridInfo newgrid = new GridInfo();
									newgrid.setGrid(grid);
									newgrid.setGridScore(scoreForGrid);
									top10Grid.set(index, newgrid);
								}
							}
						}
					}
				}
				
			}
			if (predictedGrid.equals("")) {
				predictedGrid = "1772251"; // most popular grid.
			}

			if (realGridNum.equals(predictedGrid)) {
				numberOfSuccess = numberOfSuccess + 1;
			}
			if (lineno % 100000 == 0) {
				System.out.println("TotalImage:" + lineno + "\t" + "Success:" + numberOfSuccess + "\n");
			}
//			resultWriter.write(
//					+ "\t" + "Line:" + lineno + "\n");
			String toWrite = lineno + "\t"+	realGridNum + "\t" + predictedGrid ;
			for (int index = 0; index < top10Grid.size(); index++) {
				toWrite = toWrite + "\t" + top10Grid.get(index).getGrid();
			}
			resultWriter.write(toWrite + "\n");
			resultWriter.flush();
		}
		resultWriter.close();
		System.out.println("Completed..");
	}

	public static void getTagImportance() {

		Map<String, Map<String, TagGridUser>> tagCellmap = getTagInGridUserCountMap(); // tag
																						// -
																						// cell
																						// -
																						// tagGridUser
		Map<String, CellDetail> cellUserMap = getUserCountForCell(); // cell -
																		// userCountPerGrid
		System.out.println("Total Number of grids:" + cellUserMap.size());
		System.out.println("Total Number of users:" + totalUserCountInTrain);
		System.out.println("Loaded the train data..");

		double sumOfGlobalProbOverAllTag = 0.0;
		Iterator tagIterator = tagCellmap.entrySet().iterator();
		while (tagIterator.hasNext()) {
			Entry entry = (Entry) tagIterator.next();
			String tag = (String) entry.getKey();
			Map<String, TagGridUser> cellTagInfoMap = (Map<String, TagGridUser>) entry.getValue();
			double averageGlobalTf = ((double) cellTagInfoMap.get("===TOTAL===").getPhotoCount())
					/ ((double) totalUserCountInTrain);
			double udfForTagGlobal = ((double) cellTagInfoMap.get("===TOTAL===").getUserCount())
					/ ((double) totalUserCountInTrain);
			double globalProbForTag = averageGlobalTf * udfForTagGlobal;
			tagGlobalProbMap.put(tag, globalProbForTag);
			if(tag.equals("vic")){
				//System.out.println("Inspect1..");
				System.out.println("photo count global for tag:"+ cellTagInfoMap.get("===TOTAL===").getPhotoCount());
				System.out.println("user count global for tag:" +cellTagInfoMap.get("===TOTAL===").getUserCount() );
			}
			sumOfGlobalProbOverAllTag = sumOfGlobalProbOverAllTag + globalProbForTag;
			double idfForTag = ((double) totalUserCountInTrain)
					/ ((double) cellTagInfoMap.get("===TOTAL===").getUserCount());
			Iterator cellIterator = cellTagInfoMap.entrySet().iterator();
			while (cellIterator.hasNext()) {
				Entry cellEntry = (Entry) cellIterator.next();
				String cell = (String) cellEntry.getKey();
				TagGridUser cellInfo = (TagGridUser) cellEntry.getValue();
				CellDetail cellDetail = cellUserMap.get(cell);
				
				if (!cell.equals("===TOTAL===")) {
					double averageCellTf = ((double) cellInfo.getPhotoCount()) / ((double) cellDetail.getUserCount());
					double udfForCell = ((double) cellInfo.getUserCount()) / ((double) cellDetail.getUserCount());
					double rawWeight = averageCellTf * udfForCell * idfForTag;
					//double rawWeight = averageCellTf * udfForCell ;
					if(tag.equals("vic") && cell.equals("1503155")){
						System.out.println("photo count in cell for tag :" + cellInfo.getPhotoCount());
						System.out.println("user count in cell for tag:" + cellInfo.getUserCount());
						System.out.println("user count in cell:" + cellDetail.getUserCount());
						
					}
					cellDetail.setSumOfRawWeights(cellDetail.getSumOfRawWeights() + rawWeight);
					cellUserMap.put(cell, cellDetail);
					cellInfo.setRawWeight(rawWeight);
				}
			}
		}
		System.out.println("Raw probabilities calculated. Going to normalize..");
		tagIterator = tagCellmap.entrySet().iterator();
		while (tagIterator.hasNext()) {
			Entry entry = (Entry) tagIterator.next();
			String tag = (String) entry.getKey();
			Map<String, TagGridUser> cellTagInfoMap = (Map<String, TagGridUser>) entry.getValue();
			double normalizedGlobalProbForTag = ((double) tagGlobalProbMap.get(tag))
					/ ((double) sumOfGlobalProbOverAllTag);
			cellTagInfoMap.get("===TOTAL===").setNormalizedProb(normalizedGlobalProbForTag);
//			if(tag.equals("finding")){
//				System.out.println("Inspect2..");
//			}
			Iterator cellIterator = cellTagInfoMap.entrySet().iterator();
			while (cellIterator.hasNext()) {
				Entry cellEntry = (Entry) cellIterator.next();
				String cell = (String) cellEntry.getKey();
				TagGridUser cellInfo = (TagGridUser) cellEntry.getValue();
				CellDetail cellDetail = cellUserMap.get(cell);
//				if(tag.equals("finding") && cell.equals("1615632")){
//					System.out.println("Stop2");
//				}
				if (!cell.equals("===TOTAL===")) {
					double normalizedCellProbForTag = cellInfo.getRawWeight()
							/ cellUserMap.get(cell).getSumOfRawWeights();
					cellInfo.setNormalizedProb(normalizedCellProbForTag);
					double smoothedCellProbForTag = ((1 - lambda) * normalizedCellProbForTag)
							+ (lambda * normalizedGlobalProbForTag);
					cellInfo.setSmoothedProb(smoothedCellProbForTag);
					cellDetail.setSumOfSmoothedWeights(cellDetail.getSumOfSmoothedWeights() + smoothedCellProbForTag);
					cellUserMap.put(cell, cellDetail);
				}
			}
		}

		System.out.println(
				"Normalized probabilites and smoothed prob. calculated. Going to find the final tag and cell probabilities...");

		PrintWriter resultWriter = null;
		try {
			resultWriter = new PrintWriter(new FileWriter("tag-cell-importance-0.1-withoutidf.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}

		tagIterator = tagCellmap.entrySet().iterator();
		while (tagIterator.hasNext()) {
			Entry entry = (Entry) tagIterator.next();
			String tag = (String) entry.getKey();
//			if(tag.equals("finding")){
//				System.out.println("Inspect3..");
//			}
			Map<String, TagGridUser> cellTagInfoMap = (Map<String, TagGridUser>) entry.getValue();
			Iterator cellIterator = cellTagInfoMap.entrySet().iterator();
			while (cellIterator.hasNext()) {
				Entry cellEntry = (Entry) cellIterator.next();
				String cell = (String) cellEntry.getKey();
				TagGridUser cellInfo = (TagGridUser) cellEntry.getValue();
//				if(tag.equals("finding") && cell.equals("1615632")){
//					System.out.println("Stop3");
//				}
				if (!cell.equals("===TOTAL===")) {
					
					double tagProb = cellInfo.getSmoothedProb() / cellUserMap.get(cell).getSumOfSmoothedWeights();
					double rawGlobalTagProb = tagGlobalProbMap.get(tag);
					double importance = tagProb * (Math.log(tagProb / rawGlobalTagProb));
					resultWriter.write(tag + "\t" + cell + "\t" + importance + "\n");
					resultWriter.flush();
				}
			}
		}
		resultWriter.close();

	}

	private static Map<String, CellDetail> getUserCountForCell() {
		Path file = Paths.get("sortedbycell-tag-title-grid-0.1.txt"); // sorted
																		// based
																		// on
																		// cell
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		boolean first = true;
		Map<String, CellDetail> cellUserMap = new HashMap();
		Set<String> userSet = new HashSet();
		String prevCell = "";
		for (String line : (Iterable<String>) lines::iterator) {
			String lineStr[] = line.split("\t");
			if (first) {
				first = false;
				prevCell = lineStr[2];
				userSet.add(lineStr[0]);
			} else {
				if (!prevCell.equals(lineStr[2])) {
					CellDetail obj = new CellDetail();
					obj.setUserCount(userSet.size());
					cellUserMap.put(prevCell, obj);
					totalUserCountInTrain = totalUserCountInTrain + userSet.size();
					prevCell = lineStr[2];
					userSet = new HashSet();
					userSet.add(lineStr[0]);
				} else {
					userSet.add(lineStr[0]);
				}
			}
		}
		CellDetail obj = new CellDetail();
		obj.setUserCount(userSet.size());
		cellUserMap.put(prevCell, obj);
		totalUserCountInTrain = totalUserCountInTrain + userSet.size();
		return cellUserMap;
	}

	public static Map<String, Map<String, TagGridUser>> getTagInGridUserCountMap() {
		Map<String, Map<String, TagGridUser>> map = new HashMap();
		Path file = Paths.get("sortedbytag-user-tag-title-grid-0.1.txt"); // sorted
																		// based
																		// on
																		// tag
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		boolean first = true;
		String prevTag = "";
		Set<String> userIdSet = new HashSet();
		Set<String> photoIdSet = new HashSet();
		Map<String, TagGridUser> gridUserIdCountMap = null;
		int k = 0;
		int totalUserAcrossGlobeForTag = 0;
		int totalPhotoAcrossGlobeForTag = 0;
		// Set<String> totalUsersSet = new HashSet();
		for (String line : (Iterable<String>) lines::iterator) {
			k++;
			String lineStr[] = line.split("\t");
			// gridsInTrainList.add(lineStr[2]);
			// totalUsersSet.add(lineStr[0]);
			if (first) {
				gridUserIdCountMap = new HashMap();
				TagGridUser obj = new TagGridUser();
				Set<String> userForTagGrid = new HashSet();
				Set<String> photosForTagGrid = new HashSet();
				userForTagGrid.add(lineStr[0]);
				photosForTagGrid.add(lineStr[3]);
				obj.setUsers(userForTagGrid);
				obj.setUserCount(userForTagGrid.size());
				obj.setPhotos(photosForTagGrid);
				obj.setPhotoCount(photosForTagGrid.size());
				gridUserIdCountMap.put(lineStr[2], obj);
				first = false;
				prevTag = lineStr[1];

			} else { // the assumption is that same tag in same grid by same
						// user cannot occur more than once.
				if (!prevTag.equals(lineStr[1])) {
					TagGridUser totalUser = new TagGridUser();
					Iterator it = gridUserIdCountMap.entrySet().iterator();
					String prevUser = "";
					while (it.hasNext()) {
						Map.Entry pair = (Entry) it.next();
						TagGridUser tagGridVal = (TagGridUser) pair.getValue();
						userIdSet.addAll(tagGridVal.getUsers());
						photoIdSet.addAll(tagGridVal.getPhotos());
						totalUserAcrossGlobeForTag = totalUserAcrossGlobeForTag + tagGridVal.getUserCount();
						totalPhotoAcrossGlobeForTag = totalPhotoAcrossGlobeForTag + tagGridVal.getPhotoCount();
						tagGridVal.setUsers(null);
						tagGridVal.setPhotos(null);
					}
					totalUser.setUserCount(totalUserAcrossGlobeForTag);
					// totalUserCountInTrain = totalUserCountInTrain +
					// totalUserAcrossGlobeForTag;
					totalUser.setPhotoCount(totalPhotoAcrossGlobeForTag);
					gridUserIdCountMap.put("===TOTAL===", totalUser);
					// //System.out.println("UserIdSet.size():" +
					// UserIdSet.size());
					// if(UserIdSet.size() != 1){
					//
					// }
					map.put(prevTag, gridUserIdCountMap);
					gridUserIdCountMap = new HashMap();
					totalUserAcrossGlobeForTag = 0;
					totalPhotoAcrossGlobeForTag = 0;
					userIdSet = new HashSet();
					photoIdSet = new HashSet();
					prevTag = lineStr[1];
					if (gridUserIdCountMap.containsKey(lineStr[2])) {
						TagGridUser obj = gridUserIdCountMap.get(lineStr[2]);
						Set<String> userForTagGrid = obj.getUsers();
						userForTagGrid.add(lineStr[0]);
						Set<String> photoForTagGrid = obj.getPhotos();
						photoForTagGrid.add(lineStr[3]);
						obj.setUsers(userForTagGrid);
						obj.setUserCount(userForTagGrid.size());
						obj.setPhotos(photoForTagGrid);
						obj.setPhotoCount(photoForTagGrid.size());
						gridUserIdCountMap.put(lineStr[2], obj);
					} else {
						TagGridUser obj = new TagGridUser();
						Set<String> userForTagGrid = new HashSet();
						Set<String> photosForTagGrid = new HashSet();
						userForTagGrid.add(lineStr[0]);
						photosForTagGrid.add(lineStr[3]);
						obj.setUsers(userForTagGrid);
						obj.setUserCount(userForTagGrid.size());
						obj.setPhotos(photosForTagGrid);
						obj.setPhotoCount(photosForTagGrid.size());
						gridUserIdCountMap.put(lineStr[2], obj);
					}
				} else {
					if (gridUserIdCountMap.containsKey(lineStr[2])) {
						TagGridUser obj = gridUserIdCountMap.get(lineStr[2]);
						Set<String> userForTagGrid = obj.getUsers();
						userForTagGrid.add(lineStr[0]);
						Set<String> photoForTagGrid = obj.getPhotos();
						photoForTagGrid.add(lineStr[3]);
						obj.setUsers(userForTagGrid);
						obj.setUserCount(userForTagGrid.size());
						obj.setPhotos(photoForTagGrid);
						obj.setPhotoCount(photoForTagGrid.size());
						gridUserIdCountMap.put(lineStr[2], obj);
					} else {
						TagGridUser obj = new TagGridUser();
						Set<String> userForTagGrid = new HashSet();
						Set<String> photosForTagGrid = new HashSet();
						userForTagGrid.add(lineStr[0]);
						photosForTagGrid.add(lineStr[3]);
						obj.setUsers(userForTagGrid);
						obj.setUserCount(userForTagGrid.size());
						obj.setPhotos(photosForTagGrid);
						obj.setPhotoCount(photosForTagGrid.size());
						gridUserIdCountMap.put(lineStr[2], obj);
					}

				}
			}
			if (k % 1000000 == 0) {
				System.out.println("Processed:" + k);
			}
		}
		TagGridUser totalUser = new TagGridUser();
		Iterator it = gridUserIdCountMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Entry) it.next();
			TagGridUser tagGridVal = (TagGridUser) pair.getValue();
			userIdSet.addAll(tagGridVal.getUsers());
			photoIdSet.addAll(tagGridVal.getPhotos());
			totalUserAcrossGlobeForTag = totalUserAcrossGlobeForTag + tagGridVal.getUserCount();
			totalPhotoAcrossGlobeForTag = totalPhotoAcrossGlobeForTag + tagGridVal.getPhotoCount();
			tagGridVal.setUsers(null);
			tagGridVal.setPhotos(null);
		}
		totalUser.setUserCount(totalUserAcrossGlobeForTag);
		totalUser.setPhotoCount(totalPhotoAcrossGlobeForTag);
		gridUserIdCountMap.put("===TOTAL===", totalUser);
		// gridUserIdCountMap.put("===TOTAL===", totalUserAcrossGlobeForTag);
		map.put(prevTag, gridUserIdCountMap);
		// totalUserCountInTrain = totalUsersSet.size();
		return map;
	}

}

class TagGridUser {
	public int userCount;
	public int photoCount;
	double rawWeight; // w(t|p)
	public Set<String> users;
	double normalizedProb; // w(t|p)n
	double smoothedProb; // w(t|p)s

	public double getRawWeight() {
		return rawWeight;
	}

	public double getSmoothedProb() {
		return smoothedProb;
	}

	public void setSmoothedProb(double smoothedProb) {
		this.smoothedProb = smoothedProb;
	}

	public double getNormalizedProb() {
		return normalizedProb;
	}

	public void setNormalizedProb(double normalizedProb) {
		this.normalizedProb = normalizedProb;
	}

	public void setRawWeight(double rawWeight) {
		this.rawWeight = rawWeight;
	}

	public Set<String> photos;

	public int getPhotoCount() {
		return photoCount;
	}

	public Set<String> getPhotos() {
		return photos;
	}

	public void setPhotos(Set<String> photos) {
		this.photos = photos;
	}

	public void setPhotoCount(int photoCount) {
		this.photoCount = photoCount;
	}

	public int getUserCount() {
		return userCount;
	}

	public void setUserCount(int userCount) {
		this.userCount = userCount;
	}

	public Set<String> getUsers() {
		return users;
	}

	public void setUsers(Set<String> users) {
		this.users = users;
	}

}

class CellDetail {
	int userCount;
	double sumOfSmoothedWeights;
	double sumOfRawWeights;
	
	public int getUserCount() {
		return userCount;
	}

	public double getSumOfSmoothedWeights() {
		return sumOfSmoothedWeights;
	}

	public void setSumOfSmoothedWeights(double sumOfSmoothedWeights) {
		this.sumOfSmoothedWeights = sumOfSmoothedWeights;
	}

	public void setUserCount(int userCount) {
		this.userCount = userCount;
	}

	public double getSumOfRawWeights() {
		return sumOfRawWeights;
	}

	public void setSumOfRawWeights(double sumOfRawWeights) {
		this.sumOfRawWeights = sumOfRawWeights;
	}

	
}


