package tech.lapsa.epayment.notificationDaemon.drivenBeans;

import java.util.Locale;
import java.util.Properties;

import javax.ejb.MessageDriven;
import javax.inject.Inject;

import tech.lapsa.epayment.domain.Invoice;
import tech.lapsa.epayment.notificationDaemon.resources.QRecipientUser;
import tech.lapsa.epayment.shared.jms.EpaymentDestinations;
import tech.lapsa.epayment.shared.notification.NotificationMessages;
import tech.lapsa.epayment.shared.notification.NotificationTemplates;
import tech.lapsa.java.commons.function.MyObjects;
import tech.lapsa.java.commons.function.MyStrings;
import tech.lapsa.javax.mail.MailBuilderException;
import tech.lapsa.javax.mail.MailFactory;
import tech.lapsa.javax.mail.MailMessageBuilder;
import tech.lapsa.lapsa.text.TextFactory.TextModelBuilder;

@MessageDriven(mappedName = EpaymentDestinations.NOTIFIER_PAYMENTLINK_REQUESTER_EMAIL)
public class PaymentLinkUserEmailDrivenBean extends EmailInvoiceNotificationBase<Invoice> {

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
	return NotificationMessages.PAYMENT_LINK_NOTIFICATION_SUBJECT;
    }

    @Override
    protected NotificationTemplates getBodyTemplate() {
	return NotificationTemplates.PAYMENT_LINK_NOTIFICATION_TEMPLATE;
    }

    @Override
    protected TextModelBuilder updateTextModel(final TextModelBuilder textModelBuilder, final Invoice invoice,
	    final Properties properties) {
	MyObjects.requireNonNull(textModelBuilder, "textModelBuilder");
	MyObjects.requireNonNull(invoice, "invoice");
	MyObjects.requireNonNull(properties, "properties");

	final String paymentUrl = MyStrings.requireNonEmpty(properties.getProperty("paymentUrl"), "paymentUrl");
	try {
	    textModelBuilder //
		    .bind("paymentUrl", paymentUrl);
	} catch (final IllegalStateException ignoresIfArleadyBint) {
	}
	return textModelBuilder;
    }

}
