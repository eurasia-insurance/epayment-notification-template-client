package tech.lapsa.epayment.notificationDaemon.template;

import java.util.Locale;

import javax.ejb.Local;
import javax.ejb.Remote;

import tech.lapsa.java.commons.exceptions.IllegalArgument;

public interface TemplateProvider {

    @Local
    public interface TemplateProviderLocal extends TemplateProvider {
    }

    @Remote
    public interface TemplateProviderRemote extends TemplateProvider {
    }

    String getMessage(NotificationMessages message, Locale locale) throws IllegalArgument;

    String getTemplate(NotificationTemplates message, Locale locale) throws IllegalArgument;

}
