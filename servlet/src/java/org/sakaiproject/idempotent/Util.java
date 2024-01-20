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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.sakaiproject.db.api.SqlService;

@SuppressWarnings("deprecation")
@Slf4j
public class Util {

    public static String NUMBER_TYPE = "java.lang.Number";
    public static String STRING_TYPE = "java.lang.String";

	/**
	 * Get the Metadata for a table
	 */
	public static ResultSetMetaData getMetadata(SqlService sqlService, String table) {

            String query = "SELECT * FROM " + table;
            Connection conn = null;
            Statement st = null;
            ResultSet rs = null;
            ResultSetMetaData md = null;
            boolean failed = false;
            try {
                conn = sqlService.borrowConnection();
                st = conn.createStatement();
                rs =  st.executeQuery(query);
                md = rs.getMetaData();
            } catch (SQLException e) {
                failed = true;
            } finally {
                try {
                    if ( st != null ) st.close();
                    if ( rs != null ) rs.close();
                } catch (SQLException sqlex) {
                    log.error("Error attempt to close Statement or ResultSet", sqlex);
                }
                if ( conn != null ) sqlService.returnConnection(conn);
            }

			String name = null;
			String sqlType = null;
            boolean autoIncrement = false;
            int sqlLength = -1;
            boolean isNullable = false;
			try {
                for( int i = 1; i <= md.getColumnCount(); i++ ) {
                        name = md.getColumnLabel(i);
                        sqlLength = md.getColumnDisplaySize(i);
                        autoIncrement = md.isAutoIncrement(i);
                        sqlType = getSuperType(md.getColumnClassName(i));
                        isNullable = (md.isNullable(i) == ResultSetMetaData.columnNullable);
						log.info("{} {} {}",name, sqlType, sqlLength);
                }
            } catch(Exception e) {
				e.printStackTrace();
                // ignore
            }


			return md;
	}
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

    // Walk the superclass tree to find a more general class to make portability easier
    // Mostly this marks the various extensions of java.lang.Number as java.lang.Number
    // to simplify casting
    public static String getSuperType(String className)
    {
        try {
            Class c = Class.forName(className);
            while ( c != null ) {
                if ( STRING_TYPE.equals(c.getName()) ) return STRING_TYPE;
                if ( NUMBER_TYPE.equals(c.getName()) ) return NUMBER_TYPE;
                c = c.getSuperclass();
            }
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
        return className;
    }
}

