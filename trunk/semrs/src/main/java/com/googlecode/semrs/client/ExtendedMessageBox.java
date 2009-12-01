package com.googlecode.semrs.client;

import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBox.ConfirmCallback;

public class ExtendedMessageBox extends MessageBox{
	
	
	/**
     * Displays a confirmation message box with Yes and No buttons (comparable to JavaScript's Window.confirm). If a
     * callback function is passed it will be called after the user clicks either button, and the id of the button that
     * was clicked will be passed as the only parameter to the callback (could also be the top-right close button).
     *
     * @param title   the title
     * @param message the message
     * @param cb      the callback function
     */
    public static native void confirmlg(String title, String message, String messageb1, String messageb2, ConfirmCallback cb) /*-{
        
        $wnd.Ext.MessageBox.buttonText.yes = messageb1;
        $wnd.Ext.MessageBox.buttonText.no = messageb2;
        $wnd.Ext.MessageBox.confirm(title, message, function(btnID) {
            cb.@com.gwtext.client.widgets.MessageBox.ConfirmCallback::execute(Ljava/lang/String;)(btnID);
        });
    }-*/;

}
