/*******************************************************************************
 * Copyright (C) 2010 Hajo Nils Krabbenhoeft, spratpix GmbH & Co. KG
 *
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/

package org.apache.taverna.activities.externaltool.menu;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.apache.taverna.ui.menu.AbstractMenuAction;

import org.apache.log4j.Logger;

/**
 * This class adds the feedback item to the context menu of every use case
 * activity.
 *
 * @author Hajo Nils Krabbenhoeft
 */
public class FeedbackMenuAction extends AbstractMenuAction {

	private static Logger logger = Logger.getLogger(FeedbackMenuAction.class);


	private static final URI feedbackSection = URI.create("http://taverna.sf.net/2009/contextMenu/configure");

	public FeedbackMenuAction() {
		super(feedbackSection, 51);
	}

	protected Action createAction() {
	    // final ImageIcon icon = KnowARCConfigurationFactory.getConfiguration().getIcon();
		return new SendFeedbackAction("Send Feedback...", null);
	}

	private final class SendFeedbackAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		private static final String errTitle = "Could not open web browser for feedback:";
		private static final String feedbackUrl = "http://www.taverna.org.uk/about/contact-us/feedback?product=ExternalToolService";

		private SendFeedbackAction(String name, Icon icon) {
			super(name, icon);
		}

		public void actionPerformed(ActionEvent e) {
			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().browse(URI.create(feedbackUrl));
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, feedbackUrl + "\n" + e1.getLocalizedMessage(), errTitle, JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(null, "Go to " + feedbackUrl, errTitle, JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
