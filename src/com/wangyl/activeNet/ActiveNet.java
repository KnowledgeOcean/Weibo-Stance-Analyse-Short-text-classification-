package com.wangyl.activeNet;

import java.util.HashMap;

/**
 * 
 * @author WangYunli
 *
 */
public class ActiveNet {
	double [][]connectionNet;
	HashMap<String, Integer> wordIdMap=new HashMap<String, Integer>();
	HashMap<String, Integer> conceptionIdMap=new HashMap<String, Integer>();
	public void AddOneStream(String stream,String conception) {
		
	}
	public void SetConceptions() {
		
	}
	/**
	 * @description ʹ��2*sigmoid-1��Ϊstrength����
	 * @param x
	 * @return
	 * @date 2017.4.7 δ���
	 */
	public double derivativeOfSigmoid(double y) throws Exception{
		if(Math.abs(y-1)<0.00001||Math.abs(y+1)<0.00001) {
			return 0;
		}
		else if(y>1||y<-1) {
			throw new Exception("strength�ĺ���ֵ���ܴ���1");
		}
		else {
			return 0;
		}
	}
}
