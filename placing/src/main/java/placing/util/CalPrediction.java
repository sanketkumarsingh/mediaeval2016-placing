package placing.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class CalPrediction {
	
	public static void main(String[] args) {
		Path file = Paths.get("estimation-test-video-me-1.0-tamura.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Map<String, String> idCellMap = new HashMap();
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			idCellMap.put(lineArr[lineArr.length-2] , lineArr[0]);
		}
		
		file = Paths.get("lat_long_video_1.0_run2.txt");
		lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int correctcount = 0;
		int totalCount = 0;
		for (String line : (Iterable<String>) lines::iterator) {
			String lineArr[] = line.split("\t");
			totalCount++;
			if(idCellMap.containsKey(lineArr[0])){
				String realCell = idCellMap.get(lineArr[0]);
				if(realCell.equals(lineArr[lineArr.length-4])){
					correctcount++;
				}
			}else{
				System.out.println("Test not found..");
			}
		}
		double accuracy = ((double) correctcount)/((double)totalCount);
		System.out.println("Accuracy:" + accuracy);
	}
	

}
