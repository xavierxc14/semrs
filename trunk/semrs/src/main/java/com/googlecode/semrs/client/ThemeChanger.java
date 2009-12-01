package com.googlecode.semrs.client;


import com.gwtext.client.data.Record;
import com.gwtext.client.data.SimpleStore;
import com.gwtext.client.data.Store;
import com.gwtext.client.util.CSS;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.event.ComboBoxListenerAdapter;


/**
 * A simple dynamic Theme Changer ComboBox. You must have the Ext theme stylesheet declared in your host html page
 * using the id "theme".
 * <p/>
 * For example
 * <p/>
 * &lt;link id="theme" rel="stylesheet" type="text/css" href="js/ext/resources/css/xtheme-gray.css"/&gt;
 * or
 * &lt;link id="theme" rel="stylesheet" type="text/css" href="xtheme-default.css"/&gt;
 */
public class ThemeChanger extends ComboBox {

	public ThemeChanger() {

		final Store store = new SimpleStore(new String[]{"theme", "label"}, new Object[][]{
                new Object[]{"themes/green/css/xtheme-green.css", "Estilo 1"},
                new Object[]{"themes/slate/css/xtheme-slate.css", "Estilo 2"},
				new Object[]{"js/ext/resources/css/xtheme-gray.css", "Estilo 3"},
				//new Object[]{"xtheme-default.css", "Aero Glass"},
				new Object[]{"themes/indigo/css/xtheme-indigo.css", "Estilo 4"},
				new Object[]{"themes/silverCherry/css/xtheme-silverCherry.css", "Estilo 5"}
		});

		store.load();

		setFieldLabel("Select Theme");
		setEditable(false);
		setStore(store);
		setDisplayField("label");
		setForceSelection(true);
		setTriggerAction(ComboBox.ALL);
		setValue("Estilo 1");
		setFieldLabel("Switch theme");
		addListener(new ComboBoxListenerAdapter() {
			public void onSelect(ComboBox comboBox, Record record, int index) {
				String theme = record.getAsString("theme");
				CSS.swapStyleSheet("theme", theme);
			}
		});
		setWidth(100);
	}
}

