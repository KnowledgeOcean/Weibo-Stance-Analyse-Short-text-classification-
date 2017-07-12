package com.wangyl.preprocesser;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.wangyl.config.Config;
import com.wangyl.log.Log;
import com.wangyl.tools.FileOperateAPI;
import com.wangyl.tools.IOapi;
import com.wangyl.tools.StopWordsFilter;
import com.wangyl.tools.StringAnalyzer;
import com.wangyl.tools.WordSegment;
import com.wangyl.tools.WordSegment_Ansj;

import TypeTrans.Full2Half;

/**
 * show 
 * @author WangYunli
 * 
 */ 
public class RawDataPreprocesser {
	private static String rawDataSrc;//ԭʼ����·��
	private static String outputSrc;//Ԥ�������ļ����·��
	private static String encodingType;//�ļ��ı��뷽ʽ
	private HashMap<String, Integer> topicIdMap=null;
	private static IOapi myIO=new IOapi(1);
	public static ArrayList<String> GetTopicsFromFiles(ArrayList<File> files,HashMap<String, Integer> rst) {
		rst.clear();
		ArrayList<String> arr=new ArrayList<String>();
		int id=0;
		for(File file:files) {
			if(rst.containsKey(GetClassNameFromFileName(file.getName()))) {
				continue;
			}
			else {
				rst.put(GetClassNameFromFileName(file.getName()), id);
				arr.add(GetClassNameFromFileName(file.getName()));
				id++;
			}
		}
		return arr;
	}
	public static String GetClassNameFromFileName(String filename) {
		return filename.split("_")[0];
	}
	public static String GetStanceFromFileName(String filename) {
		String []strs=filename.split("_");
		String []strs2=strs[1].split(".txt");
		return strs2[0];
	}
	public static String GetClassNameFromFileName(File file) {
		return GetClassNameFromFileName(file.getName());
	}
	public static String GetStanceFromFileName(File file) {
		return GetStanceFromFileName(file.getName());
	}
	public RawDataPreprocesser(String in_src,String out_src,String encodingString) {
		RawDataPreprocesser.rawDataSrc=in_src;
		RawDataPreprocesser.outputSrc=out_src;
		RawDataPreprocesser.encodingType=encodingString;
	}
	public void setOutputSrc(String outputSrc) {
		RawDataPreprocesser.outputSrc = outputSrc;
	}
	public void setRawDataSrc(String rawDataSrc) {
		RawDataPreprocesser.rawDataSrc = rawDataSrc;
	}
	public String getOutputSrc() {
		return outputSrc;
	}
	public String getRawDataSrc() {
		return rawDataSrc;
	}
	public ArrayList<StructuredPreproedData> GenerateValidationCorpus(String trainsrc,String testsrc,String outdir) {
		IOapi tmpIO=new IOapi(1);
		tmpIO.startRead(trainsrc, encodingType, 0);
		String line=tmpIO.readOneSentence(0);//Ҫ���һ�п�ʼ������ʽ����
		String []splitStrings=null;
		HashMap<String, Integer> classMap=new HashMap<String, Integer>();
		ArrayList<StructuredPreproedData> preproedatas=new ArrayList<StructuredPreproedData>();
		//����ѵ�����ݣ�
		while(line!=null) {
			splitStrings=line.split("\t");
			if(splitStrings.length==4) {
				String rst=PreprocessOneLine(splitStrings[2], splitStrings[1]);
				String tmptopic=splitStrings[1].replace("iPhone SE", "IphoneSE").replace("����˹�����Ƿ����ж�", "����˹�������ǵķ����ж�");
				if(!classMap.containsKey(tmptopic)) {//����÷���û���ֹ�
					classMap.put(splitStrings[1], preproedatas.size());
					StructuredPreproedData tmpPreproedData=new StructuredPreproedData(true);
					tmpPreproedData.className=splitStrings[1].replace("iPhone SE", "IphoneSE").replace("����˹�����Ƿ����ж�", "����˹�������ǵķ����ж�");
					try {
						tmpPreproedData.AddData(rst, splitStrings[3]);
						tmpPreproedData.AddOrgData(splitStrings[2], splitStrings[3]);
					} catch (Exception e) {
						// TODO �Զ����ɵ� catch ��
						e.printStackTrace();
					}
					preproedatas.add(tmpPreproedData);
				}
				else {
					try {
						preproedatas.get(classMap.get(tmptopic).intValue()).AddData(rst, splitStrings[3]);
						preproedatas.get(classMap.get(tmptopic).intValue()).AddOrgData(splitStrings[2], splitStrings[3]);
					} catch (Exception e) {
						// TODO �Զ����ɵ� catch ��
						e.printStackTrace();
					}
				}
			}
			else if(splitStrings.length==3) {//û������ǩ����Ϊ��UNKNOWN
				String rst=PreprocessOneLine(splitStrings[2], splitStrings[1]);
				String tmptopic=splitStrings[1].replace("iPhone SE", "IphoneSE").replace("����˹�����Ƿ����ж�", "����˹�������ǵķ����ж�");
				if(!classMap.containsKey(tmptopic)) {//����÷���û���ֹ�
					classMap.put(splitStrings[1], preproedatas.size());
					StructuredPreproedData tmpPreproedData=new StructuredPreproedData(true);
					tmpPreproedData.className=splitStrings[1].replace("iPhone SE", "IphoneSE").replace("����˹�����Ƿ����ж�", "����˹�������ǵķ����ж�");
					try {
						tmpPreproedData.AddData(rst, Config.unknown);
						tmpPreproedData.AddOrgData(splitStrings[2], Config.unknown);
					} catch (Exception e) {
						// TODO �Զ����ɵ� catch ��
						e.printStackTrace();
					}
					preproedatas.add(tmpPreproedData);
				}
				else {
					try {
						preproedatas.get(classMap.get(tmptopic).intValue()).AddData(rst, Config.unknown);
						preproedatas.get(classMap.get(tmptopic).intValue()).AddOrgData(splitStrings[2], Config.unknown);
					} catch (Exception e) {
						// TODO �Զ����ɵ� catch ��
						e.printStackTrace();
					}
				}
			}
			else {//����error��˵��΢���ı���Ҳ������tab
				Log.LogInf("error 10002:�ָ�ȴ���"+splitStrings.length+";ԭ��Ϊ��"+line);
			}
			line=tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
		//���ز������ݣ�
		tmpIO.startRead(testsrc, encodingType, 0);
		line=tmpIO.readOneSentence(0);
		while(line!=null) {
			splitStrings=line.split("	");
			if(splitStrings.length==4) {
				String rst=PreprocessOneLine(splitStrings[2], splitStrings[1]);
				String tmptopic=splitStrings[1].replace("iPhone SE", "IphoneSE").replace("����˹�����Ƿ����ж�", "����˹�������ǵķ����ж�");
				if(!classMap.containsKey(tmptopic)) {//����÷���û���ֹ�
					classMap.put(splitStrings[1], preproedatas.size());
					StructuredPreproedData tmpPreproedData=new StructuredPreproedData(true);
					tmpPreproedData.className=splitStrings[1].replace("iPhone SE", "IphoneSE").replace("����˹�����Ƿ����ж�", "����˹�������ǵķ����ж�");
					try {
						tmpPreproedData.AddData(rst, splitStrings[3]);
						tmpPreproedData.AddOrgData(splitStrings[2], splitStrings[3]);
					} catch (Exception e) {
						// TODO �Զ����ɵ� catch ��
						e.printStackTrace();
					}
					preproedatas.add(tmpPreproedData);
				}
				else {
					try {
						preproedatas.get(classMap.get(tmptopic).intValue()).AddData(rst, splitStrings[3]);
						preproedatas.get(classMap.get(tmptopic).intValue()).AddOrgData(splitStrings[2], splitStrings[3]);
					} catch (Exception e) {
						// TODO �Զ����ɵ� catch ��
						e.printStackTrace();
					}
				}
			}
			else if(splitStrings.length==3) {//û������ǩ����Ϊ��UNKNOWN
				String rst=PreprocessOneLine(splitStrings[2], splitStrings[1]);
				String tmptopic=splitStrings[1].replace("iPhone SE", "IphoneSE").replace("����˹�����Ƿ����ж�", "����˹�������ǵķ����ж�");
				if(!classMap.containsKey(tmptopic)) {//����÷���û���ֹ�
					classMap.put(splitStrings[1], preproedatas.size());
					StructuredPreproedData tmpPreproedData=new StructuredPreproedData(true);
					tmpPreproedData.className=splitStrings[1].replace("iPhone SE", "IphoneSE").replace("����˹�����Ƿ����ж�", "����˹�������ǵķ����ж�");
					try {
						tmpPreproedData.AddData(rst, Config.unknown);
						tmpPreproedData.AddOrgData(splitStrings[2], Config.unknown);
					} catch (Exception e) {
						// TODO �Զ����ɵ� catch ��
						e.printStackTrace();
					}
					preproedatas.add(tmpPreproedData);
				}
				else {
					try {
						preproedatas.get(classMap.get(tmptopic).intValue()).AddData(rst, Config.unknown);
						preproedatas.get(classMap.get(tmptopic).intValue()).AddOrgData(splitStrings[2], Config.unknown);
					} catch (Exception e) {
						// TODO �Զ����ɵ� catch ��
						e.printStackTrace();
					}
				}
			}
			else {//����error��˵��΢���ı���Ҳ������tab
				Log.LogInf("error 10002:�ָ�ȴ���"+splitStrings.length+";ԭ��Ϊ��"+line);
			}
			line=tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
		//������д��validationset�£�
		for(int i=0;i<preproedatas.size();i++) {
			StructuredPreproedData tmppreprodata=preproedatas.get(i);
			if(tmppreprodata.favor.size()>0) {
				tmpIO.startWrite(outdir+tmppreprodata.className+"_"+Config.favor+".txt", encodingType, 0,false);
				int size=tmppreprodata.favor.size();
				for(int j=0;j<size;j++) {
					tmpIO.writeOneString(tmppreprodata.favor.get(j)+"\n", 0);
				}
				tmpIO.endWrite(0);
			}
			if(tmppreprodata.infavor.size()>0) {
				tmpIO.startWrite(outdir+tmppreprodata.className+"_"+Config.infavor+".txt", encodingType, 0,false);
				int size=tmppreprodata.infavor.size();
				for(int j=0;j<size;j++) {
					tmpIO.writeOneString(tmppreprodata.infavor.get(j)+"\n", 0);
				}
				tmpIO.endWrite(0);
			}
			if(tmppreprodata.none.size()>0) {
				tmpIO.startWrite(outdir+tmppreprodata.className+"_"+Config.none+".txt", encodingType, 0,false);
				int size=tmppreprodata.none.size();
				for(int j=0;j<size;j++) {
					tmpIO.writeOneString(tmppreprodata.none.get(j)+"\n", 0);
				}
				tmpIO.endWrite(0);
			}
			if(tmppreprodata.unknown.size()>0) {
				tmpIO.startWrite(outdir+tmppreprodata.className+"_"+Config.unknown+".txt", encodingType, 0,false);
				int size=tmppreprodata.unknown.size();
				for(int j=0;j<size;j++) {
					tmpIO.writeOneString(tmppreprodata.unknown.get(j)+"\n", 0);
				}
				tmpIO.endWrite(0);
			}
		}
		topicIdMap=classMap;
		return preproedatas;
	}
	/**
	 * @description ѵ����Ԥ����
	 */
	public ArrayList<StructuredPreproedData> PreprocessTrainData(){
		IOapi tmpIO=new IOapi(1);
		tmpIO.startRead(rawDataSrc, encodingType, 0);
		String line=tmpIO.readOneSentence(0);//Ҫ���һ�п�ʼ������ʽ����
		String []splitStrings=null;
		HashMap<String, Integer> classMap=new HashMap<String, Integer>();
		ArrayList<StructuredPreproedData> preproedatas=new ArrayList<StructuredPreproedData>();
		while(line!=null) {
			splitStrings=line.split("	");
			if(splitStrings.length==4) {
				String rst=PreprocessOneLine(splitStrings[2], splitStrings[1]);
				if(!classMap.containsKey(splitStrings[1])) {//����÷���û���ֹ�
					classMap.put(splitStrings[1], preproedatas.size());
					StructuredPreproedData tmpPreproedData=new StructuredPreproedData(true);
					tmpPreproedData.className=splitStrings[1].replace("iPhone SE", "IphoneSE").replace("����˹�����Ƿ����ж�", "����˹�������ǵķ����ж�");
					try {
						tmpPreproedData.AddData(rst, splitStrings[3]);
						tmpPreproedData.AddOrgData(splitStrings[2], splitStrings[3]);
					} catch (Exception e) {
						// TODO �Զ����ɵ� catch ��
						e.printStackTrace();
					}
					preproedatas.add(tmpPreproedData);
				}
				else {
					try {
						preproedatas.get(classMap.get(splitStrings[1]).intValue()).AddData(rst, splitStrings[3]);
						preproedatas.get(classMap.get(splitStrings[1]).intValue()).AddOrgData(splitStrings[2], splitStrings[3]);
					} catch (Exception e) {
						// TODO �Զ����ɵ� catch ��
						e.printStackTrace();
					}
				}
			}
			else if(splitStrings.length==3) {//û������ǩ����Ϊ��UNKNOWN
				String rst=PreprocessOneLine(splitStrings[2], splitStrings[1]);
				if(!classMap.containsKey(splitStrings[1])) {//����÷���û���ֹ�
					classMap.put(splitStrings[1], preproedatas.size());
					StructuredPreproedData tmpPreproedData=new StructuredPreproedData(true);
					tmpPreproedData.className=splitStrings[1].replace("iPhone SE", "IphoneSE").replace("����˹�����Ƿ����ж�", "����˹�������ǵķ����ж�");
					try {
						tmpPreproedData.AddData(rst, Config.unknown);
						tmpPreproedData.AddOrgData(splitStrings[2], Config.unknown);
					} catch (Exception e) {
						// TODO �Զ����ɵ� catch ��
						e.printStackTrace();
					}
					preproedatas.add(tmpPreproedData);
				}
				else {
					try {
						preproedatas.get(classMap.get(splitStrings[1]).intValue()).AddData(rst, Config.unknown);
						preproedatas.get(classMap.get(splitStrings[1]).intValue()).AddOrgData(splitStrings[2], Config.unknown);
					} catch (Exception e) {
						// TODO �Զ����ɵ� catch ��
						e.printStackTrace();
					}
				}
			}
			else {//����error��˵��΢���ı���Ҳ������tab
				Log.LogInf("error 10002:�ָ�ȴ���"+splitStrings.length+";ԭ��Ϊ��"+line);
			}
			line=tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
		for(int i=0;i<preproedatas.size();i++) {
			StructuredPreproedData tmppreprodata=preproedatas.get(i);
			if(tmppreprodata.favor.size()>0) {
				tmpIO.startWrite(outputSrc+tmppreprodata.className+"_"+Config.favor+".txt", encodingType, 0,true);
				int size=tmppreprodata.favor.size();
				for(int j=0;j<size;j++) {
					tmpIO.writeOneString(tmppreprodata.favor.get(j)+"\n", 0);
				}
				tmpIO.endWrite(0);
			}
			if(tmppreprodata.infavor.size()>0) {
				tmpIO.startWrite(outputSrc+tmppreprodata.className+"_"+Config.infavor+".txt", encodingType, 0,true);
				int size=tmppreprodata.infavor.size();
				for(int j=0;j<size;j++) {
					tmpIO.writeOneString(tmppreprodata.infavor.get(j)+"\n", 0);
				}
				tmpIO.endWrite(0);
			}
			if(tmppreprodata.none.size()>0) {
				tmpIO.startWrite(outputSrc+tmppreprodata.className+"_"+Config.none+".txt", encodingType, 0,true);
				int size=tmppreprodata.none.size();
				for(int j=0;j<size;j++) {
					tmpIO.writeOneString(tmppreprodata.none.get(j)+"\n", 0);
				}
				tmpIO.endWrite(0);
			}
			if(tmppreprodata.unknown.size()>0) {
				tmpIO.startWrite(outputSrc+tmppreprodata.className+"_"+Config.unknown+".txt", encodingType, 0,true);
				int size=tmppreprodata.unknown.size();
				for(int j=0;j<size;j++) {
					tmpIO.writeOneString(tmppreprodata.unknown.get(j)+"\n", 0);
				}
				tmpIO.endWrite(0);
			}
		}
		topicIdMap=classMap;
		return preproedatas;
	}
	/**
	 * @description ���Լ�Ԥ����
	 */
	public ArrayList<StructuredPreproedData> PreprocessTestData() {
		return PreprocessTrainData();
	}
	public static ArrayList<StructuredPreproedData> LoadOrgData(String rawdatasrc,HashMap<String, Integer> classidmap) {
		IOapi tmpIO=new IOapi(1);
		tmpIO.startRead(rawdatasrc, encodingType, 0);
		String line=tmpIO.readOneSentence(0);//Ҫ���һ�п�ʼ������ʽ����
		String []splitStrings=null;
		HashMap<String, Integer> classMap=new HashMap<String, Integer>();
		ArrayList<StructuredPreproedData> preproedatas=new ArrayList<StructuredPreproedData>();
		while(line!=null) {
			splitStrings=line.split("	");
			if(splitStrings.length==4) {
				if(!classMap.containsKey(splitStrings[1])) {//����÷���û���ֹ�
					classMap.put(splitStrings[1], preproedatas.size());
					StructuredPreproedData tmpPreproedData=new StructuredPreproedData(true);
					tmpPreproedData.className=splitStrings[1].replace("iPhone SE", "IphoneSE").replace("����˹�����Ƿ����ж�", "����˹�������ǵķ����ж�");
					try {
						tmpPreproedData.AddOrgData(splitStrings[2], splitStrings[3]);
					} catch (Exception e) {
						// TODO �Զ����ɵ� catch ��
						e.printStackTrace();
					}
					preproedatas.add(tmpPreproedData);
				}
				else {
					try {
						preproedatas.get(classMap.get(splitStrings[1]).intValue()).AddOrgData(splitStrings[2], splitStrings[3]);
					} catch (Exception e) {
						// TODO �Զ����ɵ� catch ��
						e.printStackTrace();
					}
				}
			}
			else if(splitStrings.length==3) {//û������ǩ����Ϊ��UNKNOWN
				if(!classMap.containsKey(splitStrings[1])) {//����÷���û���ֹ�
					classMap.put(splitStrings[1], preproedatas.size());
					StructuredPreproedData tmpPreproedData=new StructuredPreproedData(true);
					tmpPreproedData.className=splitStrings[1].replace("iPhone SE", "IphoneSE").replace("����˹�����Ƿ����ж�", "����˹�������ǵķ����ж�");
					try {
						tmpPreproedData.AddOrgData(splitStrings[2], Config.unknown);
					} catch (Exception e) {
						// TODO �Զ����ɵ� catch ��
						e.printStackTrace();
					}
					preproedatas.add(tmpPreproedData);
				}
				else {
					try {
						preproedatas.get(classMap.get(splitStrings[1]).intValue()).AddOrgData(splitStrings[2], Config.unknown);
					} catch (Exception e) {
						// TODO �Զ����ɵ� catch ��
						e.printStackTrace();
					}
				}
			}
			else {//����error��˵��΢���ı���Ҳ������tab
				Log.LogInf("error 10002:�ָ�ȴ���"+splitStrings.length+";ԭ��Ϊ��"+line);
			}
			line=tmpIO.readOneSentence(0);
		}
		tmpIO.endRead(0);
		if(classidmap!=null) {
			classidmap=classMap;
		}
		return preproedatas;
	}
	private String PreprocessOneLine(String line,String topic) {
		try {
			return PreProcessText.preProcess4NLPCC2016(line, topic);
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
			return "";
		}
	}
	/**
	 * @description ���ɺ�preprocesseddata֮��ʹ�øú�������lsaCorpusSrc·��������ָ��topic��lsaѵ������
	 * @param topic
	 * @param senNumOfCorpus
	 * @throws Exception
	 */
	public static void PreproForLSA(String topic,int senNumOfCorpus) throws Exception {
		//�����lsa�����ļ����µ�ԭ���ļ���
		FileOperateAPI.DeleteFolder(Config.lsaCorpusDir);
		//�ٰ�ָ��topic�������ļ����ɵ�lsaCorpusSrc·���£�
		String fname=topic+"_"+Config.favor+".txt";
		String iname=topic+"_"+Config.infavor+".txt";
		String nname=topic+"_"+Config.none+".txt";
		//��favor�ļ���д��ָ��λ�ã�
		myIO.startRead(Config.preprocessedTrainDataOutputSrc+fname, Config.encodingType, 0);
		String line=myIO.readOneSentence(0);
		int docCounter=0;
		while(line!=null) {
			myIO.startWrite(Config.lsaCorpusDir+topic+"_"+Config.favor+"_"+docCounter+".txt", Config.encodingType, 0);
			for(int i=0;i<senNumOfCorpus&&line!=null;i++) {
				myIO.writeOneString(line+"\n", 0);
				line=myIO.readOneSentence(0);
			}
			myIO.endWrite(0);
			docCounter++;
		}
		myIO.endRead(0);
		//��infavor�ļ���д��ָ��λ�ã�
		myIO.startRead(Config.preprocessedTrainDataOutputSrc+iname, Config.encodingType, 0);
		line=myIO.readOneSentence(0);
		docCounter=0;
		while(line!=null) {
			myIO.startWrite(Config.lsaCorpusDir+topic+"_"+Config.infavor+"_"+docCounter+".txt", Config.encodingType, 0);
			for(int i=0;i<senNumOfCorpus&&line!=null;i++) {
				myIO.writeOneString(line+"\n", 0);
				line=myIO.readOneSentence(0);
			}
			myIO.endWrite(0);
			docCounter++;
		}
		myIO.endRead(0);
		//��none�ļ���д��ָ��λ�ã�
		myIO.startRead(Config.preprocessedTrainDataOutputSrc+nname, Config.encodingType, 0);
		line=myIO.readOneSentence(0);
		docCounter=0;
		while(line!=null) {
			myIO.startWrite(Config.lsaCorpusDir+topic+"_"+Config.none+"_"+docCounter+".txt", Config.encodingType, 0);
			for(int i=0;i<senNumOfCorpus&&line!=null;i++) {
				myIO.writeOneString(line+"\n", 0);
				line=myIO.readOneSentence(0);
			}
			myIO.endWrite(0);
			docCounter++;
		}
		myIO.endRead(0);
	}
	
	public static void PreproForLSA(String topic) throws Exception {
		PreproForLSA(topic,1);
	}
	public static void PreproForLDAGVM(String topic,int groupnum) throws IOException {
		List<File> fileList = new ArrayList<File>();
		FileOperateAPI.visitDirsAllFiles(Config.preprocessedTrainDataOutputSrc, fileList);
		ArrayList<String> favorStrList=new ArrayList<String>();
		ArrayList<String> infavorStrList=new ArrayList<String>();
		ArrayList<String> noneStrList=new ArrayList<String>();
		ArrayList<String> tmpStrList;
		for (File file : fileList) {
			if(!file.getName().contains(topic)) {//����ļ���������topic,������ע��Ҫ��֤�ļ��������Ϲ淶��
				continue;
			}
			if(GetStanceFromFileName(file.getName()).equals(Config.favor)) {
				tmpStrList=favorStrList;
			}
			else if(GetStanceFromFileName(file.getName()).equals(Config.infavor)) {
				tmpStrList=infavorStrList;
			}
			else {
				tmpStrList=noneStrList;
			}
			myIO.startRead(file, Config.encodingType, 0);
			String line=myIO.readOneSentence(0);
			while(line!=null) {
				tmpStrList.add(StopWordsFilter.filterSW(line));
				line=myIO.readOneSentence(0);
			}
			myIO.endRead(0);
		}
		myIO.startWrite(Config.ldaGVMCorpusDir+"\\"+topic+"\\"+topic+".txt", Config.encodingType, 0);
		int rows=0;
		if(favorStrList.size()%groupnum==0) {
			rows+=(favorStrList.size()/groupnum);
		}
		else {
			rows+=(favorStrList.size()/groupnum+1);
		}
		if(infavorStrList.size()%groupnum==0) {
			rows+=(infavorStrList.size()/groupnum);
		}
		else {
			rows+=(infavorStrList.size()/groupnum+1);
		}
		if(noneStrList.size()%groupnum==0) {
			rows+=(noneStrList.size()/groupnum);
		}
		else {
			rows+=(noneStrList.size()/groupnum+1);
		}
		myIO.writeOneString(rows+"\n", 0);
		int counter=0;
		//��дfavor���֣�
		counter=0;
		for(String s:favorStrList) {
			myIO.writeOneString(s+" ", 0);
			counter++;
			if(counter==groupnum) {
				counter=0;
				myIO.writeOneString("\n", 0);
			}
		}
		if(favorStrList.size()%groupnum!=0) {
			myIO.writeOneString("\n", 0);
		}
		//��дinfavor���֣�
		counter=0;
		for(String s:infavorStrList) {
			myIO.writeOneString(s+" ", 0);
			counter++;
			if(counter==groupnum) {
				counter=0;
				myIO.writeOneString("\n", 0);
			}
		}
		if(infavorStrList.size()%groupnum!=0) {
			myIO.writeOneString("\n", 0);
		}
		//��дnone���֣�
		counter=0;
		for(String s:noneStrList) {
			myIO.writeOneString(s+" ", 0);
			counter++;
			if(counter==groupnum) {
				counter=0;
				myIO.writeOneString("\n", 0);
			}
		}
		if(noneStrList.size()%groupnum!=0) {
			myIO.writeOneString("\n", 0);
		}
		myIO.endWrite(0);
		//��preproedTrainDataForLDAGVM������������ݣ�
		//дfavor��
		myIO.startWrite(Config.preproedTrainDataForLDAGVM+topic+"_"+Config.favor+".txt", Config.encodingType, 0);
		counter=0;
		for(String s:favorStrList) {
			myIO.writeOneString(s+" ", 0);
			counter++;
			if(counter==groupnum) {
				counter=0;
				myIO.writeOneString("\n", 0);
			}
		}
		if(favorStrList.size()%groupnum!=0) {
			myIO.writeOneString("\n", 0);
		}
		myIO.endWrite(0);
		//дinfavor��
		myIO.startWrite(Config.preproedTrainDataForLDAGVM+topic+"_"+Config.infavor+".txt", Config.encodingType, 0);
		counter=0;
		for(String s:infavorStrList) {
			myIO.writeOneString(s+" ", 0);
			counter++;
			if(counter==groupnum) {
				counter=0;
				myIO.writeOneString("\n", 0);
			}
		}
		if(infavorStrList.size()%groupnum!=0) {
			myIO.writeOneString("\n", 0);
		}
		myIO.endWrite(0);
		//дnone��
		myIO.startWrite(Config.preproedTrainDataForLDAGVM+topic+"_"+Config.none+".txt", Config.encodingType, 0);
		counter=0;
		for(String s:noneStrList) {
			myIO.writeOneString(s+" ", 0);
			counter++;
			if(counter==groupnum) {
				counter=0;
				myIO.writeOneString("\n", 0);
			}
		}
		if(noneStrList.size()%groupnum!=0) {
			myIO.writeOneString("\n", 0);
		}
		myIO.endWrite(0);
	}
	/**
	 * @description topic mixed����LDAinputDir������ldaѵ��ͳһ�����ļ�LDAInput.txt
	 * @throws IOException
	 */
	public static void PreproForLDA() throws IOException {
		List<File> fileList = new ArrayList<File>();
		FileOperateAPI.visitDirsAllFiles(Config.preprocessedTrainDataOutputSrc, fileList);
		ArrayList<String> strlist=new ArrayList<String>();
		for (File file : fileList) {
			myIO.startRead(file, Config.encodingType, 0);
			String line=myIO.readOneSentence(0);
			while(line!=null) {
				strlist.add(StopWordsFilter.filterSW(line)+"\n");
				line=myIO.readOneSentence(0);
			}
			myIO.endRead(0);
		}
		myIO.startWrite(Config.LDAInputSrc, Config.encodingType, 0);
		myIO.writeOneString(strlist.size()+"\n", 0);
		for(String s:strlist) {
			myIO.writeOneString(s, 0);
		}
		myIO.endWrite(0);
	}
	/**
	 * @description only one topic,Ϊָ����topic����ldaѵ�������ļ�
	 * @param topic
	 * @throws IOException 
	 */
	public static void PreproForLDA(String topic) throws IOException {
		List<File> fileList = new ArrayList<File>();
		FileOperateAPI.visitDirsAllFiles(Config.preprocessedTrainDataOutputSrc, fileList);
		ArrayList<String> strlist=new ArrayList<String>();
		for (File file : fileList) {
			if(!file.getName().contains(topic)) {//����ļ���������topic,������ע��Ҫ��֤�ļ��������Ϲ淶��
				continue;
			}
			myIO.startRead(file, Config.encodingType, 0);
			String line=myIO.readOneSentence(0);
			while(line!=null) {
				strlist.add(StopWordsFilter.filterSW(line)+"\n");
				line=myIO.readOneSentence(0);
			}
			myIO.endRead(0);
		}
		myIO.startWrite(Config.LDAInputDir+"\\"+topic+"\\"+topic+".txt", Config.encodingType, 0);
		myIO.writeOneString(strlist.size()+"\n", 0);
		for(String s:strlist) {
			myIO.writeOneString(s, 0);
		}
		myIO.endWrite(0);
	}
	public static ArrayList<StructuredPreproedData> LoadTrainDataFromPreproedFile() throws Exception {
		return LoadTrainDataFromPreproedFile(new HashMap<String, Integer>());
	}
	public static ArrayList<StructuredPreproedData> LoadTrainDataFromPreproedFile(HashMap<String, Integer> topicidmap) throws Exception {
		List<File> fileList = new ArrayList<File>();
		HashMap<String, Integer> topicIdMap=topicidmap;
		ArrayList<StructuredPreproedData> rst=new ArrayList<StructuredPreproedData>();
		FileOperateAPI.visitDirsAllFiles(Config.preprocessedTrainDataOutputSrc, fileList);
		int counter=0;
		for(File file:fileList) {
			String []strs=file.getName().replace(".txt", "").split("_");
			if(strs.length!=2) {
				throw new IOException("��Ԥ�������ļ�����ѵ������ʱ����������ļ�����ʽ!�ļ�����"+file.getName());
			}
			if(topicIdMap.containsKey(strs[0])) {
				StructuredPreproedData spd=rst.get(topicIdMap.get(strs[0]).intValue());
				myIO.startRead(file, Config.encodingType, 0);
				String line=myIO.readOneSentence(0);
				while(line!=null) {
					spd.AddData(line, strs[1]);
					line=myIO.readOneSentence(0);
				}
				myIO.endRead(0);
			}
			else {
				StructuredPreproedData spd=new StructuredPreproedData(false);
				rst.add(spd);
				topicIdMap.put(strs[0], counter);
				counter++;
				spd.className=strs[0];
				myIO.startRead(file, Config.encodingType, 0);
				String line=myIO.readOneSentence(0);
				while(line!=null) {
					spd.AddData(line, strs[1]);
					line=myIO.readOneSentence(0);
				}
				myIO.endRead(0);
			}
		}
		return rst;
	}
	public static ArrayList<StructuredPreproedData> LoadTestDataFromPreproedFile() throws Exception {
		return LoadTestDataFromPreproedFile(new HashMap<String, Integer>());
	}
	public static ArrayList<StructuredPreproedData> LoadTestDataFromPreproedFile(HashMap<String, Integer> topicidmap) throws Exception {
		List<File> fileList = new ArrayList<File>();
		HashMap<String, Integer> topicIdMap=topicidmap;
		ArrayList<StructuredPreproedData> rst=new ArrayList<StructuredPreproedData>();
		FileOperateAPI.visitDirsAllFiles(Config.preprocessedTestDataOutputSrc, fileList);
		int counter=0;
		for(File file:fileList) {
			String []strs=file.getName().replace(".txt", "").split("_");
			if(strs.length!=2) {
				throw new IOException("��Ԥ�������ļ�������������ʱ����������ļ�����ʽ!�ļ�����"+file.getName());
			}
			if(topicIdMap.containsKey(strs[0])) {
				StructuredPreproedData spd=rst.get(topicIdMap.get(strs[0]).intValue());
				myIO.startRead(file, Config.encodingType, 0);
				String line=myIO.readOneSentence(0);
				while(line!=null) {
					spd.AddData(line, strs[1]);
					line=myIO.readOneSentence(0);
				}
				myIO.endRead(0);
			}
			else {
				StructuredPreproedData spd=new StructuredPreproedData(false);
				rst.add(spd);
				topicIdMap.put(strs[0], counter);
				counter++;
				spd.className=strs[0];
				myIO.startRead(file, Config.encodingType, 0);
				String line=myIO.readOneSentence(0);
				while(line!=null) {
					spd.AddData(line, strs[1]);
					line=myIO.readOneSentence(0);
				}
				myIO.endRead(0);
			}
		}
		return rst;
	}
}

class PreProcessText {
	static public String preProcess4Task1(String inputStr, String tmpRelationP, String tmpEntityS, String tmpEntityO) throws IOException{
		if (inputStr.length()<1) return inputStr;
		//ǿ��������ʵ��ʷֿ�
		if (tmpRelationP!=null) {
			if (tmpRelationP.length()==4) { //���Ų��͡�ͬΪУ����������С����Ů�� �ȹ�ϵ ���Ĵ��ں���
				if (!inputStr.contains(tmpRelationP)) {
					tmpRelationP = tmpRelationP.substring(2);
				}
			} 
			inputStr = inputStr.replaceAll(tmpRelationP, " "+tmpRelationP+" ");
		}
		inputStr = inputStr.replaceAll(tmpEntityS, " "+tmpEntityS+" ");
		inputStr = inputStr.replaceAll(tmpEntityO, " "+tmpEntityO+" ");
		inputStr=Full2Half.ToDBC(inputStr);//ȫ��ת���						
		inputStr=inputStr.toLowerCase();//��ĸȫ��Сд
		inputStr=inputStr.replaceAll("\\s+", " ");//����ո����ɵ����ո�
		inputStr = StringAnalyzer.extractGoodCharacter(inputStr); //ȥ�����������ַ�
		//                           �޴���                                                                       ������
		inputStr = WordSegment_Ansj.splitWord(inputStr)+"\t"+WordSegment_Ansj.splitWordwithTag(inputStr);//���зִ�
		
		return inputStr;
	} 
	static public String preProcess4Task2(String inputStr) throws IOException{
		if (inputStr.length()<1) return inputStr;
		inputStr=Full2Half.ToDBC(inputStr);//ȫ��ת���						
		inputStr=inputStr.toLowerCase();//��ĸȫ��Сд
		inputStr=inputStr.replaceAll("\\s+", " ");//����ո����ɵ����ո�
		inputStr = StringAnalyzer.extractGoodCharacter(inputStr); //ȥ�����������ַ�
		//                           �޴���                                                                       ������
		inputStr = WordSegment_Ansj.splitWordwithOutTag4Task2(inputStr);//���зִ�
		
		return inputStr.trim();
	}
	/**
	 * �ı�Ԥ����˳��ȥ���������HashTag��ȥ��url��ȥ�������ʶ��ȥ��@��ʶ��
	 * ȫ��ת��ǣ���ĸתСд�������еĶ�������ո�ת��Ϊһ���ո�ȥ�����������ַ���
	 * �ִʣ�ʹ��thulac API��
	 * @author WangYunli
	 * @date 2017-03-28
	 */
	static public String preProcess4NLPCC2016(String inputStr, String topic) throws IOException{
		if (inputStr.length()<1) return inputStr;
		inputStr=inputStr.replaceAll("#"+topic+"#", " ");//��1�����˵�HashTag�ı�ʶ
		//inputStr=inputStr.replaceAll("http://t.cn/(.{7})", " ");�����������ʽ
		inputStr=inputStr.replaceAll("[a-zA-z]+://[^\\s()����\\t]*", " ");//��2�����˵�http://t.cn/[7���ַ�]
		//��3�����˵�һЩ����ķ����ʶ���磺
		inputStr=inputStr.replaceAll("������[^��]*��", " ");
		inputStr=inputStr.replaceAll("\\(����[^\\)]*\\)", " ");
		inputStr=inputStr.replaceAll("������[^��]*��", " ");
		inputStr=inputStr.replaceAll("������[^��]*��", " ");
		inputStr=inputStr.replaceAll("\\(����[^\\)]*\\)", " ");
		inputStr=inputStr.replaceAll("������[^��]*��", " ");
		//��4�����˵����е�@��ʶ����@��Ѷ���ſͻ��� @[10���ַ�����]�����һ��@���ո���з�
		String[] inputStr_sub = inputStr.split("\\s+");
		StringBuffer inputStr_bf = new StringBuffer();
		for (String tmpinputStr_sub:inputStr_sub) {
			tmpinputStr_sub = tmpinputStr_sub+"<eos>";
			tmpinputStr_sub = tmpinputStr_sub.replaceAll("@(.{0,9})@", "@");	
			tmpinputStr_sub = tmpinputStr_sub.replaceAll("@(.{0,9}) ", " ");
			tmpinputStr_sub = tmpinputStr_sub.replaceAll("@(.{0,9})<eos>", " ");
			tmpinputStr_sub = tmpinputStr_sub.replaceAll("<eos>", "");
			inputStr_bf.append(tmpinputStr_sub);
			inputStr_bf.append(" ");
		}
		inputStr = inputStr_bf.toString().trim();
		inputStr_bf = null;	
		inputStr=Full2Half.ToDBC(inputStr);//��5��ȫ��ת���					
		inputStr=inputStr.toLowerCase();//��6����ĸȫ��Сд
		inputStr=inputStr.replaceAll("\\s+", " ");//��7������ո����ɵ����ո�
		inputStr = StringAnalyzer.extractGoodCharacter(inputStr); //��8��ȥ�����������ַ�
		inputStr=WordSegment.splitWordwithTag(inputStr);//��9�����зִ�
		inputStr=WordSegment.TermsNameToString(" ");//��9�����зִ�
		//inputStr=WordSegment_Ansj.splitWord(inputStr);
		
		return inputStr.trim();
	} 	

	private static boolean isITSuffixSpamInfo(String tmpQuerySnippet, String tmpEntityS, String tmpEntityO) {
		if ((tmpQuerySnippet.contains(tmpEntityS)||tmpQuerySnippet.contains(tmpEntityO))
				&&tmpQuerySnippet.length()>4) {
			return false;
		}else {
			return true;
		}
	}
}