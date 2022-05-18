package com.chrislaforetsoftware.mockingcontext;

import com.chrislaforetsoftware.mockingcontext.annotation.mockingcontext.MockingContextAutowired;
import com.chrislaforetsoftware.mockingcontext.annotation.mockingcontext.MockingContextComponent;

import java.math.BigDecimal;

@MockingContextComponent
public class HandlerClassDependent {

	public IHandler<String, BigDecimal> stringBigDecimalIHandler;

	public HandlerClassDependent(IHandler<String, BigDecimal> stringBigDecimalIHandler) {
		this.stringBigDecimalIHandler = stringBigDecimalIHandler;
	}
}
