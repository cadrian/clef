package net.cadrian.clef.ui.form;

import javax.swing.JTextField;

public class TextFieldComponentFactory implements FieldComponentFactory<String, JTextField> {

	private static class TextFieldComponent implements FieldComponent<String, JTextField> {

		private final JTextField component;

		TextFieldComponent() {
			component = new JTextField();
		}

		@Override
		public JTextField getComponent() {
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
			return 0;
		}

	}

	@Override
	public FieldComponent<String, JTextField> createComponent() {
		return new TextFieldComponent();
	}

	@Override
	public Class<String> getDataType() {
		return String.class;
	}

}
