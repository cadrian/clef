package net.cadrian.clef.ui.app.form.field.piece;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.Beans;
import net.cadrian.clef.model.bean.Author;
import net.cadrian.clef.model.bean.BeanComparators;
import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.Work;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.Presentation;
import net.cadrian.clef.ui.app.form.BeanMover;
import net.cadrian.clef.ui.tools.SortableListModel;
import net.cadrian.clef.ui.widget.ClefTools;

class PieceMover implements BeanMover<Piece> {

	private static final Logger LOGGER = LoggerFactory.getLogger(PieceMover.class);

	private final ApplicationContext context;

	PieceMover(final ApplicationContext context) {
		this.context = context;
	}

	@Override
	public boolean canMove(final Piece piece) {
		final Work work = piece.getWork();
		final Author author = work.getAuthor();
		final Collection<? extends Work> worksBy = context.getBeans().getWorksBy(author);
		return worksBy.size() > 1;
	}

	@Override
	public boolean move(final Piece piece) {
		final Beans beans = context.getBeans();
		final Presentation presentation = context.getPresentation();
		final JFrame parent = presentation.getApplicationFrame();

		final Work work = piece.getWork();
		final Author author = work.getAuthor();
		final List<? extends Work> worksBy = new ArrayList<>(beans.getWorksBy(author));
		worksBy.remove(work);

		final SortableListModel<Work> worksModel = new SortableListModel<>(BeanComparators::compareWorks, worksBy);

		final JDialog params = new JDialog(parent, presentation.getMessage("PieceMoverTitle"), true);

		final JPanel paramsContent = new JPanel(new BorderLayout());
		params.getContentPane().add(paramsContent);
		paramsContent.add(new JLabel(presentation.getMessage("PieceMoverMessage")), BorderLayout.NORTH);

		final JList<Work> works = new JList<>(worksModel);
		final JPanel worksPanel = new JPanel(new BorderLayout());
		worksPanel.add(
				presentation.bold(new JLabel(presentation.getMessage("PieceMoverWorksTitle"), SwingConstants.CENTER)),
				BorderLayout.NORTH);
		worksPanel.add(new JScrollPane(works), BorderLayout.CENTER);

		paramsContent.add(works, BorderLayout.CENTER);

		final ClefTools tools = new ClefTools(context, ClefTools.Tool.Move);

		final AtomicBoolean added = new AtomicBoolean(false);
		final AtomicBoolean worksSelected = new AtomicBoolean(false);

		tools.addListener(new ClefTools.Listener() {

			@Override
			public void toolCalled(final ClefTools tools, final ClefTools.Tool tool) {
				switch (tool) {
				case Move:
					final Work work = works.getSelectedValue();
					if (work != null) {
						added.set(true);
						params.setVisible(false);
					}
					break;
				default:
				}
			}
		});

		tools.getAction(ClefTools.Tool.Move).setEnabled(false);

		works.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(final ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					worksSelected.set(true);
					tools.getAction(ClefTools.Tool.Move).setEnabled(true);
					added.set(false);
				}
			}
		});

		paramsContent.add(tools, BorderLayout.NORTH);

		params.setMinimumSize(new Dimension(200, 150));
		params.pack();
		params.setLocationRelativeTo(parent);
		LOGGER.debug("Showing PieceMover dialog");
		params.setVisible(true);

		final Work selectedWork = works.getSelectedValue();
		if (!added.get()) {
			LOGGER.debug("Not saved, not moving piece");
			return false;
		}
		if (selectedWork == null) {
			LOGGER.debug("Missing data, not moving piece");
			return false;
		}

		LOGGER.info("Moving piece {} from {} to {}", piece, work, selectedWork);
		return beans.movePiece(piece, selectedWork);
	}

}
