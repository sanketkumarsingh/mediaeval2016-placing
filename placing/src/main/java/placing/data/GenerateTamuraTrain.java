package placing.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
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
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import net.sf.geographiclib.GeodesicMask;
import placing.util.GenerateGrid;

public class GenerateTamuraTrain {

	public static void main(String[] args) {
		// generateFeatureFileFromS3();
		// generateTrain();
		// getData();
		// getMaximumGrid();
		// getTrainBasedonTestInstance();

		// getTrain();
		 generateCountyBasedTrain();
		//findMostPopLabels();
		 generateCountyBasedTest();
		//getTest();
		//getTrainInstanceForPopGrid();
		//generateCountyBasedTrain();
	//	generateCountyBasedTest();
		
		generateTrainForSVM();
	//	generateCountyBasedTest();
	 	generateTestForSVM();
	//	generateUniquePred();
		
	}
	
	private static void generateUniquePred(){
		Path file = Paths.get("test-tamura-photo-county-vw-1.0-pred.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, Integer> map = new HashMap();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split(" ");
			if(map.containsKey(lineArr[0])){
				map.put(lineArr[0], map.get(lineArr[0]) + 1);
			}else{
				map.put(lineArr[0],1);
			}
			
		}
		
		Iterator it = map.entrySet().iterator();
		while(it.hasNext()){
			Entry entry = (Entry) it.next();
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}
		
	}
	
	private static void generateTestForSVM(){

		Path file = Paths.get("train-with-county-properlabel.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, String> hashIdLabelMap = new HashMap();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			hashIdLabelMap.put(lineArr[3], lineArr[2]);
		}
		System.out.println("Loaded labels");
		file = Paths.get("test-photo-tamura.txt");
		lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Map<String, String> hashIdTestMap = new HashMap();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			//hashIdTestMap.put(lineArr[0], line); // photo
			hashIdTestMap.put(lineArr[0].split("-")[0], line); // video
		}
		System.out.println("Loaded test data..");
		file = Paths.get("estimation-test-photo-mediaeval-places.txt");
		lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String var = "var";
		String colon = ":";
		int lineNo = 0;
		int maxGrid = -1;
		int totalCounty = 0;

		PrintWriter trainWriter = null;
		try {
			trainWriter = new PrintWriter(new FileWriter("test-tamura-photo-county-svm-1.0.txt", true));
			// testWriter = new PrintWriter(new FileWriter("test-cnn-0.1.txt",
			// true));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			lineNo++;
			if (hashIdTestMap.containsKey(lineArr[2])) {
				String place = lineArr[lineArr.length - 1];
				String placeAtts[] = place.split(",");
				String label = "2";
				for (String placeItem : placeAtts) {
					String placeInfo[] = placeItem.split(":");
					if (placeInfo[2].equals("County")) {
						label = hashIdLabelMap.get(placeInfo[1]);
					}
				}
				String testInstance = hashIdTestMap.get(lineArr[2]);
				String testInstanceArr[] = testInstance.split("\t");
				String md5Val = testInstanceArr[0];
				String lineToWrite = "";
				String features = testInstanceArr[testInstanceArr.length - 1];
				String featureArr[] = features.split(" ");
				for (int j = 0; j < featureArr.length; j++) {
					String feature =  (j + 1) + colon + featureArr[j];
					if (j == 0) {
						lineToWrite = feature;
					} else {
						lineToWrite = lineToWrite + " " + feature;
					}
				}
				lineToWrite = lineToWrite + " # " + md5Val;
				totalCounty++;
				line = label + " " + lineToWrite;
				trainWriter.write(line + "\n");
				trainWriter.flush();
			}
			if(lineNo%100000 ==0){
				System.out.println("Processed:" + lineNo);
			}
		}
			System.out.println("Total county test found:" + totalCounty);
	
	}
	
	private static void generateTrainForSVM() {

		Path file = Paths.get("train-with-county-properlabel.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, String> hashIdLabelMap = new HashMap();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			hashIdLabelMap.put(lineArr[1], lineArr[2]);
		}

		file = Paths.get("train-photo-tamura.txt");
		lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter trainWriter = null;
		try {
			trainWriter = new PrintWriter(new FileWriter("train-tamura-photo-county-svm-1.0.txt", true));
			// testWriter = new PrintWriter(new FileWriter("test-cnn-0.1.txt",
			// true));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String var = "var";
		String colon = ":";
		int lineNo = 0;
		int maxGrid = -1;
		int totalCounty = 0;
		for (String line : (Iterable<String>) lines::iterator) {
			lineNo++;
			String lineArr[] = line.split("\t");
			if (hashIdLabelMap.containsKey(lineArr[0])) {
				totalCounty++;
				String md5Val = lineArr[0];
				String lineToWrite = "";

				String features = lineArr[lineArr.length - 1];
				int gridNumber = Integer.parseInt(hashIdLabelMap.get(md5Val));
				if (maxGrid < gridNumber) {
					maxGrid = gridNumber;
				}
				String featureArr[] = features.split(" ");
				for (int j = 0; j < featureArr.length; j++) {
					String feature =  (j + 1) + colon + featureArr[j];
					if (j == 0) {
						lineToWrite = feature;
					} else {
						lineToWrite = lineToWrite + " " + feature;
					}
				}
				lineToWrite = lineToWrite + " # "+ md5Val;
				line = gridNumber + " " + lineToWrite;
				trainWriter.write(line + "\n");
				trainWriter.flush();
				hashIdLabelMap.remove(lineArr[0]);
				// fileMap.remove(lineArr[0]);
			}
			if (lineNo % 100000 == 0) {
				System.out.println("Processed:" + lineNo);
			}
		}
		System.out.println("max grid in train:" + maxGrid + " and total county data:" + totalCounty);
		trainWriter.close();

		if (hashIdLabelMap.size() != 0) {
			// PrintWriter writer = null;
			try {
				trainWriter = new PrintWriter(new FileWriter("train-notfound-tamura-photo-county-vw-1.0.txt", true));
				// testWriter = new PrintWriter(new
				// FileWriter("test-cnn-0.1.txt",
				// true));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Iterator it = hashIdLabelMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				String key = (String) entry.getKey();
				trainWriter.write(key + "\n");
				trainWriter.flush();
			}
			trainWriter.close();
		}

	
	}

	private static void getTrainInstanceForPopGrid() {
		List<TrainInstance> trainList = getTrainForCounty("Greater+London"); // 2
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
		//return trainList.get(minIndex);
	}
	
	private static List<TrainInstance> getTrainForCounty(String county){ // "Greater+London"
		List<TrainInstance> trainList = new ArrayList();
		Path file = Paths.get("sorted-train-photo-video-mediaeval-1.0.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		PrintWriter trainWriter = null;
//		try {
//			trainWriter = new PrintWriter(new FileWriter("train-photo-mediaeval-county.txt", true));
//			// testWriter = new PrintWriter(new FileWriter("test-cnn-0.1.txt",
//			// true));
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			String place = lineArr[2];
			String placeAtts[] = place.split(",");
			for (String placeItem : placeAtts) {
				String placeInfo[] = placeItem.split(":");
				if(placeInfo[2].equals("County")){
					if(placeInfo[1].equals(county)){
//						trainWriter.write(line + "\n");
//						trainWriter.flush();
						TrainInstance instance = new TrainInstance();
						instance.setTag(lineArr[1]);
						instance.setLatitude(lineArr[5]);
						instance.setLongitude(lineArr[4]);
						instance.setId(lineArr[6]);
						trainList.add(instance);
						break;
					}
				}
			}
		}
		return trainList;
	}
	
	private static void getTest(){
		Path file = Paths.get("estimation-test-photo-mediaeval.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, String> photoTestMap = new HashMap();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			photoTestMap.put(lineArr[1], line);
		}
		System.out.println("Loaded test photo..");
		file = Paths.get("estimation-test-video-mediaeval.txt");
		lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, String> videoTestMap = new HashMap();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			videoTestMap.put(lineArr[1], line);
		}
		System.out.println("Loaded test video..");
		file = Paths.get("yfcc100m_places");
		lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		PrintWriter photoWriter = null;
		try {
			photoWriter = new PrintWriter(new FileWriter("estimation-test-photo-mediaeval-places.txt", true));
			// testWriter = new PrintWriter(new FileWriter("test-cnn-0.1.txt",
			// true));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		PrintWriter videoWriter = null;
		try {
			videoWriter = new PrintWriter(new FileWriter("estimation-test-video-mediaeval-places.txt", true));
			// testWriter = new PrintWriter(new FileWriter("test-cnn-0.1.txt",
			// true));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		long lineNo = 0;
		for (String line : (Iterable<String>) lines::iterator) {
			lineNo++;
			String lineArr[] = line.split("\t");
			if(photoTestMap.containsKey(lineArr[0])){
				photoWriter.write(photoTestMap.get(lineArr[0]) + "\t" + lineArr[1]+"\n");
				photoWriter.flush();
			}
			if(videoTestMap.containsKey(lineArr[0])){
				videoWriter.write(videoTestMap.get(lineArr[0]) + "\t" + lineArr[1]+"\n");
				videoWriter.flush();
			}
			if(lineNo%1000000 == 0){
				System.out.println("Processed:" + lineNo);
			}
		}
		photoWriter.close();
		videoWriter.close();
		System.out.println("Done..");
	}
	
	private static void findMostPopLabels(){
		Path file = Paths.get("sorted-train-with-county-properlabel.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String prevLabel = ""; boolean first = true; int count = 0; String maxLabel = ""; int maxCount = Integer.MIN_VALUE;
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			if(first){
				prevLabel = lineArr[2];
				first = false;
				count = 1;
			}else{
				if(prevLabel.equals(lineArr[2])){
					count++;
				}else{
					if(count > maxCount){
						maxCount = count;
						maxLabel = prevLabel;
					}
					prevLabel = lineArr[2];
					count = 1;
				}
			}
		}
		System.out.println("max used label:" + maxLabel);
		
	}

	private static void generateCountyBasedTest() {
		Path file = Paths.get("train-with-county-properlabel.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, String> hashIdLabelMap = new HashMap();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			hashIdLabelMap.put(lineArr[3], lineArr[2]);
		}
		System.out.println("Loaded labels");
		file = Paths.get("test-photo-tamura.txt");
		lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Map<String, String> hashIdTestMap = new HashMap();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			hashIdTestMap.put(lineArr[0], line); // photo
			//hashIdTestMap.put(lineArr[0].split("-")[0], line); // video
		}
		System.out.println("Loaded test data..");
		file = Paths.get("estimation-test-photo-mediaeval-places.txt");
		lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String var = "var";
		String colon = ":";
		int lineNo = 0;
		int maxGrid = -1;
		int totalCounty = 0;

		PrintWriter trainWriter = null;
		try {
			trainWriter = new PrintWriter(new FileWriter("test-tamura-photo-county-vw-1.0.txt", true));
			// testWriter = new PrintWriter(new FileWriter("test-cnn-0.1.txt",
			// true));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int count = 0;
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			lineNo++;
			if (hashIdTestMap.containsKey(lineArr[2])) {
				String place = lineArr[lineArr.length - 1];
				String placeAtts[] = place.split(",");
				String label = "2";
				for (String placeItem : placeAtts) {
					String placeInfo[] = placeItem.split(":");
					if (placeInfo[2].equals("County")) {
						label = hashIdLabelMap.get(placeInfo[1]);
						if(label == null || label.isEmpty()){
							label="2";
							count++;
							//System.out.println("Label is empty.");
						}
					}
				}
				String testInstance = hashIdTestMap.get(lineArr[2]);
				String testInstanceArr[] = testInstance.split("\t");
				String md5Val = testInstanceArr[0];
				String lineToWrite = "";
				String features = testInstanceArr[testInstanceArr.length - 1];
				String featureArr[] = features.split(" ");
				for (int j = 0; j < featureArr.length; j++) {
					String feature = var + (j + 1) + colon + featureArr[j];
					if (j == 0) {
						lineToWrite = feature;
					} else {
						lineToWrite = lineToWrite + " " + feature;
					}
				}
				totalCounty++;
				line = label + " " + "'" + md5Val + "| " + lineToWrite;
				trainWriter.write(line + "\n");
				trainWriter.flush();
			}
			if(lineNo%100000 ==0){
				System.out.println("Processed:" + lineNo);
			}
		}
		System.out.println("Count:" + count);
			System.out.println("Total county test found:" + totalCounty);
	}

	public static void generateCountyBasedTrain() {
		Path file = Paths.get("train-with-county-properlabel.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, String> hashIdLabelMap = new HashMap();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			hashIdLabelMap.put(lineArr[1], lineArr[2]);
		}

		file = Paths.get("train-photo-tamura.txt");
		lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter trainWriter = null;
		try {
			trainWriter = new PrintWriter(new FileWriter("train-tamura-photo-county-vw-1.0.txt", true));
			// testWriter = new PrintWriter(new FileWriter("test-cnn-0.1.txt",
			// true));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String var = "var";
		String colon = ":";
		int lineNo = 0;
		int maxGrid = -1;
		int totalCounty = 0;
		for (String line : (Iterable<String>) lines::iterator) {
			lineNo++;
			String lineArr[] = line.split("\t");
			if (hashIdLabelMap.containsKey(lineArr[0])) {
				totalCounty++;
				String md5Val = lineArr[0];
				String lineToWrite = "";

				String features = lineArr[lineArr.length - 1];
				int gridNumber = Integer.parseInt(hashIdLabelMap.get(md5Val));
				if (maxGrid < gridNumber) {
					maxGrid = gridNumber;
				}
				String featureArr[] = features.split(" ");
				for (int j = 0; j < featureArr.length; j++) {
					String feature = var + (j + 1) + colon + featureArr[j];
					if (j == 0) {
						lineToWrite = feature;
					} else {
						lineToWrite = lineToWrite + " " + feature;
					}
				}
				line = gridNumber + " " + "'" + md5Val + "| " + lineToWrite;
				trainWriter.write(line + "\n");
				trainWriter.flush();
				hashIdLabelMap.remove(lineArr[0]);
				// fileMap.remove(lineArr[0]);
			}
			if (lineNo % 100000 == 0) {
				System.out.println("Processed:" + lineNo);
			}
		}
		System.out.println("max grid in train:" + maxGrid + " and total county data:" + totalCounty);
		trainWriter.close();

		if (hashIdLabelMap.size() != 0) {
			// PrintWriter writer = null;
			try {
				trainWriter = new PrintWriter(new FileWriter("train-notfound-tamura-photo-county-vw-1.0.txt", true));
				// testWriter = new PrintWriter(new
				// FileWriter("test-cnn-0.1.txt",
				// true));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Iterator it = hashIdLabelMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				String key = (String) entry.getKey();
				trainWriter.write(key + "\n");
				trainWriter.flush();
			}
			trainWriter.close();
		}

	}

	public static void getTrain() {

		Path file = Paths.get("estimation-test-video-me-1.0-tamura.txt");
		// Path file = Paths.get("all-estimation-test-photo-me-1.0.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		PrintWriter trainWriter = null;
		try {
			trainWriter = new PrintWriter(new FileWriter("final-estimation-test-video-me-1.0-tamura.txt", true));
			// testWriter = new PrintWriter(new FileWriter("test-cnn-0.1.txt",
			// true));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");

			trainWriter.write(lineArr[lineArr.length - 1] + "\t" + line + "\n");
			trainWriter.flush();

		}
		trainWriter.close();
	}

	public static void getTrainBasedonTestInstance() {
		Path file = Paths.get("resultWriter-video-1.0-tamura.txt");
		// Path file = Paths.get("all-estimation-test-photo-me-1.0.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Map<String, String> fileMap = new HashMap();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			fileMap.put(lineArr[1], lineArr[0]);
		}
		file = Paths.get("estimation-test-photo-me-1.0-tamura.txt");
		// Path file = Paths.get("all-estimation-test-photo-me-1.0.txt");
		lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		PrintWriter trainWriter = null;
		try {
			trainWriter = new PrintWriter(new FileWriter("estimation-test-video-me-1.0-tamura.txt", true));
			// testWriter = new PrintWriter(new FileWriter("test-cnn-0.1.txt",
			// true));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			if (fileMap.containsKey(lineArr[7])) {
				trainWriter.write(line + "\n");
				trainWriter.flush();
			}
		}
		trainWriter.close();

	}

	public static void generateTrain() {

		Path file = Paths.get("sorted-train-photo-video-mediaeval-1.0.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Map<String, String> fileMap = new HashMap();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			BigInteger gridNumber = GenerateGrid.getGridNumber(lineArr[5], lineArr[4], 1.0);
			fileMap.put(lineArr[7], gridNumber.toString());
		}

		System.out.println("Loaded test file successfully." + fileMap.size());

		file = Paths.get("train-video-tamura.txt");
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		PrintWriter trainWriter = null;
		try {
			trainWriter = new PrintWriter(new FileWriter("train-tamura-video-vw-1.0.txt", true));
			// testWriter = new PrintWriter(new FileWriter("test-cnn-0.1.txt",
			// true));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String var = "var";
		String colon = ":";
		int lineNo = 0;
		long maxGrid = -1;
		for (String line : (Iterable<String>) lines::iterator) {
			lineNo++;
			String lineArr[] = line.split("\t");
			String md5Val = lineArr[0].split("-")[0];
			String lineToWrite = "";
			if (fileMap.containsKey(md5Val)) {
				String features = lineArr[lineArr.length - 1];
				Long gridNumber = Long.parseLong(fileMap.get(md5Val));
				if (maxGrid < gridNumber) {
					maxGrid = gridNumber;
				}
				String featureArr[] = features.split(" ");
				for (int j = 0; j < featureArr.length; j++) {
					String feature = var + (j + 1) + colon + featureArr[j];
					if (j == 0) {
						lineToWrite = feature;
					} else {
						lineToWrite = lineToWrite + " " + feature;
					}
				}
				line = gridNumber.toString() + " " + "'" + md5Val + "| " + lineToWrite;
				trainWriter.write(line + "\n");
				trainWriter.flush();
				// fileMap.remove(lineArr[0]);
			}
			if (lineNo % 100000 == 0) {
				System.out.println("Processed:" + lineNo);
			}
		}
		System.out.println("max grid in train:" + maxGrid);
		trainWriter.close();

		// test instances Not in data provided ..
		// PrintWriter remainingWriter = null;
		// try {
		// remainingWriter = new PrintWriter(new
		// FileWriter("remaining-test-video-1.0.txt", true));
		// // testWriter = new PrintWriter(new FileWriter("test-cnn-0.1.txt",
		// true));
		// } catch (IOException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// Iterator it = fileMap.entrySet().iterator();
		// while(it.hasNext()){
		// Entry entry = (Entry) it.next();
		// remainingWriter.write(entry.getKey() + "\n");
		// remainingWriter.flush();
		// }
		// remainingWriter.close();

	}

	public static void getMaximumGrid() {
		Path file = Paths.get("train-tamura-photo-video-vw-1.0.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		long maxGrid = -1;
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split(" ");
			long currGrid = Long.parseLong(lineArr[0]);
			if (currGrid > maxGrid) {
				maxGrid = currGrid;
			}
		}
		System.out.println("maxGrid:" + maxGrid);
	}

	public static void generateFeatureFileFromS3() {
		Path file = Paths.get("sorted-total-remaining-test.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		File folder = new File("files/");
		PrintWriter testWriter = null;
		PrintWriter testNotFoundWriter = null;
		try {
			testWriter = new PrintWriter(new FileWriter("test-remaining-tamura.txt", true));
			testNotFoundWriter = new PrintWriter(new FileWriter("test-remaining-notfound-tamura.txt", true));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		File[] listOfFiles = folder.listFiles();
		HashMap<String, String> map = new HashMap();
		for (File f : listOfFiles) {
			map.put(f.getName().substring(0, 3), f.getAbsolutePath());
		}

		for (String line : (Iterable<String>) lines::iterator) {
			String startLine = line.substring(0, 3);
			if (map.containsKey(startLine)) {
				String path = map.get(startLine);
				Path featureFile = Paths.get(path);
				Stream<String> featureLines = null;
				try {
					featureLines = Files.lines(featureFile, StandardCharsets.UTF_8);
				} catch (IOException e) {
					e.printStackTrace();
				}
				boolean found = false;
				for (String featureLine : (Iterable<String>) featureLines::iterator) {
					String[] featureLineArr = featureLine.split("\t");
					if (featureLineArr[0].equals(line.trim())) {
						testWriter.write(featureLine + "\n");
						testWriter.flush();
						found = true;
						break;
					}
				}
				if (!found) {
					testNotFoundWriter.write(line + "\n");
					testNotFoundWriter.flush();
				}
			} else {
				testNotFoundWriter.write(line + "\n");
				testNotFoundWriter.flush();
			}
		}
		testWriter.close();
		testNotFoundWriter.close();
	}

	public static void getData() {
		Path file = Paths.get("remaining-test-video-1.0.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, Integer> remMap = new HashMap();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			// remMap.put(lineArr[1], 0);
			remMap.put(lineArr[0], 0);
		}
		System.out.println("Loaded the testfile:" + remMap.size());
		file = Paths.get("all_tamura_keyframes.txt");
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter trainWriter = null;
		PrintWriter trainNotFoundWriter = null;
		try {
			trainWriter = new PrintWriter(new FileWriter("test-video-tamura.txt", true));
			trainNotFoundWriter = new PrintWriter(new FileWriter("test-video-notfound-tamura.txt", true));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			String nameArr[] = lineArr[0].split("-");
			if (remMap.containsKey(nameArr[0])) {
				trainWriter.write(line + "\n");
				trainWriter.flush();
				remMap.put(nameArr[0], 1);
			}
		}
		trainWriter.close();
		Iterator it = remMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			int val = (int) entry.getValue();
			if (val == 0) {
				trainNotFoundWriter.write(entry.getKey() + "\n");
				trainNotFoundWriter.flush();
			}
		}
		trainNotFoundWriter.close();
	}

}



