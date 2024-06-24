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

import javax.servlet.ServletContext;

import lombok.extern.slf4j.Slf4j;

import org.sakaiproject.db.api.SqlService;

@SuppressWarnings("deprecation")
@Slf4j
public class Sakai_23_25_Conversion {

    // Note that there is one of these that covers both Oracle and MySQL
    // A lot of the simple SQL is the same.  But if the SQL needs to be
    // different just an if-then-else in the code below.  D.R.Y.

    public static void idempotent(ServletContext context, SqlService sqlService) {

        // SAK-46714
        String [] lines = Util.fileToArray(context, "/scripts/SAK-46714.sql");
        for (int i=0; i<lines.length-1; i++) {
            String sql = lines[i].trim();
            if ( sql.startsWith("--") ) continue;
            // System.out.println(i+" "+sql);
            Util.runUpdateSql(sqlService, "SAK-46714", sql);
        }

    }

}

