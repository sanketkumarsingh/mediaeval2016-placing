package placing.data;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;


public class GenerateEstimateData {

	public static void main(String[] args) {
		getEstimateFile();
	}
	
	public static void getEstimateFile(){
		Path file = Paths.get("lat_long_photo_1.0_run3_text_county.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// me16pt_[subtask]_[media]_[group]_[run].txt
		PrintWriter trainWriter = null;
		try {
			trainWriter = new PrintWriter(new FileWriter("me16pt_estimation_photo_county_CSUA_run3.txt", true));
			// testWriter = new PrintWriter(new
			// FileWriter("test-photo-mediaeval.txt", true));
			// lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// [photo/video id] [longitude] [latitude]
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			trainWriter.write(lineArr[0] + "\t" + lineArr[2] + "\t" + lineArr[1] + "\n");
			trainWriter.flush();
		}
		trainWriter.close();
	}

	public static void getUniquePredTamura() {
		Path file = Paths.get("test-photo-model1-1.0.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Set<String> cellSet = new HashSet();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split(" ");
			cellSet.add(lineArr[0]);
		}

		file = Paths.get("test-video-model1-1.0.txt");
		lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Set<String> cellSet = new HashSet();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split(" ");
			cellSet.add(lineArr[0]);
		}
		
		Iterator it = cellSet.iterator();
		while(it.hasNext()){
			System.out.println(it.next() + " ");
		}
	}
}
