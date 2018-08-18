package net.cadrian.clef.ui;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.Beans;
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
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			for (final LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					LOGGER.info("Using Nimbus L&F");
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			LOGGER.info("Could not load L&F", e);
		}

		setTitle("Clef");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		initComponents();
		pack();
	}

	private void initComponents() {
		final Locale locale = Locale.getDefault();
		LOGGER.info("Current locale: {}", locale);
		final ResourceBundle messages = ResourceBundle.getBundle("Clef");

		final JTabbedPane mainPane = new JTabbedPane();
		getContentPane().add(mainPane);

		mainPane.addTab(messages.getString("Sessions"),
				new DataPane<>(beans::getSessions, new SessionCreator(beans, messages), messages));
		mainPane.addTab(messages.getString("Works"),
				new DataPane<>(beans::getWorks, new WorkCreator(beans, messages), messages));
		mainPane.addTab(messages.getString("Authors"),
				new DataPane<>(beans::getAuthors, beans::createAuthor, messages));
		mainPane.addTab(messages.getString("Pricings"),
				new DataPane<>(beans::getPricings, beans::createPricing, messages));

		mainPane.addTab(messages.getString("Statistics"), new JPanel()); // TODO
	}

	private static class SessionCreator implements DataPane.BeanCreator<Session> {

		private final Beans beans;
		private final ResourceBundle messages;

		public SessionCreator(final Beans beans, final ResourceBundle messages) {
			this.beans = beans;
			this.messages = messages;
		}

		@Override
		public Session createBean() {
			// TODO Auto-generated method stub
			// must ask for the right Piece to attach the Session to
			// (note: no Piece => return null + popup "create Piece first")
			// and the start date will be set to "now"
			return null;
		}
	}

	private static class WorkCreator implements DataPane.BeanCreator<Work> {

		private final Beans beans;
		private final ResourceBundle messages;

		public WorkCreator(final Beans beans, final ResourceBundle messages) {
			this.beans = beans;
			this.messages = messages;
		}

		@Override
		public Work createBean() {
			// TODO Auto-generated method stub
			// must ask for the right Author and Pricing
			// (note: no Author / Pricing => return null + popup "create Author / Pricing
			// first")
			return null;
		}
	}

}
