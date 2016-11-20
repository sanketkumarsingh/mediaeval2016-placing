package placing.entity;

public class Cell {

	String cellNumber;
	int numberOfTrainPhoto;
	int numberOfTestPhoto;
	int numberOfUniqueUserTagTrain;
	int totalNumberOfUserTagTrain;
	//int numberOfUniqueUserTagTest;
	int totalNumberOfUserTagTest;
	int numberOfTestPhotoMatchingAnyTrainTag;
	int numberOfCorrectPred;
	double averageProb;
	double averageDiffProb;
	
	public String getCellNumber() {
		return cellNumber;
	}
	public double getAverageProb() {
		return averageProb;
	}
	public void setAverageProb(double averageProb) {
		this.averageProb = averageProb;
	}
	public double getAverageDiffProb() {
		return averageDiffProb;
	}
	public void setAverageDiffProb(double averageDiffProb) {
		this.averageDiffProb = averageDiffProb;
	}
	public int getNumberOfCorrectPred() {
		return numberOfCorrectPred;
	}
	public void setNumberOfCorrectPred(int numberOfCorrectPred) {
		this.numberOfCorrectPred = numberOfCorrectPred;
	}
	public void setCellNumber(String cellNumber) {
		this.cellNumber = cellNumber;
	}
	public int getTotalNumberOfUserTagTrain() {
		return totalNumberOfUserTagTrain;
	}
	public void setTotalNumberOfUserTagTrain(int totalNumberOfUserTagTrain) {
		this.totalNumberOfUserTagTrain = totalNumberOfUserTagTrain;
	}
	public int getTotalNumberOfUserTagTest() {
		return totalNumberOfUserTagTest;
	}
	public void setTotalNumberOfUserTagTest(int totalNumberOfUserTagTest) {
		this.totalNumberOfUserTagTest = totalNumberOfUserTagTest;
	}
	//public void setCellNumber(int cellNumber) 
	public int getNumberOfTrainPhoto() {
		return numberOfTrainPhoto;
	}
	public void setNumberOfTrainPhoto(int numberOfTrainPhoto) {
		this.numberOfTrainPhoto = numberOfTrainPhoto;
	}
	public int getNumberOfTestPhoto() {
		return numberOfTestPhoto;
	}
	public void setNumberOfTestPhoto(int numberOfTestPhoto) {
		this.numberOfTestPhoto = numberOfTestPhoto;
	}
	public int getNumberOfUniqueUserTagTrain() {
		return numberOfUniqueUserTagTrain;
	}
	public void setNumberOfUniqueUserTagTrain(int numberOfUniqueUserTagTrain) {
		this.numberOfUniqueUserTagTrain = numberOfUniqueUserTagTrain;
	}
//	public int getNumberOfUniqueUserTagTest() {
//		return numberOfUniqueUserTagTest;
//	}
//	public void setNumberOfUniqueUserTagTest(int numberOfUniqueUserTagTest) {
//		this.numberOfUniqueUserTagTest = numberOfUniqueUserTagTest;
//	}
	public int getNumberOfTestPhotoMatchingAnyTrainTag() {
		return numberOfTestPhotoMatchingAnyTrainTag;
	}
	public void setNumberOfTestPhotoMatchingAnyTrainTag(int numberOfTestPhotoMatchingAnyTrainTag) {
		this.numberOfTestPhotoMatchingAnyTrainTag = numberOfTestPhotoMatchingAnyTrainTag;
	}
}
