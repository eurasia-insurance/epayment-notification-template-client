package tech.lapsa.epayment.notifier.beans;

import java.io.InputStream;
import java.util.Locale;

import tech.lapsa.java.commons.localization.LocalizedElement;
import tech.lapsa.java.commons.resources.Resources;

public enum NotificationTemplates implements LocalizedElement {
    PAYMENT_LINK_NOTIFICATION_TEMPLATE, //
    PAYMENT_SUCCESS_TEMPLATE, //
    //
    ;

    public InputStream getResourceAsStream(Locale locale) {
	return Resources.getAsStream(this.getClass(), regular(locale));
    }
}
