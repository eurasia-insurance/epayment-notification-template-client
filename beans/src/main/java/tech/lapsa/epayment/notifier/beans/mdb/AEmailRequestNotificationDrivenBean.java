package tech.lapsa.epayment.notifier.beans.mdb;

import java.util.Locale;

import tech.lapsa.epayment.domain.Invoice;
import tech.lapsa.epayment.notifier.beans.NotificationMessages;
import tech.lapsa.epayment.notifier.beans.NotificationTemplates;
import tech.lapsa.javax.mail.MailBuilderException;
import tech.lapsa.javax.mail.MailException;
import tech.lapsa.javax.mail.MailFactory;
import tech.lapsa.javax.mail.MailMessageBuilder;
import tech.lapsa.lapsa.text.TextFactory;
import tech.lapsa.lapsa.text.TextFactory.TextModelBuilder.TextModel;

public abstract class AEmailRequestNotificationDrivenBean<T extends Invoice> extends AOrderNotificationDrivenBean<T> {

    AEmailRequestNotificationDrivenBean(final Class<T> objectClazz) {
	super(objectClazz);
    }

    protected abstract MailFactory mailFactory();

    protected abstract MailMessageBuilder recipients(MailMessageBuilder builder, Invoice request)
	    throws MailBuilderException;

    protected abstract NotificationMessages getSubjectTemplate();

    protected abstract NotificationTemplates getBodyTemplate();

    @Override
    protected void sendWithModel(TextModel textModel, T order) {
	try {
	    Locale locale = locale(order);

	    MailMessageBuilder template = mailFactory()
		    .newMailBuilder();

	    String subject = TextFactory.newTextTemplateBuilder() //
		    .buildFromPattern(getSubjectTemplate().regular(locale)) //
		    .merge(textModel) //
		    .asString();
	    template.withSubject(subject);

	    String body = TextFactory.newTextTemplateBuilder() //
		    .buildFromInputStream(getBodyTemplate().getResourceAsStream(locale)) //
		    .merge(textModel) //
		    .asString();
	    template.withHtmlPart(body);

	    recipients(template, order)
		    .build()
		    .send();

	} catch (MailException e) {
	    throw new RuntimeException("Failed to create or send email", e);
	}
    }

}
