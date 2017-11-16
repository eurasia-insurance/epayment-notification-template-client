package tech.lapsa.epayment.notifier.beans.mdb;

import static tech.lapsa.epayment.notifier.beans.Constants.*;

import java.util.Locale;
import java.util.Properties;

import javax.ejb.MessageDriven;
import javax.inject.Inject;

import tech.lapsa.epayment.domain.Invoice;
import tech.lapsa.epayment.notifier.beans.NotificationMessages;
import tech.lapsa.epayment.notifier.beans.NotificationTemplates;
import tech.lapsa.epayment.notifier.beans.qualifiers.QRecipientUser;
import tech.lapsa.java.commons.function.MyObjects;
import tech.lapsa.java.commons.function.MyStrings;
import tech.lapsa.javax.mail.MailBuilderException;
import tech.lapsa.javax.mail.MailFactory;
import tech.lapsa.javax.mail.MailMessageBuilder;
import tech.lapsa.lapsa.text.TextFactory.TextModelBuilder;

@MessageDriven(mappedName = JNDI_JMS_DEST_PAYMENTLINK_REQUESTER_EMAIL)
public class PaymentLinkUserEmailDrivenBean extends AEmailRequestNotificationDrivenBean<Invoice> {

    @Inject
    @QRecipientUser
    protected MailFactory mailFactory;

    public PaymentLinkUserEmailDrivenBean() {
	super(Invoice.class);
    }

    @Override
    protected MailFactory mailFactory() {
	return mailFactory;
    }

    @Override
    protected MailMessageBuilder recipients(final MailMessageBuilder builder, final Invoice invoice)
	    throws MailBuilderException {
	return builder.withTORecipient(invoice.getConsumerEmail(), invoice.getConsumerName());
    }

    @Override
    protected Locale locale(final Invoice invoice) {
	return invoice.getConsumerPreferLanguage().getLocale();
    }

    @Override
    protected NotificationMessages getSubjectTemplate() {
	return NotificationMessages.KKB_ORDER_PAYMENT_LINK_SUBJECT;
    }

    @Override
    protected NotificationTemplates getBodyTemplate() {
	return NotificationTemplates.PAYMENT_LINK_NOTIFICATION_TEMPLATE;
    }

    @Override
    protected TextModelBuilder updateTextModel(TextModelBuilder textModelBuilder, Invoice invoice,
	    Properties properties) {
	MyObjects.requireNonNull(textModelBuilder, "textModelBuilder");
	MyObjects.requireNonNull(invoice, "invoice");
	MyObjects.requireNonNull(properties, "properties");

	String paymentUrl = MyStrings.requireNonEmpty(properties.getProperty("paymentUrl"), "paymentUrl");
	try {
	    textModelBuilder //
		    .bind("paymentUrl", paymentUrl);
	} catch (IllegalStateException ignoresIfArleadyBint) {
	}
	return textModelBuilder;
    }

}
