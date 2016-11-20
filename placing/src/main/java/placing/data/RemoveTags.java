package placing.data;

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
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import javax.sound.midi.Synthesizer;

public class RemoveTags {

	// remove whose ration is less 0.1
	public static void main(String[] args) throws IOException {

		// removeTestImagesFromAllyfcc();
		// sortAndRemoveTags();
		// generateWeightedTags();
		//data();
		divideData();
		// uniqueLatAndLong();
	}
	
	private static void uniqueLatAndLong() throws IOException{
		Path gridfile = Paths.get("sorted-train-photo-video-yfcc.txt");
		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		Map<String, Integer> map = new HashMap();
		long lineNo=0;
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			//lineArr[0] lineArr[1]
			lineNo++;
			String toCheck = lineArr[0] + ":" + lineArr[1];
			if(map.containsKey(toCheck)){
				map.put(toCheck, map.get(toCheck)+1);
			}else{
				map.put(toCheck, 1);
			}
			if(lineNo%1000000 == 0){
				System.out.println("Processed:" + lineNo);
			}
		}
		PrintWriter resultWriter = null;
		try {
			resultWriter = new PrintWriter(new FileWriter("unique-coordinates.txt", true));
		}catch (IOException e) {
			e.printStackTrace();
		}
		Iterator it = map.entrySet().iterator(); long index = 1;
		while(it.hasNext()){
			Entry  entry = (Entry) it.next();
			String key = (String) entry.getKey();
			String keyArr[] = key.split(":");
			//Integer value =  (Integer) entry.getValue();
			resultWriter.write(keyArr[0] + "\t" + keyArr[1]+ "\t" + index + "\n");
			resultWriter.flush();
			index++;
		}
		resultWriter.close();
		System.out.println("Total unique lat and long:" + map.size());
	}
	
	private static void divideData() throws IOException{
		//Path gridfile = Paths.get("sorted-train-photo-video-yfcc.txt");
		Path gridfile = Paths.get("unique-coordinates.txt");
		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		PrintWriter resultWriter1 = null;
		PrintWriter resultWriter2 = null;
		PrintWriter resultWriter3 = null;
		PrintWriter resultWriter4 = null;
		PrintWriter resultWriter5 = null;
		PrintWriter resultWriter6 = null;
		PrintWriter resultWriter7 = null;
		PrintWriter resultWriter8 = null;
		try {
			resultWriter1 = new PrintWriter(new FileWriter("train-photo-video-coord-1.txt", true));
			resultWriter2 = new PrintWriter(new FileWriter("train-photo-video-coord-2.txt", true));
			resultWriter3 = new PrintWriter(new FileWriter("train-photo-video-coord-3.txt", true));
			resultWriter4 = new PrintWriter(new FileWriter("train-photo-video-coord-4.txt", true));
			resultWriter5 = new PrintWriter(new FileWriter("train-photo-video-coord-5.txt", true));
			resultWriter6 = new PrintWriter(new FileWriter("train-photo-video-coord-6.txt", true));
			resultWriter7 = new PrintWriter(new FileWriter("train-photo-video-coord-7.txt", true));
			resultWriter8 = new PrintWriter(new FileWriter("train-photo-video-coord-8.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		long lineno=0;
		long processed =0;
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			lineno++;
			double longitude = Double.parseDouble(lineArr[0]);
			double latitude  = Double.parseDouble(lineArr[1]);
			boolean test = false;
			if(longitude >= -180 && longitude<=-140){
				if(latitude <= 90 && latitude >= 20){
					resultWriter1.write(line + "\n");
					resultWriter1.flush();
					test = true;
					processed++;
				}
			}
		  if(longitude > -140 && longitude<=-20){
				if(latitude <= 90 && latitude >= 40){
					resultWriter2.write(line + "\n");
					resultWriter2.flush();
					processed++;
					test = true;
				}
				if(latitude < 40 && latitude >= 20){
					resultWriter1.write(line + "\n");
					resultWriter1.flush();
					processed++;
					test = true;
				}
			} 
		  if(longitude > -180 && longitude<=-20){
				if(latitude < 20 && latitude >= -90){
					resultWriter3.write(line + "\n");
					resultWriter3.flush();
					processed++;
					test = true;
				}
			}
			if(longitude > -20 && longitude<=40){
				if(latitude <= 90 && latitude >= 35){
					resultWriter4.write(line + "\n");
					resultWriter4.flush();
					processed++;
					test = true;
				}
			}
			if(longitude > 40 && longitude<=180){
				if(latitude <= 90 && latitude >= 35){
					resultWriter5.write(line + "\n");
					resultWriter5.flush();
					processed++;
					test = true;
				}
			}
			if(longitude > -20 && longitude<=50){
				if(latitude < 35 && latitude >= -90){
					resultWriter6.write(line + "\n");
					resultWriter6.flush();
					processed++;
					test = true;
				}
			}
			if(longitude > 50 && longitude<=180){
				if(latitude < 35 && latitude >= -15){
					resultWriter7.write(line + "\n");
					resultWriter7.flush();
					processed++;
					test = true;
				}
			}
			if(longitude > 50 && longitude<=180){
				if(latitude < -15 && latitude >= -90){
					resultWriter8.write(line + "\n");
					resultWriter8.flush();
					processed++;
					test = true;
				}
			}
			if(test == false){
				System.out.println(line);
			}
			if(lineno%1000000==0){
				System.out.println("total:" + lineno + " processed:"+processed);
			}
		}
		System.out.println("total:" + lineno + " processed:"+processed);
		resultWriter1.close();
		resultWriter2.close();
		resultWriter3.close();
		resultWriter4.close();
		resultWriter5.close();
		resultWriter6.close();
		resultWriter8.close();
		resultWriter7.close();
	}

	private static void data() throws IOException {
		Path gridfile = Paths.get("train-photo-video-yfcc.txt");
		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		PrintWriter resultWriter = null;
		try {
			resultWriter = new PrintWriter(new FileWriter("train-photo-video-yfcc-longlatfirst.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			String toWrite = lineArr[6] + "\t" + lineArr[7] + "\t" + lineArr[0] + "\t" + lineArr[1] + "\t" + lineArr[2]
					+ "\t" + lineArr[3] + "\t" + lineArr[4] + "\t" + lineArr[5];
			resultWriter.write(toWrite + "\n");
			resultWriter.flush();
		}
		resultWriter.close();
		System.out.println("Done..");
	}

	private static void removeTestImagesFromAllyfcc() throws IOException {
		Path gridfile = Paths.get("estimation-test-photo-mediaeval.txt");
		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		Map<String, String> testIdUserIdMap = new HashMap();
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			testIdUserIdMap.put(lineArr[1], lineArr[1]);
			testIdUserIdMap.put(lineArr[3], lineArr[3]);

		}
		System.out.println("Loaded photo..");
		gridfile = Paths.get("estimation-test-video-mediaeval.txt");
		gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			testIdUserIdMap.put(lineArr[1], lineArr[1]);
			testIdUserIdMap.put(lineArr[3], lineArr[3]);
		}
		System.out.println("Loaded videos");
		gridfile = Paths.get("yfcc100m_dataset.txt");
		gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);
		PrintWriter resultWriter = null;
		try {
			resultWriter = new PrintWriter(new FileWriter("train-photo-video-yfcc.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		long lineNo = 0;
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			if (testIdUserIdMap.containsKey(lineArr[1]) || testIdUserIdMap.containsKey(lineArr[3])
					|| lineArr[12].isEmpty() || lineArr[13].isEmpty()) {
				continue;
			} else {
				
				String toWrite = lineArr[12] + "\t" + lineArr[13] + "\t" + lineArr[0] + "\t" + lineArr[1] + "\t"
						+ lineArr[2] + "\t" + lineArr[3] + "\t" + lineArr[8] + "\t" + lineArr[10];
				resultWriter.write(toWrite + "\n");
				resultWriter.flush();
			}
			lineNo++;
			if (lineNo % 1000000 == 0) {
				System.out.println("Processed:" + lineNo);
			}
		}
		resultWriter.close();
		System.out.println("Done..");
	}

	public static void sortAndRemoveTags() throws IOException {
		Path gridfile = Paths.get("train-posting-userid-tag-title-grid-1.0.txt");
		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);

		PrintWriter resultWriter = null;
		try {
			resultWriter = new PrintWriter(new FileWriter("posting-train-1.0.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}

		int lineNo = 0;
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			List<TagDetail> list = new ArrayList();
			String toWrite = lineArr[0];
			for (int i = 1; i < lineArr.length; i++) {
				String tagInfo[] = lineArr[i].split("===");
				double tagRatio = Double.parseDouble(tagInfo[2]);
				if ((tagInfo[1].equals("1")) && (tagInfo[2].equals("1.0"))) {
					continue;
				}
				TagDetail obj = new TagDetail();
				obj.setTag(lineArr[i]);
				// obj.setTf(tagInfo[1]);
				obj.setUserRatio(Double.parseDouble(tagInfo[2]));
				list.add(obj);
			}
			Collections.sort(list);
			int length = list.size();
			length = (int) Math.ceil(0.9 * list.size());
			// length = (int) Math.ceil(0.9 * list.size()) ; //for 0.001
			for (int index = 0; index < length; index++) {
				toWrite = toWrite + "\t" + list.get(index).getTag();
			}
			resultWriter.write(toWrite + "\n");
			resultWriter.flush();
			lineNo++;
			if (lineNo % 100000 == 0) {
				System.out.println("Processed:" + lineNo);
			}
		}
		resultWriter.close();

	}

	public static void generateWeightedTags() throws IOException {

		Path gridfile = Paths.get("sorted-posting-userid-tag-title-grid-0.1-remove1tf1scorevals.txt");
		Stream<String> gridlines = Files.lines(gridfile, StandardCharsets.UTF_8);

		PrintWriter resultWriter = null;
		try {
			resultWriter = new PrintWriter(
					new FileWriter("sorted-posting-userid-tag-title-grid-0.1-weightedByIndex.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}

		int lineNo = 0;
		for (String line : (Iterable<String>) gridlines::iterator) {
			String lineArr[] = line.split("\t");
			List<TagDetail> list = new ArrayList();
			String toWrite = lineArr[0];

			for (int i = 1; i < lineArr.length; i++) {
				String tagInfo[] = lineArr[i].split("===");
				double tagRatio = Double.parseDouble(tagInfo[2]);
				TagDetail obj = new TagDetail();
				// obj.setTag(lineArr[i]);
				// obj.setTf(tagInfo[1]);
				double ratio = tagRatio / ((double) i);
				String tag = tagInfo[0] + "===" + tagInfo[1] + "===" + ratio;
				obj.setUserRatio(ratio);
				obj.setTag(tag);
				list.add(obj);
				// }
			}

			Collections.sort(list);
			int length = list.size();
			for (int index = 0; index < length; index++) {
				toWrite = toWrite + "\t" + list.get(index).getTag();
			}
			resultWriter.write(toWrite + "\n");
			resultWriter.flush();
			lineNo++;
			if (lineNo % 100000 == 0) {
				System.out.println("Processed:" + lineNo);
			}
		}
		resultWriter.close();

	}

}

class TagDetail implements Comparable {
	String tag;
	// String tf;
	double userRatio;

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	// public String getTf() {
	// return tf;
	// }
	// public void setTf(String tf) {
	// this.tf = tf;
	// }
	public double getUserRatio() {
		return userRatio;
	}

	public void setUserRatio(double userRatio) {
		this.userRatio = userRatio;
	}

	@Override
	public int compareTo(Object o) {
		if (this.getUserRatio() > ((TagDetail) o).getUserRatio()) {
			return -1;
		} else if (this.getUserRatio() < ((TagDetail) o).getUserRatio()) {
			return 1;
		} else {
			return 0;
		}

	}
}
