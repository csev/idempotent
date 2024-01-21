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
public class Sakai23 {

    public static void idempotent(SqlService sqlService) {

        Util.ensureColumnExists(sqlService,
            "SAK-48948",
            "PINNED_SITES", "HAS_BEEN_UNPINNED",
            "ALTER TABLE PINNED_SITES ADD HAS_BEEN_UNPINNED BIT NOT NULL;"
        );

        // Only apply the !plussite template fixes if the site is there and then only
		// do each migration once
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
    }

}

