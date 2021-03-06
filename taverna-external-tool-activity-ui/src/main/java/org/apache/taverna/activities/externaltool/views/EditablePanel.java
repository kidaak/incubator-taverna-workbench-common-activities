/**
 * 
 */
package org.apache.taverna.activities.externaltool.views;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.uni_luebeck.inb.knowarc.usecases.UseCaseDescription;
import de.uni_luebeck.inb.knowarc.usecases.UseCaseEnumeration;

import org.apache.taverna.activities.externaltool.ExternalToolActivityConfigurationBean;
import org.apache.taverna.activities.externaltool.utils.Tools;
import org.apache.taverna.lang.ui.DeselectingButton;

/**
 * @author alanrw
 *
 */
public class EditablePanel extends JPanel {
	public EditablePanel(final ExternalToolConfigView view) {
		super(new FlowLayout());
		
		JButton update = new DeselectingButton("Update tool description",
				new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ExternalToolActivityConfigurationBean bean = view.getConfiguration();
				String repositoryUrl = bean.getRepositoryUrl();
				String id = bean.getExternaltoolid();
				UseCaseDescription usecase = null;
				try {
					usecase = UseCaseEnumeration.readDescriptionFromUrl(
						repositoryUrl, id);
				}
				catch (IOException ex) {
					// Already logged
				}
				if (usecase != null) {
					bean.setUseCaseDescription(usecase);
					view.refreshConfiguration(bean);
				} else {
					JOptionPane.showMessageDialog(view, "Unable to find tool description " + id, "Missing tool description", JOptionPane.ERROR_MESSAGE);
				}
			}});
		this.add(update);
		
		JButton makeEditable = new DeselectingButton("Edit tool description",
				new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ExternalToolActivityConfigurationBean config = view.makeConfiguration();
				view.setEditable(true, config);
				
			}
		});
		makeEditable.setToolTipText("Edit the tool description");
		if (Tools.areAllUnderstood(view.getConfiguration().getUseCaseDescription().getInputs())) {
		this.add(makeEditable);
		}
		
	}

}
