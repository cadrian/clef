/*
 * Copyright (C) 2018-2019 Cyril Adrian <cyril.adrian@gmail.com>
 *
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
 */
package net.cadrian.clef.ui.app;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.Beans;
import net.cadrian.clef.model.bean.Activity;
import net.cadrian.clef.model.bean.Author;
import net.cadrian.clef.model.bean.BeanComparators;
import net.cadrian.clef.model.bean.Pricing;
import net.cadrian.clef.model.bean.Session;
import net.cadrian.clef.model.bean.Work;
import net.cadrian.clef.ui.ApplicationContext.AdvancedConfigurationEntry;
import net.cadrian.clef.ui.app.form.creator.SessionCreator;
import net.cadrian.clef.ui.app.form.creator.WorkCreator;
import net.cadrian.clef.ui.app.form.field.AbstractSimpleFieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.AbstractSimpleFieldModel;
import net.cadrian.clef.ui.app.form.model.ActivityFormModel;
import net.cadrian.clef.ui.app.form.model.AuthorFormModel;
import net.cadrian.clef.ui.app.form.model.PricingFormModel;
import net.cadrian.clef.ui.app.form.model.SessionFormModel;
import net.cadrian.clef.ui.app.form.model.WorkFormModel;
import net.cadrian.clef.ui.app.tab.ConfigurationPanel;
import net.cadrian.clef.ui.app.tab.DataPane;
import net.cadrian.clef.ui.app.tab.HelpPanel;
import net.cadrian.clef.ui.app.tab.StatisticsPanel;
import net.cadrian.clef.ui.app.tab.filter.SessionFilter;

public class Application extends JFrame {

	private final class ApplicationWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(final WindowEvent e) {
			context.setClosing(true);
			if (sessionsPanel.isDirty() || worksPanel.isDirty() || authorsPanel.isDirty()
					|| pricingsPanel.isDirty() | configurationPanel.isDirty()) {
				LOGGER.info("unsaved work on exit: asking confirmation");

				final int response = JOptionPane.showConfirmDialog(Application.this,
						context.getPresentation().getMessage("ConfirmExitMessage"),
						context.getPresentation().getMessage("ConfirmExitTitle"), JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				switch (response) {
				case JOptionPane.YES_OPTION:
					LOGGER.info("User asked to save before exit.");
					sessionsPanel.saveData();
					worksPanel.saveData();
					authorsPanel.saveData();
					pricingsPanel.saveData();
					configurationPanel.saveData();
					break;
				case JOptionPane.NO_OPTION:
					LOGGER.info("USER CONFIRMED DIRTY EXIT.");
					break;
				case JOptionPane.CANCEL_OPTION:
				case JOptionPane.CLOSED_OPTION:
					LOGGER.info("User did not confirm, aborting exit");
					return;
				}
			}
			dispose();
		}
	}

	private final class MainPaneRefresher implements ChangeListener {
		private final JTabbedPane mgtPane;
		private final JTabbedPane mainPane;

		private MainPaneRefresher(final JTabbedPane mgtPane, final JTabbedPane mainPane) {
			this.mgtPane = mgtPane;
			this.mainPane = mainPane;
		}

		@Override
		public void stateChanged(final ChangeEvent e) {
			refreshMainPane(mainPane.getSelectedIndex(), mgtPane.getSelectedIndex());
		}
	}

	private final class MgtPaneRefresher implements ChangeListener {
		private final JTabbedPane mgtPane;

		private MgtPaneRefresher(final JTabbedPane mgtPane) {
			this.mgtPane = mgtPane;
		}

		@Override
		public void stateChanged(final ChangeEvent e) {
			refreshMgtPane(mgtPane.getSelectedIndex());
		}
	}

	private static final long serialVersionUID = 6368233544150671678L;

	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

	private final ApplicationContextImpl context;

	private final DataPane<Session> sessionsPanel;
	private final DataPane<Work> worksPanel;
	private final DataPane<Author> authorsPanel;
	private final DataPane<Pricing> pricingsPanel;
	private final DataPane<Activity> activitiesPanel;
	private final StatisticsPanel statisticsPanel;
	private final ConfigurationPanel configurationPanel;
	private final HelpPanel helpPanel;

	public Application(final Beans beans) {
		final PresentationImpl presentation = new PresentationImpl(this);
		context = new ApplicationContextImpl(beans, presentation);
		context.setValue(AdvancedConfigurationEntry.offlineMode, false);

		setLookAndFeel(false);
		setTitle(presentation.getMessage("ClefTitle"));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		AbstractSimpleFieldComponentFactory.installCacheListener(context);
		AbstractSimpleFieldModel.installCacheListener(context);

		sessionsPanel = new DataPane<>(context, true, Session.class, beans::getSessions, new SessionCreator(context),
				new SessionFilter(), null, BeanComparators::compareSessions, new SessionFormModel(Session.class));
		worksPanel = new DataPane<>(context, true, Work.class, beans::getWorks, new WorkCreator(context), null, null,
				BeanComparators::compareWorks, new WorkFormModel(Work.class), "Description", "Pieces", "Statistics");
		authorsPanel = new DataPane<>(context, true, Author.class, beans::getAuthors, beans::createAuthor, null, null,
				BeanComparators::compareAuthors, new AuthorFormModel(Author.class));
		pricingsPanel = new DataPane<>(context, true, Pricing.class, beans::getPricings, beans::createPricing, null,
				null, BeanComparators::comparePricings, new PricingFormModel(Pricing.class));
		activitiesPanel = new DataPane<>(context, true, Activity.class, beans::getActivities, beans::createActivity,
				null, null, BeanComparators::compareActivities, new ActivityFormModel(Activity.class));
		statisticsPanel = new StatisticsPanel(context);
		configurationPanel = new ConfigurationPanel(context);
		helpPanel = new HelpPanel(context);

		initComponents();
		pack();
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

		mgtPane.addTab(context.getPresentation().getMessage("Works"), worksPanel);
		mgtPane.addTab(context.getPresentation().getMessage("Authors"), authorsPanel);
		mgtPane.addTab(context.getPresentation().getMessage("Pricings"), pricingsPanel);
		mgtPane.addTab(context.getPresentation().getMessage("Activities"), activitiesPanel);

		mgtPane.addChangeListener(new MgtPaneRefresher(mgtPane));

		mainPane.addTab(context.getPresentation().getMessage("Sessions"), sessionsPanel);
		mainPane.addTab(context.getPresentation().getMessage("Management"), mgtPane);
		mainPane.addTab(context.getPresentation().getMessage("Statistics"), statisticsPanel);
		mainPane.addTab(context.getPresentation().getMessage("Configuration"), configurationPanel);
		mainPane.addTab(context.getPresentation().getMessage("Help"), helpPanel);

		mainPane.addChangeListener(new MainPaneRefresher(mgtPane, mainPane));

		addWindowListener(new ApplicationWindowListener());
	}

	private void refreshMgtPane(final int selectedMgtIndex) {
		LOGGER.debug("Selected management index {}", selectedMgtIndex);
		switch (selectedMgtIndex) {
		case 0:
			worksPanel.refresh();
			break;
		case 1:
			authorsPanel.refresh();
			break;
		case 2:
			pricingsPanel.refresh();
			break;
		case 3:
			activitiesPanel.refresh();
			break;
		default:
			LOGGER.debug("ignored strange management index {}", selectedMgtIndex);
		}
	}

	private void refreshMainPane(final int selectedMainIndex, final int selectedMgtIndex) {
		LOGGER.debug("Selected main index {}", selectedMainIndex);
		switch (selectedMainIndex) {
		case 0:
			sessionsPanel.refresh();
			break;
		case 1:
			refreshMgtPane(selectedMgtIndex);
			break;
		case 2:
			statisticsPanel.refresh();
			break;
		default:
			LOGGER.debug("ignored strange main index {}", selectedMainIndex);
		}
	}

}
