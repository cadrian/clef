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
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFrame;
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
import net.cadrian.clef.ui.ApplicationContext.AdvancedConfigurationEntry;

public class Application extends JFrame {

	private static final long serialVersionUID = 6368233544150671678L;

	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

	private final ApplicationContextImpl context;

	public Application(final Beans beans) {
		final Presentation presentation = getPresentation();
		context = new ApplicationContextImpl(beans, presentation);
		context.setValue(AdvancedConfigurationEntry.allowStartWrite, false);
		initUI();
	}

	private void initUI() {
		setLookAndFeel(false);
		setTitle(context.getPresentation().getMessage("ClefTitle"));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		initComponents();
		pack();
	}

	private Presentation getPresentation() {
		final Locale locale = Locale.getDefault();
		LOGGER.info("Current locale: {}", locale);
		final ResourceBundle messages = ResourceBundle.getBundle("Clef");
		return new Presentation(messages, this);
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

	private void initComponents() {
		final JTabbedPane mainPane = new JTabbedPane(SwingConstants.TOP);
		getContentPane().add(mainPane);

		final JTabbedPane mgtPane = new JTabbedPane(SwingConstants.TOP);
		final Beans beans = context.getBeans();

		mainPane.addTab(context.getPresentation().getMessage("Sessions"),
				new DataPane<>(context, true, beans::getSessions, new SessionCreator(context),
						(s1, s2) -> compareSessions(s1, s2), new SessionFormModel(Session.class)));

		mgtPane.addTab(context.getPresentation().getMessage("Works"),
				new DataPane<>(context, true, beans::getWorks, new WorkCreator(context),
						(w1, w2) -> compareWorks(w1, w2), new WorkFormModel(Work.class),
						Arrays.asList("Description", "Pieces")));
		mgtPane.addTab(context.getPresentation().getMessage("Authors"), new DataPane<>(context, true, beans::getAuthors,
				beans::createAuthor, (a1, a2) -> compareAuthors(a1, a2), new AuthorFormModel(Author.class)));
		mgtPane.addTab(context.getPresentation().getMessage("Pricings"),
				new DataPane<>(context, true, beans::getPricings, beans::createPricing,
						(p1, p2) -> comparePricings(p1, p2), new PricingFormModel(Pricing.class)));
		mainPane.addTab(context.getPresentation().getMessage("Management"), mgtPane);

		mainPane.addTab(context.getPresentation().getMessage("Statistics"), new StatisticsPanel(context));

		mainPane.addTab(context.getPresentation().getMessage("Configuration"), new ConfigurationPanel(context));
	}

	private static int compareSessions(final Session s1, final Session s2) {
		final Date start1 = s1.getStart();
		final Date start2 = s2.getStart();
		if (start1 == null) {
			if (start2 == null) {
				return 0;
			}
			return 1;
		}
		if (start2 == null) {
			return -1;
		}
		return start1.compareTo(start2);
	}

	private static int compareWorks(final Work w1, final Work w2) {
		final String name1 = w1.getName();
		final String name2 = w2.getName();
		return (name1 == null ? "" : name1).compareTo(name2 == null ? "" : name2);
	}

	private static int compareAuthors(final Author a1, final Author a2) {
		final String name1 = a1.getName();
		final String name2 = a2.getName();
		return (name1 == null ? "" : name1).compareTo(name2 == null ? "" : name2);
	}

	private static int comparePricings(final Pricing p1, final Pricing p2) {
		final String name1 = p1.getName();
		final String name2 = p2.getName();
		return (name1 == null ? "" : name1).compareTo(name2 == null ? "" : name2);
	}

}
