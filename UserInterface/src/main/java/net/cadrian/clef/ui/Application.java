/*
 * This file is part of Clef.
 *
 * Clef is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * Clef is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Clef.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.cadrian.clef.ui;

import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.Beans;
import net.cadrian.clef.model.bean.Author;
import net.cadrian.clef.model.bean.Pricing;
import net.cadrian.clef.model.bean.Session;
import net.cadrian.clef.model.bean.Work;

public class Application extends JFrame {

	private static final long serialVersionUID = 6368233544150671678L;

	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

	private final Beans beans;

	public Application(final Beans beans) {
		this.beans = beans;
		initUI();
	}

	private void initUI() {
		final Resources rc = getResources();
		setLookAndFeel(false);
		setTitle(rc.getMessage("ClefTitle"));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		initComponents(rc);
		pack();
	}

	private Resources getResources() {
		final Locale locale = Locale.getDefault();
		LOGGER.info("Current locale: {}", locale);
		final ResourceBundle messages = ResourceBundle.getBundle("Clef");
		return new Resources(messages);
	}

	private void setLookAndFeel(final boolean system) {
		try {
			if (system) {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} else {
				boolean foundNimbus = false;
				for (final LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
					if ("Nimbus".equals(info.getName())) {
						LOGGER.info("Using Nimbus L&F");
						UIManager.setLookAndFeel(info.getClassName());
						foundNimbus = true;
						break;
					}
				}
				if (!foundNimbus) {
					UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				}
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			LOGGER.info("Could not load L&F", e);
		}
	}

	private void initComponents(final Resources rc) {
		final JTabbedPane mainPane = new JTabbedPane(SwingConstants.TOP);
		getContentPane().add(mainPane);

		final JTabbedPane mgtPane = new JTabbedPane(SwingConstants.TOP);

		mainPane.addTab(rc.getMessage("Sessions"), new DataPane<>(rc, beans::getSessions,
				new SessionCreator(rc, this, beans), new SessionFormModel(Session.class)));

		mgtPane.addTab(rc.getMessage("Works"),
				new DataPane<>(rc, (pane) -> pane.getSelection(), beans::getWorks, new WorkCreator(rc, this, beans),
						new WorkFormModel(beans, Work.class), Arrays.asList("Description", "Pieces")));
		mgtPane.addTab(rc.getMessage("Authors"),
				new DataPane<>(rc, beans::getAuthors, beans::createAuthor, new AuthorFormModel(Author.class)));
		mgtPane.addTab(rc.getMessage("Pricings"),
				new DataPane<>(rc, beans::getPricings, beans::createPricing, new PricingFormModel(Pricing.class)));
		mainPane.addTab(rc.getMessage("Management"), mgtPane);

		mainPane.addTab(rc.getMessage("Statistics"), new JPanel()); // TODO
	}

}
