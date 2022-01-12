package com.nobot.plugin.dice.expressionAnalyzer;

import com.nobot.plugin.dice.entity.COCCard;
import lombok.NonNull;
import lombok.var;
import net.sourceforge.jeval.Evaluator;
import net.sourceforge.jeval.function.math.Exp;

import java.util.*;

public class ExpressionSorter implements SpecialSymbol
{
	public static SingleExpression getSingleExpression(@NonNull Random random,@NonNull String s)
	{
		var chars=s.toCharArray();
		var justNum=true;
		for (char c : chars)
		{
			if(c<'0'||c>'9')
			{
				justNum=false;
				break;
			}
		}
		if(justNum)
			return new JustNum(s);
		else
			return new RandomExpression(random,s);
	}
}
