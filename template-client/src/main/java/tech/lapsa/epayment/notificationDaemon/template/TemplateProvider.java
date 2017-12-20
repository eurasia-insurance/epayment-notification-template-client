package tech.lapsa.epayment.notificationDaemon.template;

import java.util.Locale;

import javax.ejb.Local;
import javax.ejb.Remote;

import tech.lapsa.java.commons.exceptions.IllegalArgument;
import tech.lapsa.java.commons.localization.Localized.LocalizationVariant;

public interface TemplateProvider {

    @Local
    public interface TemplateProviderLocal extends TemplateProvider {
    }

    @Remote
    public interface TemplateProviderRemote extends TemplateProvider {
    }

    String getMessage(NotificationMessages message, LocalizationVariant variant, Locale locale) throws IllegalArgument;

    String getTemplate(NotificationTemplates message, Locale locale) throws IllegalArgument;

}
