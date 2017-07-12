package com.wangyl.config;

import com.wangyl.tools.IOapi;

public class Config {
	
	/**
	 * favor��ǩ��Ӧ���ַ���
	 */
	public static String favor="FAVOR";
	public static String infavor="AGAINST";
	public static String none="NONE";
	public static String unknown="UNKNOWN";
	public static int favor_int=0;
	public static int infavor_int=1;
	public static int none_int=2;
	public static int unknown_int=3;
	public static String encodingType="utf-8";
	/**
	 * Ԥ����ѵ���������·��
	 */
	public static String preprocessedTrainDataOutputSrc=IOapi.GetCurrentDir()+"\\generatedData\\preprocessedTraindata\\";
	/**
	 * ԭʼ����·��
	 */
	public static String rawDataDir=IOapi.GetCurrentDir()+"\\data\\";
	public static String labeledTrainDataName="evasampledata4-TaskAA.txt";
	public static String notLabeledTrainDataName="evasampledata4-TaskAR.txt";
	public static String testDataName="NLPCC_2016_Stance_Detection_Task_A_gold.txt";
	/**
	 * Ԥ������Լ������·��
	 */
	public static String preprocessedTestDataOutputSrc=IOapi.GetCurrentDir()+"\\generatedData\\preprocessedTestdata\\";
	/**
	 * debugģʽ����ʱ��errorlog����Ϣ��ͬʱ�ڿ���̨���
	 */
	public static boolean isDebugMode=true;
	/**
	 * lsa�������ϵ�·��
	 */
	public static String lsaCorpusDir=IOapi.GetCurrentDir()+"\\generatedData\\corpus\\";
	/**
	 * log�ļ���·��
	 */
	public static String logsrc=IOapi.GetCurrentDir()+"\\errorlog\\log.txt";
	/**
	 * ���ս����·��
	 */
	public static String finalResultSrc=IOapi.GetCurrentDir()+"\\generatedData\\finalResult.txt";
	/**
	 * Ӣ��ֹͣ�ʵ�·��
	 */
	public static String EnStopwordsSrc=IOapi.GetCurrentDir()+"\\models\\english_stopword.txt";
	/**
	 * ����ֹͣ�ʵ�·��
	 */
	public static String ChStopwordsSrcString=IOapi.GetCurrentDir()+"\\models\\chinese_stopwords.txt";
	public static String stopDict2Src=IOapi.GetCurrentDir()+"\\models\\stopDict.dic";
	/**
	 * �û��Զ���ֹͣ�ʵ�·��
	 */
	public static String UserStopwordSrc=IOapi.GetCurrentDir()+"\\models\\userstopwords.txt";
	/**
	 * ldaͳһ�����ļ�·��
	 */
	public static String LDAInputSrc=IOapi.GetCurrentDir()+"\\generatedData\\LDAInput\\ldainput.txt";
	/**
	 * lda������������ļ���·��
	 */
	public static String LDAInputDir=IOapi.GetCurrentDir()+"\\generatedData\\LDAInput\\";
	public static String LDAInputFileName="ldainput.txt";
	public static String lsaGroupVoteModelOutputSrc=IOapi.GetCurrentDir()+"\\generatedData\\classifier\\lsaGVM_ensemble\\";
	public static String ensemblelsaGVMOutputSrc=IOapi.GetCurrentDir()+"\\generatedData\\classifier\\ensemble\\lsagvm\\";
	public static String ldasvmModelDir=IOapi.GetCurrentDir()+"\\generatedData\\ldasvmmodel\\";
	public static String ldasvmPredictResultDir=IOapi.GetCurrentDir()+"\\generatedData\\ldasvmresult\\";
	public static String ldaSvmFeaturesDir_train=IOapi.GetCurrentDir()+"\\generatedData\\ldasvmfea\\traindata\\";
	public static String ldaSvmFeaturesDir_test=IOapi.GetCurrentDir()+"\\generatedData\\ldasvmfea\\testdata\\";
	public static String lsaSvmFeature_train=IOapi.GetCurrentDir()+"\\generatedData\\lsasvmfea\\traindata\\";
	public static String lsaSvmFeature_test=IOapi.GetCurrentDir()+"\\generatedData\\lsasvmfea\\testdata\\";
	public static String lsaSvmResult=IOapi.GetCurrentDir()+"\\generatedData\\lsasvmresult\\";
	public static String lsasvmModelDir=IOapi.GetCurrentDir()+"\\generatedData\\lsasvmmodel\\";
	
	public static String ldaGVMResultDir=IOapi.GetCurrentDir()+"\\generatedData\\ldaGVMResult\\";
	public static String ldaGVMCorpusDir=IOapi.GetCurrentDir()+"\\generatedData\\ldaGVMCorpus\\";
	public static String ldaGVMFeature_train=IOapi.GetCurrentDir()+"\\generatedData\\ldaGVMFeature\\traindata\\";
	public static String ldaGVMFeature_test=IOapi.GetCurrentDir()+"\\generatedData\\ldaGVMFeature\\testdata\\";
	public static String preproedTrainDataForLDAGVM=IOapi.GetCurrentDir()+"\\generatedData\\preproedTrainDataForLDAGVM\\";
	//��д�·����
	public static String sentimentLexiconPosSrc_Hownet=IOapi.GetCurrentDir()+"\\lexicon\\Hownet\\pos_opinion.txt";
	public static String sentimentLexiconPosSrc_Tsinghua=IOapi.GetCurrentDir()+"\\lexicon\\Tsinghua\\tsinghua.positive.gb.txt";
	public static String sentimentLexiconNegSrc_Hownet=IOapi.GetCurrentDir()+"\\lexicon\\Hownet\\neg_opinion.txt";
	public static String sentimentLexiconNegSrc_Tsinghua=IOapi.GetCurrentDir()+"\\lexicon\\Tsinghua\\tsinghua.negative.gb.txt";
	public static String senFeatureDir=IOapi.GetCurrentDir()+"\\generatedData\\senFeature\\";
	public static String senFeatureDir_train=IOapi.GetCurrentDir()+"\\generatedData\\senFeature\\train\\";
	public static String senFeatureDir_test=IOapi.GetCurrentDir()+"\\generatedData\\senFeature\\test\\";
	public static String senFeatureSvmModelDir=IOapi.GetCurrentDir()+"\\generatedData\\senFeatureSvmModel\\";
	public static String senFeaSvmResultDir=IOapi.GetCurrentDir()+"\\generatedData\\senFeaSvmResult\\";
	public static String []topics={"IphoneSE","����˹�������ǵķ����ж�","���Ŷ�̥","���ڷű���","���ڽ�Ħ�޵�"};
	//������֤
	public static String validationCorpusDir=IOapi.GetCurrentDir()+"\\generatedData\\validationCorpus\\";
}
