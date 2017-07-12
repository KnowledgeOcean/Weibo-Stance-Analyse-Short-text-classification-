package com.wangyl.lda;



import java.util.ArrayList;

import com.wangyl.config.Config;
import com.wangyl.preprocesser.RawDataPreprocesser;
import com.wangyl.preprocesser.StructuredPreproedData;

import jgibblda.Estimator;
import jgibblda.Inferencer;
import jgibblda.LDACmdOption;
import jgibblda.Model;

/**
 *@Option(name="-est", usage="Specify whether we want to estimate model from scratch")
 @Default est = false;�Ƿ���㿪ʼ����ģ�ͣ�
 @Option(name="-estc", usage="Specify whether we want to continue the last estimation")
 @Default estc = false; �Ƿ������һ�εĹ��ƣ�
 *@Option(name="-inf", usage="Specify whether we want to do inference")
 *@Default inf = true;�Ƿ�Ҫ���ƶϣ�
 *@Option(name="-dir", usage="Specify directory")
 *@Default dir = "";Ŀ¼
 *@Option(name="-dfile", usage="Specify data file")
 *@Default dfile = "";�����ļ�����
 *@Option(name="-model", usage="Specify the model name")
 *@Default modelName = "";ѡ��ʹ����һ��������ģ�ͽ���������ƶ�,һ����model-final
 *@Option(name="-alpha", usage="Specify alpha")
 *@Default alpha = -1.0D;LDA�е�alpha����
 *@Option(name="-beta", usage="Specify beta")
 *@Default beta = -1.0D;LDA�е�beta����
 @Option(name="-ntopics", usage="Specify the number of topics")
 @Default K = 100;�������Ŀ
 @Option(name="-niters", usage="Specify the number of iterations")
 @Default niters = 1000;��������
 @Option(name="-savestep", usage="Specify the number of steps to save the model since the last save")
 @Default savestep = 100;ָ�����ٲ�����֮�󱣴�һ��ģ�ͣ�
 @Option(name="-twords", usage="Specify the number of most likely words to be printed for each topic")
 @Default twords = 100;ÿ�������г����ٸ�����ܵĴ�������������top n��
 @Option(name="-withrawdata", usage="Specify whether we include raw data in the input")
 @Default withrawdata = false;ѵ�������Ƿ����raw data
 @Option(name="-wordmap", usage="Specify the wordmap file")
 @Default wordMapFileName = "wordmap.txt";ָ��wordmap�ļ�
 @Detest ���ݼ�Ҫ�󣺵�һ����total num
 */

public class LDA_api
{
	public LDACmdOption option = new LDACmdOption();
	public LDA_api() {}
	
	public void Analyse()
	{
		if ((option.est) || (option.estc)) {
			Estimator estimator = new Estimator();
			estimator.init(option);
			estimator.estimate();
		}
		else if (option.inf) {
			Inferencer inferencer = new Inferencer();
			inferencer.init(option);
			Model newModel = inferencer.inference(); 
			/*for (int i = 0; i < newModel.phi.length; i++)
			{
				System.out.println("-----------------------\ntopic" + i + " : ");
				for (int j = 0; j < 10; j++) {
					System.out.println((String)inferencer.globalDict.id2word.get(Integer.valueOf(j)) + "\t" + newModel.phi[i][j]);
				}
			}*/
		}
	}
	public static void Infer(String dir,String inputfilename,int n) {
		LDA_api mylda=new LDA_api();
		mylda.option.dir=dir;
		mylda.option.dfile=inputfilename;
		mylda.option.est=false;
		mylda.option.estc=false;
		mylda.option.inf=true;
		mylda.option.modelName="model-final";
		mylda.option.K=n;
		mylda.option.savestep=1000;
		//mylda.option.alpha=0.1;
		mylda.option.beta=0.01;
		mylda.option.twords=50;
		mylda.Analyse();
	}
	private static void GenerateModelUseDefaultPara(String dir,String inputfilename,int n) {
		LDA_api mylda=new LDA_api();
		mylda.option.dir=dir;
		mylda.option.dfile=inputfilename;
		mylda.option.est=true;
		mylda.option.estc=false;
		mylda.option.inf=false;
		mylda.option.modelName="model-final";
		mylda.option.K=n;
		mylda.option.savestep=1000;
		//mylda.option.alpha=0.1;
		mylda.option.beta=0.01;
		mylda.option.twords=50;
		mylda.Analyse();
	}
	/**
	 * @description Ϊÿ����������һ��ldaģ�ͣ�������LDAԤ���������ֻ�����raw data��Ԥ������
	 */
	public static void GenerateModelsForEachTagert(String modelDir,int n) {
		ArrayList<StructuredPreproedData> spd;
		try {
			spd = RawDataPreprocesser.LoadTrainDataFromPreproedFile();
			for(int i=0;i<spd.size();i++) {
				RawDataPreprocesser.PreproForLDA(spd.get(i).className);
				GenerateModelUseDefaultPara(modelDir+spd.get(i).className+"\\",spd.get(i).className+".txt",n);
			}
			
			//Test(Config.LDAInputDir+"���ڽ�Ħ�޵�\\", "���ڽ�Ħ�޵�_AGAINST_0.txt");
		} catch (Exception e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
	}
	public static void GenerateUniversalModel(int n) {
		GenerateModelUseDefaultPara(Config.LDAInputDir, Config.LDAInputFileName,n);
	}
}