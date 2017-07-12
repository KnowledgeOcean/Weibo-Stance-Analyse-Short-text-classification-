package com.wangyl.lda;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.kohsuke.args4j.spi.Setter;

import com.wangyl.config.Config;
import com.wangyl.preprocesser.RawDataPreprocesser;
import com.wangyl.preprocesser.StructuredPreproedData;
import com.wangyl.tools.FileOperateAPI;
import com.wangyl.tools.IOapi;
class topic {
	public int topicId;
	public HashMap<String, Double> pword;
	public topic(int id) {
		this.topicId=id;
		pword=new HashMap<String, Double>();
	}
}
class LDAModel {
	private String modelName;
	public ArrayList<topic> topics=new ArrayList<topic>();
	public LDAModel(String name) {
		this.setModelName(name);
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	
}
public class ExtractFeatureBaseLda {
	private static LDAModel model;
	private static double []topicsScoreFeature;
	private static void LoadLDAModel(String modelSrc) {
		model=new LDAModel(modelSrc);
		IOapi tmpIO=new IOapi(1);
		tmpIO.startRead(modelSrc, Config.encodingType, 0);
		String line=tmpIO.readOneSentence(0);
		topic topic_tmp;
		while(line!=null) {
			if(line.matches("Topic [0-9]+th:")) {
				model.topics.add(new topic(model.topics.size()));
			}
			else {
				String []strs=line.replace("\t", "").split(" ");
				if(strs.length!=2) {
					try {
						throw new Exception("Wrong Format of LDA model");
					} catch (Exception e) {
						// TODO �Զ����ɵ� catch ��
						e.printStackTrace();
					}
				}
				else {
					topic_tmp=model.topics.get(model.topics.size()-1);
					topic_tmp.pword.put(strs[0], Double.parseDouble(strs[1]));
				}
			}
			line=tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
	}
	/**
	 * 
	 * @param inputSrc -processedTrainData �ļ�����ʽ��target_stance.txt
	 * @param outputSrc
	 */
	public static void GenerateFeatureFile(String inputDir,String target,String outputDir) {
		topicsScoreFeature=new double[model.topics.size()];
		for(int i=0;i<topicsScoreFeature.length;i++) {
			topicsScoreFeature[i]=0;
		}
		IOapi tmpIO=new IOapi(1);
		String line;
		String featureStr;
		tmpIO.startWrite(outputDir+target+"_features.txt", Config.encodingType, 0);
		//��favor:
		tmpIO.startRead(inputDir+target+"_"+Config.favor+".txt", Config.encodingType, 0);
		line=tmpIO.readOneSentence(0);
		while(line!=null) {
			String []words=line.split(" ");
			for(String word: words) {
				for(int i=0;i<topicsScoreFeature.length;i++) {
					if(model.topics.get(i).pword.containsKey(word)) {
						//topicsScoreFeature[i]+=model.topics.get(i).pword.get(word);
						topicsScoreFeature[i]++;
					}
				}
			}
			ArrayVectorNormalization(topicsScoreFeature);
			//save features:
			//feature �ļ���ʽ��<label> <index1>:<value1> <index2>:<value2> ...
			//favor=0,infavor=1,none=2
			featureStr="0";
			for(int i=0;i<topicsScoreFeature.length;i++) {
				featureStr=featureStr+" "+(i+1)+":"+topicsScoreFeature[i];
			}
			tmpIO.writeOneString(featureStr+"\n", 0);
			line=tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
		//��infavor��
		tmpIO.startRead(inputDir+target+"_"+Config.infavor+".txt", Config.encodingType, 0);
		line=tmpIO.readOneSentence(0);
		while(line!=null) {
			String []words=line.split(" ");
			for(String word: words) {
				for(int i=0;i<topicsScoreFeature.length;i++) {
					if(model.topics.get(i).pword.containsKey(word)) {
						//topicsScoreFeature[i]+=model.topics.get(i).pword.get(word);
						topicsScoreFeature[i]++;
					}
				}
			}
			ArrayVectorNormalization(topicsScoreFeature);
			//save features:
			//feature �ļ���ʽ��<label> <index1>:<value1> <index2>:<value2> ...
			//favor=0,infavor=1,none=2
			featureStr="1";
			for(int i=0;i<topicsScoreFeature.length;i++) {
				featureStr=featureStr+" "+(i+1)+":"+topicsScoreFeature[i];
			}
			tmpIO.writeOneString(featureStr+"\n", 0);
			line=tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
		//��none��
		tmpIO.startRead(inputDir+target+"_"+Config.none+".txt", Config.encodingType, 0);
		line=tmpIO.readOneSentence(0);
		while(line!=null) {
			String []words=line.split(" ");
			for(String word: words) {
				for(int i=0;i<topicsScoreFeature.length;i++) {
					if(model.topics.get(i).pword.containsKey(word)) {
						//topicsScoreFeature[i]+=model.topics.get(i).pword.get(word);
						topicsScoreFeature[i]++;
					}
				}
			}
			ArrayVectorNormalization(topicsScoreFeature);
			//save features:
			//feature �ļ���ʽ��<label> <index1>:<value1> <index2>:<value2> ...
			//favor=0,infavor=1,none=2
			featureStr="2";
			for(int i=0;i<topicsScoreFeature.length;i++) {
				featureStr=featureStr+" "+(i+1)+":"+topicsScoreFeature[i];
			}
			tmpIO.writeOneString(featureStr+"\n", 0);
			line=tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
		tmpIO.endWrite(0);
	}
	private static void ArrayVectorNormalization(double []vec) {
		double m=0;
		for(int i=0;i<vec.length;i++) {
			m+=vec[i]*vec[i];
		}
		m=Math.sqrt(m);
		for(int i=0;i<vec.length;i++) {
			vec[i]=vec[i]/m;
		}
	}
	/**
	 * @description �����������jgibblda��lda�ƶϽ�����������ļ��������ļ���ʽת����.׷��д
	 * @param inputSrc
	 * @param outputSrc
	 * @param stancelabel
	 */
	private static void TransferInferOutputToFetureFile(String inputSrc,String outputSrc,int stancelabel) {
		IOapi tmpIO=new IOapi(1);
		tmpIO.startRead(inputSrc, Config.encodingType, 0);
		tmpIO.startWrite(outputSrc, Config.encodingType, 0,true);//׷��д
		String line=tmpIO.readOneSentence(0);
		StringBuffer strbuf=new StringBuffer();
		while(line!=null) {
			String []strs=line.split(" ");
			strbuf.setLength(0);//���stringbuffer
			strbuf.append(stancelabel);
			for(int i=0;i<strs.length;i++) {
				strbuf.append(" "+(i+1)+":"+String.valueOf((Double.parseDouble(strs[i]))*10));
			}
			tmpIO.writeOneString(strbuf.toString()+"\n", 0);
			line=tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
		tmpIO.endWrite(0);
	}
	private static void AddCounterTagForTXT(String src) {
		IOapi tmpIO=new IOapi(1);
		StringBuffer stringBuffer=new StringBuffer();
		tmpIO.startRead(src, Config.encodingType, 0);
		String line=tmpIO.readOneSentence(0);
		int counter=0;
		while(line!=null) {
			counter++;
			stringBuffer.append(line+"\n");
			line=tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
		tmpIO.startWrite(src, Config.encodingType, 0);
		tmpIO.writeOneString(counter+"\n", 0);
		tmpIO.writeOneString(stringBuffer.toString(), 0);
		tmpIO.endWrite(0);
	}
	/**
	 * 
	 * @param trainFeaDir ָ��ѵ���������ļ�·����ѵ�������������ڴ�·����
	 * @param testFeaDir  ָ�����Լ������ļ�·�������Լ����������ڴ�·����
	 * @param preproedTraindataDir ָ����������������Ԥ������ѵ����·��
	 * @param preproedTestDataDir  ָ����������������Ԥ�����Ĳ��Լ�·��
	 * @param originLDAModelDir    ָ��LDAģ�͵�·����For each Target��ģ�ͣ�
	 * @return ���ز������ݣ�����structuredpreproeddata
	 * @throws Exception
	 */
	public static ArrayList<StructuredPreproedData> ExtractLDAFeatures(String trainFeaDir,String testFeaDir,String preproedTraindataDir,String preproedTestDataDir,String originLDAModelDir,int n) throws Exception {
		ArrayList<File> filelist=new ArrayList<File>();
		FileOperateAPI.visitDirsAllFiles(preproedTestDataDir, filelist);
		for(File file:filelist) {
			FileOperateAPI.CopyFile(file.getPath(), testFeaDir);
		}
		filelist.clear();
		FileOperateAPI.visitDirsAllFiles(preproedTraindataDir, filelist);
		for(File file:filelist) {
			FileOperateAPI.CopyFile(file.getPath(), trainFeaDir);
		}
		filelist.clear();
		HashMap<String,Integer> topicidmap=new HashMap<String, Integer>();
		ArrayList<StructuredPreproedData> testdata=RawDataPreprocesser.LoadTestDataFromPreproedFile(topicidmap);
		for(int i=0;i<testdata.size();i++) {
			
			StructuredPreproedData tmpdata=testdata.get(i);
			//��ģ���ƶ�����Ӧ�ļ���
			FileOperateAPI.CopyFile(originLDAModelDir+tmpdata.className+"\\model-final.others", testFeaDir);
			FileOperateAPI.CopyFile(originLDAModelDir+tmpdata.className+"\\model-final.phi", testFeaDir);
			FileOperateAPI.CopyFile(originLDAModelDir+tmpdata.className+"\\model-final.tassign", testFeaDir);
			FileOperateAPI.CopyFile(originLDAModelDir+tmpdata.className+"\\model-final.theta", testFeaDir);
			FileOperateAPI.CopyFile(originLDAModelDir+tmpdata.className+"\\model-final.twords", testFeaDir);
			FileOperateAPI.CopyFile(originLDAModelDir+tmpdata.className+"\\wordmap.txt", testFeaDir);
			//�淶���ƶ������ļ�
			AddCounterTagForTXT(testFeaDir+tmpdata.className+"_"+Config.favor+".txt");
			AddCounterTagForTXT(testFeaDir+tmpdata.className+"_"+Config.infavor+".txt");
			AddCounterTagForTXT(testFeaDir+tmpdata.className+"_"+Config.none+".txt");
			LDA_api.Infer(testFeaDir, tmpdata.className+"_"+Config.favor+".txt",n);
			//��ɾ��ԭ�е������ļ�����Ϊ����д�����ļ��Ƿֶ�׷��д��
			FileOperateAPI.DeleteFolder(testFeaDir+tmpdata.className+"_feature.txt");
			//д��favor���֣�
			TransferInferOutputToFetureFile(testFeaDir+tmpdata.className+"_"+Config.favor+".txt.model-final.theta", testFeaDir+tmpdata.className+"_feature.txt", 0);
			LDA_api.Infer(testFeaDir, tmpdata.className+"_"+Config.infavor+".txt",n);
			//д��infavor����
			TransferInferOutputToFetureFile(testFeaDir+tmpdata.className+"_"+Config.infavor+".txt.model-final.theta", testFeaDir+tmpdata.className+"_feature.txt", 1);
			LDA_api.Infer(testFeaDir, tmpdata.className+"_"+Config.none+".txt",n);
			//д��none���֣�
			TransferInferOutputToFetureFile(testFeaDir+tmpdata.className+"_"+Config.none+".txt.model-final.theta", testFeaDir+tmpdata.className+"_feature.txt", 2);
			//ɾ��ģ���ļ�
			FileOperateAPI.DeleteFolder(testFeaDir+tmpdata.className+"\\model-final.others");
			FileOperateAPI.DeleteFolder(testFeaDir+tmpdata.className+"\\model-final.phi");
			FileOperateAPI.DeleteFolder(testFeaDir+tmpdata.className+"\\model-final.tassign");
			FileOperateAPI.DeleteFolder(testFeaDir+tmpdata.className+"\\model-final.theta");
			FileOperateAPI.DeleteFolder(testFeaDir+tmpdata.className+"\\model-final.twords");
			FileOperateAPI.DeleteFolder(testFeaDir+tmpdata.className+"\\wordmap.txt");
		}
		//ʹ��lda��ѵ���������ƶ�
		ArrayList<StructuredPreproedData> traindata=RawDataPreprocesser.LoadTrainDataFromPreproedFile();
		for(int i=0;i<traindata.size();i++) {
			StructuredPreproedData tmpdata=traindata.get(i);
			//��ģ���ƶ�����Ӧ�ļ���
			FileOperateAPI.CopyFile(originLDAModelDir+tmpdata.className+"\\model-final.others", trainFeaDir);
			FileOperateAPI.CopyFile(originLDAModelDir+tmpdata.className+"\\model-final.phi", trainFeaDir);
			FileOperateAPI.CopyFile(originLDAModelDir+tmpdata.className+"\\model-final.tassign", trainFeaDir);
			FileOperateAPI.CopyFile(originLDAModelDir+tmpdata.className+"\\model-final.theta", trainFeaDir);
			FileOperateAPI.CopyFile(originLDAModelDir+tmpdata.className+"\\model-final.twords", trainFeaDir);
			FileOperateAPI.CopyFile(originLDAModelDir+tmpdata.className+"\\wordmap.txt", trainFeaDir);
			//�淶���ƶ������ļ�
			AddCounterTagForTXT(trainFeaDir+tmpdata.className+"_"+Config.favor+".txt");
			AddCounterTagForTXT(trainFeaDir+tmpdata.className+"_"+Config.infavor+".txt");
			AddCounterTagForTXT(trainFeaDir+tmpdata.className+"_"+Config.none+".txt");
			FileOperateAPI.DeleteFolder(trainFeaDir+tmpdata.className+"_feature.txt");
			LDA_api.Infer(trainFeaDir, tmpdata.className+"_"+Config.favor+".txt",n);
			TransferInferOutputToFetureFile(trainFeaDir+tmpdata.className+"_"+Config.favor+".txt.model-final.theta", trainFeaDir+tmpdata.className+"_feature.txt", 0);
			LDA_api.Infer(trainFeaDir, tmpdata.className+"_"+Config.infavor+".txt",n);
			TransferInferOutputToFetureFile(trainFeaDir+tmpdata.className+"_"+Config.infavor+".txt.model-final.theta", trainFeaDir+tmpdata.className+"_feature.txt", 1);
			LDA_api.Infer(trainFeaDir, tmpdata.className+"_"+Config.none+".txt",n);
			TransferInferOutputToFetureFile(trainFeaDir+tmpdata.className+"_"+Config.none+".txt.model-final.theta", trainFeaDir+tmpdata.className+"_feature.txt", 2);
			//ɾ��ģ���ļ�
			FileOperateAPI.DeleteFolder(trainFeaDir+tmpdata.className+"\\model-final.others");
			FileOperateAPI.DeleteFolder(trainFeaDir+tmpdata.className+"\\model-final.phi");
			FileOperateAPI.DeleteFolder(trainFeaDir+tmpdata.className+"\\model-final.tassign");
			FileOperateAPI.DeleteFolder(trainFeaDir+tmpdata.className+"\\model-final.theta");
			FileOperateAPI.DeleteFolder(trainFeaDir+tmpdata.className+"\\model-final.twords");
			FileOperateAPI.DeleteFolder(trainFeaDir+tmpdata.className+"\\wordmap.txt");
		}
		return testdata;
	}
}
