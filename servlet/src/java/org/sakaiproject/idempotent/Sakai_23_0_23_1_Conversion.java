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
public class Sakai_23_0_23_1_Conversion {

    // Note that there is one of these that covers both Oracle and MySQL
    // A lot of the simple SQL is the same.  But if the SQL needs to be
    // different just an if-then-else in the code below.  D.R.Y.

    public static void idempotent(SqlService sqlService) {

        Util.ensureColumnExists(sqlService,
            "SAK-48948",
            "PINNED_SITES", "HAS_BEEN_UNPINNED",
            "ALTER TABLE PINNED_SITES ADD HAS_BEEN_UNPINNED BIT NOT NULL;"
        );

        // We do not want this set of patches to !plussite to run too early (i.e. Pre 23.0)
        // so we check for the existence of !plussite using a COUNT query.  But once it is found
        // each of the patches to it runs once and exactly once - this way admins can adjust !plussite
        // after the migration and not have the migration keep stomping their changes for all time.

        if ( Util.getCount(sqlService, "SELECT COUNT(*) FROM SAKAI_SITE WHERE SITE_ID='!plussite'") > 0 ) {
            Util.runMigrationOnce(sqlService,
                "SAK-49537",
                "UPDATE SAKAI_SITE_PAGE SET LAYOUT='0' WHERE PAGE_ID='!plussite-100';"
            );
            Util.runMigrationOnce(sqlService,
                "SAK-49537",
                "UPDATE SAKAI_SITE_PAGE SET LAYOUT='0' WHERE TITLE='Dashboard';"
            );

            Util.runMigrationOnce(sqlService,
                "SAK-49633",
                "UPDATE SAKAI_SITE SET TYPE='course' WHERE SITE_ID = '!plussite';"
            );

            Util.runMigrationOnce(sqlService,
                "SAK-49652",
                "UPDATE SAKAI_SITE SET CUSTOM_PAGE_ORDERED='1' WHERE SITE_ID='!plussite';"
            );
        }

        Util.runMigrationOnce(sqlService,
            "SAK-49584",
            "UPDATE GB_GRADEBOOK_T SET GRADE_TYPE = 1 WHERE GRADE_TYPE = 0;"
        );
    }

}

