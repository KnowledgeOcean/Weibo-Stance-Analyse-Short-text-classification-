package com.wangyl.lsa;

import java.util.ArrayList;
import java.util.HashMap;

import com.wangyl.config.Config;
import com.wangyl.log.Log;
import com.wangyl.preprocesser.RawDataPreprocesser;
import com.wangyl.preprocesser.StructuredPreproedData;
import com.wangyl.tools.FileOperateAPI;
import com.wangyl.tools.IOapi;

public class ExtractFeatureBaseLsa {
	/**
	 * 
	 * @param groupnum
	 * @param dims
	 * @param maxdims
	 * @param outputDir
	 * @param testDatas �ò���Ϊѵ������ʱ���˲�����Ϊnull�����ɵ���svmѵ�����ݣ�Ϊ��������ʱ�����ɵ���svm�������ݣ���������Ϊarraylist<StructuredPreproedData>
	 * @param isTrainFeature �Ƿ�Ϊѵ������
	 * @param 
	 */
	public static void GenerateFeatureFile(int groupnum,int dims,boolean maxdims,String outputDir,ArrayList<StructuredPreproedData> testDatas,boolean isTrainFeature) {
		try {
			HashMap<String, Integer> topicIdMapInTrainData=new HashMap<String, Integer>();
			ArrayList<StructuredPreproedData> traindata=RawDataPreprocesser.LoadTrainDataFromPreproedFile(topicIdMapInTrainData);
			if(isTrainFeature) {
				testDatas=traindata;
			}
			IOapi tmpIO=new IOapi(1);
			for(int i=0;i<testDatas.size();i++) {//һ��ѭ������һ�ֻ���
				
				StructuredPreproedData preTestData=testDatas.get(i);
				LsiLsa lsa=new LsiLsa();
				if(topicIdMapInTrainData.containsKey(preTestData.className)) {
					int index=topicIdMapInTrainData.get(preTestData.className).intValue();
					StructuredPreproedData preTrainData=traindata.get(index);
					//���ѵ�����Ƿ����ȫ�����ࣺ
					if(preTrainData.favor.size()==0||preTrainData.infavor.size()==0||preTrainData.none.size()==0) {
						try {
							Log.LogInf("error:ѵ����δ����ȫ�����࣡");
						} catch (Exception e) {
							// TODO �Զ����ɵ� catch ��
							e.printStackTrace();
						}
						continue;
					}
					tmpIO.startWrite(outputDir+preTestData.className+"_lsafeature.txt", Config.encodingType, 0);
					//׼����preTestData.className��Ӧ��lsaѵ�����ݣ�corpus�ļ����£�
					RawDataPreprocesser.PreproForLSA(preTestData.className, groupnum);
					//����ά����
					if(maxdims) {
						LsiLsa.LSD=FileOperateAPI.HowManyFileInDir(Config.lsaCorpusDir);
					}
					else {
						LsiLsa.LSD=Math.min(dims, FileOperateAPI.HowManyFileInDir(Config.lsaCorpusDir));
					}
					//����lsa״̬��
					lsa.Reset();
					//Ϊtestdata��favor��������������
					if(preTestData.favor.size()>0) {
						LsaResultAnalyser[][]rstAnalysers=lsa.GetFeatureDq_groupdata(preTestData.favor,false);
						for(int k=0;k<rstAnalysers.length;k++) {
							tmpIO.writeOneString(LsaResultToString(rstAnalysers[k], Config.favor)+"\n", 0);
						}
					}
					//Ϊtestdata��infavor��������������
					if(preTestData.infavor.size()>0) {
						LsaResultAnalyser[][]rstAnalysers=lsa.GetFeatureDq_groupdata(preTestData.infavor,false);
						//������д��feature�ļ���:
						for(int k=0;k<rstAnalysers.length;k++) {
							tmpIO.writeOneString(LsaResultToString(rstAnalysers[k], Config.infavor)+"\n", 0);
						}
					}
					//Ϊtestdata��none��������������
					if(preTestData.none.size()>0) {
						LsaResultAnalyser[][]rstAnalysers=lsa.GetFeatureDq_groupdata(preTestData.none,false);
						for(int k=0;k<rstAnalysers.length;k++) {
							tmpIO.writeOneString(LsaResultToString(rstAnalysers[k], Config.none)+"\n", 0);
						}
					}
					tmpIO.endWrite(0);
				}
				else {
					try {
						Log.LogInf("error:ѵ������δ���ֵ����:"+preTestData.className);
					} catch (Exception e) {
						// TODO �Զ����ɵ� catch ��
						e.printStackTrace();
					}
				}
			}
		}
		catch (Exception e1) {
			// TODO �Զ����ɵ� catch ��
			e1.printStackTrace();
		}
	}
	private static String LsaResultToString(LsaResultAnalyser[] lra,String stance) throws Exception {
		StringBuffer strBuf=new StringBuffer("");
		if(stance.equals(Config.favor)) {
			strBuf.append("0");
		}
		else if(stance.equals(Config.infavor)) {
			strBuf.append("1");
		}
		else if(stance.equals(Config.none)) {
			strBuf.append("2");
		}
		else {
			throw new Exception("�����������ǩ��");
		}
		for(int i=0;i<lra.length;i++) {
			//lra[i].ConfirmStanceAndTopic();
			strBuf.append(" "+(i+1)+":"+lra[i].getSimilarity());
		}
		return strBuf.toString();
	}
}
