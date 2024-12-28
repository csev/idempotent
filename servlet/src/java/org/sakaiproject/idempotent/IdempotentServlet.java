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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.util.ResourceLoader;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

@SuppressWarnings("deprecation")
@Slf4j
public class IdempotentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static ResourceLoader rb = new ResourceLoader("idempotent");

    public static String SAKAI_IDEMPOTENT_ENABLED = "idempotent.enabled";
    public static boolean SAKAI_IDEMPOTENT_ENABLED_DEFAULT = false;

    @Autowired private ServerConfigurationService serverConfigurationService;
    @Autowired private SqlService sqlService;

    @Override
    public void init(ServletConfig config) throws ServletException {

        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());

        if ( ! serverConfigurationService.getBoolean(SAKAI_IDEMPOTENT_ENABLED, SAKAI_IDEMPOTENT_ENABLED_DEFAULT)) {
            log.debug("Idempotent not enabled, enable with {}=true", SAKAI_IDEMPOTENT_ENABLED);
            return;
        }

        log.debug("Idempotent enabled. DB Vendor: {}", sqlService.getVendor());

        // TODO:  Fix the Oracle TODOs
        if ( Util.ORACLE.equals(sqlService.getVendor()) ) {
            log.error("There is still more work to do for Idempotent to work with Oracle");
            return;
        }

        Util.ensureIdempotentTable(sqlService);

        // https://github.com/sakaiproject/sakai-reference/blob/master/docs/conversion/sakai_23_0-23_1_mysql_conversion.sql
        Sakai_23_0_23_1_Conversion.idempotent(getServletContext(), sqlService);

        Sakai_23_1_23_2_Conversion.idempotent(getServletContext(), sqlService);

        Sakai_23_2_23_3_Conversion.idempotent(getServletContext(), sqlService);

    }

}

