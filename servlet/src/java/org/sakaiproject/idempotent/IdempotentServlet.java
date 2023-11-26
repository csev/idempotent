/**
 * $URL$
 * $Id$
 *
 * Copyright (c) 2009- The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *			 http://www.opensource.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

import java.time.Instant;

import org.sakaiproject.component.api.ServerConfigurationService;
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

	@Override
	public void init(ServletConfig config) throws ServletException {

		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);

		ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());

System.out.println("Idempotent is Alive!!!!");
	}

}

