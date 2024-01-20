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

import lombok.extern.slf4j.Slf4j;

import org.sakaiproject.db.api.SqlService;

@SuppressWarnings("deprecation")
@Slf4j
public class Util {

		/* We have to be very quiet because if we do something that has already been done, we will get an "Already exists" error. */
		/* A return value of -1 indicates any kind of error from duplicate column to SQL syntax error */
	/**
	 * Run a single SQL statement checking for error
	 *
	 * @param sqlService Our service
	 * @param note A note such as the JIRA string
	 * @param sql The SQL statement
	 *
	 * @retval The count of the number of rows affected, or -1 for some kind of error
	 *
	 * The return value is 0 for successful ALTER statements, a count of the number of rows affected
	 * for UPDATE or DELETE statements and -1 for any kind of error from not really fatal like 
	 * "Column already exists" or "SQL syntax error".  We turn on the "quietest" failure mode
	 * so the log does not fill up with unnecessary "Column already exists" errors.
	 */
	public static int runUpdateSql(SqlService sqlService, String note, String sql) {
		int failQuiet = 2;  /* Very very quiet */
		int count = sqlService.dbWriteCount(sql, null, null, null, failQuiet);
		if ( count >= 0 ) {
			log.info("{}({}): {}",note, count, sql);
		} else {
			log.debug("{}({}): {}",note, count, sql);
		}
		return count;
	}

}

