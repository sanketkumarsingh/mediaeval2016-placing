package placing.util;

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

public class DetermineCoordinateForTamura implements Runnable {

	static double degree = 1.0;
	static String mostPopGridForDeg = "13859";
	static int totalGrid = 9867;
	static Map<String, List<TrainInstance>> testMap = null;
	String resultFileName;
	String testFileName;
	static Map<String, TrainInstance> popGridTrainInstanceMap;

	public DetermineCoordinateForTamura(String resultFileName, String testFileName) {
		this.resultFileName = resultFileName;
		this.testFileName = testFileName;
	}

	public static void main(String[] args) throws IOException {
		// testMap = loadPredictions();
		loadPopCountyCells();
		for (int i = 1; i <= 1; i++) {
			DetermineCoordinateForTamura obj = new DetermineCoordinateForTamura(
					"sorted-test-tamura-photo-county-vw-1.0-pred.txt",
					"sorted-estimation-test-photo-me-1.0-tamura.txt");
			Thread thread = new Thread(obj, "Thread-photo");
			thread.start();
		}
		DetermineCoordinateForTamura obj = new DetermineCoordinateForTamura(
				"sorted-test-tamura-video-county-vw-1.0-pred.txt", "sorted-estimation-test-video-me-1.0-tamura.txt");
		Thread thread = new Thread(obj, "Thread-video");
		thread.start();
	}

	private static void loadPopCountyCells() {
		popGridTrainInstanceMap = new HashMap();
		TrainInstance obj1 = new TrainInstance();
		obj1.setId("9670661964");
		obj1.setLatitude("37.781");
		obj1.setLongitude("-122.418167");
		popGridTrainInstanceMap.put("4", obj1);

		TrainInstance obj2 = new TrainInstance();
		obj2.setId("4169904886");
		obj2.setLatitude("40.749353");
		obj2.setLongitude("-73.986675");
		popGridTrainInstanceMap.put("36", obj2);

		TrainInstance obj3 = new TrainInstance();
		obj3.setId("6913083943");
		obj3.setLatitude("26.121053");
		obj3.setLongitude("-80.145113");
		popGridTrainInstanceMap.put("1", obj3);

		TrainInstance obj4 = new TrainInstance();
		obj4.setId("7969001470");
		obj4.setLatitude("51.50981");
		obj4.setLongitude("-0.123338");
		popGridTrainInstanceMap.put("2", obj4);
	}

	private static void loadPopGrids() {
		popGridTrainInstanceMap = new HashMap();
		/// setting train info of 13859 for 1 as it doesn't contain any train
		/// but was predicted thru images.
		TrainInstance obj1 = new TrainInstance();
		obj1.setId("6461241879");
		obj1.setLatitude("51.5098");
		obj1.setLongitude("-0.130041");
		popGridTrainInstanceMap.put("1", obj1);

		TrainInstance obj2 = new TrainInstance();
		obj2.setId("4429032234");
		obj2.setLatitude("35.302215");
		obj2.setLongitude("-88.638607");
		popGridTrainInstanceMap.put("19531", obj2);

		TrainInstance obj3 = new TrainInstance();
		obj3.setId("8606919573");
		obj3.setLatitude("39.859561");
		obj3.setLongitude("-4.025812");
		popGridTrainInstanceMap.put("18175", obj3);

		TrainInstance obj4 = new TrainInstance();
		obj4.setId("2665730949");
		obj4.setLatitude("-30.039229");
		obj4.setLongitude("-51.221351");
		popGridTrainInstanceMap.put("43328", obj4);

		TrainInstance obj5 = new TrainInstance();
		obj5.setId("3493761294");
		obj5.setLatitude("4.635627");
		obj5.setLongitude("-74.083642");
		popGridTrainInstanceMap.put("30705", obj5);

		TrainInstance obj6 = new TrainInstance();
		obj6.setId("4777259267");
		obj6.setLatitude("19.422522");
		obj6.setLongitude("-99.158563");
		popGridTrainInstanceMap.put("25280", obj6);

		TrainInstance obj7 = new TrainInstance();
		obj7.setId("343197587");
		obj7.setLatitude("50.086817");
		obj7.setLongitude("14.418729");
		popGridTrainInstanceMap.put("14234", obj7);

		TrainInstance obj8 = new TrainInstance();
		obj8.setId("2652614247");
		obj8.setLatitude("43.768382");
		obj8.setLongitude("11.255879");
		popGridTrainInstanceMap.put("16751", obj8);

		TrainInstance obj9 = new TrainInstance();
		obj9.setId("18971806");
		obj9.setLatitude("39.949523");
		obj9.setLongitude("-75.174003");
		popGridTrainInstanceMap.put("18104", obj9);

		TrainInstance obj10 = new TrainInstance();
		obj10.setId("5496124504");
		obj10.setLatitude("51.451269");
		obj10.setLongitude("-2.578107");
		popGridTrainInstanceMap.put("13857", obj10);

		TrainInstance obj11 = new TrainInstance();
		obj11.setId("5684475025");
		obj11.setLatitude("37.7794");
		obj11.setLongitude("-122.409553");
		popGridTrainInstanceMap.put("18777", obj11);
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
					new FileWriter("output-1.0-" + Thread.currentThread().getName() + "-county.txt", true));
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
			String testLine[] = testPrediction.get(index).split(" ");
			index++;
			String predCell = testLine[0];
			TrainInstance popGridTrainInstance = popGridTrainInstanceMap.get(predCell);
			if (popGridTrainInstance == null) {
				System.out.println("Cell:" + predCell);
			}
			GeodesicData g = Geodesic.WGS84.Inverse(Double.parseDouble(popGridTrainInstance.getLatitude()),
					Double.parseDouble(popGridTrainInstance.getLongitude()), Double.parseDouble(lineArr[6]),
					Double.parseDouble(lineArr[5]), GeodesicMask.DISTANCE);
			outputWriter.write(lineArr[7] + "\t" + popGridTrainInstance.getLatitude() + "\t"
					+ popGridTrainInstance.getLongitude() + "\t" + lineArr[6] + "\t" + lineArr[5] + "\t" + g.s12 + "\t"
					+ "0.0" + "\t" + predCell + "\t" + "true" + "\t" + popGridTrainInstance.getId() + "\n");
			outputWriter.flush();

			if (index % 100000 == 0) {
				System.out.println("For Thread:" + Thread.currentThread().getName() + "Processed:" + index);
			}
		}

		outputWriter.close();
		System.out.println("Done for Thread:" + Thread.currentThread().getName());
	}

	private static Map<String, List<TrainInstance>> loadPredictions() throws IOException {
		Map<String, List<TrainInstance>> trainMap = new HashMap(totalGrid);
		Path gridfile = Paths.get("sorted-train-photo-video-mediaeval-1.0.txt");
		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			String gridNumber = lineArr[0];// GenerateGrid.getGridNumber(lineArr[13],
											// lineArr[12], degree).toString();
			if (trainMap.containsKey(gridNumber)) {
				TrainInstance instance = new TrainInstance();
				instance.setTag(lineArr[1]);
				instance.setLatitude(lineArr[5]);
				instance.setLongitude(lineArr[4]);
				instance.setId(lineArr[6]);
				// instance.setPlace(lineArr[2]);
				trainMap.get(gridNumber).add(instance);
			} else {
				TrainInstance instance = new TrainInstance();
				instance.setTag(lineArr[1]);
				instance.setLatitude(lineArr[5]);
				instance.setLongitude(lineArr[4]);
				instance.setId(lineArr[6]);
				// instance.setPlace(lineArr[2]);
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
	// String place;

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

	// public String getPlace() {
	// return place;
	// }
	//
	// public void setPlace(String place) {
	// this.place = place;
	// }

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
