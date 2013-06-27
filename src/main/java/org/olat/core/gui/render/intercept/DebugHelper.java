/**
 * OLAT - Online Learning and Training<br>
 * http://www.olat.org
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Copyright (c) 1999-2006 at Multimedia- & E-Learning Services (MELS),<br>
 * University of Zurich, Switzerland.
 * <p>
 */
package org.olat.core.gui.render.intercept;

import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.panel.Panel;

/**
 * Description:<br>
 * TODO: Felix Jost Class Description
 * <P>
 * Initial Date: 25.01.2007 <br>
 * 
 * @author Felix Jost, http://www.goodsolutions.ch
 */
public class DebugHelper {
	private static final String MAGIC_KEY = "o_debug_mkEscDebug";

	/**
	 * to wrap a component in panel to disallow appending of debug-information. used only by e.g. the development controller
	 * 
	 * @return a protected panel
	 */
	public static Component createDebugProtectedWrapper(Component comp) {
		Panel p = new Panel(MAGIC_KEY);
		p.setContent(comp);
		return p;
	}

	/**
	 * @param source
	 * @return true if in the self-or-parent hierarchy the magic key occurs, meaning to not gui-debug this component
	 */
	public static boolean isProtected(Component source) {
		do {
			String cname = source.getComponentName();
			if (cname != null && cname.equals(MAGIC_KEY)) { return true; }
		} while ((source = source.getParent()) != null);
		return false;
	}

}
