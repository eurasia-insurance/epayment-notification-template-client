package tech.lapsa.epayment.notificationDaemon.drivenBeans;

import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.EJBException;

import tech.lapsa.epayment.domain.Invoice;
import tech.lapsa.epayment.notificationDaemon.template.NotificationMessages;
import tech.lapsa.epayment.notificationDaemon.template.NotificationTemplates;
import tech.lapsa.epayment.notificationDaemon.template.TemplateProvider.TemplateProviderRemote;
import tech.lapsa.java.commons.exceptions.IllegalArgument;
import tech.lapsa.java.commons.localization.Localized.LocalizationVariant;
import tech.lapsa.javax.mail.MailBuilderException;
import tech.lapsa.javax.mail.MailException;
import tech.lapsa.javax.mail.MailFactory;
import tech.lapsa.javax.mail.MailMessageBuilder;
import tech.lapsa.lapsa.text.TextFactory;
import tech.lapsa.lapsa.text.TextFactory.TextModelBuilder.TextModel;

public abstract class EmailInvoiceNotificationBase<T extends Invoice> extends InvoiceNotificationBase<T> {

    EmailInvoiceNotificationBase(final Class<T> objectClazz) {
	super(objectClazz);
    }

    protected abstract MailFactory mailFactory();

    protected abstract MailMessageBuilder recipients(MailMessageBuilder builder, Invoice request)
	    throws MailBuilderException;

    protected abstract NotificationMessages getSubjectTemplate();

    protected abstract NotificationTemplates getBodyTemplate();

    @EJB
    private TemplateProviderRemote templates;

    @Override
    protected void sendWithModel(final TextModel textModel, final T invoice) {
	try {
	    final Locale locale = locale(invoice);

	    final MailMessageBuilder template = mailFactory()
		    .newMailBuilder();

	    final String subjectTemplate;
	    try {
		subjectTemplate = templates.getMessage(getSubjectTemplate(), LocalizationVariant.NORMAL,
			locale);
	    } catch (IllegalArgument e) {
		// it should not happens
		throw new EJBException(e.getMessage());
	    }

	    final String subject = TextFactory.newTextTemplateBuilder() //
		    .buildFromPattern(subjectTemplate) //
		    .merge(textModel) //
		    .asString();
	    template.withSubject(subject);

	    final String bodyTemplate;
	    try {
		bodyTemplate = templates.getTemplate(getBodyTemplate(), locale);
	    } catch (IllegalArgument e) {
		// it should not happens
		throw new EJBException(e.getMessage());
	    }

	    final String body = TextFactory.newTextTemplateBuilder() //
		    .buildFromPattern(bodyTemplate) //
		    .merge(textModel) //
		    .asString();
	    template.withHtmlPart(body);

	    recipients(template, invoice)
		    .build()
		    .send();

	} catch (final MailException e) {
	    throw new RuntimeException("Failed to create or send email", e);
	}
    }

}
