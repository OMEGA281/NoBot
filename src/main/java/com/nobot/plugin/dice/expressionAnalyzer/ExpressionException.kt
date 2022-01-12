package com.nobot.plugin.dice.expressionAnalyzer

class ExpressionException(private val expression: String?,private val info: String?) : RuntimeException()