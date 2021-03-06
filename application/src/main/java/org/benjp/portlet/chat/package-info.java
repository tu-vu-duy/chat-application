/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

@Application(defaultController = ChatApplication.class)
@Portlet(name="ChatPortlet")
@Bindings(
        {
                @Binding(value = org.exoplatform.services.organization.OrganizationService.class, implementation=GateInMetaProvider.class),
                @Binding(value = org.exoplatform.social.core.space.spi.SpaceService.class, implementation=GateInMetaProvider.class)
        }
)

@Assets(
        location = AssetLocation.SERVER,
        scripts = {
                @Script(src = "js/jquery-1.7.1.min.js"),
                @Script(src = "js/jquery-juzu-utils-0.1.0.js"),
                @Script(src = "js/chat.js"),
                @Script(src = "js/sh_main.min.js"),
                @Script(src = "js/sh_html.min.js"),
                @Script(src = "js/sh_java.min.js"),
                @Script(src = "js/sh_javascript.min.js"),
                @Script(src = "js/sh_css.min.js")
        },
        stylesheets = {
                @Stylesheet(src = "/org/benjp/assets/bootstrap.css", location = AssetLocation.CLASSPATH),
                @Stylesheet(src = "css/chat.css"),
                @Stylesheet(src = "css/notif.css"),
                @Stylesheet(src = "css/sh_style.css")
        }
)


package org.benjp.portlet.chat;

import juzu.Application;
import juzu.asset.AssetLocation;
import juzu.plugin.asset.Assets;
import juzu.plugin.asset.Script;
import juzu.plugin.asset.Stylesheet;
import juzu.plugin.binding.Binding;
import juzu.plugin.binding.Bindings;
import juzu.plugin.portlet.Portlet;
import org.benjp.portlet.notification.GateInMetaProvider;
