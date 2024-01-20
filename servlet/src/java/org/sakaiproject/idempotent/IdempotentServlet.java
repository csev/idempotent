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

	@Autowired private ServerConfigurationService serverConfigurationService;
	@Autowired private SqlService sqlService;

	@Override
	public void init(ServletConfig config) throws ServletException {

		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());

		log.debug("DB Vendor: {}", sqlService.getVendor());

		if ( ! Util.tableExists(sqlService, "SAKAI_IDEMPOTENT") ) {
			String sql = "CREATE TABLE SAKAI_IDEMPOTENT ( " +
				"NOTE VARCHAR (256) NOT NULL, " +
				"SQL_TEXT VARCHAR (1024) NOT NULL, " + 
				"CREATEDON DATETIME NULL)";

			// TODO: Test this :)
			if ( "oracle".equals(sqlService.getVendor()) ) {
				sql = "CREATE TABLE SAKAI_IDEMPOTENT ( " +
					"NOTE VARCHAR (256) NOT NULL, " +
					"SQL_TEXT VARCHAR (1024) NOT NULL, " + 
					"TIMESTAMP DATETIME NULL)";
			}

			log.info("Creating the SAKAI_IDEMPOTENT Table");
			Util.runUpdateSql(sqlService, "IDEMPOTENT-001", sql);
		}

		Sakai23.idempotent(sqlService);

	}

}

