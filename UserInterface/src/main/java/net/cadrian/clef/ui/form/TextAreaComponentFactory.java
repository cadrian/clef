package net.cadrian.clef.ui.form;

import javax.swing.JTextArea;

public class TextAreaComponentFactory implements FieldComponentFactory<String, JTextArea> {

	private static class TextAreaComponent implements FieldComponent<String, JTextArea> {

		private final JTextArea component;

		TextAreaComponent() {
			component = new JTextArea();
		}

		@Override
		public JTextArea getComponent() {
			return component;
		}

		@Override
		public String getData() {
			return component.getText();
		}

		@Override
		public void setData(final String data) {
			component.setText(data);
		}

		@Override
		public double getWeight() {
			return 1;
		}

	}

	@Override
	public FieldComponent<String, JTextArea> createComponent() {
		return new TextAreaComponent();
	}

	@Override
	public Class<String> getDataType() {
		return String.class;
	}

}
