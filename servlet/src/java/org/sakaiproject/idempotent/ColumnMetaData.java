/*
 * $URL$
 * $Id$
 *
 * Copyright (c) 2024- Charles R. Severance
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */

package org.sakaiproject.idempotent;

public class ColumnMetaData {

    public static String NUMBER_TYPE = "java.lang.Number";
    public static String STRING_TYPE = "java.lang.String";

	public String table = null;
	public static String name = null;
    public static String sqlType = null;
    public static boolean autoIncrement = false;
    public static int sqlLength = -1;
    public static boolean isNullable = false;

	@Override
	public String toString() {
		String retval = this.table + "." + this.name + " " + this.sqlType + " (" +this.sqlLength + ")";
		if ( this.isNullable ) retval += " NULL";
		return retval;
	}
}

