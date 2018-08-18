package net.cadrian.clef.ui.form;

import java.text.NumberFormat;

import javax.swing.JFormattedTextField;

public class NumericFieldComponentFactory implements FieldComponentFactory<Long, JFormattedTextField> {

	private static class NumericFieldComponent implements FieldComponent<Long, JFormattedTextField> {

		private final JFormattedTextField component;

		NumericFieldComponent() {
			component = new JFormattedTextField(NumberFormat.getIntegerInstance());
		}

		@Override
		public JFormattedTextField getComponent() {
			return component;
		}

		@Override
		public Long getData() {
			try {
				return Long.parseLong(component.getText());
			} catch (final NumberFormatException e) {
				return 0L;
			}
		}

		@Override
		public void setData(final Long data) {
			component.setText(data == null ? "" : data.toString());
		}

		@Override
		public double getWeight() {
			return 0;
		}

	}

	@Override
	public FieldComponent<Long, JFormattedTextField> createComponent() {
		return new NumericFieldComponent();
	}

	@Override
	public Class<Long> getDataType() {
		return Long.class;
	}

}
