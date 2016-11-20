package placing.data;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import net.sf.geographiclib.GeodesicMask;
import placing.util.GenerateGrid;

public class DetermineCoordinate implements Runnable {

	static double degree = 1.0;
	static String mostPopGridForDeg = "13859";
	static int totalGrid = 14250;
	static Map<String, List<TrainInstance>> testMap = null;
	String resultFileName;
	String testFileName;
	static TrainInstance popGridTrainInstance;

	public DetermineCoordinate(String resultFileName, String testFileName) {
		this.resultFileName = resultFileName;
		this.testFileName = testFileName;
	}

	public static void main(String[] args) throws IOException {
		testMap = loadPredictions();
		//popGridTrainInstance = getTrainInstanceForPopGrid();
		popGridTrainInstance = new TrainInstance();
		popGridTrainInstance.setId("10029527915");
		popGridTrainInstance.setLatitude("51.50632");
		popGridTrainInstance.setLongitude("-0.12714");
		popGridTrainInstance.setTag("");
		System.out.println("Training data Loaded..");
		for (int i = 1; i <= 15; i++) {
			DetermineCoordinate obj = new DetermineCoordinate("resultWriter-1.0-Thread-" + i + ".txt",
					"estimation-test-photo-me-1.0-" + i + ".txt");  
			Thread thread = new Thread(obj, "Thread-" + i);
			thread.start();
		}
		DetermineCoordinate obj = new DetermineCoordinate("resultWriter-1.0-Thread-video.txt",
				"estimation-test-video-me-1.0.txt");
		Thread thread = new Thread(obj, "Thread-video");
		thread.start();
	}

	private static TrainInstance getTrainInstanceForPopGrid() {
		List<TrainInstance> trainList = testMap.get(mostPopGridForDeg);
		int minIndex = Integer.MAX_VALUE;
		System.out.println("Total training instances:" + trainList.size());
		//double distance[][] = new double[trainList.size()][trainList.size()];
		double minDistance = Double.MAX_VALUE;
		for (int i = 0; i < trainList.size(); i++) {
			TrainInstance ithTrainInstance = trainList.get(i);
			double ithDistance = 0.0;
			for (int j = 0; j < trainList.size(); j++) {
				if(i!=j){
				TrainInstance jthTrainInstance = trainList.get(j);
				GeodesicData g = Geodesic.WGS84.Inverse(Double.parseDouble(ithTrainInstance.getLatitude()),
						Double.parseDouble(ithTrainInstance.getLongitude()),
						Double.parseDouble(jthTrainInstance.getLatitude()),
						Double.parseDouble(jthTrainInstance.getLongitude()), GeodesicMask.DISTANCE);
				ithDistance = ithDistance + g.s12;
				}
			}
			if (ithDistance < minDistance) {
				minDistance = ithDistance;
				minIndex = i;
			}
		}
//		instance.setTag(lineArr[1]);
//		instance.setLatitude(lineArr[5]);
//		instance.setLongitude(lineArr[4]);
//		instance.setId(lineArr[6]);
		System.out.println("Train Instance found:" + trainList.get(minIndex).getId());
		System.out.println(trainList.get(minIndex).getLatitude()+"  " + trainList.get(minIndex).getLongitude());
		System.out.println(trainList.get(minIndex).getTag());
		return trainList.get(minIndex);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			getLatLongForCell();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getLatLongForCell() throws IOException {

		Path gridfile = Paths.get(resultFileName);
		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);

		PrintWriter outputWriter = null;
		try {
			outputWriter = new PrintWriter(
					new FileWriter("output-photo-1.0-" + Thread.currentThread().getName() + ".txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<String> testPrediction = new ArrayList();
		for (String line : (Iterable<String>) gridlines::iterator) {
			testPrediction.add(line);
		}
		System.out.println("Running for file:" + testFileName + "by thread:" + Thread.currentThread().getName());
		gridfile = Paths.get(testFileName);

		// instance.setTestId(lineArr[6]);
		// instance.setTags(lineArr[1]);
		// instance.setRealLat(lineArr[5]);
		// instance.setRealLong(lineArr[4]);
		gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		int index = 0;
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			String testLine[] = testPrediction.get(index).split("\t");
			index++;
			String predCell = testLine[2];
			if (testLine[testLine.length - 1].equals("true")) {
				// Centroid centroid = getCentroidOfCell(predCell);
				// GeodesicData g =
				// Geodesic.WGS84.Inverse(Double.parseDouble(centroid.getLatitude()),
				// Double.parseDouble(centroid.getLongitude()),
				// Double.parseDouble(lineArr[5]),
				// Double.parseDouble(lineArr[4]), GeodesicMask.DISTANCE);
				// outputWriter.write(lineArr[6] + "\t" + centroid.getLatitude()
				// + "\t" + centroid.getLongitude() + "\t"
				// + lineArr[5] + "\t" + lineArr[4] + "\t" + g.s12 + "\t" +
				// "0.0" + "\t" + predCell + "\t"+"true"+"\n");
				// outputWriter.flush();
				GeodesicData g = Geodesic.WGS84.Inverse(Double.parseDouble(popGridTrainInstance.getLatitude()),
						Double.parseDouble(popGridTrainInstance.getLongitude()), Double.parseDouble(lineArr[5]),
						Double.parseDouble(lineArr[4]), GeodesicMask.DISTANCE);
				outputWriter.write(lineArr[6] + "\t" + popGridTrainInstance.getLatitude() + "\t"
						+ popGridTrainInstance.getLongitude() + "\t" + lineArr[5] + "\t" + lineArr[4] + "\t" + g.s12
						+ "\t" + "0.0" + "\t" + predCell + "\t" + lineArr[0] +"\t"+"true" + "\t" + popGridTrainInstance.getId() + "\n");
				outputWriter.flush();

			} else {
				if (testMap.containsKey(predCell)) {
					List<TrainInstance> trainDataForCell = testMap.get(predCell);
					double bestSim = -1.0;
					TrainInstance bestTrain = null;
					for (int i = 0; i < trainDataForCell.size(); i++) {
						double similarity = getJaccardSimilarity(trainDataForCell.get(i).getTag().split(","),
								lineArr[1].split(","));
						if (bestSim < similarity) {
							bestSim = similarity;
							bestTrain = trainDataForCell.get(i);
						}
					}
					GeodesicData g = Geodesic.WGS84.Inverse(Double.parseDouble(bestTrain.getLatitude()),
							Double.parseDouble(bestTrain.getLongitude()), Double.parseDouble(lineArr[5]),
							Double.parseDouble(lineArr[4]), GeodesicMask.DISTANCE);
					outputWriter.write(lineArr[6] + "\t" + bestTrain.getLatitude() + "\t" + bestTrain.getLongitude()
							+ "\t" + lineArr[5] + "\t" + lineArr[4] + "\t" + g.s12 + "\t" + bestSim + "\t" + predCell
							+ "\t" + lineArr[0] +"\t"+"false" + "\t" + bestTrain.getId() + "\n");
					outputWriter.flush();

				} else {
					System.out.println(
							"Predicted cell is not in training data. Using the most popular grid of the train.");
					// Centroid centroid = getCentroidOfCell(mostPopGridForDeg);
					// GeodesicData g =
					// Geodesic.WGS84.Inverse(Double.parseDouble(centroid.getLatitude()),
					// Double.parseDouble(centroid.getLongitude()),
					// Double.parseDouble(lineArr[5]),
					// Double.parseDouble(lineArr[4]), GeodesicMask.DISTANCE);
					// outputWriter.write(lineArr[6] + "\t" +
					// centroid.getLatitude() + "\t" + centroid.getLongitude()
					// + "\t" + lineArr[5] + "\t" + lineArr[4] + "\t" + g.s12 +
					// "\t" + "0.0" + "\t" + predCell
					// + "\t"+"false"+"\n");
					// outputWriter.flush();

					GeodesicData g = Geodesic.WGS84.Inverse(Double.parseDouble(popGridTrainInstance.getLatitude()),
							Double.parseDouble(popGridTrainInstance.getLongitude()), Double.parseDouble(lineArr[5]),
							Double.parseDouble(lineArr[4]), GeodesicMask.DISTANCE);
					outputWriter.write(lineArr[6] + "\t" + popGridTrainInstance.getLatitude() + "\t"
							+ popGridTrainInstance.getLongitude() + "\t" + lineArr[5] + "\t" + lineArr[4] + "\t" + g.s12
							+ "\t" + "0.0" + "\t" + predCell + "\t" + lineArr[0] +"\t"+"true" + "\t" + popGridTrainInstance.getId() + "\n");
					outputWriter.flush();
				}
			}

			if (index % 10000 == 0) {
				System.out.println("For Thread:" + Thread.currentThread().getName() + "Processed:" + index);
			}
		}

		outputWriter.close();
		System.out.println("Done for Thread:" + Thread.currentThread().getName());
	}

	private static Centroid getCentroidOfCell(String predCell) {
		double corners[] = GenerateGrid.getCornersOfGrid(predCell, degree);
		double latitude = (corners[0] + corners[2] + corners[4] + corners[6]) / ((double) 4);
		double longitude = corners[1] + corners[3] + corners[5] + corners[7] / ((double) 4);
		Centroid centroid = new Centroid();
		centroid.setLatitude(String.valueOf(latitude));
		centroid.setLongitude(String.valueOf(longitude));
		return centroid;
	}

	private static double getJaccardSimilarity(String[] trainTags, String[] testTags) {
		Set<String> unionSet = new HashSet();
		for (int i = 0; i < trainTags.length; i++) {
			unionSet.add(trainTags[i]);
		}
		int intersectionCount = 0;
		for (int i = 0; i < testTags.length; i++) {
			if (unionSet.contains(testTags[i])) {
				intersectionCount++;
			}
			unionSet.add(testTags[i]);
		}
		// unionSet.addAll(trainTags)
		return ((double) intersectionCount) / ((double) unionSet.size());
	}

	private static Map<String, List<TrainInstance>> loadPredictions() throws IOException {
		Map<String, List<TrainInstance>> trainMap = new HashMap(totalGrid);
		Path gridfile = Paths.get("yfcc-train-grid-ut-title-1.0.txt");
		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			String gridNumber = lineArr[0];// GenerateGrid.getGridNumber(lineArr[13],
											// lineArr[12], degree).toString();
//			gridNumber + "\t" +userTag + "\t" + place + "\t" + title +"\t"+longStr +
//			"\t" +latStr+"\t" + lineStr[3] +"\t" + hash+ "\n");
			
			if (trainMap.containsKey(gridNumber)) {
				TrainInstance instance = new TrainInstance();
				instance.setTag(lineArr[1]);
				instance.setLatitude(lineArr[5]);
				instance.setLongitude(lineArr[4]);
				instance.setId(lineArr[6]);
			//	instance.setPlace(lineArr[2]);
				trainMap.get(gridNumber).add(instance);
			} else {
				TrainInstance instance = new TrainInstance();
				instance.setTag(lineArr[1]);
				instance.setLatitude(lineArr[5]);
				instance.setLongitude(lineArr[4]);
				instance.setId(lineArr[6]);
			//	instance.setPlace(lineArr[2]);
				List<TrainInstance> list = new ArrayList();
				list.add(instance);
				trainMap.put(gridNumber, list);
			}
		}

		return trainMap;
	}
	
	

}

class TrainInstance {
	String tag;
	String latitude;
	String longitude;
	String id;
	//String place;

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

//	public String getPlace() {
//		return place;
//	}
//
//	public void setPlace(String place) {
//		this.place = place;
//	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
}

class Centroid {
	String latitude;

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	String longitude;
}
