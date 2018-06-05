package com.aspire.service;

import javax.jws.WebService;

@WebService
public interface MyService {
	int add(int a, int b);

	int minus(int a, int b);
}
