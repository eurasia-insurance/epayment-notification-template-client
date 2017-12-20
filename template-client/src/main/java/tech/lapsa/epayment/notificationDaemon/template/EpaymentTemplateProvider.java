package tech.lapsa.epayment.notificationDaemon.template;

import java.util.Locale;

import javax.ejb.Local;
import javax.ejb.Remote;

import tech.lapsa.java.commons.exceptions.IllegalArgument;

public interface EpaymentTemplateProvider {

    @Local
    public interface TemplateProviderLocal extends EpaymentTemplateProvider {
    }

    @Remote
    public interface TemplateProviderRemote extends EpaymentTemplateProvider {
    }

    String getMessage(NotificationMessages message, Locale locale) throws IllegalArgument;

    String getTemplate(NotificationTemplates message, Locale locale) throws IllegalArgument;

}
