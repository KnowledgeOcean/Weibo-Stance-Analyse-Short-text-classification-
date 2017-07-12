package com.wangyl.lsa;

import java.io.IOException;
import java.util.ArrayList;

import com.wangyl.config.Config;
import com.wangyl.laplacianEigenmaps.LaplacianEigenmaps;
import com.wangyl.log.Log;
import com.wangyl.preprocesser.RawDataPreprocesser;
import com.wangyl.preprocesser.StructuredPreproedData;
import com.wangyl.svmAPI.svm_result;
import com.wangyl.tools.ResultAnalyser;


public class TestLSA {
	public static void main(String []args) throws Exception {
		//extractLsaFeturesForSVM(1);
		//svm_classify();
		runlsagvm(0.2,20,25);
	}
	public static void svm_classify(double c) {
		ArrayList<StructuredPreproedData> testData;
		try {
			testData = RawDataPreprocesser.LoadTestDataFromPreproedFile();
			for(int i=0;i<testData.size();i++) {
				StructuredPreproedData tmpData=testData.get(i);
				LsaSvmClassifier.classify(Config.lsaSvmFeature_train+tmpData.className+"_lsafeature.txt", Config.lsaSvmFeature_test+tmpData.className+"_lsafeature.txt", tmpData.className,c);
				
			}
			double f_avg=0;
			for(int i=0;i<testData.size();i++) {
				StructuredPreproedData tmpData=testData.get(i);
				ResultAnalyser rstAnalyser=ResultAnalyser.GetResult(svm_result.GetSVMResultFromFile(Config.lsaSvmResult+tmpData.className+"_result.txt", tmpData.className), tmpData.className);
				rstAnalyser.print();
				f_avg+=rstAnalyser.f_avg;
			}
			f_avg=f_avg/testData.size();
			System.out.printf("%s%.4f\n","total f_avg:",f_avg);
		} catch (Exception e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		
	}
	public static void extractLsaFeturesForSVM(int groupnum) {
		ArrayList<StructuredPreproedData> testData;
		try {
			testData = RawDataPreprocesser.LoadTestDataFromPreproedFile();
			ExtractFeatureBaseLsa.GenerateFeatureFile(groupnum, 0, true, Config.lsaSvmFeature_train, null, true);
			ExtractFeatureBaseLsa.GenerateFeatureFile(groupnum, 0, true, Config.lsaSvmFeature_test, testData, false);
		} catch (Exception e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		
	}
	public static void runlsasvm() throws Exception {
		
		
		
	}
	public static void runlsagvm(double p,int k,double len) {
		try {
			//Log.EndLog();
			//Config.isDebugMode=false;
			/*for(int k=7;k<=9;k++) {
				for(int j=20;j<=100;j+=30) {
					System.out.println("k,j="+k+" "+j+" "+LsaGroupVoteModel.Classify_fixedVoteNum(k,j));
				}
				//ѡȡ��789���ַ��飬20,50.80����ͶƱ����������9�����
			}*/
			for(int j=(int)(5+p*len/2);j<(int)(5+len-len*p/2);j++) {
				System.out.println(LsaGroupVoteModel.Classify_fixedVoteNum(j,k));
			}
		} catch (Exception e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		System.out.println("finish");
	}
}

//TODO Auto-generated method stub
		/*LsiLsa m = new LsiLsa(); 
		String string="���� ���� �� �� ���� �� ���� �� ���� ��Ȼ ���� ���� ���� ���� ���� ÿ�� �� ���� �� �� �� �¼� �Ծ� �� �� ���� ��Ⱦ ���� ���� ���� �˷� ��Ǯ ���� ���� �� ����  ";
		WordSegment.splitWordwithTag(string);
		string=WordSegment.TermsNameToString(" ");
		//m.querySimilarity("���� ���԰� ������ ʵ����ѧ 2002 �� 10 �� 12 �� �賿 ������ ʵ����ѧ ������ ���� ���� ���� ����");
		m.querySimilarity(string);*/
		//RawDataPreprocesser rawDataPreprocesser=new RawDataPreprocesser(Config.rawDataSrc+Config.testDataName, Config.preprocessedTestDataOutputSrc, Config.encodingType);
		//rawDataPreprocesser.PreprocessTestData();
		//LsaGroupVoteModel.Classify(17, 0.6);
		/*RawDataPreprocesser rawDataPreprocesser=new RawDataPreprocesser("", "", Config.encodingType);
		try {
			rawDataPreprocesser.PreproForLSA("IphoneSE");
		} catch (Exception e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}*/
		/*ExtractFeatureBaseLsa.GenerateFeatureFile(10, 10, true, Config.lsaSvmFeature_train, null,true);
		try {
			ExtractFeatureBaseLsa.GenerateFeatureFile(10, 10, true, Config.lsaSvmFeature_test, new RawDataPreprocesser("", "", Config.encodingType).LoadTestDataFromPreproedFile(),false);
		} catch (Exception e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		System.out.println("TestLSA.main()");*/


/*ArrayList<Integer> stanceIdArrayList=new ArrayList<Integer>();
double [][]features=LaplacianEigenmaps.LapEig(100, 15, LsaSvmClassifier.LoadFeatures(Config.lsaSvmFeature_train+"���ڽ�Ħ�޵�_lsafeature.txt",stanceIdArrayList));
LaplacianEigenmaps.SaveFeaturesForSVM(Config.lsaSvmFeature_train+"���ڽ�Ħ�޵�_lsalefeature.txt", features,stanceIdArrayList);

features=LaplacianEigenmaps.LapEig(100, 15, LsaSvmClassifier.LoadFeatures(Config.lsaSvmFeature_test+"���ڽ�Ħ�޵�_lsafeature.txt",stanceIdArrayList));
LaplacianEigenmaps.SaveFeaturesForSVM(Config.lsaSvmFeature_test+"���ڽ�Ħ�޵�_lsalefeature.txt", features,stanceIdArrayList);

LsaSvmClassifier.classify(Config.lsaSvmFeature_train+"���ڽ�Ħ�޵�_lsalefeature.txt", Config.lsaSvmFeature_test+"���ڽ�Ħ�޵�_lsalefeature.txt", "���ڽ�Ħ�޵�");
*/