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
public class Sakai_23_1_23_2_Conversion {

    // Note that there is one of these that covers both Oracle and MySQL
    // A lot of the simple SQL is the same.  But if the SQL needs to be
    // different just an if-then-else in the code below.  D.R.Y.

    public static void idempotent(SqlService sqlService) {

	    if ( ! Util.tableExists(sqlService, "SAKAI_IDEMPOTENT") ) {

			String sql = "CREATE TABLE PUSH_SUBSCRIPTIONS (ID BIGINT AUTO_INCREMENT NOT NULL, AUTH VARCHAR(255) NOT NULL, CREATED datetime NOT NULL, ENDPOINT VARCHAR(2048) NOT NULL, FINGERPRINT VARCHAR(255) NOT NULL, USER_ID VARCHAR(99) NOT NULL, USER_KEY VARCHAR(255) NOT NULL, CONSTRAINT PK_PUSH_SUBSCRIPTIONS PRIMARY KEY (ID), UNIQUE (FINGERPRINT));";

			Util.runUpdateSql(sqlService, "SAK-48083-01", sql);

			sql = "CREATE INDEX IDX_PUSH_SUBSCRIPTIONS_USER ON PUSH_SUBSCRIPTIONS(USER_ID);";

			Util.runUpdateSql(sqlService, "SAK-48083-02", sql);
        }

    }

}

