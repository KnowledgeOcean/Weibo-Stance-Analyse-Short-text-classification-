package com.wangyl.lsa;

import java.util.ArrayList;
import java.util.HashMap;

import com.wangyl.config.Config;
import com.wangyl.log.Log;
import com.wangyl.preprocesser.RawDataPreprocesser;
import com.wangyl.preprocesser.StructuredPreproedData;
import com.wangyl.tools.FileOperateAPI;
import com.wangyl.tools.IOapi;

public class LsaGroupVoteModel {
	
	public static double Classify(int groupnum,int dims,double voteproportion,boolean maxdims,boolean fixedVoteNum,double vote_num) {
		try {
			HashMap<String, Integer> topicIdMapInTrainData=new HashMap<String, Integer>();
			ArrayList<StructuredPreproedData> traindata=RawDataPreprocesser.LoadTrainDataFromPreproedFile(topicIdMapInTrainData);
			ArrayList<StructuredPreproedData> testDatas = RawDataPreprocesser.LoadTestDataFromPreproedFile();
			double all_f_avg=0;
			IOapi tmpIO=new IOapi(1);
			double voteNum=vote_num;
			if(fixedVoteNum==false) {
				voteNum=(groupnum*voteproportion);
			}
			for(int i=0;i<testDatas.size();i++) {//һ��ѭ������һ�ֻ���
				double rightNum=0;
				double rightFavorNum=0;
				double rightInfavorNum=0;
				double preInfavorNum=0;
				double preFavorNum=0;
				double p_favor=0;
				double p_infavor=0;
				double r_favor=0;
				double r_infavor=0;
				double f_infavor=0;
				double f_favor=0;
				double f_avg=0;
				StructuredPreproedData preTestData=testDatas.get(i);
				LsiLsa lsa=new LsiLsa();
				if(topicIdMapInTrainData.containsKey(preTestData.className)) {
					int index=topicIdMapInTrainData.get(preTestData.className).intValue();
					StructuredPreproedData preTrainData=traindata.get(index);
					//���ѵ�������ݣ�
					if(preTrainData.favor.size()==0||preTrainData.infavor.size()==0||preTrainData.none.size()==0) {
						try {
							Log.LogInf("error:ѵ����δ����ȫ�����࣡");
						} catch (Exception e) {
							// TODO �Զ����ɵ� catch ��
							e.printStackTrace();
						}
						continue;
					}
					//��Ҫд����ļ���
					tmpIO.startWrite(Config.lsaGroupVoteModelOutputSrc+preTestData.className+"_"+groupnum+"_"+(int)(voteNum)+".txt", Config.encodingType, 0);
					//ΪLSA������Ԥ����
					RawDataPreprocesser.PreproForLSA(preTestData.className, groupnum);
					//����ά����
					if(maxdims) {
						LsiLsa.LSD=FileOperateAPI.HowManyFileInDir(Config.lsaCorpusDir);
					}
					else {
						LsiLsa.LSD=Math.min(dims, FileOperateAPI.HowManyFileInDir(Config.lsaCorpusDir));
					}
					//lsa״̬��ʼ�������ã���
					lsa.Reset();
					//������Լ���favor���ֵ����ݣ����ࣩ��
					if(preTestData.favor.size()>0) {
						LsaResultAnalyser[][]rstAnalysers=lsa.getQuerysSimilarity(preTestData.favor);
						for(int x=0;x<rstAnalysers.length;x++) {
							String predictStance=LsaResultAnalyser.VoteForMostSimilarityEntity(rstAnalysers[x],voteNum);
							if(predictStance.equals(Config.favor)) {
								rightNum++;
								preFavorNum++;
								rightFavorNum++;
							}
							else {
								if(predictStance.equals(Config.infavor)) {
									preInfavorNum++;
								}
							}
							tmpIO.writeOneString(preTestData.favor.get(x)+"\t"+predictStance+"\t"+Config.favor+"\n", 0);
						}
					}
					//������Լ���infavor���ֵ����ݣ����ࣩ��
					if(preTestData.infavor.size()>0) {
						LsaResultAnalyser[][]rstAnalysers=lsa.getQuerysSimilarity(preTestData.infavor);
						for(int x=0;x<rstAnalysers.length;x++) {
							String predictStance=LsaResultAnalyser.VoteForMostSimilarityEntity(rstAnalysers[x],voteNum);
							if(predictStance.equals(Config.infavor)) {
								rightNum++;
								preInfavorNum++;
								rightInfavorNum++;
							}
							else {
								if(predictStance.equals(Config.favor)) {
									preFavorNum++;
								}
								//Log.LogInf("���⣺"+preTestData.className+"\t"+"���ݣ�"+preTestData.infavor.get(x)+"\t"+"Ԥ�⣺"+predictStance+"\t"+"�𰸣�"+Config.infavor);
							}
							tmpIO.writeOneString(preTestData.infavor.get(x)+"\t"+predictStance+"\t"+Config.infavor+"\n", 0);
						}
					}
					//������Լ���none���ֵ����ݣ����ࣩ��
					if(preTestData.none.size()>0) {
						LsaResultAnalyser[][]rstAnalysers=lsa.getQuerysSimilarity(preTestData.none);
						for(int x=0;x<rstAnalysers.length;x++) {
							String predictStance=LsaResultAnalyser.VoteForMostSimilarityEntity(rstAnalysers[x],voteNum);
							if(predictStance.equals(Config.none)) {
								rightNum++;
							}
							else {
								if(predictStance.equals(Config.infavor)) {
									preInfavorNum++;
								}
								if(predictStance.equals(Config.favor)) {
									preFavorNum++;
								}
							}
							tmpIO.writeOneString(preTestData.none.get(x)+"\t"+predictStance+"\t"+Config.none+"\n", 0);
						}
					}
					//д����ϣ��ر�output�ļ����ͷ���Դ
					tmpIO.endWrite(0);
					//�����������ָ�꣺
					double totalNum=preTestData.infavor.size()+preTestData.favor.size()+preTestData.none.size();
					Log.LogInf(preTestData.className+" ׼ȷ��:"+rightNum/totalNum);
					p_infavor=rightInfavorNum/preInfavorNum;
					r_infavor=rightInfavorNum/preTestData.infavor.size();
					f_infavor=2*p_infavor*r_infavor/(p_infavor+r_infavor);
					p_favor=rightFavorNum/preFavorNum;
					r_favor=rightFavorNum/preTestData.favor.size();
					f_favor=2*p_favor*r_favor/(p_favor+r_favor);
					f_avg=(f_favor+f_infavor)/2;
					//���Fֵ��
					Log.LogInf("F-score:"+f_avg);
					all_f_avg+=f_avg;
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
			//��������topic��Fֵ��ֵ��
			all_f_avg=all_f_avg/testDatas.size();
			Log.LogInf("all f avg:"+all_f_avg);
			return all_f_avg;
		} 
		catch (Exception e1) {
			// TODO �Զ����ɵ� catch ��
			e1.printStackTrace();
			return Double.NEGATIVE_INFINITY;
		}
	}
	public static double Classify(int groupnum,int dims,double voteproportion) {
		return Classify(groupnum, dims, voteproportion, false,false,0);
	}
	public static double Classify(int groupnum,double voteproportion) {
		return Classify(groupnum, 3, voteproportion, true,false,0);
	}
	public static double Classify_fixedVoteNum(int groupnum,int dims,double votenum) {
		return Classify(groupnum, dims, 0, false, true, votenum);
	}
	public static double Classify_fixedVoteNum(int groupnum,double votenum) {
		return Classify(groupnum,0,0,true,true,votenum);
	}
}
