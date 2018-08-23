package net.cadrian.clef.ui.rte;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.undo.UndoManager;

import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.Presentation;

public class RichTextEditor extends JPanel {
	// Developed using as examples:
	// - http://www.javaquizplayer.com/examples/text-editor-using-java-example.html
	// - https://www.artima.com/forums/flat.jsp?forum=1&thread=1276

	private static final long serialVersionUID = 7187888775973838539L;

	final StyledDocument document;
	final JTextPane editor;

	private final UndoManager undoManager;

	public RichTextEditor(final ApplicationContext context) {
		super(new BorderLayout());
		final Presentation presentation = context.getPresentation();

		setBorder(BorderFactory.createEtchedBorder());
		document = new DefaultStyledDocument();
		editor = new JTextPane();
		undoManager = new UndoManager();

		editor.setDocument(document);
		add(new JScrollPane(editor), BorderLayout.CENTER);
		final JToolBar tools = new JToolBar(SwingConstants.HORIZONTAL);
		tools.setFloatable(false);

		document.addUndoableEditListener(new UndoableEditListener() {
			@Override
			public void undoableEditHappened(final UndoableEditEvent e) {
				undoManager.addEdit(e.getEdit());
			}
		});

		final ActionListener editButtonActionListener = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				editor.requestFocusInWindow();
			}
		};

		final Action undoAction = new AbstractAction("RTE.Undo") {
			private static final long serialVersionUID = 1606783148768829015L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (undoManager.canUndo()) {
					undoManager.undo();
				} else {
					editor.requestFocusInWindow();
				}
			}
		};

		final Action redoAction = new AbstractAction("RTE.Redo") {
			private static final long serialVersionUID = -2878268317676372393L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (undoManager.canRedo()) {
					undoManager.redo();
				} else {
					editor.requestFocusInWindow();
				}
			}
		};

		// CLIPBOARD ACTIONS
		final Action cutAction = new DefaultEditorKit.CutAction();
		cutAction.putValue(Action.NAME, "RTE.Cut");
		final Action copyAction = new DefaultEditorKit.CopyAction();
		copyAction.putValue(Action.NAME, "RTE.Copy");
		final Action pasteAction = new DefaultEditorKit.PasteAction();
		pasteAction.putValue(Action.NAME, "RTE.Paste");

		// TEXT ACTIONS
		final Action boldAction = new StyledEditorKit.BoldAction();
		boldAction.putValue(Action.NAME, "RTE.Bold");
		final Action italicAction = new StyledEditorKit.ItalicAction();
		italicAction.putValue(Action.NAME, "RTE.Italic");
		final Action underlineAction = new StyledEditorKit.UnderlineAction();
		underlineAction.putValue(Action.NAME, "RTE.Underline");

		final JButton undoButton = tools.add(undoAction);
		undoButton.addActionListener(editButtonActionListener);
		final JButton redoButton = tools.add(redoAction);
		redoButton.addActionListener(editButtonActionListener);
		tools.add(new JSeparator(SwingConstants.VERTICAL));
		tools.add(cutAction).addActionListener(editButtonActionListener);
		tools.add(copyAction).addActionListener(editButtonActionListener);
		tools.add(pasteAction).addActionListener(editButtonActionListener);
		tools.add(new JSeparator(SwingConstants.VERTICAL));
		final JButton boldButton = tools.add(boldAction);
		boldButton.addActionListener(editButtonActionListener);
		final JButton italicButton = tools.add(italicAction);
		italicButton.addActionListener(editButtonActionListener);
		final JButton underlineButton = tools.add(underlineAction);
		underlineButton.addActionListener(editButtonActionListener);

		undoAction.setEnabled(false);
		redoAction.setEnabled(false);

		document.addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(final DocumentEvent e) {
				undoAction.setEnabled(undoManager.canUndo());
				redoAction.setEnabled(undoManager.canRedo());
			}

			@Override
			public void insertUpdate(final DocumentEvent e) {
				undoAction.setEnabled(undoManager.canUndo());
				redoAction.setEnabled(undoManager.canRedo());
			}

			@Override
			public void changedUpdate(final DocumentEvent e) {
				undoAction.setEnabled(undoManager.canUndo());
				redoAction.setEnabled(undoManager.canRedo());
			}
		});

		editor.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				if ((e.getModifiers() & InputEvent.CTRL_MASK) == 0) {
					return;
				}
				switch (e.getKeyCode()) {
				// Don't put Ctrl-C,X,V -- already taken into account by the editor itself
				case KeyEvent.VK_Y:
					redoButton.doClick(0);
					break;
				case KeyEvent.VK_Z:
					undoButton.doClick(0);
					break;
				case KeyEvent.VK_B:
					boldButton.doClick(0);
					break;
				case KeyEvent.VK_I:
					italicButton.doClick(0);
					break;
				case KeyEvent.VK_U:
					underlineButton.doClick(0);
					break;
				}
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						undoAction.setEnabled(undoManager.canUndo());
						redoAction.setEnabled(undoManager.canRedo());
					}
				});
			}
		});

		add(presentation.awesome(tools), BorderLayout.NORTH);
	}

	public void replaceSelection(String content) {
		editor.replaceSelection(content);
		undoManager.discardAllEdits();
	}

	public void setEditable(final boolean writable) {
		editor.setEditable(writable);
	}

	public String getText() {
		return editor.getText();
	}

	public void setText(final String text) {
		editor.setText(text);
	}

}