package tech.lapsa.epayment.notifier.beans.mdb;

import static tech.lapsa.epayment.notifier.beans.Constants.*;

import java.util.Locale;

import javax.ejb.MessageDriven;
import javax.inject.Inject;

import tech.lapsa.epayment.domain.Invoice;
import tech.lapsa.epayment.notifier.beans.NotificationMessages;
import tech.lapsa.epayment.notifier.beans.NotificationTemplates;
import tech.lapsa.epayment.notifier.beans.qualifiers.QRecipientUser;
import tech.lapsa.javax.mail.MailBuilderException;
import tech.lapsa.javax.mail.MailFactory;
import tech.lapsa.javax.mail.MailMessageBuilder;

@MessageDriven(mappedName = JNDI_JMS_DEST_PAYMENTSUCCESS_REQUESTER_EMAIL)
public class PaymentSuccessUserEmailDrivenBean extends AEmailRequestNotificationDrivenBean<Invoice> {

    @Inject
    @QRecipientUser
    protected MailFactory mailFactory;

    public PaymentSuccessUserEmailDrivenBean() {
	super(Invoice.class);
    }

    @Override
    protected MailFactory mailFactory() {
	return mailFactory;
    }

    @Override
    protected MailMessageBuilder recipients(MailMessageBuilder builder, Invoice invoice) throws MailBuilderException {
	return builder.withTORecipient(invoice.getConsumerEmail(), invoice.getConsumerName());
    }

    @Override
    protected Locale locale(Invoice invoice) {
	return invoice.getConsumerPreferLanguage().getLocale();
    }

    @Override
    protected NotificationMessages getSubjectTemplate() {
	return NotificationMessages.KKB_ORDER_PAYMENT_SUCCESS_SUBJECT;
    }

    @Override
    protected NotificationTemplates getBodyTemplate() {
	return NotificationTemplates.PAYMENT_SUCCESS_TEMPLATE;
    }

}
