package net.cadrian.clef.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.Beans;
import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.Work;

class StatisticsPanel extends JPanel {

	private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsPanel.class);

	private static final long serialVersionUID = -2023860576290261246L;

	private final Beans beans;

	private final JLabel meanPerWork;
	private final JLabel stdevPerWork;
	private final JLabel meanPerPiece;
	private final JLabel stdevPerPiece;

	StatisticsPanel(final Resources rc, final Beans beans) {
		super(new GridBagLayout());
		this.beans = beans;

		final JPanel panel = new JPanel(new GridBagLayout());

		meanPerWork = new JLabel();
		stdevPerWork = new JLabel();
		meanPerPiece = new JLabel();
		stdevPerPiece = new JLabel();
		Map<String, JLabel> labels = new LinkedHashMap<>();
		labels.put("MeanPerWork", meanPerWork);
		labels.put("StdDeviationPerWork", stdevPerWork);
		labels.put("MeanPerPiece", meanPerPiece);
		labels.put("StdDeviationPerPiece", stdevPerPiece);
		addLabels(rc, labels, panel);

		panel.setBorder(BorderFactory.createEtchedBorder());

		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		add(panel, constraints);

		addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				refresh();
			}
		});

		refresh();
	}

	private void addLabels(Resources rc, Map<String, JLabel> labels, JPanel panel) {
		int gridy = 0;
		for (final Map.Entry<String, JLabel> entry : labels.entrySet()) {
			final GridBagConstraints labelConstraints = new GridBagConstraints();
			labelConstraints.gridy = gridy;
			labelConstraints.anchor = GridBagConstraints.WEST;
			labelConstraints.insets = new Insets(4, 2, 4, 5);
			final String label = rc.getMessage("Statistics." + entry.getKey());
			LOGGER.debug("Label for {} is {}", entry.getKey(), label);
			panel.add(rc.bolden(new JLabel(label)), labelConstraints);

			final GridBagConstraints fieldConstraints = new GridBagConstraints();
			fieldConstraints.gridx = 1;
			fieldConstraints.gridy = gridy;
			fieldConstraints.weightx = 1;
			fieldConstraints.insets = new Insets(4, 5, 4, 2);
			fieldConstraints.anchor = GridBagConstraints.WEST;
			panel.add(entry.getValue(), fieldConstraints);

			gridy++;
		}
	}

	private void refresh() {
		long nw = 0;
		long tw = 0;
		long np = 0;
		long tp = 0;

		for (final Work work : beans.getWorks()) {
			nw++;
			long twp = 0;
			for (final Piece piece : work.getPieces()) {
				Long duration = piece.getDuration();
				if (duration != null) {
					twp += duration;
					tp += duration;
					np++;
				}
			}
			tw += twp;
		}

		long mw = tw / nw;
		long mp = tp / np;

		long sw = 0;
		long sp = 0;

		for (final Work work : beans.getWorks()) {
			long twp = 0;
			for (final Piece piece : work.getPieces()) {
				Long duration = piece.getDuration();
				if (duration != null) {
					twp += duration;
					long dp = mp - duration;
					sp += dp * dp;
				}
			}
			long dp = mw - twp;
			sw += dp * dp;
		}

		sw = Math.round(Math.sqrt(sw));
		sp = Math.round(Math.sqrt(sp));

		meanPerWork.setText(format(mw));
		stdevPerWork.setText(format(sw));
		meanPerPiece.setText(format(mp));
		stdevPerPiece.setText(format(sp));
	}

	private final String format(long time) {
		StringBuilder result = new StringBuilder();
		long s = time;
		long m = s / 60;
		s -= m * 60;
		final long h = m / 24;
		m -= h * 24;
		if (h < 10) {
			result.append("0");
		}
		result.append(h).append(":");
		if (m < 10) {
			result.append("0");
		}
		result.append(m).append(":");
		if (s < 10) {
			result.append("0");
		}
		result.append(s);
		return result.toString();
	}

}