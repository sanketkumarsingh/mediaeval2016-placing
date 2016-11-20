package placing.data;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import placing.util.GenerateGrid;
import weka.core.Stopwords;

public class OrderingLocationByUserCount {

	public static void main(String[] args) throws UnsupportedEncodingException {
		generateUserIdTagFile();
	//	generateWeightBasedGridFile();
	}

	public static void generateWeightBasedGridFile() {
		Map<String, Map<String, TagGridUser>> map = getTagInGridUserCountMap();
	//	Map<String, Map<String, TagGridUser>> map =getTagInGridUserPhotoCountMap();
		System.out.println("**************Loaded the map**************");
		Path file = Paths.get("train-posting-ut-title-grid-1.0deg.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		PrintWriter gridWriter = null;
		try {
			gridWriter = new PrintWriter(new FileWriter("train-posting-userid-tag-title-grid-1.0.txt", true));
			// lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int lineno = 0;
		for (String line : (Iterable<String>) lines::iterator) {
			lineno++;
			String lineStr[] = line.split("\t");
			String toWrite = lineStr[0];
		//	long totalTermCountInGrid = 0;
			for (int i = 1; i < lineStr.length; i++) {
				String tagCount[] = lineStr[i].split("===");

				int gridUserIdCountForTag = 0;int globalUserIdCountForTag =0;
				if(map.containsKey(tagCount[0])){
					if(map.get(tagCount[0]).containsKey(lineStr[0])){
						gridUserIdCountForTag = map.get(tagCount[0]).get(lineStr[0]).getUserCount();
						globalUserIdCountForTag = map.get(tagCount[0]).get("===TOTAL===").getUserCount();
						double ratio = 0.0;
						if (globalUserIdCountForTag != 0) {
							ratio = ((double) gridUserIdCountForTag) / ((double) globalUserIdCountForTag);
							toWrite = toWrite + "\t" + tagCount[0] + "===" + tagCount[1] + "===" + ratio ;
						}
					}
				}
			}
			gridWriter.write(toWrite + "\n");
			gridWriter.flush();
			if (lineno % 10000 == 0) {
				System.out.println("Processed:" + lineno);
			}
		}
		gridWriter.close();
	}

	
	public static void generateWeightBasedGridFileWithTf() {
		Map<String, Map<String, TagGridUser>> map = getTagInGridUserCountMap();
	//	Map<String, Map<String, TagGridUser>> map =getTagInGridUserPhotoCountMap();
		System.out.println("**************Loaded the map**************");
		Path file = Paths.get("train-posting-ut-title-grid-1.0deg.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		PrintWriter gridWriter = null;
		try {
			gridWriter = new PrintWriter(new FileWriter("train-posting-userid-tag-title-grid-1.0.txt", true));
			// lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int lineno = 0;
		for (String line : (Iterable<String>) lines::iterator) {
			lineno++;
			String lineStr[] = line.split("\t");
			String toWrite = lineStr[0];
			long totalTermCountInGrid = 0;
			for (int i = 1; i < lineStr.length; i++) {
				String tagCount[] = lineStr[i].split("===");
				
//				System.out.println(tagCount[0] + " " + lineStr[0]);
//				if(map.containsKey(tagCount[0])){
//					System.out.println("Present1");
//					if(map.get(tagCount[0]).containsKey(lineStr[0])){
//						System.out.println("Present2");
//					}
//				}
				int gridUserIdCountForTag = 0;int globalUserIdCountForTag =0;
				if(map.containsKey(tagCount[0])){
					if(map.get(tagCount[0]).containsKey(lineStr[0])){
						gridUserIdCountForTag = map.get(tagCount[0]).get(lineStr[0]).getUserCount();
						globalUserIdCountForTag = map.get(tagCount[0]).get("===TOTAL===").getUserCount();
						double ratio = 0.0;
						if (globalUserIdCountForTag != 0) {
							ratio = ((double) gridUserIdCountForTag) / ((double) globalUserIdCountForTag);
							toWrite = toWrite + "\t" + tagCount[0] + "===" + tagCount[1] + "===" + ratio ;
						}
					}
				}
				// gridUserIdCountForTag = map.get(tagCount[0]).get(lineStr[0]).getUserCount();
				//int globalUserIdCountForTag = map.get(tagCount[0]).get("===TOTAL===").getUserCount();
			//	int gridphotoCountForTag = map.get(tagCount[0]).get(lineStr[0]).getPhotoCount();
			//	int globalphotoCountForTag = map.get(tagCount[0]).get("===TOTAL===").getPhotoCount();
				
//				double photoratio = 0.0; double tfratio = 0.0;
//				if (globalUserIdCountForTag != 0) {
//					photoratio = ((double) gridphotoCountForTag) / ((double) globalphotoCountForTag);
//					tfratio= (Double.parseDouble(tagCount[1])) / ((double) globalphotoCountForTag);
//				}
				
				//+ "==="+ photoratio + "===" + tfratio;
				
				totalTermCountInGrid= totalTermCountInGrid + Long.parseLong(tagCount[1]);
			}
			toWrite = toWrite + "\t" + totalTermCountInGrid ;
			gridWriter.write(toWrite + "\n");
			gridWriter.flush();
			if (lineno % 10000 == 0) {
				System.out.println("Processed:" + lineno);
			}
		}
		gridWriter.close();
	}
	
	
	public static Map<String, Map<String, TagGridUser>> getTagInGridUserCountMap() {
		Map<String, Map<String, TagGridUser>> map = new HashMap();
		Path file = Paths.get("sorted-userid-tag-title-grid-1.0.txt"); // sorted by tag
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		boolean first = true;
		String prevTag = "";
		Set<String> UserIdSet = new HashSet();
		Map<String, TagGridUser> gridUserIdCountMap = null;
		int k = 0;
		int totalUserAcrossGlobeForTag = 0;
		for (String line : (Iterable<String>) lines::iterator) {
			k++;
			String lineStr[] = line.split("\t");
			
			if (first) {
				gridUserIdCountMap = new HashMap();
				//totalUserAcrossGlobeForTag = 1;
				TagGridUser obj = new TagGridUser();
				Set<String> userForTagGrid = new HashSet();
				userForTagGrid.add(lineStr[0]);
				obj.setUsers(userForTagGrid);
				obj.setUserCount(userForTagGrid.size());
			//	totalUserAcrossGlobeForTag = userForTagGrid.size();
				gridUserIdCountMap.put(lineStr[2], obj);
				first = false;
				prevTag = lineStr[1];
			//	UserIdSet.add(lineStr[0]);
			} else { // the assumption is that same tag in same grid by same user cannot occur more than once.
				if (!prevTag.equals(lineStr[1])) {
					TagGridUser totalUser = new TagGridUser();
			//		
					Iterator it = gridUserIdCountMap.entrySet().iterator();
					String prevUser="";
					while(it.hasNext()){
						Map.Entry pair = (Entry) it.next();
						TagGridUser tagGridVal= (TagGridUser) pair.getValue();
//						if(tagGridVal.getUserCount() == 1){
//							String user = tagGridVal.getUsers().iterator().next();
//							
//						}
						UserIdSet.addAll(tagGridVal.getUsers());
						totalUserAcrossGlobeForTag = totalUserAcrossGlobeForTag + tagGridVal.getUserCount();
						tagGridVal.setUsers(null);
					}
					totalUser.setUserCount(totalUserAcrossGlobeForTag);
					gridUserIdCountMap.put("===TOTAL===", totalUser);
					//System.out.println("UserIdSet.size():" + UserIdSet.size());
					if(UserIdSet.size() != 1){
						map.put(prevTag, gridUserIdCountMap);
					}
					gridUserIdCountMap = new HashMap();
					totalUserAcrossGlobeForTag = 0;
					UserIdSet = new HashSet();
					prevTag = lineStr[1];
					if (gridUserIdCountMap.containsKey(lineStr[2])) {
						//totalUserAcrossGlobeForTag = totalUserAcrossGlobeForTag + 1;
						//gridUserIdCountMap.put(lineStr[2], gridUserIdCountMap.get(lineStr[2]) + 1);
						TagGridUser obj = gridUserIdCountMap.get(lineStr[2]);
						Set<String> userForTagGrid = obj.getUsers();
						userForTagGrid.add(lineStr[0]);
						obj.setUsers(userForTagGrid);
						obj.setUserCount(userForTagGrid.size());
				//		totalUserAcrossGlobeForTag = totalUserAcrossGlobeForTag + userForTagGrid.size();
						gridUserIdCountMap.put(lineStr[2], obj);
					} else {
//						totalUserAcrossGlobeForTag = totalUserAcrossGlobeForTag + 1;
					//	gridUserIdCountMap.put(lineStr[2], 1);
						TagGridUser obj = new TagGridUser();
						Set<String> userForTagGrid = new HashSet();
						userForTagGrid.add(lineStr[0]);
						obj.setUsers(userForTagGrid);
						obj.setUserCount(userForTagGrid.size());
				//		totalUserAcrossGlobeForTag = totalUserAcrossGlobeForTag + userForTagGrid.size();
						gridUserIdCountMap.put(lineStr[2], obj);
					}
				//	UserIdSet.add(lineStr[0]);
				} else {
					if (gridUserIdCountMap.containsKey(lineStr[2])) {
						TagGridUser obj = gridUserIdCountMap.get(lineStr[2]);
						Set<String> userForTagGrid = obj.getUsers();
						userForTagGrid.add(lineStr[0]);
						obj.setUsers(userForTagGrid);
						obj.setUserCount(userForTagGrid.size());
				//		totalUserAcrossGlobeForTag = totalUserAcrossGlobeForTag + userForTagGrid.size();
						gridUserIdCountMap.put(lineStr[2], obj);
					} else {
						TagGridUser obj = new TagGridUser();
						Set<String> userForTagGrid = new HashSet();
						userForTagGrid.add(lineStr[0]);
						obj.setUsers(userForTagGrid);
						obj.setUserCount(userForTagGrid.size());
				//		totalUserAcrossGlobeForTag = totalUserAcrossGlobeForTag + userForTagGrid.size();
						gridUserIdCountMap.put(lineStr[2], obj);
					}
				//	UserIdSet.add(lineStr[0]);
				}
			}
			if (k % 1000000 == 0) {
				System.out.println("Processed:" + k);
			}
		}
		TagGridUser totalUser = new TagGridUser();
	//	totalUser.setUserCount(totalUserAcrossGlobeForTag);
		Iterator it = gridUserIdCountMap.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pair = (Entry) it.next();
			TagGridUser tagGridVal= (TagGridUser) pair.getValue();
			totalUserAcrossGlobeForTag = totalUserAcrossGlobeForTag + tagGridVal.getUserCount();
			tagGridVal.setUsers(null);
		}
		totalUser.setUserCount(totalUserAcrossGlobeForTag);
		gridUserIdCountMap.put("===TOTAL===", totalUser);
		//gridUserIdCountMap.put("===TOTAL===", totalUserAcrossGlobeForTag);
		map.put(prevTag, gridUserIdCountMap);
		return map;
	}
	
	public static void generateUserIdTagFile() throws UnsupportedEncodingException {

		Path file = Paths.get("train-photo-video-yfcc.txt");
		Stream<String> lines = null;
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		PrintWriter gridWriter = null;
		try {
			gridWriter = new PrintWriter(new FileWriter("userid-tag-title-grid-1.0.txt", true));
			lines = Files.lines(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		Stopwords wnet = new Stopwords();
		int lineNo = 0;
		long numberofTrain = 0;
		for (String line : (Iterable<String>) lines::iterator) {
			String lineStr[] = line.split("\t");
			lineNo++;
			String longStr = lineStr[6];
			String latStr = lineStr[7];
			BigInteger gridNumber = new BigInteger("0");
			if (!longStr.isEmpty() && !latStr.isEmpty()) {
				gridNumber = GenerateGrid.getGridNumber(latStr, longStr, 1.0);
			}
			
			//String toWrite = lineArr[0] +"\t" + lineArr[1] +"\t" +  lineArr[2] +"\t" +
			//lineArr[3] +"\t" +  lineArr[8] +"\t" +  lineArr[10] +"\t" + 
			//lineArr[12] + "\t" + lineArr[13];
			boolean countFlag = false;
			String userTag = lineStr[5];
			String userId = lineStr[3];
			if (userId.isEmpty()) {
				System.out.println("UserId is empty:" + lineNo);
			}
			if(!userTag.isEmpty()){
				String tags[] = userTag.split(",");
				
				for(String tag: tags)
				{countFlag = true;
					String toWrite = userId + "\t" + tag + "\t" + gridNumber + "\t" + lineNo + "\n";
					gridWriter.write(toWrite);
					gridWriter.flush();
				}
			}

			String title = lineStr[4];
			String titleTags[] = title.split(Pattern.quote("+"));
			for (String tag : titleTags) {
				if (tag != null && !tag.isEmpty()) {
					String result = java.net.URLDecoder.decode(tag, "UTF-8");
					result = result.trim().toLowerCase();
					if (result.isEmpty()) {
						continue;
					}
					if (result.contains("href") || result.contains("http") || result.contains("https")
							|| result.contains(">") || result.contains("<")) {
						continue;
					}
					if (result.matches(".*\\d+.*")) {
						continue;
					}
					Matcher m = p.matcher(result);
					boolean b = m.find();
					if (b) {
						continue;
					}
					if (result.contains("-")) {
						String result1 = result.replaceAll("-", "");
						if (result1.isEmpty()) {
							continue;
						}
					}
					if (result.contains(".")) {
						result = result.replace(".", "");
					}
					if (result.contains(",")) {
						result = result.replace(",", "");
					}
					if (result.contains("\n")) {
						result = result.replace("\n", "");
					}
					if (wnet.isStopword(result)) {
						continue;
					}
					if (result.isEmpty()) {
						continue;
					}
					countFlag = true;
					String toWrite = userId + "\t" + result + "\t" + gridNumber + "\t" + lineNo +"\n";
					gridWriter.write(toWrite);
					gridWriter.flush();
				}
			}
			if (lineNo % 1000000 == 0) {
				System.out.println("Procesed:" + lineNo);
			}
			if(countFlag){
				numberofTrain++;
			}
		}
		System.out.println("Number of train:" + numberofTrain);
		gridWriter.close();
	}

}

class TagGridUser{
	public int userCount;
	public int photoCount;
	
	public Set<String> users;
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
