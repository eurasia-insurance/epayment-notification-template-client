package tech.lapsa.epayment.notificationDaemon.drivenBeans;

import java.util.Locale;

import tech.lapsa.epayment.domain.Invoice;
import tech.lapsa.epayment.shared.notification.NotificationMessages;
import tech.lapsa.epayment.shared.notification.NotificationTemplates;
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

    @Override
    protected void sendWithModel(final TextModel textModel, final T invoice) {
	try {
	    final Locale locale = locale(invoice);

	    final MailMessageBuilder template = mailFactory()
		    .newMailBuilder();

	    final String subject = TextFactory.newTextTemplateBuilder() //
		    .buildFromPattern(getSubjectTemplate().regular(locale)) //
		    .merge(textModel) //
		    .asString();
	    template.withSubject(subject);

	    final String body = TextFactory.newTextTemplateBuilder() //
		    .buildFromInputStream(getBodyTemplate().getResourceAsStream(locale)) //
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
