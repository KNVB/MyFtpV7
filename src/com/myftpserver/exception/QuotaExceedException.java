package com.myftpserver.exception;
/*
 * Copyright 2004-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 * @author SITO3
 *
 */
public class QuotaExceedException extends Exception 
{
	private static final long serialVersionUID = -3487638359243265219L;
	/**
	 * Checked exception thrown when a user quota exceed
	 */
	public QuotaExceedException(String msg)
	{
		//550_Quota_Exceed
		super(msg);
	}
	
}
