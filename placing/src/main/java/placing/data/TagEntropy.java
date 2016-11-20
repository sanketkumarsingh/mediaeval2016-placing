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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import weka.core.Stopwords;

public class TagEntropy {

	private static Map<String, Map<String, TagProb>> gridPosting = new HashMap();
	// static long averageTagPerGrid;
	// private static Map<String, String> tagGridCountMap = new HashMap();
	// static long totalGrid = 6480000;
	// static List<String> gridsInTrainList = new ArrayList();

	public static void main(String[] args) throws IOException {

		Path gridfile = Paths.get("train-posting-userid-tag-title-grid-1.0.txt");
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
				// mapTermCount.put(termCount[0],
				// Integer.parseInt(termCount[1]));
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
				totalNumberOfTags = totalNumberOfTags + totalTermCount;
			}
			if (maxTagCount < totalTermCount) {
				maxTagCount = totalTermCount;
				maxTagCountGrid = lineArr[0];
			}
			//
			// TagProb tagprob = new TagProb();
			// tagprob.setTagCount(totalTermCount);
			// mapTermCount.put("===TOTAL===", tagprob);

			/// mapTermCount.put("===TOTAL===", totalTermCount);
			// gridPosting.put(lineArr[0], mapTermCount);
			// if (!gridsInTrainList.contains(lineArr[0])) {
			// gridsInTrainList.add(lineArr[0]);
			// }
		}
		System.out.println("loaded grid posting map..");
		PrintWriter tagEntropyWriter = null;
		PrintWriter tagRawEntropyWriter = null;
		try {
			tagEntropyWriter = new PrintWriter(new FileWriter("tag-normalized-entropy-1.0.txt", true));
			tagRawEntropyWriter = new PrintWriter(new FileWriter("tag-raw-entropy-1.0.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int lineno = 0;
		Iterator it = gridPosting.entrySet().iterator();
		double meanEntropy = 0.0;
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			String tag = (String) pair.getKey();
			lineno++;
			Map<String, TagProb> gridUserCountMap = (Map<String, TagProb>) pair.getValue();
			double entropy = getEntropyForTag(gridUserCountMap);
			TagProb tagProb = new TagProb();
			tagProb.setTagProb(entropy);
			gridUserCountMap.put("===ENTROPY===", tagProb);
			tagRawEntropyWriter.write(tag + "\t" + entropy + "\n");
			tagRawEntropyWriter.flush();
			meanEntropy = meanEntropy + tagProb.getTagProb();

			if (lineno % 100000 == 0) {
				System.out.println("Processed:" + lineno);
			}
		}
		tagRawEntropyWriter.close();
		meanEntropy = (double) meanEntropy / (double) lineno;
		System.out.println("mean entropy:" + meanEntropy);
		it = gridPosting.entrySet().iterator();

		double deviation = 0.0;
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			String tag = (String) pair.getKey();
			Map<String, TagProb> gridUserCountMap = (Map<String, TagProb>) pair.getValue();
			double tagEntropy = gridUserCountMap.get("===ENTROPY===").getTagProb();
			deviation = deviation + Math.pow((tagEntropy - meanEntropy), 2);
		}
		deviation = (double) deviation / (double) lineno;
		deviation = Math.sqrt(deviation);
		System.out.println("standard deviation:" + deviation);

		it = gridPosting.entrySet().iterator();
		//Set<String> tagSet = new HashSet();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			String tag = (String) pair.getKey();
			Map<String, TagProb> gridUserCountMap = (Map<String, TagProb>) pair.getValue();
			double tagEntropy = gridUserCountMap.get("===ENTROPY===").getTagProb();
			double power = -Math.pow(((tagEntropy - meanEntropy) / (2.0 * deviation)), 2);
			double normalizedEntropy = Math.exp(power) / (deviation * Math.sqrt(2 * Math.PI));
			gridUserCountMap.get("===ENTROPY===").setTagProb(normalizedEntropy);
//			if(tagSet.contains(tag)){
//				System.out.println("present.");
//				
//			}else{
//				tagSet.add(tag);
//			}
			if(tagEntropy == -0.0 || tagEntropy >= 6.0){
				normalizedEntropy = 0.0;
			}
			tagEntropyWriter.write(tag + "\t" + normalizedEntropy + "\n");
			tagEntropyWriter.flush();
		}

		System.out.println("Done..");
		tagEntropyWriter.close();
	}

	private static double getEntropyForTag(Map<String, TagProb> gridUserCountMap) {
		Iterator it = gridUserCountMap.entrySet().iterator();
		double score = 0.0;
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			String tag = (String) pair.getKey();
			TagProb tagProb = (TagProb) pair.getValue();
			if(tagProb.getTagProb() != 0.0){
				score = score + tagProb.getTagProb() * Math.log(tagProb.getTagProb());
			}
			
		}
		return -score;
	}

}

class TagProb {
	int tagCount;
	double tagProb;

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
