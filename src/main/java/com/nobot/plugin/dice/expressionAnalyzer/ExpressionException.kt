package com.nobot.plugin.dice.expressionAnalyzer

import java.lang.Exception

class ExpressionException(val expression: String, val info: String, val userNum: Long?, val groupNum: Long?) : Exception()
