package tech.lapsa.epayment.notifier.beans;

import java.io.InputStream;
import java.util.Locale;

import tech.lapsa.java.commons.io.MyResources;
import tech.lapsa.java.commons.localization.LocalizedElement;

public enum NotificationTemplates implements LocalizedElement {
    PAYMENT_LINK_NOTIFICATION_TEMPLATE, //
    PAYMENT_SUCCESS_TEMPLATE, //
    //
    ;

    public InputStream getResourceAsStream(Locale locale) {
	return MyResources.getAsStream(this.getClass(), regular(locale));
    }
}
