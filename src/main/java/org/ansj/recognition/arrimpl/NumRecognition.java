package org.ansj.recognition.arrimpl;

import org.ansj.domain.Term;
import org.ansj.recognition.TermArrRecognition;
import org.ansj.util.TermUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class NumRecognition implements TermArrRecognition {

	public static final Set<Character> j_NUM = new HashSet<>();
	public static final Set<Character> f_NUM = new HashSet<>();

	static {
		j_NUM.add('零');
		j_NUM.add('一');
		j_NUM.add('二');
		j_NUM.add('三');
		j_NUM.add('四');
		j_NUM.add('五');
		j_NUM.add('六');
		j_NUM.add('七');
		j_NUM.add('八');
		j_NUM.add('九');
		j_NUM.add('十');
		j_NUM.add('百');
		j_NUM.add('千');
		j_NUM.add('万');
		j_NUM.add('亿');

		f_NUM.add('零');
		f_NUM.add('壹');
		f_NUM.add('贰');
		f_NUM.add('叁');
		f_NUM.add('肆');
		f_NUM.add('伍');
		f_NUM.add('陆');
		f_NUM.add('柒');
		f_NUM.add('捌');
		f_NUM.add('玖');
		f_NUM.add('拾');
		f_NUM.add('佰');
		f_NUM.add('仟');
		f_NUM.add('万');
		f_NUM.add('亿');

	}

	;

	private boolean quantifierRecognition;

	public NumRecognition(boolean quantifierRecognition) {
		this.quantifierRecognition = quantifierRecognition;
	}

	/**
	 * 数字+数字合并,zheng
	 *
	 * @param terms
	 */
	@Override
	public void recognition(Term[] terms) {
		int length = terms.length - 1;
		Term from = null;
		Term to = null;
		Term temp = null;
		for (int i = 0; i < length; i++) {
			temp = terms[i];

			if (temp == null) {
				continue;
			}

			if (!temp.termNatures().numAttr.num) {
				continue;
			}

			if(temp.getName().length()==1){
				if (j_NUM.contains(temp.getName().charAt(0))){
					to = temp.to() ;
					while(to.getName().length()==1 && j_NUM.contains(to.getName().charAt(0))){
						temp.setName(temp.getName()+to.getName());
						terms[to.getOffe()] = null ;
						TermUtil.termLink(temp, to.to());
						to = to.to() ;

					}
				}

				if(temp.getName().length()>1){
					i-- ;
					continue;
				}

				if (f_NUM.contains(temp.getName().charAt(0))){
					to = temp.to() ;
					while(to.getName().length()==1 && f_NUM.contains(to.getName().charAt(0))){
						temp.setName(temp.getName()+to.getName());
						terms[to.getOffe()] = null ;
						TermUtil.termLink(temp, to.to());
						to = to.to() ;
					}
				}
				if(temp.getName().length()>1){
					i-- ;
					continue;
				}

				if (".".equals(temp.getName()) || "．".equals(temp.getName())) { //修复小数
					// 如果是.前后都为数字进行特殊处理
					to = temp.to();
					from = temp.from();
					if (to.to().getName().equals(temp.getName())) { //防止123.231.123
						continue;
					}
					if (from.from().getName().equals(temp.getName())) { //防止123.231.123
						continue;
					}
					if (from.termNatures().numAttr.num && to.termNatures().numAttr.num) {
						from.setName(from.getName() + "." + to.getName());
						TermUtil.termLink(from, to.to());
						terms[to.getOffe()] = null;
						terms[i] = null;
					}
					i = from.getOffe() - 1;
					continue;
				}

			}


			if (quantifierRecognition) { //开启量词识别
				to = temp.to();
				if (to.termNatures().numAttr.qua) {
					temp.setName(temp.getName() + to.getName());
					terms[to.getOffe()] = null;
					TermUtil.termLink(temp, to.to());
					temp.setNature(to.termNatures().numAttr.nature);

					if ("m".equals(to.termNatures().numAttr.nature.natureStr)) {
						i--;
					} else {
						i = to.getOffe();
					}
				}
			}


		}

	}


}
