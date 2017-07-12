package com.wangyl.stanceDetection;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.encog.ensemble.Ensemble;

import com.wangyl.config.Config;
import com.wangyl.ensembleLearning.EnsembleFramework;
import com.wangyl.ensembleLearning.EnsembleVote;
import com.wangyl.lda.LDA_api;
import com.wangyl.lda.TestLDA;
import com.wangyl.log.Log;
import com.wangyl.lsa.LsaGroupVoteModel;
import com.wangyl.lsa.TestLSA;
import com.wangyl.preprocesser.RawDataPreprocesser;
import com.wangyl.preprocesser.StructuredPreproedData;
import com.wangyl.svmAPI.svm_result;
import com.wangyl.tools.*;
import com.wangyunli.sentimentLexicon.SentimentAnalyse;
//import com.e2one.example;
public class StanceDetectionSystemGUI {

	/**
	 * @param args
	 */
	static Thread t=null;
	static RawDataPreprocesser rawDataPreprocesser=new RawDataPreprocesser("", Config.preprocessedTrainDataOutputSrc, Config.encodingType);
	static String trainsrc="";
	static String nltrainsrc="";
	static String testsrc="";
	static int lsagvm_n=8;
	static int lsagvm_k=20;
	static double lsasvm_c=0.1;
	static int ldatopicnum=60;
	static double ldasvm_c=1;
	static double slsvm_l=0.1;
	static double slsvm_c=1;
	static double fp=0.2;
	static int fk=20;
	public static boolean CheckArgs(int type,String argsline) {
		String []strs;
		switch (type) {
		case 1://lsagvm
			strs=argsline.split(",");
			if(strs.length!=2) {
				return false;
			}
			else {
				try {
					lsagvm_n=Integer.parseInt(strs[0]);
					lsagvm_k=Integer.parseInt(strs[1]);
				} catch (Exception e) {
					// TODO: handle exception
					return false;
				}
				return true;
			}
		case 2://lsasvm
			strs=argsline.split(",");
			if(strs.length!=1) {
				return false;
			}
			else {
				try {
					lsasvm_c=Double.parseDouble(strs[0]);
				} catch (Exception e) {
					// TODO: handle exception
					return false;
				}
				return true;
			}
			
		case 3://ldasvm
			strs=argsline.split(",");
			if(strs.length!=2) {
				return false;
			}
			else {
				try {
					ldatopicnum=Integer.parseInt(strs[0]);
					ldasvm_c=Double.parseDouble(strs[1]);
				} catch (Exception e) {
					// TODO: handle exception
					return false;
				}
				
				return true;
			}
		case 4://slsvm
			strs=argsline.split(",");
			if(strs.length!=2) {
				return false;
			}
			else {
				try {
					slsvm_l=Double.parseDouble(strs[0]);
					slsvm_c=Double.parseDouble(strs[1]);
				} catch (Exception e) {
					// TODO: handle exception
					return false;
				}
				
				return true;
			}
		case 5://f e lsagvm
			strs=argsline.split(",");
			if(strs.length!=2) {
				return false;
			}
			else {
				try {
					fp=Double.parseDouble(strs[0]);
					fk=Integer.parseInt(strs[1]);
				} catch (Exception e) {
					// TODO: handle exception
					return false;
				}
				return true;
			}
		default:
			return false;
		}
	}
	public static void main(String[] args) {
		// TODO �Զ����ɵķ������
		Display display = new Display();
	    final Shell shell = new Shell(display);
	    shell.setText("΢��������������");
	    GridLayout mainUILayout=new GridLayout(10,false);//2�У����ȳ�
	    //mainUILayout.numColumns = 3;
	    mainUILayout.makeColumnsEqualWidth = false;
	    mainUILayout.marginWidth = 15;
	    mainUILayout.marginHeight = 15;
	    mainUILayout.verticalSpacing = 1;
	    mainUILayout.horizontalSpacing = 1;
	    //��ע��ѵ���ļ�·��label��
	    final Label trainDataSrcLabel=new Label(shell, SWT.BORDER|SWT.SHADOW_NONE);
	    trainDataSrcLabel.setText("�ѱ�עѵ�����ݵ�·����");
	    trainDataSrcLabel.setAlignment(SWT.CENTER);
	    GridData trainDataSrcLabelData=new GridData(SWT.FILL,SWT.LEFT,false,false);
	    trainDataSrcLabelData.verticalSpan=3;
	    trainDataSrcLabelData.horizontalSpan=3;
	    trainDataSrcLabelData.widthHint=200;
	    trainDataSrcLabelData.heightHint=36;
	    trainDataSrcLabel.setLayoutData(trainDataSrcLabelData);
	    //����ѵ���ļ�·���ı���
	    final Text trainDataSrcText = new Text(shell, SWT.BORDER|SWT.H_SCROLL);
	    trainDataSrcText.setText(Config.rawDataDir+Config.labeledTrainDataName);
	    GridData trainDataSrcTextData=new GridData(SWT.FILL,SWT.LEFT,false,false);
	    trainDataSrcTextData.verticalSpan=3;
	    trainDataSrcTextData.horizontalSpan=6;
	    trainDataSrcTextData.widthHint=500;
	    trainDataSrcText.setLayoutData(trainDataSrcTextData);
	    //���ñ�ע��ѵ���ļ�·��ѡ��button��
	    final Button chooseltdSrcBtn=new Button(shell, SWT.PUSH|SWT.CENTER);
	    chooseltdSrcBtn.setText("ѡ��·��");
	    chooseltdSrcBtn.setToolTipText("���ѡ���ע��ѵ���ļ���Ҳ��ֱ������ߵ��ı���������·��");
	    chooseltdSrcBtn.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				// TODO �Զ����ɵķ������
				JFileChooser chooser=new JFileChooser() ;
				chooser.setCurrentDirectory(new File(Config.rawDataDir)) ;
				if(chooser.showOpenDialog(new JLabel())==JFileChooser.APPROVE_OPTION)
				{
					trainDataSrcText.setText(chooser.getSelectedFile().getPath());
				}
			}
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO �Զ����ɵķ������
			}
		});
	    GridData chooseltdSrcBtnData=new GridData(SWT.FILL,SWT.LEFT,false,false);
	    chooseltdSrcBtnData.verticalSpan=3;
	    chooseltdSrcBtnData.horizontalSpan=1;
	    chooseltdSrcBtnData.widthHint=60;
	    chooseltdSrcBtn.setLayoutData(chooseltdSrcBtnData);
	    //δ��ע��ѵ���ļ���·��label��
	    final Label nltrainDataSrcLabel=new Label(shell, SWT.BORDER|SWT.SHADOW_NONE);
	    nltrainDataSrcLabel.setText("δ��עѵ�����ݵ�·����");
	    nltrainDataSrcLabel.setAlignment(SWT.CENTER);
	    GridData nltrainDataSrcLabelData=new GridData(SWT.FILL,SWT.LEFT,false,false);
	    nltrainDataSrcLabelData.verticalSpan=3;
	    nltrainDataSrcLabelData.horizontalSpan=3;
	    nltrainDataSrcLabelData.widthHint=200;
	    nltrainDataSrcLabelData.heightHint=36;
	    nltrainDataSrcLabel.setLayoutData(nltrainDataSrcLabelData);
	    //δ��ע��ѵ���ļ���·���ı���
	    final Text nltrainDataSrcText=new Text(shell, SWT.BORDER|SWT.H_SCROLL);
	    nltrainDataSrcText.setText(Config.rawDataDir+Config.notLabeledTrainDataName);
	    GridData nltrainDataSrcTextData=new GridData(SWT.FILL,SWT.LEFT,false,false);
	    nltrainDataSrcTextData.verticalSpan=3;
	    nltrainDataSrcTextData.horizontalSpan=6;
	    nltrainDataSrcTextData.widthHint=500;
	    nltrainDataSrcText.setLayoutData(nltrainDataSrcTextData);
	    //����δ��ע��ѵ���ļ�·��ѡ��button��
	    final Button choosenltdSrcBtn=new Button(shell, SWT.PUSH|SWT.CENTER);
	    choosenltdSrcBtn.setText("ѡ��·��");
	    choosenltdSrcBtn.setToolTipText("���ѡ��δ��ע��ѵ���ļ����Ǳ���ѵ���ļ�����Ҳ��ֱ������ߵ��ı���������·��");
	    choosenltdSrcBtn.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				// TODO �Զ����ɵķ������
				JFileChooser chooser=new JFileChooser() ;
				chooser.setCurrentDirectory(new File(Config.rawDataDir)) ;
				if(chooser.showOpenDialog(new JLabel())==JFileChooser.APPROVE_OPTION)
				{
					nltrainDataSrcText.setText(chooser.getSelectedFile().getPath());
				}
			}
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO �Զ����ɵķ������
			}
		});
	    GridData choosenltdSrcBtnData=new GridData(SWT.FILL,SWT.LEFT,false,false);
	    choosenltdSrcBtnData.verticalSpan=3;
	    choosenltdSrcBtnData.horizontalSpan=1;
	    choosenltdSrcBtnData.widthHint=60;
	    choosenltdSrcBtn.setLayoutData(choosenltdSrcBtnData);
	    //�����ļ���·����label��
	    final Label testDataSrcLabel=new Label(shell, SWT.BORDER|SWT.SHADOW_NONE);
	    testDataSrcLabel.setText("���������ļ���·����");
	    testDataSrcLabel.setAlignment(SWT.CENTER);
	    GridData testDataSrcLabelData=new GridData(SWT.FILL,SWT.LEFT,false,false);
	    testDataSrcLabelData.verticalSpan=3;
	    testDataSrcLabelData.horizontalSpan=3;
	    testDataSrcLabelData.widthHint=200;
	    testDataSrcLabelData.heightHint=36;
	    testDataSrcLabel.setLayoutData(testDataSrcLabelData);
	    //�����ļ���·���ı���
	    final Text testDataSrcText=new Text(shell, SWT.BORDER|SWT.H_SCROLL);
	    testDataSrcText.setText(Config.rawDataDir+Config.testDataName);
	    GridData testDataSrcTextData=new GridData(SWT.FILL,SWT.LEFT,false,false);
	    testDataSrcTextData.verticalSpan=3;
	    testDataSrcTextData.horizontalSpan=6;
	    testDataSrcTextData.widthHint=500;
	    testDataSrcText.setLayoutData(testDataSrcTextData);
	    //���ò����ļ�·��ѡ��button��
	    final Button choosettdSrcBtn=new Button(shell, SWT.PUSH|SWT.CENTER);
	    choosettdSrcBtn.setText("ѡ��·��");
	    choosettdSrcBtn.setToolTipText("���ѡ������ļ���Ҳ��ֱ������ߵ��ı���������·��");
	    choosettdSrcBtn.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				// TODO �Զ����ɵķ������
				JFileChooser chooser=new JFileChooser() ;
				chooser.setCurrentDirectory(new File(Config.rawDataDir)) ;
				if(chooser.showOpenDialog(new JLabel())==JFileChooser.APPROVE_OPTION)
				{
					testDataSrcText.setText(chooser.getSelectedFile().getPath());
				}
			}
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO �Զ����ɵķ������
			}
		});  
	    GridData choosettdSrcBtnData=new GridData(SWT.FILL,SWT.LEFT,false,false);
	    choosettdSrcBtnData.verticalSpan=3;
	    choosettdSrcBtnData.horizontalSpan=1;
	    choosettdSrcBtnData.widthHint=60;
	    choosettdSrcBtn.setLayoutData(choosettdSrcBtnData);
	    
	    
	    
	    //���෽��label��
	    final Label classifyMethodLabel=new Label(shell, SWT.BORDER|SWT.SHADOW_NONE);
	    classifyMethodLabel.setText("���෽����");
	    classifyMethodLabel.setAlignment(SWT.CENTER);
	    GridData classifyMethodLabelData=new GridData(SWT.FILL,SWT.LEFT,false,false);
	    classifyMethodLabelData.verticalSpan=2;
	    classifyMethodLabelData.horizontalSpan=3;
	    classifyMethodLabelData.widthHint=200;
	    classifyMethodLabelData.heightHint=24;
	    classifyMethodLabel.setLayoutData(classifyMethodLabelData);
	    //����ѡ����෽���õĵ�ѡ��
	    final Button checkButton= new Button(shell, SWT.RADIO);
	    checkButton.setText("LSA-GVM");
	    GridData checkButtonData=new GridData();
	    checkButtonData.verticalSpan=2;
	    checkButtonData.horizontalSpan=1;
	    checkButtonData.heightHint=24;
	    checkButtonData.widthHint=120;
	    checkButton.setToolTipText("����:������n(�������5С��30)��������k(�������5)��������дʾ����10,20");
	    checkButton.setLayoutData(checkButtonData);
	    //����ѡ����෽���õĵ�ѡ��
	    final Button checkButton2= new Button(shell, SWT.RADIO);
	    checkButton2.setText("LSA-SVM");
	    GridData checkButtonData2=new GridData();
	    checkButtonData2.verticalSpan=2;
	    checkButtonData2.horizontalSpan=1;
	    checkButtonData2.heightHint=24;
	    checkButtonData2.widthHint=120;
	    checkButton2.setToolTipText("����:��ʵ��c��������дʾ����0.1");
	    checkButton2.setLayoutData(checkButtonData2);
	    //����ѡ����෽���õĵ�ѡ��
	    final Button checkButton3= new Button(shell, SWT.RADIO);
	    checkButton3.setText("LDA-SVM");
	    GridData checkButtonData3=new GridData();
	    checkButtonData3.verticalSpan=2;
	    checkButtonData3.horizontalSpan=1;
	    checkButtonData3.heightHint=24;
	    checkButtonData3.widthHint=120;
	    checkButton3.setToolTipText("������������n(�������20С��300)����ʵ��c��������дʾ��:60,1");
	    checkButton3.setLayoutData(checkButtonData3);
	  //����ѡ����෽���õĵ�ѡ��
	    final Button checkButton4= new Button(shell, SWT.RADIO);
	    checkButton4.setText("SL-SVM");
	    GridData checkButtonData4=new GridData();
	    checkButtonData4.verticalSpan=2;
	    checkButtonData4.horizontalSpan=1;
	    checkButtonData4.heightHint=24;
	    checkButtonData4.widthHint=120;
	    checkButton4.setToolTipText("��������ʵ��l����ʵ��c��������дʾ����0.1,1");
	    checkButton4.setLayoutData(checkButtonData4);
	  //����ѡ����෽���õĵ�ѡ��
	    final Button checkButton5= new Button(shell, SWT.RADIO);
	    checkButton5.setText("Fast Ensemble LSA-GVM");
	    GridData checkButtonData5=new GridData();
	    checkButtonData5.verticalSpan=2;
	    checkButtonData5.horizontalSpan=2;
	    checkButtonData5.heightHint=24;
	    checkButtonData5.widthHint=180;
	    checkButton5.setToolTipText("������ʵ��p=[0,1),������k��������дʾ����0.2,20");
	    checkButton5.setLayoutData(checkButtonData5);
	    
	    //������label��
	    final Label argsLabel=new Label(shell, SWT.BORDER|SWT.SHADOW_NONE);
	    argsLabel.setText("�㷨������");
	    argsLabel.setAlignment(SWT.CENTER);
	    GridData argsLabelData=new GridData(SWT.FILL,SWT.LEFT,false,false);
	    argsLabelData.verticalSpan=2;
	    argsLabelData.horizontalSpan=2;
	    argsLabelData.widthHint=200;
	    argsLabelData.heightHint=36;
	    argsLabel.setToolTipText("���ұߵ��ı����������㷨�еĲ����������ͣ���㷨ѡ��ؼ��ϣ��ɲ鿴������д˵��");
	    argsLabel.setLayoutData(argsLabelData);
	    //������
	    final Text argsText = new Text(shell, SWT.BORDER|SWT.H_SCROLL);
	    argsText.setText("�ڴ������㷨�еĲ����������ͣ���㷨ѡ��ؼ��ϣ��ɲ鿴������д˵��");
	    GridData argsTextData=new GridData(SWT.FILL,SWT.LEFT,false,false);
	    argsTextData.verticalSpan=2;
	    argsTextData.horizontalSpan=8;
	    argsTextData.widthHint=500;
	    argsLabelData.heightHint=36;
	    argsText.setLayoutData(argsTextData);
	    
	    //��������õ��ı�
	    final Text outputConsoleText = new Text(shell, SWT.WRAP|SWT.BORDER|SWT.V_SCROLL);// �����ı��򣬿��Զ����� | ��ֱ������  
	    //outputConsoleText.setBounds(0, 0, 600, 300);// ��x, y, width, height��
	    outputConsoleText.setText("console");
	    outputConsoleText.setEditable(false);
	    GridData outputConsoleTextData=new GridData(SWT.FILL,SWT.CENTER,true,true);
	    outputConsoleTextData.verticalSpan=1;//���ռ�Ĵ�ֱ����
	    outputConsoleTextData.horizontalSpan=10;//���ռ��ˮƽ����
	    outputConsoleTextData.heightHint=220;//����߶�
	    outputConsoleTextData.widthHint=500;//������
	    outputConsoleText.setLayoutData(outputConsoleTextData);
	    //final org.eclipse.swt.widgets.Label trainDataSrcLabel=new org.eclipse.swt.widgets.Label(shell, style);
	    //ִ��button��
	    Button runButton= new Button(shell, SWT.PUSH|SWT.CENTER);
	    runButton.setText("START");
	    GridData runButtonData=new GridData();
	    runButtonData.verticalSpan=1;
	    runButtonData.horizontalSpan=5;
	    runButtonData.heightHint=28;
	    runButtonData.widthHint=360;
	    runButtonData.horizontalAlignment=SWT.FILL;
	    runButton.setLayoutData(runButtonData);
	    runButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				if(t!=null&&t.isAlive()) {
					JOptionPane.showMessageDialog(null, "��ȴ���ǰ������ִ�����", "����������", JOptionPane.ERROR_MESSAGE);
					return;
				}
				// TODO �Զ����ɵķ������
				outputConsoleText.setText("");
				if(checkButton.getSelection()) {//LSA-GVM
					if(!CheckArgs(1, argsText.getText())) {
						JOptionPane.showMessageDialog(null, "�밴��ȷ��ʽ��д����", "����������", JOptionPane.ERROR_MESSAGE);
						argsText.setText("�ڴ������㷨�еĲ����������ͣ���㷨ѡ��ؼ��ϣ��ɲ鿴������д˵��");
						return;
					}
					System.out.println("LSA-GVM:");
					testsrc=testDataSrcText.getText();
					trainsrc=trainDataSrcText.getText();
					t = new Thread(new Runnable(){  
			            public void run(){
			            	try {
			            		System.out.println(Timer.GetNowTimeToMillisecends()+" ѵ����Ԥ������...");
								rawDataPreprocesser.setOutputSrc(Config.preprocessedTrainDataOutputSrc);
								rawDataPreprocesser.setRawDataSrc(trainsrc);
								FileOperateAPI.DeleteFolder(Config.preprocessedTrainDataOutputSrc);
								rawDataPreprocesser.PreprocessTrainData();
								System.out.println(Timer.GetNowTimeToMillisecends()+" ���Լ�Ԥ������...");
								rawDataPreprocesser.setOutputSrc(Config.preprocessedTestDataOutputSrc);
								rawDataPreprocesser.setRawDataSrc(testsrc);
								FileOperateAPI.DeleteFolder(Config.preprocessedTestDataOutputSrc);
								ArrayList<StructuredPreproedData> testDatas=rawDataPreprocesser.PreprocessTestData();
								System.out.println(Timer.GetNowTimeToMillisecends()+" Anaylse...");
								System.out.println(LsaGroupVoteModel.Classify_fixedVoteNum(lsagvm_n,lsagvm_k));
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
			            }});  
			        t.start();
				}
				else if(checkButton2.getSelection()) {//LSA-SVM
					if(!CheckArgs(2, argsText.getText())) {
						JOptionPane.showMessageDialog(null, "�밴��ȷ��ʽ��д����", "����������", JOptionPane.ERROR_MESSAGE);
						argsText.setText("�ڴ������㷨�еĲ����������ͣ���㷨ѡ��ؼ��ϣ��ɲ鿴������д˵��");
						return;
					}
					System.out.println("LSA-SVM:");
					testsrc=testDataSrcText.getText();
					trainsrc=trainDataSrcText.getText();
					t = new Thread(new Runnable(){  
			            public void run(){
			            	try {
			            		System.out.println(Timer.GetNowTimeToMillisecends()+" ѵ����Ԥ������...");
								rawDataPreprocesser.setOutputSrc(Config.preprocessedTrainDataOutputSrc);
								rawDataPreprocesser.setRawDataSrc(trainsrc);
								FileOperateAPI.DeleteFolder(Config.preprocessedTrainDataOutputSrc);
								rawDataPreprocesser.PreprocessTrainData();
								System.out.println(Timer.GetNowTimeToMillisecends()+" ���Լ�Ԥ������...");
								rawDataPreprocesser.setOutputSrc(Config.preprocessedTestDataOutputSrc);
								rawDataPreprocesser.setRawDataSrc(testsrc);
								FileOperateAPI.DeleteFolder(Config.preprocessedTestDataOutputSrc);
								ArrayList<StructuredPreproedData> testDatas=rawDataPreprocesser.PreprocessTestData();
								System.out.println(Timer.GetNowTimeToMillisecends()+" Anaylse...");
								TestLSA.extractLsaFeturesForSVM(1);
								TestLSA.svm_classify(lsasvm_c);
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
			            }});  
			        t.start();
				}
				else if(checkButton3.getSelection()) {//LDA-SVM
					if(!CheckArgs(3, argsText.getText())) {
						JOptionPane.showMessageDialog(null, "�밴��ȷ��ʽ��д����", "����������", JOptionPane.ERROR_MESSAGE);
						argsText.setText("�ڴ������㷨�еĲ����������ͣ���㷨ѡ��ؼ��ϣ��ɲ鿴������д˵��");
						return;
					}
					System.out.println("LDA-SVM:");
					testsrc=testDataSrcText.getText();
					trainsrc=trainDataSrcText.getText();
					t = new Thread(new Runnable(){  
			            public void run(){
			            	try {
			            		System.out.println(Timer.GetNowTimeToMillisecends()+" ѵ����Ԥ������...");
								rawDataPreprocesser.setOutputSrc(Config.preprocessedTrainDataOutputSrc);
								rawDataPreprocesser.setRawDataSrc(trainsrc);
								FileOperateAPI.DeleteFolder(Config.preprocessedTrainDataOutputSrc);
								rawDataPreprocesser.PreprocessTrainData();
								System.out.println(Timer.GetNowTimeToMillisecends()+" ���Լ�Ԥ������...");
								rawDataPreprocesser.setOutputSrc(Config.preprocessedTestDataOutputSrc);
								rawDataPreprocesser.setRawDataSrc(testsrc);
								FileOperateAPI.DeleteFolder(Config.preprocessedTestDataOutputSrc);
								ArrayList<StructuredPreproedData> testDatas=rawDataPreprocesser.PreprocessTestData();
								System.out.println(Timer.GetNowTimeToMillisecends()+" Anaylse...");
								TestLDA.runLdaSvm(ldatopicnum, ldasvm_c);
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
							
							
			            }});  
			        t.start();
				}
				else if(checkButton4.getSelection()) {//SL-SVM
					if(!CheckArgs(4, argsText.getText())) {
						JOptionPane.showMessageDialog(null, "�밴��ȷ��ʽ��д����", "����������", JOptionPane.ERROR_MESSAGE);
						argsText.setText("�ڴ������㷨�еĲ����������ͣ���㷨ѡ��ؼ��ϣ��ɲ鿴������д˵��");
						return;
					}
					System.out.println("SL-SVM:");
					testsrc=testDataSrcText.getText();
					trainsrc=trainDataSrcText.getText();
					t = new Thread(new Runnable(){  
			            public void run(){
			            	//Log.EndLog();
			            	//Config.isDebugMode=false;
			            	try {
			            		System.out.println(Timer.GetNowTimeToMillisecends()+" ѵ����Ԥ������...");
								rawDataPreprocesser.setOutputSrc(Config.preprocessedTrainDataOutputSrc);
								rawDataPreprocesser.setRawDataSrc(trainsrc);
								FileOperateAPI.DeleteFolder(Config.preprocessedTrainDataOutputSrc);
								rawDataPreprocesser.PreprocessTrainData();
								System.out.println(Timer.GetNowTimeToMillisecends()+" ���Լ�Ԥ������...");
								rawDataPreprocesser.setOutputSrc(Config.preprocessedTestDataOutputSrc);
								rawDataPreprocesser.setRawDataSrc(testsrc);
								FileOperateAPI.DeleteFolder(Config.preprocessedTestDataOutputSrc);
								ArrayList<StructuredPreproedData> testDatas=rawDataPreprocesser.PreprocessTestData();
								System.out.println(Timer.GetNowTimeToMillisecends()+" Anaylse...");
								SentimentAnalyse.Run(false, slsvm_l, slsvm_c);
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
							
			            }});  
			        t.start(); 
				}
				else if(checkButton5.getSelection()) {//Fast Ensemble LSA-GVM
					if(!CheckArgs(5, argsText.getText())) {
						JOptionPane.showMessageDialog(null, "�밴��ȷ��ʽ��д����", "����������", JOptionPane.ERROR_MESSAGE);
						argsText.setText("�ڴ������㷨�еĲ����������ͣ���㷨ѡ��ؼ��ϣ��ɲ鿴������д˵��");
						return;
					}
					testsrc=testDataSrcText.getText();
					trainsrc=trainDataSrcText.getText();
					t = new Thread(new Runnable(){  
			            public void run(){
			            	//Log.EndLog();
			            	//Config.isDebugMode=false;
			            	try {
			            		System.out.println("Fast Ensemble LSA-GVM:");
								System.out.println(Timer.GetNowTimeToMillisecends()+" ѵ����Ԥ������...");
								rawDataPreprocesser.setOutputSrc(Config.preprocessedTrainDataOutputSrc);
								rawDataPreprocesser.setRawDataSrc(trainsrc);
								FileOperateAPI.DeleteFolder(Config.preprocessedTrainDataOutputSrc);
								ArrayList<StructuredPreproedData> trainDatas=rawDataPreprocesser.PreprocessTrainData();
								System.out.println(Timer.GetNowTimeToMillisecends()+" ���Լ�Ԥ������...");
								rawDataPreprocesser.setOutputSrc(Config.preprocessedTestDataOutputSrc);
								rawDataPreprocesser.setRawDataSrc(testsrc);
								FileOperateAPI.DeleteFolder(Config.preprocessedTestDataOutputSrc);
								ArrayList<StructuredPreproedData> testDatas=rawDataPreprocesser.PreprocessTestData();
								System.out.println(Timer.GetNowTimeToMillisecends()+" LSA-GVM...");
								FileOperateAPI.DeleteFolder(Config.lsaGroupVoteModelOutputSrc);
								double avgdatanum=0;
								for(int i=0;i<trainDatas.size();i++) {
									avgdatanum+=trainDatas.get(i).favor.size();
									avgdatanum+=trainDatas.get(i).infavor.size();
									avgdatanum+=trainDatas.get(i).none.size();
								}
								avgdatanum=avgdatanum/trainDatas.size();
								TestLSA.runlsagvm(fp,fk,1.5*Math.sqrt(avgdatanum/1.5));
								System.out.println(Timer.GetNowTimeToMillisecends()+" Ensemble...");
								EnsembleVote.run(testDatas);
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
			            }});  
			        t.start(); 
					
				}
				else {
					JOptionPane.showMessageDialog(null, "δѡ��Ҫʹ�õ��㷨��", "����������", JOptionPane.ERROR_MESSAGE);
				}
				/*try {
					TongJi_0520_tmp.main(null);
				} catch (Exception e) {
					// TODO �Զ����ɵ� catch ��
					e.printStackTrace();
				}*/
			}
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO �Զ����ɵķ������
			}
		});
	    //outputConsoleTextData.grabExcessVerticalSpace=true;
	    //outputConsoleTextData.grabExcessHorizontalSpace=true;
	    //
	    Button readButton= new Button(shell, SWT.PUSH|SWT.CENTER);
	    readButton.setText("��ʾԤ����");
	    GridData readButtonData=new GridData();
	    readButtonData.verticalSpan=1;
	    readButtonData.horizontalSpan=5;
	    readButtonData.heightHint=28;
	    readButtonData.widthHint=360;
	    readButtonData.horizontalAlignment=SWT.FILL;
	    readButton.setLayoutData(readButtonData);
	    readButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				if(t!=null&&t.isAlive()) {
					JOptionPane.showMessageDialog(null, "��ȴ���ǰ������ִ�����", "����������", JOptionPane.ERROR_MESSAGE);
					return;
				}
				// TODO �Զ����ɵķ������
				outputConsoleText.setText("");
				if(checkButton.getSelection()) {//LSA-GVM
					if(!CheckArgs(1, argsText.getText())) {
						JOptionPane.showMessageDialog(null, "��������Ҫ��ȷ����д����ʱѡ�õĲ������ܲ鿴��Ӧ�Ľ������Ϊ��Ž�����ļ����¿��ܱ����Ų�ͬ�����µõ��Ľ��", "����������", JOptionPane.ERROR_MESSAGE);
						argsText.setText("�ڴ������㷨�еĲ����������ͣ���㷨ѡ��ؼ��ϣ��ɲ鿴������д˵��");
						return;
					}
					System.out.println("LSA-GVM:");
					testsrc=testDataSrcText.getText();
					trainsrc=trainDataSrcText.getText();
					t = new Thread(new Runnable(){  
			            public void run(){
			            	try {
			            		ArrayList<StructuredPreproedData> testOrgData=RawDataPreprocesser.LoadOrgData(testsrc,null);
			            		for(int i=0;i<testOrgData.size();i++) {
			            			StructuredPreproedData tmpdata=testOrgData.get(i);
			            			try {
			            				int [][]rst=ResultAnalyser.LoadLSAGVMResultINTARRAY(Config.lsaGroupVoteModelOutputSrc+testOrgData.get(i).className+"_"+lsagvm_n+"_"+lsagvm_k+".txt", testOrgData.get(i).className);
				            			System.out.println("Target:"+testOrgData.get(i).className);
				            			int k=0;
				            			for(int j=0;j<tmpdata.org_favor.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".Ԥ�⣺"+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("��ȷ��"+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("��ȷ��"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("��ȷ��"+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_favor.get(j));
				            			}
				            			for(int j=0;j<tmpdata.org_infavor.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".Ԥ�⣺"+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("��ȷ��"+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("��ȷ��"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("��ȷ��"+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_infavor.get(j));
				            			}
				            			for(int j=0;j<tmpdata.org_none.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".Ԥ�⣺"+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("��ȷ��"+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("��ȷ��"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("��ȷ��"+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_none.get(j));
				            			}
				            			
									} catch (Exception e) {
										// TODO: handle exception
										e.printStackTrace();
										System.out.println("�������ļ��Ƿ���ڣ�"+Config.lsaGroupVoteModelOutputSrc+testOrgData.get(i).className+"_"+lsagvm_n+"_"+lsagvm_k+".txt");
									}
			            			System.out.println("\n");
			            		}
			            	} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
			            }});  
			        t.start();
				}
				else if(checkButton2.getSelection()) {//LSA-SVM
					
					System.out.println("LSA-SVM:");
					testsrc=testDataSrcText.getText();
					trainsrc=trainDataSrcText.getText();
					t = new Thread(new Runnable(){  
			            public void run(){
			            	try {
			            		ArrayList<StructuredPreproedData> testOrgData=RawDataPreprocesser.LoadOrgData(testsrc,null);
			            		for(int i=0;i<testOrgData.size();i++) {
			            			StructuredPreproedData tmpdata=testOrgData.get(i);
			            			try {
			            				int [][]rst=svm_result.GetSVMResultFromFile(Config.lsaSvmResult+testOrgData.get(i).className+"_result.txt", testOrgData.get(i).className);
				            			System.out.println("Target:"+testOrgData.get(i).className);
				            			int k=0;
				            			for(int j=0;j<tmpdata.org_favor.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".Ԥ�⣺"+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("��ȷ��"+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("��ȷ��"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("��ȷ��"+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_favor.get(j));
				            			}
				            			for(int j=0;j<tmpdata.org_infavor.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".Ԥ�⣺"+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("��ȷ��"+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("��ȷ��"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("��ȷ��"+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_infavor.get(j));
				            			}
				            			for(int j=0;j<tmpdata.org_none.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".Ԥ�⣺"+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("��ȷ��"+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("��ȷ��"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("��ȷ��"+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_none.get(j));
				            			}
				            			
									} catch (Exception e) {
										// TODO: handle exception
										System.out.println("�������ļ��Ƿ���ڣ�"+Config.ldasvmPredictResultDir+testOrgData.get(i).className+"_result.txt"+"\n");
									}
			            			System.out.println("\n");
			            		}
			            		
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
			            }});  
			        t.start();
				}
				else if(checkButton3.getSelection()) {//LDA-SVM
					
					System.out.println("LDA-SVM:");
					testsrc=testDataSrcText.getText();
					trainsrc=trainDataSrcText.getText();
					t = new Thread(new Runnable(){  
			            public void run(){
			            	try {
			            		ArrayList<StructuredPreproedData> testOrgData=RawDataPreprocesser.LoadOrgData(testsrc,null);
			            		for(int i=0;i<testOrgData.size();i++) {
			            			StructuredPreproedData tmpdata=testOrgData.get(i);
			            			try {
			            				int [][]rst=svm_result.GetSVMResultFromFile(Config.ldasvmPredictResultDir+testOrgData.get(i).className+"_result.txt", testOrgData.get(i).className);
				            			System.out.println("Target:"+testOrgData.get(i).className);
				            			int k=0;
				            			for(int j=0;j<tmpdata.org_favor.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".Ԥ�⣺"+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("��ȷ��"+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("��ȷ��"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("��ȷ��"+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_favor.get(j));
				            			}
				            			for(int j=0;j<tmpdata.org_infavor.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".Ԥ�⣺"+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("��ȷ��"+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("��ȷ��"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("��ȷ��"+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_infavor.get(j));
				            			}
				            			for(int j=0;j<tmpdata.org_none.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".Ԥ�⣺"+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("��ȷ��"+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("��ȷ��"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("��ȷ��"+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_none.get(j));
				            			}
				            			
									} catch (Exception e) {
										// TODO: handle exception
										System.out.println("�������ļ��Ƿ���ڣ�"+Config.ldasvmPredictResultDir+testOrgData.get(i).className+"_result.txt"+"\n");
									}
			            			System.out.println("\n");
			            		}
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
			            }});  
			        t.start();
				}
				else if(checkButton4.getSelection()) {//SL-SVM
					
					System.out.println("SL-SVM:");
					testsrc=testDataSrcText.getText();
					trainsrc=trainDataSrcText.getText();
					t = new Thread(new Runnable(){  
			            public void run(){
			            	try {
			            		ArrayList<StructuredPreproedData> testOrgData=RawDataPreprocesser.LoadOrgData(testsrc,null);
			            		for(int i=0;i<testOrgData.size();i++) {
			            			StructuredPreproedData tmpdata=testOrgData.get(i);
			            			try {
			            				int [][]rst=svm_result.GetSVMResultFromFile(Config.senFeaSvmResultDir+testOrgData.get(i).className+"_result.txt", testOrgData.get(i).className);
				            			System.out.println("Target:"+testOrgData.get(i).className);
				            			int k=0;
				            			for(int j=0;j<tmpdata.org_favor.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".Ԥ�⣺"+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("��ȷ��"+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("��ȷ��"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("��ȷ��"+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_favor.get(j));
				            			}
				            			for(int j=0;j<tmpdata.org_infavor.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".Ԥ�⣺"+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("��ȷ��"+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("��ȷ��"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("��ȷ��"+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_infavor.get(j));
				            			}
				            			for(int j=0;j<tmpdata.org_none.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".Ԥ�⣺"+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("��ȷ��"+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("��ȷ��"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("��ȷ��"+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_none.get(j));
				            			}
				            			
									} catch (Exception e) {
										// TODO: handle exception
										System.out.println("�������ļ��Ƿ���ڣ�"+Config.ldasvmPredictResultDir+testOrgData.get(i).className+"_result.txt"+"\n");
									}
			            			System.out.println("\n");
			            		}
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
							
			            }});  
			        t.start(); 
				}
				else if(checkButton5.getSelection()) {//Fast Ensemble LSA-GVM
					System.out.println("Fast Ensemble LSA-GVM:");
					testsrc=testDataSrcText.getText();
					trainsrc=trainDataSrcText.getText();
					t = new Thread(new Runnable(){  
			            public void run(){
			            	try {
			            		ArrayList<StructuredPreproedData> testOrgData=RawDataPreprocesser.LoadOrgData(testsrc,null);
			            		for(int i=0;i<testOrgData.size();i++) {
			            			StructuredPreproedData tmpdata=testOrgData.get(i);
			            			try {
			            				int [][]rst=ResultAnalyser.LoadLSAGVMResultINTARRAY(Config.ensemblelsaGVMOutputSrc+testOrgData.get(i).className+".txt", testOrgData.get(i).className);
				            			System.out.println("Target:"+testOrgData.get(i).className);
				            			int k=0;
				            			for(int j=0;j<tmpdata.org_favor.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".Ԥ�⣺"+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("��ȷ��"+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("��ȷ��"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("��ȷ��"+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_favor.get(j));
				            			}
				            			for(int j=0;j<tmpdata.org_infavor.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".Ԥ�⣺"+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("��ȷ��"+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("��ȷ��"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("��ȷ��"+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_infavor.get(j));
				            			}
				            			for(int j=0;j<tmpdata.org_none.size();j++,k++) {
				            				if(rst[0][k]==Config.favor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.favor+" ");
				            				}
				            				else if(rst[0][k]==Config.infavor_int) {
				            					System.out.print(k+".Ԥ�⣺"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print(k+".Ԥ�⣺"+Config.none+" ");
				            				}
				            				if(rst[1][k]==Config.favor_int) {
				            					System.out.print("��ȷ��"+Config.favor+" ");
				            				}
				            				else if(rst[1][k]==Config.infavor_int) {
				            					System.out.print("��ȷ��"+Config.infavor+" ");
				            				}
				            				else {
				            					System.out.print("��ȷ��"+Config.none+" ");
				            				}
				            				System.out.println(tmpdata.org_none.get(j));
				            			}
				            			
									} catch (Exception e) {
										// TODO: handle exception
										e.printStackTrace();
										System.out.println("�������ļ��Ƿ���ڣ�"+Config.ensemblelsaGVMOutputSrc+testOrgData.get(i).className+".txt");
									}
			            			System.out.println("\n");
			            		}
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
			            }});  
			        t.start(); 
					
				}
				else {
					JOptionPane.showMessageDialog(null, "δѡ���㷨���", "����������", JOptionPane.ERROR_MESSAGE);
				}
				/*try {
					TongJi_0520_tmp.main(null);
				} catch (Exception e) {
					// TODO �Զ����ɵ� catch ��
					e.printStackTrace();
				}*/
			}
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO �Զ����ɵķ������
			}
		});
	    
	    MyPrintStream mps = new MyPrintStream(System.out, outputConsoleText);
	    System.setOut(mps);
	    System.setErr(mps);
	    shell.setLayout(mainUILayout);
	    shell.open();
	    // ��ʼ�¼�����ѭ����ֱ���û��رմ���
	    while (!shell.isDisposed()) {
	        if (!display.readAndDispatch())
	            display.sleep();
	    }
	    display.dispose();
	}

}
class MyPrintStream extends PrintStream {
	private Text text;
	public MyPrintStream(OutputStream out, Text text) {
		super(out);
		this.text = text;
	}
	/** *//**
	 * �������ؽ�,���еĴ�ӡ������Ҫ���õķ���
	 */
	@Override
	public void write(byte[] buf, int off, int len) {
		final String message = new String(buf, off, len);
		/**//* SWT�ǽ����̷߳�������ķ�ʽ */
		Display.getDefault().syncExec(new Thread(){
			public void run(){
				/**//* ���������Ϣ��ӵ������ */
				text.append(message);
			}
		});
	}
}
