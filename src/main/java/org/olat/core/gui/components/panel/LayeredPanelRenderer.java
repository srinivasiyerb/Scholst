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
 * Copyright (c) frentix GmbH<br>
 * http://www.frentix.com<br>
 * <p>
 */
package org.olat.core.gui.components.panel;

import java.util.List;

import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.ComponentRenderer;
import org.olat.core.gui.render.RenderResult;
import org.olat.core.gui.render.Renderer;
import org.olat.core.gui.render.RenderingState;
import org.olat.core.gui.render.StringOutput;
import org.olat.core.gui.render.URLBuilder;
import org.olat.core.gui.translator.Translator;

/**
 * Description:<br>
 * Renderer for the LayeredPanel. Renders the layers with a class "b_layeredPanel" and a z-index style starting with the configuration on the LayeredPanel component.
 * <P>
 * Initial Date: 28.10.2010 <br>
 * 
 * @author gnaegi
 */
public class LayeredPanelRenderer extends PanelRenderer implements ComponentRenderer {

	/**
	 * 
	 */
	public LayeredPanelRenderer() {
		//
	}

	/**
	 * @see org.olat.core.gui.render.ui.ComponentRenderer#render(org.olat.core.gui.render.Renderer, org.olat.core.gui.render.StringOutput,
	 *      org.olat.core.gui.components.Component, org.olat.core.gui.render.URLBuilder, org.olat.core.gui.translator.Translator, org.olat.core.gui.render.RenderResult,
	 *      java.lang.String[])
	 */
	@Override
	public void render(Renderer renderer, StringOutput sb, Component source, URLBuilder ubu, Translator translator, RenderResult renderResult, String[] args) {

		LayeredPanel panel = (LayeredPanel) source;
		int layerLevel = panel.getStartLayerIndex();
		int increment = panel.getIndexIncrement();
		List<Component> layers = panel.getLayers();
		// Render lower layers first, highest last
		int level = 0;
		for (Component component : layers) {
			if (component.getSpanAsDomReplaceable()) {
				sb.append("<span");
			} else {
				sb.append("<div");
			}
			sb.append(" class=\"b_layeredPanel b_layer_").append(level).append("\" style=\"z-index:").append(layerLevel).append("\">");
			renderer.render(sb, component, args);
			if (component.getSpanAsDomReplaceable()) {
				sb.append("</span>");
			} else {
				sb.append("</div>");
			}

			// Increment layer z-index for next layer iteration
			layerLevel += increment;
			level++;
		}
	}

	/**
	 * @see org.olat.core.gui.render.ui.ComponentRenderer#renderHeaderIncludes(org.olat.core.gui.render.Renderer, org.olat.core.gui.render.StringOutput,
	 *      org.olat.core.gui.components.Component, org.olat.core.gui.render.URLBuilder, org.olat.core.gui.translator.Translator)
	 */
	@Override
	public void renderHeaderIncludes(Renderer renderer, StringOutput sb, Component source, URLBuilder ubu, Translator translator, RenderingState rstate) {
		LayeredPanel panel = (LayeredPanel) source;
		List<Component> layers = panel.getLayers();
		//
		for (Component component : layers) {
			if (component != null) {
				// delegate header rendering to the content
				renderer.renderHeaderIncludes(sb, component, rstate);
			}
		}
	}

	/**
	 * @see org.olat.core.gui.render.ui.ComponentRenderer#renderBodyOnLoadJSFunctionCall(org.olat.core.gui.render.Renderer, org.olat.core.gui.render.StringOutput,
	 *      org.olat.core.gui.components.Component)
	 */
	@Override
	public void renderBodyOnLoadJSFunctionCall(Renderer renderer, StringOutput sb, Component source, RenderingState rstate) {
		LayeredPanel panel = (LayeredPanel) source;
		List<Component> layers = panel.getLayers();
		//
		for (Component component : layers) {
			if (component != null) {
				// delegate header rendering to the content
				renderer.renderBodyOnLoadJSFunctionCall(sb, component, rstate);
			}
		}
	}

}
